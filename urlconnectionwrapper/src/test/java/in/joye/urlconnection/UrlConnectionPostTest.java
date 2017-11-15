package in.joye.urlconnection;

import com.google.gson.reflect.TypeToken;

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
import in.joye.urlconnection.entity.Login;
import in.joye.urlconnection.entity.ResponseDataWrapper;
import in.joye.urlconnection.http.HttpRequestMethod;

/**
 * Post请求类型测试
 * <p>
 * Created by joye on 2017/11/14.
 */

public class UrlConnectionPostTest extends BaseJunitTest {
    UrlConnectionWrapper urlConnectionWrapper;
    Request request = null;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        urlConnectionWrapper = UrlConnectionWrapper.getInstance();
        RequestBuilder requestBuilder = new RequestBuilder("https://o.qfpay.com/mchnt/user/login", HttpRequestMethod.POST, RequestType.FORM_URL_ENCODED);
        requestBuilder.addFormField("username", "15330059740");
        requestBuilder.addFormField("password", "059740");
        requestBuilder.addFormField("udid", "357541051316166");
        request = requestBuilder.build();
    }

    /**
     * 测试实体类型同步返回
     */
    @Test
    public void testObjectPostSync() throws IOException {
        Call<ResponseDataWrapper<Login>> call = urlConnectionWrapper.create(request, new TypeToken<ResponseDataWrapper<Login>>(){}.getType());
        ResponseWrapper<ResponseDataWrapper<Login>> responseWrapper = call.execute();
        log("原始返回值", responseWrapper.getRawResponse());
        log("请求成功:", responseWrapper.getResponseBody().data);
        assertNotNull(responseWrapper.getResponseBody().data);
    }

    /**
     * 测试实体类型异步返回
     */
    @Test
    public void testObjectPostAsync() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        Call<ResponseDataWrapper<Login>> call = urlConnectionWrapper.create(request, new TypeToken<ResponseDataWrapper<Login>>(){}.getType());
        call.enqueue(new Callback<ResponseDataWrapper<Login>>() {
            @Override
            public void success(ResponseDataWrapper<Login> login, Response response) {
                log("请求成功: ", login.data);
                assertNotNull(login.data);
                countDownLatch.countDown();
            }

            @Override
            public void failure(int statusCode, String error) {
                log("请求失败: ", error);

                countDownLatch.countDown();
            }
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testStringPostSync() throws IOException {
        Call<String> call = urlConnectionWrapper.create(request, new TypeToken<String>(){}.getType());
        ResponseWrapper<String> responseWrapper = call.execute();
        log("返回值: ", responseWrapper.getResponseBody());
        assertNotNull(responseWrapper.getResponseBody());
    }

    @Test
    public void testStringPostAsync() throws IOException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        Call<String> call = urlConnectionWrapper.create(request, new TypeToken<String>(){}.getType());
        call.enqueue(new Callback<String>() {
            @Override
            public void success(String login, Response response) {
                log("请求成功: ", login);
                assertNotNull(login);
                countDownLatch.countDown();
            }

            @Override
            public void failure(int statusCode, String error) {
                log("请求失败: ", error);

                countDownLatch.countDown();
            }
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }
}
