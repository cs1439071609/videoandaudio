package pers.cs.videoandaudio.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pers.cs.videoandaudio.MyApplication;
import pers.cs.videoandaudio.proxy.utils.Constants;

/**
 * @author chensen
 * @time 2020/4/1  20:53
 * @desc
 * 百度侦测到你是手机访问的话会自动跳转到另一个主机，也就拿不到想要的HTTP响应了。
 * 所以需要通过HttpClient伪造HTTP请求中的User-Agent这一项，让百度认为你是通过桌面访问的。
 */
public class OkHttpUtil {

    private static final String TAG = OkHttpUtil.class.getSimpleName();

    public static final OkHttpClient.Builder OkhttpBuilder;

    static {
        Log.d(TAG, "static initializer: ");
        OkhttpBuilder = new OkHttpClient.Builder();
    }

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

    public static String getResposeString(String action1) {
        try {
            OkHttpClient okHttpClient = OkhttpBuilder
                    .connectTimeout(1000, TimeUnit.MINUTES)
                    .readTimeout(1000, TimeUnit.MINUTES)
                    .build();

            Request request = new Request.Builder()
                    .addHeader("user-agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:0.9.4)")
                    .url(action1)
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String c = response.body().string();
                Log.e("billboard", c);
                return c;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 生成返回MediaPlayer的Response Header
     *
     * @param rangeStart
     * @param rangeEnd
     * @param fileLength
     * @return
     */
    public static String genResponseHeader(int rangeStart, int rangeEnd, int fileLength) {
        StringBuffer sb = new StringBuffer();
        sb.append("HTTP/1.1 206 Partial Content").append("\n");
        sb.append("Content-Type: audio/mpeg").append("\n");
        sb.append("Content-Length: ").append(rangeEnd - rangeStart + 1).append("\n");
        sb.append("Connection: keep-alive").append("\n");
        sb.append("Accept-Ranges: bytes").append("\n");
        String contentRangeValue = String.format(Constants.CONTENT_RANGE_PARAMS + "%d-%d/%d", rangeStart, rangeEnd,
                fileLength);
        sb.append("Content-Range: ").append(contentRangeValue).append("\n");
        sb.append("\n");
        return sb.toString();
    }
    /**
     * 发送请求,得到Response
     *
     * @param request
     * @return
     */
    public static HttpURLConnection send(URLConnection request) {
        /*
         * 添加需要的Header
         */
        //		request.setRequestProperty(Constants.USER_AGENT, "TrafficRadio_BedPotato_Exclusive_UA");
        // TODO Others Header
        /*
         * 发送请求
         */
        HttpURLConnection httpURLConnection = (HttpURLConnection) request;
        httpURLConnection.setConnectTimeout(20000);
        httpURLConnection.setReadTimeout(60000);
        //		request.set
        //		DefaultHttpClient seed = new DefaultHttpClient();
        //		SchemeRegistry registry = new SchemeRegistry();
        //		registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        //		SingleClientConnManager mgr = new MyClientConnManager(seed.getParams(), registry);
        //		DefaultHttpClient http = new DefaultHttpClient(mgr, seed.getParams());
        //		HttpResponse response = null;
        //		try {
        //			Log.d(LOG_TAG, "sending request");
        //		//	response = http.execute(request);
        //			Log.d(LOG_TAG, "response receive");
        //	} catch (ClientProtocolException e) {
        //		Log.e(LOG_TAG, "Error downloading", e);
        //	} catch (IOException e) {
        //		Log.e(LOG_TAG, "Error downloading", e);
        //	}
        //	StatusLine line = response.getStatusLine();
        //	if (line.getStatusCode() != 200 && line.getStatusCode() != 206) {
        //		Log.i(LOG_TAG, "ERROR Response Status:" + line.toString());
        //		return null;
        //	}else {
        //		return response;
        //	}
        return httpURLConnection;
    }
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

            OkHttpClient okHttpClient = OkhttpBuilder.cache(cache)
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

    public static JsonObject getResposeJsonObject1(String action, Context context, boolean forceCache) {

        try {

            File cacheDir = context.getCacheDir();
            //30Mb
            Cache cache = new Cache(cacheDir.getAbsoluteFile(),1024 * 1024 * 30);

            OkHttpClient okHttpClient = OkhttpBuilder.cache(cache)
                    .connectTimeout(1000, TimeUnit.MINUTES)
//                    .readTimeout(1000, TimeUnit.MINUTES)
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

                JsonParser parser = new JsonParser();
                JsonElement el = parser.parse(c);
                return el.getAsJsonObject();

            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JsonObject getResposeJsonObject(String action) {
        try {
            OkHttpClient okHttpClient = OkhttpBuilder
                    .connectTimeout(3000, TimeUnit.MINUTES)
//                    .readTimeout(5000, TimeUnit.MINUTES)
                    .build();
            Request request = new Request.Builder()
                    .addHeader("user-agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:0.9.4)")
                    .url(action)
                    .build();
            Log.d(TAG, "getResposeJsonObject: "+action);
            Response response = okHttpClient.newCall(request).execute();
            if(response.isSuccessful()){
                String result = response.body().string();

                JsonObject obj = MyApplication.gsonInstance().fromJson(result, JsonObject.class);

                return obj;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



}
