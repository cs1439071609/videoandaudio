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
public class NetVideoPager extends BasePager {

    private static final String TAG = NetVideoPager.class.getSimpleName();
    private TextView tv_net_video;

    public NetVideoPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        Log.d(TAG, "initView: " + "...");

        View view = View.inflate(mContext, R.layout.fragment_net_video,null);
        tv_net_video = view.findViewById(R.id.tv_net_video);
        return view;
    }

    @Override
    public void initDate() {
        Log.d(TAG, "initDate: " + "...");
        super.initDate();
        tv_net_video.setText("网络视频");
    }
}
