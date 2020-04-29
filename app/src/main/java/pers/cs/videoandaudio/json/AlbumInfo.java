package pers.cs.videoandaudio.json;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author chensen
 * @time 2020/4/4  21:32
 * @desc 专辑信息
 */

public class AlbumInfo implements Parcelable {
    /**
     * resource_type_ext : 0
     * all_artist_id : 601,56068,31653341,674169892
     * publishtime : 2020-02-12
     * song_num : 1
     * company : 华宇世博音乐文化（北京）有限公司
     * album_desc :
     * title : <em>我们在一起</em>
     * album_id : 674169894
     * pic_small : http://qukufile2.qianqian.com/data2/pic/9744cd7eceaf7a7a8205e0aa1015b89b/674272732/674272732.jpg@s_2,w_90,h_90
     * hot : 4672
     * author : 胡海泉,汪苏泷,于文文,玖月奇迹（王小玮）
     * artist_id : 601
     */

    private String resource_type_ext;
    //所有艺术家id
    private String all_artist_id;
    //发行时间
    private String publishtime;
    //歌曲数
    private int song_num;
    //公司
    private String company;
    //专辑描述
    private String album_desc;
    //专辑名
    private String title;
    //专辑id
    private String album_id;
    //专辑图片地址
    private String pic_small;
    //专辑热度？？？
    private int hot;
    //艺术家
    private String author;
    //艺术家id？？？
    private String artist_id;



    public String getResource_type_ext() {
        return resource_type_ext;
    }

    public void setResource_type_ext(String resource_type_ext) {
        this.resource_type_ext = resource_type_ext;
    }

    public String getAll_artist_id() {
        return all_artist_id;
    }

    public void setAll_artist_id(String all_artist_id) {
        this.all_artist_id = all_artist_id;
    }

    public String getPublishtime() {
        return publishtime;
    }

    public void setPublishtime(String publishtime) {
        this.publishtime = publishtime;
    }

    public int getSong_num() {
        return song_num;
    }

    public void setSong_num(int song_num) {
        this.song_num = song_num;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAlbum_desc() {
        return album_desc;
    }

    public void setAlbum_desc(String album_desc) {
        this.album_desc = album_desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(String album_id) {
        this.album_id = album_id;
    }

    public String getPic_small() {
        return pic_small;
    }

    public void setPic_small(String pic_small) {
        this.pic_small = pic_small;
    }

    public int getHot() {
        return hot;
    }

    public void setHot(int hot) {
        this.hot = hot;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getArtist_id() {
        return artist_id;
    }

    public void setArtist_id(String artist_id) {
        this.artist_id = artist_id;
    }

    /**
     * 描述
     * 返回的是内容的描述信息
     * 只针对一些特殊的需要描述信息的对象,需要返回1,其他情况返回0就可以
     * @return
     */
    @Override
    public int describeContents() {
        return 0;
    }
    /**
     * 序列化
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.resource_type_ext);
        dest.writeString(this.all_artist_id);
        dest.writeString(this.publishtime);
        dest.writeInt(this.song_num);
        dest.writeString(this.company);
        dest.writeString(this.album_desc);
        dest.writeString(this.title);
        dest.writeString(this.album_id);
        dest.writeString(this.pic_small);
        dest.writeInt(this.hot);
        dest.writeString(this.author);
        dest.writeString(this.artist_id);
    }

    public AlbumInfo(){}

    public AlbumInfo(Parcel source) {
        this.resource_type_ext = source.readString();
        this.all_artist_id = source.readString();
        this.publishtime = source.readString();
        this.song_num = source.readInt();
        this.company = source.readString();
        this.album_desc = source.readString();
        this.title = source.readString();
        this.album_id = source.readString();
        this.pic_small = source.readString();
        this.hot = source.readInt();
        this.author = source.readString();
        this.artist_id = source.readString();
    }
    /**
     * 负责反序列化
     */
    public static final Creator<AlbumInfo> CREATOR = new Creator<AlbumInfo>() {
        /**
         * 从序列化对象中，获取原始的对象
         * @param source
         * @return
         */
        @Override
        public AlbumInfo createFromParcel(Parcel source) {
            return new AlbumInfo(source);
        }
        /**
         * 创建指定长度的原始对象数组
         * @param size
         * @return
         */
        @Override
        public AlbumInfo[] newArray(int size) {
            return new AlbumInfo[size];
        }
    };
}
