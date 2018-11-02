package in.joye.urlconnection.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import in.joye.urlconnection.client.Response;
import in.joye.urlconnection.mime.TypedByteArray;
import in.joye.urlconnection.mime.TypedInput;

/**
 * Http协议数据部分操作工具类
 *
 * Created by joye on 2018/11/2.
 */

public class HttpBodyUtil {

    private HttpBodyUtil() {
        //No instance
    }

    /**
     * Conditionally replace a {@link Response} with an identical copy whose body is backed by a
     * byte[] rather than an input stream.
     */
    public static Response readBodyToBytesIfNecessary(Response response) throws IOException {
        TypedInput body = response.getBody();
        if (body == null || body instanceof TypedByteArray) {
            return response;
        }

        String bodyMime = body.mimeType();
        InputStream is = body.in();
        try {
            byte[] bodyBytes = streamToBytes(is);
            body = new TypedByteArray(bodyMime, bodyBytes);

            return replaceResponseBody(response, body);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * Creates a {@code byte[]} from reading the entirety of an {@link InputStream}. May return an
     * empty array but never {@code null}.
     * <p>
     * Copied from Guava's {@code ByteStreams} class.
     */
    private static final int BUFFER_SIZE = 0x1000;
    private static byte[] streamToBytes(InputStream stream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (stream != null) {
            byte[] buf = new byte[BUFFER_SIZE];
            int r;
            while ((r = stream.read(buf)) != -1) {
                baos.write(buf, 0, r);
            }
        }
        return baos.toByteArray();
    }

    private static Response replaceResponseBody(Response response, TypedInput body) {
        return new Response(response.getUrl(), response.getStatus(), response.getReason(),
                response.getHeaders(), body);
    }

}
