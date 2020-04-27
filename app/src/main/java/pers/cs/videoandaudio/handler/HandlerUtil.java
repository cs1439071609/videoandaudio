package pers.cs.videoandaudio.handler;

import android.content.Context;
import android.os.Handler;

import java.lang.ref.WeakReference;

/**
 * @author chensen
 * @time 2020/4/17  15:59
 * @desc
 */
public class HandlerUtil extends Handler {

    private static HandlerUtil instance = null;
    WeakReference<Context> mActivityReference;

    public static HandlerUtil getInstance(Context context) {
        if (instance == null) {
            instance = new HandlerUtil(context.getApplicationContext());
        }
        return instance;
    }

    HandlerUtil(Context context) {
        mActivityReference = new WeakReference<>(context);
    }
}
