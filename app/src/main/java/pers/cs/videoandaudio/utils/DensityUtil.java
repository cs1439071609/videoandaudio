package pers.cs.videoandaudio.utils;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * @author chensen
 * @time 2020/2/9  12:10
 * @desc
 */
public class DensityUtil {


    public static int dip2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density; //当前屏幕密度因子
        return (int) (dp * scale + 0.5f);
    }

    public static int Px2Dp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }



}
