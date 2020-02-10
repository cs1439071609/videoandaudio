package pers.cs.videoandaudio.bean;

/**
 * @author chensen
 * @time 2020/1/31  21:21
 * @desc
 */
public class AudioItem extends BaseItem {

    private String artist;

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public AudioItem() {
    }
    public AudioItem(String name, String size, String time, String data) {
        super(name,size,time,data);
    }
}
