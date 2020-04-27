package pers.cs.videoandaudio.ui.Pager;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.base.BasePager;

/**
 * @author chensen
 * @time 2020/1/6  21:01
 * @desc
 */
public class NetMusicPager extends BasePager {

    private static final String TAG = NetMusicPager.class.getSimpleName();
    private TextView tv_net_music;

    public NetMusicPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        Log.d(TAG, "initView: " + "...");

        View view = View.inflate(mContext, R.layout.fragment_net_music,null);

        return view;
    }

    @Override
    public void initDate() {
        Log.d(TAG, "initDate: " + "...");
        super.initDate();
        tv_net_music.setText("网络音乐");
    }
}
