package in.joye.urlconnection.client;

/**
 * 网络请求结果回调
 *
 * Created by joye on 2017/8/28.
 */
public interface Callback<T> {

    void success(T t, Response response);

    void failure(int statusCode, String error);
}
