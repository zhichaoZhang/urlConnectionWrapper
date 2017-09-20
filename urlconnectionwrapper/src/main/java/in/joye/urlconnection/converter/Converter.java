package in.joye.urlconnection.converter;


import java.lang.reflect.Type;

import in.joye.urlconnection.mime.TypedInput;
import in.joye.urlconnection.mime.TypedOutput;

/**
 * Java对象与Http数据流转换器
 * <p>
 * Created by joye on 2017/8/31.
 */

public interface Converter {

    /**
     * Http数据转成Java对象
     *
     * @param body HTTP 响应实体
     * @param type 目标对象类型
     * @return type类型的实例，调用者需要做类型转换
     * @throws ConversionException 转换异常
     */
    Object fromBody(TypedInput body, Type type) throws ConversionException;

    /**
     * 将Java对象写入到Http数据流
     *
     * @param object 需要转换的bject实例
     * @return 将object转换成的字节流
     */
    TypedOutput toBody(Object object);
}
