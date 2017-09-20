package in.joye.urlconnection.client;

/**
 * 请求配置外观接口
 * <p>
 * Created by joye on 2017/9/15.
 */

public interface RequestFacade {

    /**
     * 向请求中添加一个请求头属性。如果属性值相同，不会替换已有的header
     *
     * @param name  属性名称
     * @param value 属性值
     */
    void addHeader(String name, String value);

    /**
     * 添加一个路径参数替换
     *
     * @param name  属性名称
     * @param value 属性值
     */
    void addPathParam(String name, String value);

    /**
     * 添加一个已经编码过的路径参数
     *
     * @param name  属性名称
     * @param value 属性值
     */
    void addEncodedPathParam(String name, String value);

    /**
     * 添加额外的查询参数。不会替换已有的查询参数
     *
     * @param name  属性名称
     * @param value 属性值
     */
    void addQueryParam(String name, String value);

    /**
     * 添加一个已经编码过的查询参数，不会替换已有参数
     *
     * @param name  属性名称
     * @param value 属性值
     */
    void addEncodedQueryParam(String name, String value);
}
