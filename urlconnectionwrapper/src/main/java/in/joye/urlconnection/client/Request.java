package in.joye.urlconnection.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import in.joye.urlconnection.mime.TypedOutput;

/**
 * 网络请求实体
 * <p>
 * Created by joye on 2017/8/25.
 */

public final class Request {
    private final String url;
    private final String method;
    private final List<Header> headers;
    private final TypedOutput body;

    public Request(String url, String method, List<Header> headers, TypedOutput body) {
        if (url == null) {
            throw new NullPointerException("Url must not be null.");
        }
        if (method == null) {
            throw new NullPointerException("Method must not be null.");
        }
        this.url = url;
        this.method = method;
        if (headers == null) {
            this.headers = Collections.emptyList();
        } else {
            this.headers = Collections.unmodifiableList(new ArrayList<Header>(headers));
        }

        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public TypedOutput getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Request{" +
                "url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", headers=" + headers +
                ", body=" + body +
                '}';
    }
}
