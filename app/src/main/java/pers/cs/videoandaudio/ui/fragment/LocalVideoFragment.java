package pers.cs.videoandaudio.ui.fragment;


import android.util.Log;
import android.view.View;
import android.widget.ListView;

import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.adapter.LocalVideoFragmentAdapter;
import pers.cs.videoandaudio.base.BaseFragment;

/**
 * @author chensen
 *
 * @time 2020/1/6  15:54
 *
 * @desc
 *
 */


public class LocalVideoFragment extends BaseFragment {

    private static final String TAG = LocalVideoFragment.class.getSimpleName();
    private View view;
    private ListView lv_local_video;
    private LocalVideoFragmentAdapter mLocalVideoFragmentAdapter;

    @Override
    protected View initView() {
        Log.d(TAG, "initView: " + "...");
        view = View.inflate(mContext, R.layout.fragment_local_video,null);
//        lv_local_video = view.findViewById(R.id.lv_local_video);

        return view;
    }

    @Override
    protected void initData() {
        Log.d(TAG, "initData: " + "...");
        super.initData();

        mLocalVideoFragmentAdapter = new LocalVideoFragmentAdapter();
        lv_local_video.setAdapter(mLocalVideoFragmentAdapter);

    }
}
