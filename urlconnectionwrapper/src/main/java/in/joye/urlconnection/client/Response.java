package in.joye.urlconnection.client;

import java.util.List;

import in.joye.urlconnection.mime.TypedInput;

/**
 * Http响应实体
 *
 * Created by joye on 2017/8/28.
 */

public final class Response {

    private final String url;
    private final int status;
    private final String reason;
    private final List<Header> headers;
    private final TypedInput body;

    public Response(String url, int status, String reason, List<Header> headers, TypedInput body) {
        this.url = url;
        this.status = status;
        this.reason = reason;
        this.headers = headers;
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    public int getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public TypedInput getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Response{" +
                "url='" + url + '\'' +
                ", status=" + status +
                ", reason='" + reason + '\'' +
                ", headers=" + headers +
                ", body=" + body +
                '}';
    }
}
