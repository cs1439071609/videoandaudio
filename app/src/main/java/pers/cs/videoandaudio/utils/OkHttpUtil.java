package pers.cs.videoandaudio.utils;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author chensen
 * @time 2020/4/1  20:53
 * @desc
 */
public class OkHttpUtil {

//    private static final OkHttpUtil okHttpUtil = new OkHttpUtil();
//
//    private static  OkHttpClient mOkHttpClient;
//
//    public static OkHttpUtil getInstance(){
//        return okHttpUtil;
//    };

//    private OkHttpUtil(){
//        File cacheFile = context.getCacheDir();
//        //30Mb
//        Cache cache = new Cache(cacheFile.getAbsoluteFile(), 1024 * 1024 * 30);
//        CacheControl cacheControl = new CacheControl.Builder()
//                .
//                .build();
//        Request request = new Request.Builder().cacheControl(n)
//
//    }


    /**
     *
     * @param action
     * @param context
     * @param forceCache 是否强制使用本地缓存，如果无可用缓存则返回一个code为504的响应
     */
    public static JSONObject getResposeJsonObject(String action, Context context, boolean forceCache) {

        try {

            File cacheDir = context.getCacheDir();
            //30Mb
            Cache cache = new Cache(cacheDir.getAbsoluteFile(),1024 * 1024 * 30);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cache(cache)
                    .connectTimeout(1000, TimeUnit.MINUTES)
                    .readTimeout(1000, TimeUnit.MINUTES)
                    .build();
            Request.Builder builder = new Request.Builder()
                    //用户代理
                    .addHeader("user-agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:0.9.4)")
                    .url(action);
            if(forceCache){
                //CacheControl.FORCE_CACHE，即强制使用本地缓存，如果无可用缓存则返回一个code为504的响应
                builder.cacheControl(CacheControl.FORCE_CACHE);
            }
            Request request = builder.build();
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {

                String c = response.body().string();
//                Log.e("cache", c);

//                JsonParser parser = new JsonParser();
//                JsonElement el = parser.parse(c);
//                Log.e("cache", el.getAsJsonObject().toString());
//                return el.getAsJsonObject();

                return new JSONObject(c);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
}
