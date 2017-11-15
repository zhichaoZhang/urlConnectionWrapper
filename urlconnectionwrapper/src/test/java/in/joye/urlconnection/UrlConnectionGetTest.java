package in.joye.urlconnection;

import com.google.gson.reflect.TypeToken;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import in.joye.urlconnection.base.BaseJunitTest;
import in.joye.urlconnection.client.Call;
import in.joye.urlconnection.client.Callback;
import in.joye.urlconnection.client.Request;
import in.joye.urlconnection.client.RequestBuilder;
import in.joye.urlconnection.client.RequestType;
import in.joye.urlconnection.client.Response;
import in.joye.urlconnection.client.ResponseWrapper;
import in.joye.urlconnection.entity.Patch;
import in.joye.urlconnection.http.HttpRequestMethod;

/**
 * Get请求类型测试
 * <p>
 * Created by joye on 2017/11/14.
 */

public class UrlConnectionGetTest extends BaseJunitTest {

    UrlConnectionWrapper urlConnectionWrapper;
    Request request = null;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        urlConnectionWrapper = UrlConnectionWrapper.getInstance();
        urlConnectionWrapper.setIsDebug(true);
        RequestBuilder requestBuilder = new RequestBuilder("http://120.26.215.30:8889/client/v1/patch", HttpRequestMethod.GET, RequestType.SIMPLE);
        requestBuilder.addQueryParam("app_key", "gQq3hVbnedmZK2US");
        requestBuilder.addQueryParam("app_version_code", "4.8.4");
        requestBuilder.addQueryParam("client_patch_version_num", "0");
        requestBuilder.addQueryParam("params", "{'user_id':'123'}");
        request = requestBuilder.build();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        urlConnectionWrapper = null;
    }

    /**
     * 同步请求返回String类型
     */
    @Test
    public void testStringTypeSync() throws IOException {
        Call<String> call = urlConnectionWrapper.create(request, new TypeToken<String>() {
        }.getType());
        ResponseWrapper<String> responseWrapper = call.execute();
        Response rawResponse = responseWrapper.getRawResponse();
        log("rawResponse : ", rawResponse);
        String responseBody = responseWrapper.getResponseBody();
        log("responseBody : ", responseBody);
        assertNotNull(responseBody);
    }

    /**
     * 异步请求返回String类型
     */
    @Test
    public void testStringAsync() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        Call<String> call = urlConnectionWrapper.create(request, new TypeToken<String>() {
        }.getType());
        call.enqueue(new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                log("返回值", s);
                assertNotNull(s);
                countDownLatch.countDown();
            }

            @Override
            public void failure(int statusCode, String error) {
                log("请求失败", error);
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 同步请求返回对象类型
     */
    @Test
    public void testObjectSync() throws IOException {
        Call<Patch> call = urlConnectionWrapper.create(request, new TypeToken<Patch>() {
        }.getType());
        ResponseWrapper<Patch> responseWrapper = call.execute();
        Patch patch = responseWrapper.getResponseBody();
        log("返回值：", patch);
        assertNotNull(patch);
    }

    /**
     * 异步请求返回对象类型
     */
    @Test
    public void testObjectAsync() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        Call<Patch> call = urlConnectionWrapper.create(request, new TypeToken<Patch>() {
        }.getType());
        call.enqueue(new Callback<Patch>() {
            @Override
            public void success(Patch patch, Response response) {
                log("返回值：", patch);
                assertNotNull(patch);
                countDownLatch.countDown();
            }

            @Override
            public void failure(int statusCode, String error) {
                log("请求失败", error);
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
