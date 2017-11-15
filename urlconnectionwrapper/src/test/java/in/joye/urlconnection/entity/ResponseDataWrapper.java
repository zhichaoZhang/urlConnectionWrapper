package in.joye.urlconnection.entity;


/**
 * 服务器响应的数据包装
 * <p>
 * Created by LiFZhe on 3/11/15.
 */
public class ResponseDataWrapper<T> extends ResponseContainer {
    public T data;

    @Override
    public String toString() {
        return "respcode----->" + respcd + "   " + "respmsg----->" + respmsg + "  resperr" + resperr;
    }

    public boolean isSuccess() {
        return "0000".equalsIgnoreCase(respcd);
    }
}
