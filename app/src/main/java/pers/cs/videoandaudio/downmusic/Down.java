package pers.cs.videoandaudio.downmusic;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonSyntaxException;

import pers.cs.videoandaudio.MyApplication;
import pers.cs.videoandaudio.json.MusicFileDownInfo;
import pers.cs.videoandaudio.net.MA;
import pers.cs.videoandaudio.utils.OkHttpUtil;

/**
 * @author chensen
 * @time 2020/4/15  20:38
 * @desc
 */
public class Down {

    /**
     * {
     * "share_info": {},
     * "songinfo": {},
     * "songurl": {"url": [{}{}]}
     * "mv_info": {},
     * "error_code":
     *
     * @param context
     * @param id
     * @return
     */
    public static MusicFileDownInfo getUrl(final Context context, final String id) {
        MusicFileDownInfo musicFileDownInfo = null;
        try {
            JsonArray jsonArray = OkHttpUtil.getResposeJsonObject1(MA.Song.songInfo(id).trim(), context, false).get("songurl")
                    .getAsJsonObject().get("url").getAsJsonArray();
            int len = jsonArray.size();
            /*
            推荐比特率,现在的文件比特率很高128,320
            OGG的优势范围：96K以上（OGG）
            AAC的优势范围：AAC LC应高于（包含）256K AAC HE 48K-96K
            Mp3的优势范围：192K（包含）以上
            WMA的优势范围：128K（包含）以下
                    */
            int downloadBit = 192;

            for (int i = len - 1; i > -1; i--) {
                //比特率
                int bit = Integer.parseInt(jsonArray.get(i).getAsJsonObject().get("file_bitrate").toString());
                if (bit == downloadBit) {
                    musicFileDownInfo = MyApplication.gsonInstance().fromJson(jsonArray.get(i), MusicFileDownInfo.class);
                    break;
                } else if (bit < downloadBit && bit >= 64) {
                    musicFileDownInfo = MyApplication.gsonInstance().fromJson(jsonArray.get(i), MusicFileDownInfo.class);
                }

            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return musicFileDownInfo;
    }
}
