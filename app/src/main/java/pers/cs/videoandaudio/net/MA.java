package pers.cs.videoandaudio.net;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import pers.cs.videoandaudio.MyApplication;

/**
 * @author chensen
 * @time 2020/4/2  22:00
 * @desc Music Address
 */
public class MA {
    
    private static final String TAG = MA.class.getSimpleName();
    private static final Boolean DEBUG = true;

    public static final String FORMATE = "json";
    public static final String BASE = "http://tingapi.ting.baidu.com/v1/restserver/ting?from=android&version=5.6.5.6&format=" + FORMATE;


    public static class Song {

        /**
         * 歌曲基本信息
         *
         * @param songid 歌曲id
         * @return
         */
        public static String songBaseInfo(String songid) {
            StringBuffer sb = new StringBuffer(BASE);
            sb.append("&method=").append("baidu.ting.song.baseInfos")
                    .append("&song_id=").append(songid);
            return sb.toString();
        }
        /**
         * 歌曲信息和下载地址
         * 需加密
         * @param songid
         * @return
         */
        public static String songInfo(String songid) {
            StringBuffer sb = new StringBuffer(BASE);
            String str = "songid=" + songid + "&ts=" + System.currentTimeMillis();
            String e = AESTools.encrpty(str);
            sb.append("&method=").append("baidu.ting.song.getInfos")
                    .append("&").append(str)
                    .append("&e=").append(e);
            if(MyApplication.DEBUG){
                Log.d(TAG,"songInfo:" + sb.toString());
            }
            return sb.toString();
        }

    }

    /**
     * 搜索
     */
    public static class Search{
        /**
         * 热门关键字
         * @return
         */
        public static String hotWord() {
            StringBuffer sb = new StringBuffer(BASE);
            sb.append("&method=").append("baidu.ting.search.hot");
            if(MyApplication.DEBUG){
                Log.d(TAG,"hotWord:" + sb.toString());
            }
            return sb.toString();
        }

        /**
         * 合并搜索结果，用于搜索建议中的歌曲
         *
         * @param query
         * @return
         */
        public static String searchMerge(String query, int pageNo, int pageSize) {
            StringBuffer sb = new StringBuffer(BASE);
            sb.append("&method=").append("baidu.ting.search.merge")
                    .append("&query=").append(encode(query))
                    .append("&page_no=").append(pageNo)
                    .append("&page_size=").append(pageSize)
                    .append("&type=-1&data_source=0");
            if(MyApplication.DEBUG){
                Log.d(TAG,"searchMerge:" + sb.toString());
            }
            return sb.toString();
        }
        /**
         * 搜歌词
         *
         * @param songname 歌名
         * @param artist   艺术家
         * @return
         */
        public static String searchLrcPic(String songname, String artist) {
            StringBuffer sb = new StringBuffer(BASE);
            String ts = Long.toString(System.currentTimeMillis());
            String query = encode(songname) + "$$" + encode(artist);
            String e = AESTools.encrpty("query=" + songname + "$$" + artist + "&ts=" + ts);
            sb.append("&method=").append("baidu.ting.search.lrcpic")
                    .append("&query=").append(query)
                    .append("&ts=").append(ts)
                    .append("&type=2")
                    .append("&e=").append(e);
            return sb.toString();
        }
    }

    /**
     * 歌单
     */
    public static class GeDan {
        /**
         * 歌单信息和歌曲
         *
         * @param listid 歌单id
         * @return
         */
        public static String geDanInfo(String listid) {
            StringBuffer sb = new StringBuffer(BASE);
            sb.append("&method=").append("baidu.ting.diy.gedanInfo")
                    .append("&listid=").append(listid);
            return sb.toString();
        }
    }

    /**
     * 音乐榜
     */
    public static class Billboard {

        /**
         * 所有音乐榜类别
         *
         * @return
         */
        public static String billCategory() {
            StringBuffer sb = new StringBuffer(BASE);
            sb.append("&method=").append("baidu.ting.billboard.billCategory")
                    .append("&kflag=1");
            return sb.toString();
        }

        /**
         * 音乐榜歌曲
         *
         * @param type   类型
         * @param offset 偏移
         * @param size   获取数量
         * @return
         */
        public static String billSongList(int type, int offset, int size) {
            StringBuffer sb = new StringBuffer(BASE);
            sb.append("&method=").append("baidu.ting.billboard.billList")
                    .append("&type=").append(type)
                    .append("&offset=").append(offset)
                    .append("&size=").append(size)
                    .append("&fields=").append(encode("song_id,title,author,album_title,pic_big,pic_small,havehigh,all_rate,charge,has_mv_mobile,learn,song_source,korean_bb_song"));
            return sb.toString();
        }
    }

    public static String encode(String str) {
        if (str == null) return "";
        try {
            return URLEncoder.encode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }
}
