package pers.cs.videoandaudio.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pers.cs.videoandaudio.bean.AudioItem;
import pers.cs.videoandaudio.info.ArtistInfo;
import pers.cs.videoandaudio.info.MusicInfo;
import pers.cs.videoandaudio.json.AlbumInfo;

/**
 * @author chensen
 * @time 2020/4/24  21:15
 * @desc
 */
public class MusicUtils {

    //歌手和专辑列表点击都会进入MyMusic 此时要传递参数表明是从哪里进入的
    public static final int START_FROM_ARTIST = 1;
    public static final int START_FROM_ALBUM = 2;
    public static final int START_FROM_LOCAL = 3;
    public static final int START_FROM_FOLDER = 4;

    private static final int FILTER_SIZE = 1 * 1024 * 1024;// 1MB
    private static final int FILTER_DURATION = 1 * 60 * 1000;// 1分钟

    private static String SONG_A_Z = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;

    public static final String ALBUM_SORT_ORDER = "album_sort_order";

    private static String[] proj_music = new String[]{
            MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ARTIST_ID, MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE};
    private static String[] proj_album = new String[]{MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART,
            MediaStore.Audio.Albums.ALBUM, MediaStore.Audio.Albums.NUMBER_OF_SONGS, MediaStore.Audio.Albums.ARTIST};
    private static String[] proj_artist = new String[]{
            MediaStore.Audio.Artists.ARTIST,
            MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
            MediaStore.Audio.Artists._ID};
    private static String[] proj_folder = new String[]{MediaStore.Files.FileColumns.DATA};


    /**
     * 获取专辑信息
     *
     * @param context
     * @return
     */
    public static List<AlbumInfo> queryAlbums(Context context) {

        ContentResolver cr = context.getContentResolver();
        StringBuilder where = new StringBuilder(MediaStore.Audio.Albums._ID
                + " in (select distinct " + MediaStore.Audio.Media.ALBUM_ID
                + " from audio_meta where (1=1)");
        where.append(" and " + MediaStore.Audio.Media.SIZE + " > " + FILTER_SIZE);
        where.append(" and " + MediaStore.Audio.Media.DURATION + " > " + FILTER_DURATION);
        where.append(" )");

        // Media.ALBUM_KEY 按专辑名称排序
        List<AlbumInfo> list = getAlbumList(cr.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, proj_album,
                where.toString(), null, null));
        return list;
    }
    public static List<AlbumInfo> getAlbumList(Cursor cursor) {
        List<AlbumInfo> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            AlbumInfo info = new AlbumInfo();
            info.setTitle(cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Albums.ALBUM)));
            info.setAlbum_id(String.valueOf(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums._ID))));
            info.setSong_num(cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS)));
            info.setPic_small(String.valueOf(getAlbumArtUri(Long.parseLong(info.getAlbum_id()))));
            info.setAuthor(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST)));
            list.add(info);
        }
        cursor.close();
        return list;
    }

    /**
     * 获取歌手信息
     *
     * @param context
     * @return
     */
    public static List<ArtistInfo> queryArtist(Context context) {

        Uri uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        ContentResolver cr = context.getContentResolver();
        StringBuilder where = new StringBuilder(MediaStore.Audio.Artists._ID
                + " in (select distinct " + MediaStore.Audio.Media.ARTIST_ID
                + " from audio_meta where (1=1 )");
        where.append(" and " + MediaStore.Audio.Media.SIZE + " > " + FILTER_SIZE);
        where.append(" and " + MediaStore.Audio.Media.DURATION + " > " + FILTER_DURATION);

        where.append(")");

        List<ArtistInfo> list = getArtistList(cr.query(uri, proj_artist,
                where.toString(), null, SONG_A_Z));

        return list;
    }

    public static List<ArtistInfo> getArtistList(Cursor cursor) {
        List<ArtistInfo> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            ArtistInfo info = new ArtistInfo();
            info.artist_name = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Artists.ARTIST));
            info.number_of_tracks = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS));
            info.artist_id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Artists._ID));
            info.artist_sort = SONG_A_Z;
            list.add(info);
        }
        cursor.close();
        return list;
    }


    /**
     * 查询音乐信息
     * @param context
     * @param from
     * @return
     */
    public static ArrayList<MusicInfo> queryMusicInfo(Context context, int from) {
        return queryMusicInfo(context,null,from);
    }


    /**
     * 查询音乐相关信息(音乐列表、)
     * @param context
     * @param id
     * @param from
     * @return
     */
    public static ArrayList<MusicInfo> queryMusicInfo(Context context, String id, int from) {

        ContentResolver resolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        StringBuilder select = new StringBuilder();
        // 查询语句：检索出.mp3为后缀名，时长大于1分钟，文件大小大于1MB的媒体文件
        select.append(MediaStore.Audio.Media.SIZE + " > " + FILTER_SIZE);
        select.append(" and " + MediaStore.Audio.Media.DURATION + " > " + FILTER_DURATION);

        switch (from) {
            case START_FROM_LOCAL:
                ArrayList<MusicInfo> list = getMusicListCursor(resolver.query(uri, proj_music,
                        select.toString(), null,
                        SONG_A_Z));
                return list;
            default:
                return null;
        }


    }

    /**
     * 查询音乐信息
     * @param cursor
     * @return
     */
    public static ArrayList<MusicInfo> getMusicListCursor(Cursor cursor) {
        if (cursor == null) {
            return null;
        }

        ArrayList<MusicInfo> musicList = new ArrayList<>();
        while (cursor.moveToNext()) {
            MusicInfo music = new MusicInfo();
            music.songId = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media._ID));
            music.albumId = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            music.albumName = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Albums.ALBUM));
            music.albumData = getAlbumArtUri(music.albumId) + "";
            music.duration = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DURATION));
            music.musicName = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.TITLE));
            music.artist = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ARTIST));
            music.artistId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));
            String filePath = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DATA));
            music.data = filePath;
            music.folder = filePath.substring(0, filePath.lastIndexOf(File.separator));
            music.size = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media.SIZE));
            music.islocal = true;
            musicList.add(music);
        }
        cursor.close();
        return musicList;
    }


    /**
     * 本地音乐-根据专辑id获取专辑地址
     * @param albumId
     * @return
     */
    public static Uri getAlbumArtUri(long albumId) {
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId);
    }


    /**
     * 废弃
     */
    public static ArrayList<AudioItem> queryMusic(Context context, String id, int from) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        StringBuilder select = new StringBuilder();
        // 查询语句：检索出.mp3为后缀名，时长大于1分钟，文件大小大于1MB的媒体文件
        select.append(MediaStore.Audio.Media.SIZE + " > " + FILTER_SIZE);
        select.append(" and " + MediaStore.Audio.Media.DURATION + " > " + FILTER_DURATION);
        Cursor cursor = resolver.query(uri, proj_music, select.toString(), null, SONG_A_Z);

        if (cursor == null) {
            return null;
        }

        ArrayList<AudioItem> musicList = new ArrayList<>();
        while (cursor.moveToNext()) {
            AudioItem music = new AudioItem(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
            music.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
            musicList.add(music);
        }
        cursor.close();
        return musicList;


    }

}
