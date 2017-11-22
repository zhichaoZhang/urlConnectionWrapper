package in.joye.urlconnection.utils;

/**
 * Log实现
 *
 * Created by joye on 2017/11/21.
 */

public interface Log {

    void log(String message);

    Log NONE = new Log() {
        @Override
        public void log(String message) {

        }
    };
}
