package pers.cs.videoandaudio.bean;

import java.io.Serializable;

/**
 * @author chensen
 * @time 2020/1/7  23:12
 * @desc
 */
public class VideoItem implements Serializable {

    private String name;

    private String size;

    private String time;

    //地址
    private String data;

    private String imageUrl;

    private String desc;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public VideoItem() {
    }

    public VideoItem(String name, String size, String time, String data) {
        this.name = name;
        this.size = size;
        this.time = time;
        this.data = data;
    }

    @Override
    public String toString() {
        return "VideoItem{" +
                "name='" + name + '\'' +
                ", size=" + size +
                ", time=" + time +
                ", data='" + data + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSimpleName() {
        int dot = name.lastIndexOf(".");
        return name.substring(0,dot);
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
