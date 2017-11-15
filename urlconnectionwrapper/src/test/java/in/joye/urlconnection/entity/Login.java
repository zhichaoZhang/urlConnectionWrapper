package in.joye.urlconnection.entity;

/**
 * 测试登录实体
 *
 * Created by joye on 2017/11/15.
 */

public class Login {
    private String mobile;
    private String sessionid;
    private int userid;
    private String is_signup;
    private int is_creat_shop;
    private int is_bpgroup;//是否是白牌渠道
    private int is_qfgroup;//是否是直营商户


    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getIs_signup() {
        return is_signup;
    }

    public void setIs_signup(String is_signup) {
        this.is_signup = is_signup;
    }

    public int getIs_creat_shop() {
        return is_creat_shop;
    }

    public void setIs_creat_shop(int is_creat_shop) {
        this.is_creat_shop = is_creat_shop;
    }

    public int getIs_bpgroup() {
        return is_bpgroup;
    }

    public void setIs_bpgroup(int is_bpgroup) {
        this.is_bpgroup = is_bpgroup;
    }

    public int getIs_qfgroup() {
        return is_qfgroup;
    }

    public void setIs_qfgroup(int is_qfgroup) {
        this.is_qfgroup = is_qfgroup;
    }

    @Override
    public String toString() {
        return "Login{" +
                "mobile='" + mobile + '\'' +
                ", sessionid='" + sessionid + '\'' +
                ", userid=" + userid +
                ", is_signup='" + is_signup + '\'' +
                ", is_creat_shop=" + is_creat_shop +
                ", is_bpgroup=" + is_bpgroup +
                ", is_qfgroup=" + is_qfgroup +
                '}';
    }
}
