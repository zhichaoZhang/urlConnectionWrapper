package in.joye.urlconnection.client;

/**
 * 原始返回值和经过Converter转换后的Response值
 *
 * Created by joye on 2017/8/31.
 */

public class ResponseWrapper<T> {
    final Response response;
    final T responseBody;

    public ResponseWrapper(Response response, T responseBody) {
        this.response = response;
        this.responseBody = responseBody;
    }

    public Response getRawResponse() {
        return response;
    }

    public T getResponseBody() {
        return responseBody;
    }

    public boolean isSuccess() {
        return response.getStatus() == 200;
    }
}
