package in.joye.urlconnection.client;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import in.joye.urlconnection.converter.Converter;
import in.joye.urlconnection.converter.GsonConverter;
import in.joye.urlconnection.http.HttpRequestMethod;
import in.joye.urlconnection.mime.FormUrlEncodedTypedOutput;
import in.joye.urlconnection.mime.MultipartTypedOutput;
import in.joye.urlconnection.mime.TypedOutput;
import in.joye.urlconnection.mime.TypedString;

/**
 * 请求构建器
 * <p>
 * Created by joye on 2017/8/31.
 */

public class RequestBuilder implements RequestInterceptor.RequestFacade {
    private List<Header> mHeaders;
    @HttpRequestMethod.HttpRequestMethodDef
    private String mMethod;
    private StringBuilder mQueryParam;
    private String mApiUrl;
    private TypedOutput mBody;
    private final FormUrlEncodedTypedOutput mFormBody;
    private final MultipartTypedOutput mMultipartBody;
    private final Converter mConverter;

    /**
     * 构造方法
     *
     * @param apiUrl      请求地址
     * @param method      请求方法
     * @param requestType 请求类型
     */
    public RequestBuilder(String apiUrl, @HttpRequestMethod.HttpRequestMethodDef String method, RequestType requestType) {
        this.mApiUrl = apiUrl;
        this.mMethod = method;
        this.mConverter = new GsonConverter(new Gson());
        switch (requestType) {
            case FORM_URL_ENCODED:
                mFormBody = new FormUrlEncodedTypedOutput();
                mMultipartBody = null;
                mBody = mFormBody;
                break;
            case MULTIPART:
                mFormBody = null;
                mMultipartBody = new MultipartTypedOutput();
                mBody = mMultipartBody;
                break;
            case SIMPLE:
                mFormBody = null;
                mMultipartBody = null;
                //如果是简单类型的请求，不需构造请求体，直接将参数添加到Url后面。
                break;
            default:
                throw new IllegalArgumentException("Unknown request type :" + requestType);
        }
    }

    public Request build() {
        if (mMultipartBody != null && mMultipartBody.getPartSize() == 0) {
            throw new IllegalArgumentException("Multipart requests must contain at least one part.");
        }
        String apiUrl = this.mApiUrl;
        StringBuilder url = new StringBuilder(apiUrl);
        //去除域名末尾的反斜杠
        if (apiUrl.endsWith("/")) {
            url.deleteCharAt(url.length() - 1);
        }

        StringBuilder queryParam = this.mQueryParam;
        if (queryParam != null) {
            url.append(queryParam);
        }
        TypedOutput body = this.mBody;
        List<Header> headers = this.mHeaders;

        return new Request(url.toString(), mMethod, headers, body);
    }

    @Override
    public void addHeader(String name, String value) {
        if (mHeaders == null) {
            mHeaders = new ArrayList<>();
        }
        mHeaders.add(new Header(name, value));
    }

    @Override
    public void addQueryParam(String name, String value) {
        addQueryParam(name, value, false, true);
    }

    @Override
    public void addEncodedQueryParam(String name, String value) {
        addQueryParam(name, value, false, false);
    }

    /**
     * 添加请求参数，value定义为Object类型，支持任何集合、数组、数组类型
     *
     * @param name        参数名
     * @param value       参数值
     * @param encodeName  是否编码参数名
     * @param encodeValue 是否编码参数值
     */
    public void addQueryParam(String name, Object value, boolean encodeName, boolean encodeValue) {
        if (name == null) {
            throw new IllegalArgumentException("Query param name must not be null.");
        }
        if (value == null) {
            throw new IllegalArgumentException("Query param value must not be null.");
        }
        if (value instanceof Iterable) {
            //处理集合类型的参数值
            for (Object iterableValue : (Iterable<?>) value) {
                if (iterableValue != null) {
                    addQueryParam(name, iterableValue.toString(), encodeName, encodeValue);
                }
            }
        } else if (value.getClass().isArray()) {
            //处理数组类型的参数值
            for (int x = 0, arrayLength = Array.getLength(value); x < arrayLength; x++) {
                Object arrayValue = Array.get(value, x);
                if (arrayValue != null) {
                    addQueryParam(name, arrayValue.toString(), encodeName, encodeValue);
                }
            }
        } else {
            //默认按照字符串类型处理
            addQueryParam(name, value.toString(), encodeName, encodeValue);
        }
    }

