package pers.cs.videoandaudio.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * @author chensen
 * @time 2020/4/15  20:32
 * @desc
 */
public class PreferencesUtility {

    private static PreferencesUtility sInstance;
    private static SharedPreferences mPreferences;

    public PreferencesUtility(final Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static final PreferencesUtility getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new PreferencesUtility(context.getApplicationContext());
        }
        return sInstance;
    }
    /**
     * commit() 有返回值，成功返回 true，失败返回 false。而 apply() 没有返回值。
     * commit() 方法是同步提交到硬件磁盘，因此，在多个并发的提交 commit 的时候，他们会等待正在处理的 commit 保存到磁盘后在操作，从而降低了效率。
     * apply() 是将修改的数据提交到内存，而后异步真正的提交到硬件磁盘。
     * @param id
     * @param link
     */
    public void setPlayLink(long id, String link) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(id + "", link);
        editor.apply();
    }
    public String getPlayLink(long id) {
        return mPreferences.getString(id + "", null);
    }
}
