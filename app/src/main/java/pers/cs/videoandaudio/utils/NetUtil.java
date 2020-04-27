package pers.cs.videoandaudio.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;

/**
 * @author chensen
 * @time 2020/1/15  16:51
 * @desc
 */
public class NetUtil {


    /**
     * MMS(Microsoft Media Server Protocol)，中文“微软媒体服务器协议”，
     * 用来访问并流式接收 Windows Media 服务器中 .asf 文件的一种协议。
     * MMS 协议用于访问 Windows Media 发布点上的单播内容。
     *
     * RTSP（Real Time Streaming Protocol），实时流传输协议，
     * 是TCP/IP协议体系中的一个应用层协议.
     * 该协议定义了一对多应用程序如何有效地通过IP网络传送多媒体数据
     * @param uri
     * @return
     */
    public boolean isNetVideo(String uri){

        boolean result = false;

        if (uri != null){

            if (uri.toLowerCase().startsWith("http")
                    || uri.toLowerCase().startsWith("rtsp")
                    || uri.toLowerCase().startsWith("mms")) {
                result = true;
            }
        }
        return result;
    }

    //上次字节数和上次时间戳
    private long lastTotalRxBytes = 0;
    private long lastTimeStamp = 0;

    public String getNetSpeed(Context context){

        String result = "0Kb/s";
        //获取某个网络UID的接受字节数
        long nowTotalRxBytes =
                TrafficStats.getUidRxBytes(context.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED
                        ? 0 :(TrafficStats.getTotalRxBytes()/1024);//转为KB;
        long nowTimeStamp = System.currentTimeMillis();
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换
        long speed2 = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 % (nowTimeStamp - lastTimeStamp));//毫秒转换

        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;


        result = String.valueOf(speed) + "." + String.valueOf(speed2) + " kb/s";

        return result;
    }


    /**
     * 是否连接网络
     * @param context
     * @return
     */
    public static boolean isConnectInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null) {
            return networkInfo.isAvailable();
        }
        return false;
    }
}
