package in.joye.urlconnection.converter;


/**
 * 转换异常
 *
 * Created by joye on 2017/8/31.
 */

public class ConversionException extends Exception {

    public ConversionException(String message) {
        super(message);
    }

    public ConversionException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ConversionException(Throwable throwable) {
        super(throwable);
    }

}
