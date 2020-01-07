package pers.cs.videoandaudio.ui.Pager;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.base.BasePager;

/**
 * @author chensen
 * @time 2020/1/6  20:59
 * @desc
 */
public class LocalVideoPager extends BasePager {

    private static final String TAG = LocalVideoPager.class.getSimpleName();
    private TextView tv_local_video;

    public LocalVideoPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        Log.d(TAG, "initView: " + "...");

        View view = View.inflate(mContext, R.layout.fragment_local_video,null);
        tv_local_video = view.findViewById(R.id.tv_local_video);
        return view;
    }

    @Override
    public void initDate() {
        Log.d(TAG, "initDate: " + "...");
        super.initDate();
        tv_local_video.setText("本地视频");
    }
}
