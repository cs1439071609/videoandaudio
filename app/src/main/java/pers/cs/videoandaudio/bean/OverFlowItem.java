package pers.cs.videoandaudio.bean;

/**
 * @author chensen
 * @time 2020/4/25  14:19
 * @desc
 */
public class OverFlowItem {
    //信息标题
    private String title;
    //图片ID
    private int avatar;

    public String getTitle() {
        return title;
    }

    //标题
    public void setTitle(String title) {
        this.title = title;
    }

    public int getAvatar() {
        return avatar;
    }

    //图片
    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }
}
