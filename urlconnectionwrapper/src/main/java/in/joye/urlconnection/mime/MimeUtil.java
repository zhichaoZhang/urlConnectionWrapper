package in.joye.urlconnection.mime;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Mime相关工具方法
 * <p>
 * Created by joye on 2017/8/31.
 */

public final class MimeUtil {

    private static final Pattern CHARSET = Pattern.compile("\\Wcharset=([^\\s;]+)", Pattern.CASE_INSENSITIVE);

    /**
     * 从Http响应头Content-Type字段解析MIME类型
     *
     * @param mimeType       Content-Type字段值
     * @param defaultCharset 默认编码格式
     * @return 返回Content-Type字段中的编码格式
     */
    public static String parseCharset(String mimeType, String defaultCharset) {
        Matcher matcher = CHARSET.matcher(mimeType);
        if(matcher.find()) {
            return matcher.group(1).replaceAll("[\"\\\\]", "");
        }
        return defaultCharset;
    }
}
