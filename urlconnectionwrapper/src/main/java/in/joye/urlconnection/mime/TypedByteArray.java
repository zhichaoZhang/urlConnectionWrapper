package in.joye.urlconnection.mime;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * 字节数组类型的实体域
 * <p>
 * Created by joye on 2017/9/17.
 */

public class TypedByteArray implements TypedOutput, TypedInput {

    private final String mimeType;
    private final byte[] bytes;

    /**
     * 构造一个指定类型的字节数组
     *
     * @param mimeType 类型，默认为 {@code application/unknown}
     * @param bytes    字节数组
     * @throws NullPointerException 如果bytes为空，抛出空指针异常
     */
    public TypedByteArray(String mimeType, byte[] bytes) {
        if (mimeType == null) {
            mimeType = "application/unknown";
        }
        if (bytes == null) {
            throw new NullPointerException("bytes");
        }
        this.mimeType = mimeType;
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
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
        return bytes.length;
    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
        outputStream.write(bytes);
    }

    @Override
    public InputStream in() throws IOException {
        return new ByteArrayInputStream(bytes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TypedByteArray that = (TypedByteArray) o;

        if (!Arrays.equals(bytes, that.bytes)) return false;
        if (!mimeType.equals(that.mimeType)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mimeType.hashCode();
        result = 31 * result + Arrays.hashCode(bytes);
        return result;
    }

    @Override
    public String toString() {
        return "TypedByteArray[length=" + length() + "]";
    }
}
