package pers.cs.videoandaudio.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.drawerlayout.widget.DrawerLayout;

/**
 * @author chensen
 * @time 2020/3/30  17:20
 * @desc
 */
public class StatusBarUtil {

    public static final int DEFAULT_STATUS_BAR_ALPHA = 112;//透明度默认值，在 0 ~ 255 之间


    /**
     * 为 DrawerLayout 布局设置状态栏透明
     *
     * @param activity     需要设置的activity
     * @param drawerLayout DrawerLayout
     */
    public static void setTransparentForDrawerLayout(Activity activity, DrawerLayout drawerLayout) {
        //<4.4
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        //>=5.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);

        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        ViewGroup contentLayout = (ViewGroup) drawerLayout.getChildAt(0);

        // 内容布局不是 LinearLayout 时,设置padding top  ????
        if (!(contentLayout instanceof LinearLayout) && contentLayout.getChildAt(1) != null) {
            contentLayout.getChildAt(1).setPadding(0, getStatusBarHeight(activity), 0, 0);
        }
//        contentLayout.getChildAt(0).setPadding(0, getStatusBarHeight(activity), 0, 0);
        // 设置属性
        setDrawerLayoutProperty(drawerLayout, contentLayout);
    }


    /**
     * 设置 DrawerLayout 属性
     *
     * @param drawerLayout              DrawerLayout
     * @param drawerLayoutContentLayout DrawerLayout 的内容布局
     */
    private static void setDrawerLayoutProperty(DrawerLayout drawerLayout, ViewGroup drawerLayoutContentLayout) {
        ViewGroup drawer = (ViewGroup) drawerLayout.getChildAt(1);
        Log.d(Activity.class.getSimpleName(), "setDrawerLayoutProperty: "+drawer);
        drawerLayout.setFitsSystemWindows(false);
        drawerLayoutContentLayout.setFitsSystemWindows(true);
//        drawerLayoutContentLayout.setClipToPadding(true);
//        drawer.setFitsSystemWindows(false);
    }

    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    private static int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }
    /**
     * 设置全透明
     * @param activity
     */
    public static void setTransparent(Activity activity){
        //大于4.4
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        transparentStatusBar(activity);
        setRootView(activity);
    }

    /**
     * 设置根布局参数
     */
    private static void setRootView(Activity activity) {
        ViewGroup parent = (ViewGroup) activity.findViewById(android.R.id.content);
        for (int i = 0, count = parent.getChildCount(); i < count; i++) {
            View childView = parent.getChildAt(i);
            Log.d(Activity.class.getSimpleName(), "setRootView: "+count +"-" + i + childView);
            if (childView instanceof ViewGroup) {
                childView.setFitsSystemWindows(true);
                ((ViewGroup) childView).setClipToPadding(true);
            }
        }
    }

    private static void transparentStatusBar(Activity activity) {
        //>=5.0
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }else{
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

    }


}
