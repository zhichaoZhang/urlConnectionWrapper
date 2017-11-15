package in.joye.urlconnection.entity;

/**
 * 测试日志上传实体类
 *
 * Created by joye on 2017/11/15.
 */

public class LogResult {

    private String fail;
    private String respcd;
    private String resperr;
    private String respmsg;
    private String succ;

    @Override
    public String toString() {
        return "LogResult{" +
                "fail='" + fail + '\'' +
                ", respcd='" + respcd + '\'' +
                ", resperr='" + resperr + '\'' +
                ", respmsg='" + respmsg + '\'' +
                ", succ='" + succ + '\'' +
                '}';
    }
}
