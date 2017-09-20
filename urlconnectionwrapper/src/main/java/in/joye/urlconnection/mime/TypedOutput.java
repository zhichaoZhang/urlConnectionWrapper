package in.joye.urlconnection.mime;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 指定mime类型的二进制请求实体
 * <p>
 * Created by joye on 2017/8/28.
 */

public interface TypedOutput {

    /**
     * 文件名称
     * <p>
     * 在multipart请求中使用，可能为空
     *
     * @return String
     */
    String fileName();

    /**
     * mime类型
     *
     * @return String
     */
    String mimeType();

    /**
     * 请求体的长度，未知的话返回-1
     *
     * @return long
     */
    long length();

    /**
     * 向指定的输出流中写入二进制数据
     *
     * @param outputStream 输出流
     * @throws IOException 读写异常
     */
    void writeTo(OutputStream outputStream) throws IOException;
}
