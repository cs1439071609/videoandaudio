package pers.cs.videoandaudio.bean;

/**
 * @author chensen
 * @time 2020/4/28  21:14
 * @desc 我的音乐标题信息
 */
public class MyMusicTitleInfo {

    //信息标题
    private String title;
    private int count;
    //图片ID
    private int avatar;
    private boolean countChanged = true;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public boolean isCountChanged() {
        return countChanged;
    }

    public void setCountChanged(boolean countChanged) {
        this.countChanged = countChanged;
    }
}
