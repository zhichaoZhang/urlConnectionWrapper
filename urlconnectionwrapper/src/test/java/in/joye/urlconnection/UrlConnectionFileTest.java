package in.joye.urlconnection;

import com.google.gson.reflect.TypeToken;

import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;

import in.joye.urlconnection.base.BaseJunitTest;
import in.joye.urlconnection.client.Call;
import in.joye.urlconnection.client.Callback;
import in.joye.urlconnection.client.Request;
import in.joye.urlconnection.client.RequestBuilder;
import in.joye.urlconnection.client.RequestType;
import in.joye.urlconnection.client.Response;
import in.joye.urlconnection.client.ResponseWrapper;
import in.joye.urlconnection.entity.LogResult;
import in.joye.urlconnection.http.HttpRequestMethod;
import in.joye.urlconnection.mime.TypedFile;

/**
 * 文件上下下载请求类型测试
 * <p>
 * Created by joye on 2017/11/14.
 */

public class UrlConnectionFileTest extends BaseJunitTest {
    private final String BASE_TEST_DIR = "urlconnectionwrapper/src/test/";
    UrlConnectionWrapper urlConnectionWrapper;
    private final String mDownloadFileUrl = "http://near.qfpay.com.cn/op_upload/72/151072616844.txt";

    @Override
    public void setUp() throws Exception {
        urlConnectionWrapper = UrlConnectionWrapper.getInstance();
        urlConnectionWrapper.setIsDebug(true);
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * 测试同步下载文件
     */
    @Test
    public void testDownloadFileSync() throws IOException {
        File file = new File(BASE_TEST_DIR + "test-download.txt");
        RequestBuilder requestBuilder = new RequestBuilder(mDownloadFileUrl, HttpRequestMethod.GET, RequestType.SIMPLE);
        Request request = requestBuilder.build();
        Call call = urlConnectionWrapper.create(request);
        ResponseWrapper responseWrapper = call.execute();
        assertNotNull(responseWrapper);
        assertNotNull(responseWrapper.getRawResponse());
        assertTrue(responseWrapper.isSuccess());
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            inputStream = responseWrapper.getRawResponse().getBody().in();
            fileOutputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int count;
            while ((count = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, count);
            }
            fileOutputStream.flush();
            assertTrue(file.exists());
            file.delete();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }
    }

    /**
     * 测试异步下载文件
     */
    @Test
    public void testDownloadFileAsync() throws IOException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final File file = new File(BASE_TEST_DIR + "test-download.txt");
        RequestBuilder requestBuilder = new RequestBuilder(mDownloadFileUrl, HttpRequestMethod.GET, RequestType.SIMPLE);
        final Request request = requestBuilder.build();
        Call call = urlConnectionWrapper.create(request);
        call.enqueue(new Callback() {
            @Override
            public void success(Object o, Response response) {
                log("当前线程", Thread.currentThread().getName());
                log("请求成功", response);
                assertNotNull(response);
                InputStream inputStream = null;
                FileOutputStream fileOutputStream = null;
                try {
                    inputStream = response.getBody().in();
                    fileOutputStream = new FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    int count;
                    while ((count = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, count);
                    }
                    fileOutputStream.flush();
                    assertTrue(file.exists());
                    file.delete();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        if (fileOutputStream != null) {
                            fileOutputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    countDownLatch.countDown();
                }
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
     * 测试同步上传文件
     */
    @Test
    public void testPostFileSync() {
        File file = new File(BASE_TEST_DIR + "test.txt");
        log("文件路径", file.getAbsolutePath());
        String getUrl = "http://120.26.215.30:8889/api/hjsh/clientstat";
//        String getUrl = "https://o.qfpay.com/clientlog/v1";
        RequestBuilder requestBuilder = new RequestBuilder(getUrl, HttpRequestMethod.POST, RequestType.MULTIPART);
//        requestBuilder.addHeader("Content-Encoding", "gzip");
//        requestBuilder.addHeader("Accept-Encoding", "gzip");
        TypedFile typedFile = new TypedFile("file", file);
        requestBuilder.addMultiPart("log", typedFile);
        Request request = requestBuilder.build();
        log("请求", request);
        try {
            request.getBody().writeTo(getPrintOps());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Call<String> call = urlConnectionWrapper.create(request, new TypeToken<String>() {
        }.getType());
        try {
            ResponseWrapper<String> responseWrapper = call.execute();
            log("响应", responseWrapper.getRawResponse());
            log("响应实体", responseWrapper.getResponseBody());
            assertNotNull(responseWrapper.getResponseBody());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试异步上传文件
     */
    @Test
    public void testPostFileAsync() {
        final CountDownLatch childThreadLatch = new CountDownLatch(1);
        File file = new File(BASE_TEST_DIR + "test.txt");
        log("文件路径", file.getAbsolutePath());
        String getUrl = "http://120.26.215.30:8889/api/hjsh/clientstat";
//        String getUrl = "https://o.qfpay.com/clientlog/v1";
        RequestBuilder requestBuilder = new RequestBuilder(getUrl, HttpRequestMethod.POST, RequestType.MULTIPART);
//        requestBuilder.addHeader("Content-Encoding", "gzip");
//        requestBuilder.addHeader("Accept-Encoding", "gzip");
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
                assertNotNull(logResult);
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

    private OutputStream getPrintOps() {
        return System.out;
    }
}
