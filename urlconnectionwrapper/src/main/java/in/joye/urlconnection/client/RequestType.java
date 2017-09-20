package in.joye.urlconnection.client;

/**
 * 请求类型
 *
 * Created by joye on 2017/9/15.
 */

public enum RequestType {
    /**
     * 简单请求，未指定额外请求内容，例如GET请求
     */
    SIMPLE,
    /**
     * 多类型请求体，例如POST请求
     */
    MULTIPART,
    /**
     * 经过URL编码的表单请求体
     */
    FORM_URL_ENCODED
}
