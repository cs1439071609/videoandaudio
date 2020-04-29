package pers.cs.videoandaudio.bean;

/**
 * @author chensen
 * @time 2020/4/28  21:08
 * @desc 我的音乐列表内容信息
 */
public class MyMusicPlaylistItem {

    private final long id;
    private final String name;
    private final int songCount;
    private final String album;

    //local或其他
    private final String author;

    public MyMusicPlaylistItem() {
        this.id = -1;
        this.name = "";
        this.songCount = -1;
        this.album = "";
        this.author = "";
    }

    public MyMusicPlaylistItem(long _id, String _name, int _songCount, String _album, String author) {
        this.id = _id;
        this.name = _name;
        this.songCount = _songCount;
        this.album = _album;
        this.author = author;
    }


    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getSongCount() {
        return songCount;
    }

    public String getAlbum() {
        return album;
    }

    public String getAuthor() {
        return author;
    }
}
