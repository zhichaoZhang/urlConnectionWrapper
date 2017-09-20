package in.joye.urlconnection.client;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import in.joye.urlconnection.UrlConnectionWrapper;
import in.joye.urlconnection.converter.ConversionException;
import in.joye.urlconnection.converter.Converter;
import in.joye.urlconnection.mime.TypedInput;
import in.joye.urlconnection.mime.TypedOutput;

/**
 * 基于URLConnection的请求
 * <p>
 * Created by joye on 2017/8/28.
 */

public class UrlConnectionCall<T> implements Call<T> {
    private static final int CHUNK_SIZE = 4096;
    private Request request;
    private boolean executed = false;
    private UrlConnectionWrapper urlConnectionWrapper;
    private volatile boolean canceled;
    private Type bodyType;

    public UrlConnectionCall(UrlConnectionWrapper urlConnectionWrapper, Request request) {
        this(urlConnectionWrapper, request, null);
    }

    public UrlConnectionCall(UrlConnectionWrapper urlConnectionWrapper, Request request, Type bodyType) {
        this.request = request;
        this.urlConnectionWrapper = urlConnectionWrapper;
        this.bodyType = bodyType;
    }

    @Override
    public ResponseWrapper<T> execute() throws IOException {
        if (executed) throw new IllegalStateException("Already executed");
        executed = false;

        HttpURLConnection connection;
        synchronized (this) {
            connection = openConnection(request);
            prepareRequest(connection, request);
        }
        return convertResponse(readResponse(connection));
    }

    private HttpURLConnection openConnection(Request request) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(request.getUrl()).openConnection();
        connection.setConnectTimeout(Defaults.CONNECT_TIMEOUT_MILLIS);
        connection.setReadTimeout(Defaults.READ_TIMEOUT_MILLIS);
        return connection;
    }

    private void prepareRequest(HttpURLConnection connection, Request request) throws IOException {
        connection.setRequestMethod(request.getMethod());
        connection.setDoInput(true);

        for (Header header : request.getHeaders()) {
            connection.addRequestProperty(header.getName(), header.getValue());
        }
        TypedOutput body = request.getBody();
        if (body != null) {
            connection.setDoOutput(true);
            connection.addRequestProperty("Content-Type", body.mimeType());
            long length = body.length();
            if (length != -1) {
                connection.setFixedLengthStreamingMode((int) length);
                connection.addRequestProperty("Content-Length", String.valueOf(length));
            } else {
                connection.setChunkedStreamingMode(CHUNK_SIZE);
            }
            body.writeTo(connection.getOutputStream());
        }
    }

    Response readResponse(HttpURLConnection connection) throws IOException {
        if (canceled) return null;

        int status = connection.getResponseCode();
        String reason = connection.getResponseMessage();
        if (reason == null) reason = "";
        List<Header> headers = new ArrayList<>();
        for (Map.Entry<String, List<String>> field : connection.getHeaderFields().entrySet()) {
            String name = field.getKey();
            for (String value : field.getValue()) {
                headers.add(new Header(name, value));
            }
        }

        String mimeType = connection.getContentType();
        int length = connection.getContentLength();
        InputStream stream;
        if (status >= 400) {
            stream = connection.getErrorStream();
        } else {
            stream = connection.getInputStream();
            String contentEncoding = connection.getContentEncoding();
            if(contentEncoding != null && contentEncoding.equalsIgnoreCase("gzip")) {
                stream = new GZIPInputStream(stream);
            }
        }
        TypedInput responseBody = new TypedInputStream(mimeType, length, stream);
        return new Response(connection.getURL().toString(), status, reason, headers, responseBody);
    }

    ResponseWrapper<T> convertResponse(Response response) {
        if (response == null) return null;
        int statusCode = response.getStatus();
        if (statusCode >= 200 && statusCode < 300 && bodyType != null) {
            Converter converter = urlConnectionWrapper.getConverter();
            try {
                return new ResponseWrapper<>(response, (T) converter.fromBody(response.getBody(), bodyType));
            } catch (ConversionException e) {
                e.printStackTrace();
            }
        }
        return new ResponseWrapper<>(response, null);
    }

    @Override
    public void enqueue(Callback<T> callback) {
        urlConnectionWrapper.getHttpExecutor().execute(new CallbackRunnable<T>(callback, urlConnectionWrapper.getCallbackExecutor()) {
            @Override
            public ResponseWrapper obtainResponse() throws IOException {
                return execute();
            }
        });
    }

    @Override
    public boolean isExecuted() {
        return executed;
    }

    @Override
    public void cancel() {
        canceled = true;
    }

    @Override
    public boolean isCanceled() {
        return canceled;
    }

    @Override
    public UrlConnectionCall<T> clone() {
        return new UrlConnectionCall<>(urlConnectionWrapper, request);
    }

    @Override
    public Request request() {
        return request;
    }

    private static class TypedInputStream implements TypedInput {

        private final String mimeType;
        private final long length;
        private final InputStream stream;

        public TypedInputStream(String mimeType, long length, InputStream stream) {
            this.mimeType = mimeType;
            this.length = length;
            this.stream = stream;
        }

        @Override
        public String mimeType() {
            return mimeType;
        }

        @Override
        public long length() {
            return length;
        }

        @Override
        public InputStream in() throws IOException {
            return stream;
        }
    }
}
