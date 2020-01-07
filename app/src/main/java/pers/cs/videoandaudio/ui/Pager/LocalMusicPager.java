package pers.cs.videoandaudio.ui.Pager;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.base.BasePager;

/**
 * @author chensen
 * @time 2020/1/6  21:00
 * @desc
 */
public class LocalMusicPager extends BasePager {

    private static final String TAG = LocalMusicPager.class.getSimpleName();
    private TextView tv_local_music;

    public LocalMusicPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        Log.d(TAG, "initView: " + "...");

        View view = View.inflate(mContext, R.layout.fragment_local_music,null);
        tv_local_music = view.findViewById(R.id.tv_local_music);
        return view;
    }

    @Override
    public void initDate() {
        Log.d(TAG, "initDate: " + "...");
        super.initDate();
        tv_local_music.setText("本地音乐");
    }
}
