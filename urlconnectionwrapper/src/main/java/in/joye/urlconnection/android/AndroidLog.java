package in.joye.urlconnection.android;

import in.joye.urlconnection.utils.Log;

/**
 * Android平台Log实现
 *
 * Created by joye on 2017/11/21.
 */

public class AndroidLog implements Log {
    private static final int LOG_CHUNK_SIZE = 4000;

    private String tag;

    public AndroidLog() {
    }

    public AndroidLog(String tag) {
        this.tag = tag;
    }

    @Override
    public void log(String message) {
        for (int i = 0, len = message.length(); i < len; i += LOG_CHUNK_SIZE) {
            int end = Math.min(len, i + LOG_CHUNK_SIZE);
            logChunk(message.substring(i, end));
        }
    }

    public void logChunk(String chunk) {
        android.util.Log.d(getTag(), chunk);
    }

    public String getTag() {
        return tag != null ? tag : "UrlConnectionWrapper";
    }
}
