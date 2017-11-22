package in.joye.urlconnection;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executor;

import in.joye.urlconnection.client.Call;
import in.joye.urlconnection.client.Request;
import in.joye.urlconnection.client.UrlConnectionCall;
import in.joye.urlconnection.converter.Converter;
import in.joye.urlconnection.converter.GsonConverter;
import in.joye.urlconnection.utils.Log;

/**
 * 基于UrlConnection的网络请求封装
 * <p>
 * Created by joye on 2017/8/25.
 */

public class UrlConnectionWrapper {
    private boolean isDebug = false;
    private static UrlConnectionWrapper mInstance;
    private Executor mHttpExecutor;
    private Executor mCallbackExecutor;
    private Converter mConverter;
    private Log log;

    private UrlConnectionWrapper() {
        //默认接受服务端返回的Set-Cookie，后续请求会自动添加Cookie到请求头中。
        CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        CookieHandler.setDefault(cookieManager);
        setLog(Platform.get().defaultLog());
    }

    public static UrlConnectionWrapper getInstance() {
        if (mInstance == null) {
            synchronized (UrlConnectionWrapper.class) {
                if (mInstance == null) {
                    mInstance = new UrlConnectionWrapper();
                }
            }
        }
        return mInstance;
    }

    public void setIsDebug(boolean isDebug) {
        this.isDebug = isDebug;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public <T> Call<T> create(Request request) {
        return create(request, null);
    }

    public <T> Call<T> create(Request request, Type bodyType) {
        return new UrlConnectionCall<>(this, request, bodyType);
    }

    public void setHttpExecutor(Executor httpExecutor) {
        this.mHttpExecutor = httpExecutor;
    }

    public void setCallbackExecutor(Executor executor) {
        this.mCallbackExecutor = executor;
    }

    public void setConverter(Converter converter) {
        this.mConverter = converter;
    }

    public Executor getHttpExecutor() {
        if (mHttpExecutor == null) {
            mHttpExecutor = Platform.get().defaultHttpExecutor();
        }
        return mHttpExecutor;
    }

    public Executor getCallbackExecutor() {
        if (mCallbackExecutor == null) {
            mCallbackExecutor = Platform.get().defaultCallbackExecutor();
        }
        return mCallbackExecutor;
    }

    public Converter getConverter() {
        if (mConverter == null) {
            mConverter = new GsonConverter(new Gson());
        }
        return mConverter;
    }

    public Log getLog() {
        return log;
    }

    public void setLog(Log log) {
        if (log != null) {
            this.log = log;
        }
    }

    /**
     * 添加Cookie
     *
     * @param url   域名，如https://www.android.com
     * @param name  Cookie名
     * @param value Cookie值
     */
    public void addCookie(String url, String name, String value) {
        if (url == null) {
            throw new IllegalArgumentException("url must not be null.");
        }
        if (name == null) {
            throw new IllegalArgumentException("name must not be null.");
        }
        if (value == null) {
            throw new IllegalArgumentException("value must not be null.");
        }
        HttpCookie cookie = new HttpCookie(name, value);
        cookie.setDomain(url);
        cookie.setPath("/");
        cookie.setVersion(0);
        CookieManager cookieManager = (CookieManager) CookieHandler.getDefault();
        try {
            cookieManager.getCookieStore().add(new URI(url), cookie);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
