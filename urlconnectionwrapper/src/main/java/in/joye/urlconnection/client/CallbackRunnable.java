package in.joye.urlconnection.client;

import java.io.IOException;
import java.util.concurrent.Executor;

/**
 * 后台执行Http请求的线程，请求返回值是一个数据对象或者一个异常，在回调线程中调用结果。
 * <p>
 * Created by joye on 2017/9/18.
 */

public abstract class CallbackRunnable<T> implements Runnable {
    private final Callback<T> callback;
    private final Executor callbackExecutor;

    public CallbackRunnable(Callback<T> callback, Executor callbackExecutor) {
        this.callback = callback;
        this.callbackExecutor = callbackExecutor;
    }

    @Override
    public void run() {
        try {
            final ResponseWrapper wrapper = obtainResponse();
            callbackExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    callback.success((T) wrapper.responseBody, wrapper.response);
                }
            });
        } catch (final Exception e) {
            e.printStackTrace();
            callbackExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    callback.failure(-1, e.getMessage());
                }
            });
        }
    }

    public abstract ResponseWrapper obtainResponse() throws IOException;
}
