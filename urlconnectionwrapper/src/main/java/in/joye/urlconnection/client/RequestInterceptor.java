package in.joye.urlconnection.client;

/**
 * 在请求执行前插入额外的数据
 *
 * Created by joye on 2017/9/4.
 */

public interface RequestInterceptor {

    void intercept(RequestFacade requestFacade);

    interface RequestFacade {

        /**
         * 向请求中添加头部
         */
        void addHeader(String name, String value);

        /**
         * 添加额外的查询参数
         */
        void addQueryParam(String name, String value);

        /**
         * 添加编码后的查询参数
         */
        void addEncodedQueryParam(String name, String value);
    }
}
