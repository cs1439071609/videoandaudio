package pers.cs.videoandaudio;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;

import org.xutils.x;

/**
 * @author chensen
 * @time 2020/1/26  21:25
 * @desc
 */
public class MyApplication extends Application {

    public static final Boolean DEBUG = true;

    public static Context context;
    private static Gson gson;
    public static Gson gsonInstance() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        x.Ext.init(this);
        //是否输出debug日志，开启debug会影响性能。
        x.Ext.setDebug(true);
    }
}
