# urlConnectionWrapper
Simple UrlConnection wrapper for Android.

# Feature
1. Base on System‘s http library - 'UrlConnection'
2. Java Bean auto transfer，and can special custom 'Converter'
3. Auto use cookie from request
4. Support GET、POST、PUT、DELETE、HEAD
5. Support file upload and download
6. Just like simple Retrofit

# Getting started

## Download

add dependency by adding the following lines to your *app/build.gradle*

```
compile 'in.joye:urlconnectionwrapper:0.0.4'

```

## GET request

```

UrlConnectionWrapper urlConnectionWrapper = UrlConnectionWrapper.getInstance();
RequestBuilder requestBuilder = new RequestBuilder("server-url", HttpRequestMethod.GET, RequestType.SIMPLE);
//add get params
requestBuilder.addQueryParam("param", "value");
Request request = requestBuilder.build();

//you can special Java Bean type
 Call<POJO> call = urlConnectionWrapper.create(request, new TypeToken<POJO>() {}.getType());

//Sync request
Response<POJO> response = call.execute();

//Async request
call.enqueue(new Callback<POJO>() {
            @Override
            public void success(POJO pojo, Response response) {

            }

            @Override
            public void failure(int statusCode, String error) {

            }
        });

```

## POST request

```

UrlConnectionWrapper urlConnectionWrapper = UrlConnectionWrapper.getInstance();
RequestBuilder requestBuilder = new RequestBuilder("server-url", HttpRequestMethod.POST, RequestType.FORM_URL_ENCODED);
requestBuilder.addFormField("param", "value");

//the request process is as the same as GET

```

## File download and upload

### file download

```

RequestBuilder requestBuilder = new RequestBuilder("file-url", HttpRequestMethod.GET, RequestType.SIMPLE);
Request request = requestBuilder.build();
Call call = urlConnectionWrapper.create(request);

ResponseWrapper responseWrapper = call.execute();

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

```

### file upload

```

File file = new File("");
RequestBuilder requestBuilder = new RequestBuilder(getUrl, HttpRequestMethod.POST, RequestType.MULTIPART);
        TypedFile typedFile = new TypedFile("file", file);
        requestBuilder.addMultiPart("log", typedFile);
        Request request = requestBuilder.build();
        Call<String> call = urlConnectionWrapper.create(request, new TypeToken<String>() {}.getType());
        try {
            ResponseWrapper<String> responseWrapper = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

```



