package in.joye.urlconnection.mime;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

/**
 * 表单编码类型的请求体
 * <p>
 * Created by joye on 2017/9/17.
 */

public class FormUrlEncodedTypedOutput implements TypedOutput {

    final ByteArrayOutputStream content = new ByteArrayOutputStream();

    public void addField(String name, String value) {
        addField(name, true, value, true);
    }

    public void addField(String name, boolean encodeName, String value, boolean encodeValue) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        if (value == null) {
            throw new NullPointerException("value");
        }
        try {
            if (encodeName) {
                name = URLEncoder.encode(name, "UTF-8");
            }
            if (encodeValue) {
                value = URLEncoder.encode(value, "UTF-8");
            }
            if (content.size() > 0) {
                content.write('&');
            }
            content.write(name.getBytes("UTF-8"));
            content.write('=');
            content.write(value.getBytes("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String fileName() {
        return null;
    }

    @Override
    public String mimeType() {
        return "application/x-www-form-urlencoded; charset=UTF-8";
    }

    @Override
    public long length() {
        return content.size();
    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
        outputStream.write(content.toByteArray());
    }
}
