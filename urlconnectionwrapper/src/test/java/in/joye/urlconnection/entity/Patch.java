package in.joye.urlconnection.entity;

/**
 * 测试补丁实体
 *
 * Created by joye on 2017/11/15.
 */

public class Patch {

    private String patch_link;

    private int patch_version;

    private int state;

    public String getPatch_link() {
        return patch_link;
    }

    public void setPatch_link(String patch_link) {
        this.patch_link = patch_link;
    }

    public int getPatch_version() {
        return patch_version;
    }

    public void setPatch_version(int patch_version) {
        this.patch_version = patch_version;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Patch{" +
                "patch_link='" + patch_link + '\'' +
                ", patch_version=" + patch_version +
                ", state=" + state +
                '}';
    }
}
