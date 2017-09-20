package in.joye.urlconnection.http;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Http请求方法类型
 * <p>
 * Created by joye on 2017/9/16.
 */

public class HttpRequestMethod {
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    public static final String HEAD = "HEAD";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({GET, POST, PUT, DELETE, HEAD})
    public @interface HttpRequestMethodDef {

    }
}
