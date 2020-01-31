package pers.cs.videoandaudio.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author chensen
 * @time 2020/1/31  11:39
 * @desc 缓存工具类
 */
public class CacheUtils {

    public static void putString(Context context,String key,String values){
        SharedPreferences sharedPreferences = context.getSharedPreferences("videoandaudio",Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key,values).commit();
    }
    public static String getString(Context context,String key){

        SharedPreferences sharedPreferences = context.getSharedPreferences("videoandaudio",Context.MODE_PRIVATE);
        return sharedPreferences.getString(key,"");
    }

}