    //添加String类型的参数
    private void addQueryParam(String name, String value, boolean encodeName, boolean encodeValue) {
        if (name == null) {
            throw new IllegalArgumentException("Query param name must not be null.");
        }
        if (value == null) {
            throw new IllegalArgumentException("Query param \"" + name + "\" value must not be null.");
        }
        StringBuilder queryParams = this.mQueryParam;
        if(queryParams == null) {
            queryParams = mQueryParam = new StringBuilder();
        }
        try {
            if (encodeName) {
                name = URLEncoder.encode(name, "UTF-8");
            }
            if (encodeValue) {
                value = URLEncoder.encode(value, "UTF-8");
            }
            queryParams.append(queryParams.length() > 0 ? '&' : '?');
            queryParams.append(name).append('=').append(value);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 以Map类型添加参数
     *
     * @param map          参数集合
     * @param encodeNames  是否编码参数名
     * @param encodeValues 是否编码参数值
     */
    public void addQueryParamMap(Map<?, ?> map, boolean encodeNames, boolean encodeValues) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object entryKey = entry.getKey();
            Object entryValue = entry.getValue();
            if (entryKey != null && entryValue != null) {
                addQueryParam(entryKey.toString(), entryValue, encodeNames, encodeValues);
            }
        }
    }

    public void addMultiPart(String name, Object value) {
        addMultiPart(name, value, MultipartTypedOutput.DEFAULT_TRANSFER_ENCODING);
    }

    /**
     * 添加块数据
     *
     * @param name             参数名
     * @param value            参数值
     * @param transferEncoding 传输编码 默认为binary
     */
    public void addMultiPart(String name, Object value, String transferEncoding) {
        if (name == null) {
            throw new IllegalArgumentException("Part name must not be null.");
        }

        if (value == null) {
            throw new IllegalArgumentException("Part value must not be null.");
        }

        if (transferEncoding == null) {
            throw new IllegalArgumentException("Part transfer encoding must not be null.");
        }

        if (value instanceof TypedOutput) {
            mMultipartBody.addPart(name, transferEncoding, (TypedOutput) value);
        } else if (value instanceof String) {
            mMultipartBody.addPart(name, transferEncoding, new TypedString((String) value));
        } else {
            mMultipartBody.addPart(name, transferEncoding, mConverter.toBody(value));
        }
    }

    public void addMultiPartMap(Map<?, ?> map) {
        addMultiPartMap(map, MultipartTypedOutput.DEFAULT_TRANSFER_ENCODING);
    }

    public void addMultiPartMap(Map<?, ?> map, String transferEncoding) {
        if (map == null) {
            throw new IllegalArgumentException("Part map must not be null.");
        }
        if (transferEncoding == null) {
            throw new IllegalArgumentException("Part transfer encoding must not be null.");
        }
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String entryKey = entry.getKey().toString();
            Object entryValue = entry.getValue();
            if (entryKey != null && entryValue != null) {
                addMultiPart(entryKey, entryValue, transferEncoding);
            }
        }
    }

    public void addFormField(String name, String value) {
        addFormField(name, false, value, false);
    }

    /**
     * 添加一个表单请求参数域
     *
     * @param name        参数名
     * @param encodeName  是否编码
     * @param fieldValue  参数值
     * @param encodeValue 是否编码
     */
    public void addFormField(String name, boolean encodeName, Object fieldValue, boolean encodeValue) {
        if (name == null) {
            throw new IllegalArgumentException("Form field name must not be null.");
        }
        if (fieldValue == null) {
            throw new IllegalArgumentException("Form field value must not be null.");
        }
        //区分参数值类型
        if (fieldValue instanceof Iterable) {
            for (Object iterableValue : (Iterable<?>) fieldValue) {
                if (iterableValue != null) {
                    mFormBody.addField(name, encodeName, iterableValue.toString(), encodeValue);
                }
            }
        } else if (fieldValue.getClass().isArray()) {
            for (int x = 0, arrayLength = Array.getLength(fieldValue); x < arrayLength; x++) {
                Object arrayValue = Array.get(fieldValue, x);
                if (arrayValue != null) {
                    mFormBody.addField(name, encodeName, arrayValue.toString(), encodeValue);
                }
            }
        } else {
            mFormBody.addField(name, encodeName, fieldValue.toString(), encodeValue);
        }
    }

    /**
     * 以Map类型添加参数域
     *
     * @param map         参数域
     * @param encodeName  是否编码参数名
     * @param encodeValue 是否编码参数值
     */
    public void addFormFieldMap(Map<?, ?> map, boolean encodeName, boolean encodeValue) {
        if (map == null) {
            throw new IllegalArgumentException("Form map field must not be null.");
        }

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object entryKey = entry.getKey();
            Object entryValue = entry.getValue();
            if (entryKey != null && entryValue != null) {
                addFormField(entryKey.toString(), encodeName, entryValue.toString(), encodeValue);
            }
        }
    }
}
