package in.joye.urlconnection;

import com.google.gson.reflect.TypeToken;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import in.joye.urlconnection.client.Call;
import in.joye.urlconnection.client.Callback;
import in.joye.urlconnection.client.Header;
import in.joye.urlconnection.client.Request;
import in.joye.urlconnection.client.RequestBuilder;
import in.joye.urlconnection.client.RequestType;
import in.joye.urlconnection.client.Response;
import in.joye.urlconnection.client.ResponseWrapper;
import in.joye.urlconnection.http.HttpRequestMethod;
import in.joye.urlconnection.mime.TypedFile;

/**
 * UrlConnectionWrapper测试
 * Created by joye on 2017/9/18.
 */

public class UrlConnectionTest extends TestCase {
    UrlConnectionWrapper urlConnectionWrapper;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        urlConnectionWrapper = UrlConnectionWrapper.getInstance();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        urlConnectionWrapper = null;
    }

    @Test
    public void testGetSync() {
        String getUrl = "https://o.qfpay.com/mchnt/user/qrcode";
        RequestBuilder requestBuilder = new RequestBuilder(getUrl, HttpRequestMethod.GET, RequestType.SIMPLE);
//        requestBuilder.addHeader("Cookie", "sessionid=b36c5280-8f93-4976-9359-441609916b97;");
        requestBuilder.addQueryParam("opuid", "0001");
        Request request = requestBuilder.build();
        log("请求", request);
        Call<ResponseContainer> call = urlConnectionWrapper.create(request, new TypeToken<ResponseContainer>() {
        }.getType());
        try {
            ResponseWrapper<ResponseContainer> responseWrapper = call.execute();
            log("响应", responseWrapper.getRawResponse());
            log("响应实体", responseWrapper.getResponseBody());
            assertEquals(200, responseWrapper.getRawResponse().getStatus());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test404GetSync() {
        String getUrl = "https://o.qfpay.com/mchnt/user/qrcode_error";
        RequestBuilder requestBuilder = new RequestBuilder(getUrl, HttpRequestMethod.GET, RequestType.SIMPLE);
        requestBuilder.addHeader("Cookie", "sessionid=b36c5280-8f93-4976-9359-441609916b97;");
        requestBuilder.addQueryParam("opuid", "0001");
        Request request = requestBuilder.build();
        log("请求", request);
        Call<ResponseContainer> call = urlConnectionWrapper.create(request, new TypeToken<ResponseContainer>() {
        }.getType());
        try {
            ResponseWrapper<ResponseContainer> responseWrapper = call.execute();
            log("响应", responseWrapper.getRawResponse());
            if (responseWrapper.isSuccess()) {
                log("响应实体", responseWrapper.getResponseBody());
            }
            assertEquals(404, responseWrapper.getRawResponse().getStatus());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetAsync() {
        final CountDownLatch childThreadLatch = new CountDownLatch(1);
        String getUrl = "https://o.qfpay.com/mchnt/user/qrcode";
        RequestBuilder requestBuilder = new RequestBuilder(getUrl, HttpRequestMethod.GET, RequestType.SIMPLE);
        requestBuilder.addHeader("Cookie", "sessionid=b36c5280-8f93-4976-9359-441609916b97;");
        requestBuilder.addQueryParam("opuid", "0001");
        Request request = requestBuilder.build();
        log("请求", request);
        Call<ResponseContainer> call = urlConnectionWrapper.create(request, new TypeToken<ResponseContainer>() {
        }.getType());
        call.enqueue(new Callback<ResponseContainer>() {
            @Override
            public void success(ResponseContainer responseContainer, Response response) {
                log("响应", response);
                log("响应实体", responseContainer);
                childThreadLatch.countDown();
            }

            @Override
            public void failure(int statusCode, String error) {
                log("请求失败", error);
                childThreadLatch.countDown();
            }
        });
        try {
            log("childThreadLatch", "等待子线程。。。");
            childThreadLatch.await();
            log("childThreadLatch", "子线程完成，主线程结束。。。");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPost() {
        String getUrl = "https://o.qfpay.com/mchnt/user/login";
        RequestBuilder requestBuilder = new RequestBuilder(getUrl, HttpRequestMethod.POST, RequestType.FORM_URL_ENCODED);
        requestBuilder.addFormField("username", "15330059740");
        requestBuilder.addFormField("password", "059740");
        requestBuilder.addFormField("udid", "357541051316166");
        Request request = requestBuilder.build();
        log("请求", request);
        try {
            request.getBody().writeTo(getPrintOps());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Call<String> call = urlConnectionWrapper.create(request, null);
        try {
            ResponseWrapper<String> responseWrapper = call.execute();
            log("响应", responseWrapper.getRawResponse());
            log("响应实体", responseWrapper.getRawResponse().getBody().in());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAutoUseCookie() {
        String getUrl = "https://o.qfpay.com/mchnt/user/login";
        RequestBuilder requestBuilder = new RequestBuilder(getUrl, HttpRequestMethod.POST, RequestType.FORM_URL_ENCODED);
        requestBuilder.addFormField("username", "15330059740");
        requestBuilder.addFormField("password", "059740");
        requestBuilder.addFormField("udid", "357541051316166");
        Request request = requestBuilder.build();
        log("请求", request);
        try {
            request.getBody().writeTo(getPrintOps());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Call<String> call = urlConnectionWrapper.create(request, null);
        try {
            ResponseWrapper<String> responseWrapper = call.execute();
            log("响应", responseWrapper.getRawResponse());
            log("响应实体", responseWrapper.getRawResponse().getBody().in());
        } catch (IOException e) {
            e.printStackTrace();
        }
        CookieHandler cookieHandler = CookieHandler.getDefault();
        try {
            Map<String, List<String>> requestHeaders = new HashMap<>();
            requestHeaders = cookieHandler.get(new URI("https://o.qfpay.com"), requestHeaders);
            Set<Map.Entry<String, List<String>>> entries = requestHeaders.entrySet();
            for(Map.Entry<String, List<String>> entry : entries) {
                String key = entry.getKey();
                List<String> value = entry.getValue();
                log("key", key);
                log("value", value);
            }
            assertTrue(requestHeaders.size() > 0);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPostFileSync() {
        File file = new File("test.txt");
        log("文件路径", file.getAbsolutePath());
//        String getUrl = "http://120.26.215.30:8889/api/hjsh/clientstat";
        String getUrl = "https://o.qfpay.com/clientlog/v1";
        RequestBuilder requestBuilder = new RequestBuilder(getUrl, HttpRequestMethod.POST, RequestType.MULTIPART);
        requestBuilder.addHeader("Content-Encoding", "gzip");
        requestBuilder.addHeader("Accept-Encoding", "gzip");
        TypedFile typedFile = new TypedFile("file", file);
        requestBuilder.addMultiPart("log", typedFile);
        Request request = requestBuilder.build();
        log("请求", request);
        try {
            request.getBody().writeTo(getPrintOps());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Call<String> call = urlConnectionWrapper.create(request, null);
        try {
            ResponseWrapper<String> responseWrapper = call.execute();
            log("响应", responseWrapper.getRawResponse());
            log("响应实体", responseWrapper.getRawResponse().getBody().in());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPostFileAsync() {
        final CountDownLatch childThreadLatch = new CountDownLatch(1);
        File file = new File("test.txt");
        log("文件路径", file.getAbsolutePath());
//        String getUrl = "http://120.26.215.30:8889/api/hjsh/clientstat";
        String getUrl = "https://o.qfpay.com/clientlog/v1";
        RequestBuilder requestBuilder = new RequestBuilder(getUrl, HttpRequestMethod.POST, RequestType.MULTIPART);
        requestBuilder.addHeader("Content-Encoding", "gzip");
        requestBuilder.addHeader("Accept-Encoding", "gzip");
        TypedFile typedFile = new TypedFile("file", file);
        requestBuilder.addMultiPart("log", typedFile);
        Request request = requestBuilder.build();
        log("请求", request);
        try {
            request.getBody().writeTo(getPrintOps());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Call<LogResult> call = urlConnectionWrapper.create(request, new TypeToken<LogResult>() {
        }.getType());
        call.enqueue(new Callback<LogResult>() {
            @Override
            public void success(LogResult logResult, Response response) {
                log("响应", response);
                log("响应实体", logResult);
                childThreadLatch.countDown();
            }

            @Override
            public void failure(int statusCode, String error) {
                log("失败", error);
                childThreadLatch.countDown();
            }
        });

        try {
            childThreadLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testConnectionRepeatUse() {
        //How to test
    }

    @Test
    public void testAddCookie() {
        String url = "https://www.baidu.com";
        String cookieName = "sessionid";
        String cookieValue = "123123123123";
        urlConnectionWrapper.addCookie(url, cookieName, cookieValue);
        CookieManager cookieManager = (CookieManager) CookieHandler.getDefault();

        CookieStore cookieStore = cookieManager.getCookieStore();
        List<HttpCookie> httpCookies = cookieStore.getCookies();
        for(HttpCookie httpCookie : httpCookies) {
            log("key", httpCookie.getName());
            log("value", httpCookie.getValue());
        }
        assertTrue(httpCookies.size() > 0);
        HttpCookie httpCookie = httpCookies.get(httpCookies.size() - 1);
        assertTrue(httpCookie.getName().contentEquals(cookieName));
        assertTrue(httpCookie.getValue().contentEquals(cookieValue));
    }

    @Test
    public void testAddCookieManual() {
        String url = "https://o.qfpay.com/mchnt/user/login";
        String cookieName = "sessionid";
        String cookieValue = "5b40b567-8470-4f0c-8e5f-143f04a9351d";
        urlConnectionWrapper.addCookie(url, cookieName, cookieValue);
        String getUrl = "https://o.qfpay.com/mchnt/user/qrcode";

        RequestBuilder requestBuilder = new RequestBuilder(getUrl, HttpRequestMethod.GET, RequestType.SIMPLE);
        requestBuilder.addQueryParam("opuid", "0001");
        Request request = requestBuilder.build();
        log("请求", request);
        Call<ResponseContainer> call = urlConnectionWrapper.create(request, new TypeToken<ResponseContainer>() {
        }.getType());
        try {
            ResponseWrapper<ResponseContainer> responseWrapper = call.execute();
            log("响应", responseWrapper.getRawResponse());
            log("响应实体", responseWrapper.getResponseBody());
            assertEquals(200, responseWrapper.getRawResponse().getStatus());
            assertEquals("0000", responseWrapper.getResponseBody().respcd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void log(String desc, Object o) {
        System.out.println(desc + " : " + o.toString());
    }

    private void log(String desc, InputStream inputStream) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line = null;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                log(desc, line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void log(String desc, OutputStream outputStream) {
        PrintStream printStream = new PrintStream(outputStream);
        printStream.println();
    }

    private OutputStream getPrintOps() {
        return System.out;
    }

    private class LogResult {
        private String fail;
        private String respcd;
        private String resperr;
        private String respmsg;
        private String succ;

        @Override
        public String toString() {
            return "LogResult{" +
                    "fail='" + fail + '\'' +
                    ", respcd='" + respcd + '\'' +
                    ", resperr='" + resperr + '\'' +
                    ", respmsg='" + respmsg + '\'' +
                    ", succ='" + succ + '\'' +
                    '}';
        }
    }

    public class ResponseContainer {
        public String respcd;
        public String respmsg;
        public String resperr;

        @Override
        public String toString() {
            return "ResponseContainer{" +
                    "respcd='" + respcd + '\'' +
                    ", respmsg='" + respmsg + '\'' +
                    ", resperr='" + resperr + '\'' +
                    '}';
        }
    }

    private Header getCookieHeader(List<Header> headers) {
        if (headers != null) {
            for (Header header : headers) {
                if (header.getName() != null && header.getName().equalsIgnoreCase("Set-Cookie")) {
                    return header;
                }
            }
        }
        return null;
    }
}
