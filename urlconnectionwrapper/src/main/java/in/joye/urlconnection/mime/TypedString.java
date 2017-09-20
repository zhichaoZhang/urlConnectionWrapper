package in.joye.urlconnection.mime;

import java.io.UnsupportedEncodingException;

/**
 * 字符串类型的实体域
 * <p>
 * Created by joye on 2017/9/17.
 */

public class TypedString extends TypedByteArray {

    public TypedString(String content) {
        super("text/plain; charset=UTF-8", convertToBytes(content));
    }

    private static byte[] convertToBytes(String content) {
        try {
            return content.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        try {
            return "TypedString[" + new String(getBytes(), "UTF-8") + "]";
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("Must be able to decode UTF-8");
        }
    }
}
