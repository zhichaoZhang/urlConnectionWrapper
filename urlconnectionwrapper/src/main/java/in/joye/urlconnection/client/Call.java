package in.joye.urlconnection.client;

import java.io.IOException;

/**
 * Call代表了一次向web服务器发送请求并返回结果的过程
 *
 * Call支持同步和异步执行两种方式，并可以随时调用取消方法。
 *
 * Created by joye on 2017/8/28.
 */

public interface Call<T> extends Cloneable {

    ResponseWrapper<T> execute() throws IOException;

    void enqueue(Callback<T> callback);

    boolean isExecuted();

    void cancel();

    boolean isCanceled();

    Call<T> clone();

    Request request();
}
