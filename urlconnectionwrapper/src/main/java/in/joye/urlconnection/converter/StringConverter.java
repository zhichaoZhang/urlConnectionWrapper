package in.joye.urlconnection.converter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;

import in.joye.urlconnection.mime.TypedInput;
import in.joye.urlconnection.mime.TypedOutput;

/**
 * 将输入输出流按照字符串类型处理
 * <p>
 * Created by joye on 2017/11/14.
 */

public class StringConverter implements Converter {
    private String charset = null;

    public StringConverter() {
        this("utf-8");
    }

    public StringConverter(String charset) {
        this.charset = charset;
    }

    @Override
    public String fromBody(TypedInput body, Type type) throws ConversionException {
        ByteArrayOutputStream bos = null;
        try {
            bos = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int count;
            while ((count = body.in().read(buffer, 0, bufferSize)) != -1) {
                bos.write(buffer, 0 ,count);
            }
            return new String(bos.toByteArray(), charset);
        } catch (IOException e) {
            throw new ConversionException(e);
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public TypedOutput toBody(Object object) {
        return new StringTypedOutput(((String)object).getBytes(), charset);
    }

    private static class StringTypedOutput implements TypedOutput {
        private final byte[] stringBytes;
        private final String mimeType;

        public StringTypedOutput(byte[] stringBytes, String charset) {
            this.stringBytes = stringBytes;
            this.mimeType = "application/string; charset=" + charset;
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
            return stringBytes.length;
        }

        @Override
        public void writeTo(OutputStream outputStream) throws IOException {
            outputStream.write(stringBytes);
        }
    }
}
