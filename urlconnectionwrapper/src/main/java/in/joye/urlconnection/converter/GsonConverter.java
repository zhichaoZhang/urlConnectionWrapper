package in.joye.urlconnection.converter;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

import in.joye.urlconnection.mime.MimeUtil;
import in.joye.urlconnection.mime.TypedInput;
import in.joye.urlconnection.mime.TypedOutput;

/**
 * 使用GSON来序列化和反序列化实体的转换器
 * <p>
 * Created by joye on 2017/8/31.
 */

public class GsonConverter implements Converter {

    private Gson gson;
    private String charset;

    public GsonConverter(Gson gson) {
        this(gson, "utf-8");
    }

    public GsonConverter(Gson gson, String charset) {
        this.gson = gson;
        this.charset = charset;
    }

    @Override
    public Object fromBody(TypedInput body, Type type) throws ConversionException {
        String charset = this.charset;
        if (body.mimeType() != null) {
            charset = MimeUtil.parseCharset(body.mimeType(), charset);
        }
        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader(body.in(), charset);
            return gson.fromJson(isr, type);
        }catch (IOException e) {
            throw new ConversionException(e);
        } finally {
            if(isr != null) {
                try {
                    isr.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    @Override
    public TypedOutput toBody(Object object) {
        try {
            return new JsonTypedOutput(gson.toJson(object).getBytes(charset), charset);
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }

    private static class JsonTypedOutput implements TypedOutput {
        private final byte[] jsonBytes;
        private final String mimeType;

        public JsonTypedOutput(byte[] jsonBytes, String mimeType) {
            this.jsonBytes = jsonBytes;
            this.mimeType = "application/json; charset=" + mimeType;
        }

        @Override
        public String fileName() {
            return null;
        }

        @Override
        public String mimeType() {
            return mimeType;
        }

        @Override
        public long length() {
            return jsonBytes.length;
        }

        @Override
        public void writeTo(OutputStream outputStream) throws IOException {
            outputStream.write(jsonBytes);
        }
    }
}
