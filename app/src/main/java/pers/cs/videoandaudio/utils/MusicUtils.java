package pers.cs.videoandaudio.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;

import pers.cs.videoandaudio.bean.AudioItem;
import pers.cs.videoandaudio.info.MusicInfo;

/**
 * @author chensen
 * @time 2020/4/24  21:15
 * @desc
 */
public class MusicUtils {

    private static final int FILTER_SIZE = 1 * 1024 * 1024;// 1MB
    private static final int FILTER_DURATION = 1 * 60 * 1000;// 1分钟

    private static String SONG_A_Z = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;

    private static String[] proj_music = new String[]{
            MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ARTIST_ID, MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE};

    public static ArrayList<MusicInfo> queryMusicInfo(Context context, String id, int from) {

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

    public static Uri getAlbumArtUri(long albumId) {
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId);
    }
}
