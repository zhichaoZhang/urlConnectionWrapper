package in.joye.urlconnection.mime;

import java.io.IOException;
import java.io.InputStream;

/**
 * 指定mime类型的二进制响应实体数据
 * <p>
 * Created by joye on 2017/8/28.
 */

public interface TypedInput {

    String mimeType();

    long length();

    /**
     * 以流的形式读取响应实体。
     * 除非特殊说明，这个方法只能被调用一次，有调用者负责关闭这个流
     *
     * @return InputStream
     * @throws IOException
     */
    InputStream in() throws IOException;

}
