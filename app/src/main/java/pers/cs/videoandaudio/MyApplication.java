package pers.cs.videoandaudio;

import android.app.Application;

import org.xutils.x;

/**
 * @author chensen
 * @time 2020/1/26  21:25
 * @desc
 */
public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        //是否输出debug日志，开启debug会影响性能。
        x.Ext.setDebug(true);
    }
}
