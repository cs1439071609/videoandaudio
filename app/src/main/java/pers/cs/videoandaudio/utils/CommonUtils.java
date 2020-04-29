package pers.cs.videoandaudio.utils;

import android.os.Build;

/**
 * @author chensen
 * @time 2020/4/28  22:44
 * @desc
 */
public class CommonUtils {


    /**
     * 是否大于21(5.0)
     * @return
     */
    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }




}
