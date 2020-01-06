package pers.cs.videoandaudio.ui.fragment;


import android.util.Log;
import android.view.View;

import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.base.BaseFragment;

/**
 * @author chensen
 *
 * @time 2020/1/6  16:00
 *
 * @desc
 *
 */

public class LocalMusicFragment extends BaseFragment {

    private static final String TAG = LocalMusicFragment.class.getSimpleName();

    private View mView;

    @Override
    protected View initView() {
        Log.d(TAG, "initView: " + "...");

        mView = View.inflate(mContext, R.layout.fragment_local_music, null);
        return mView;

    }

    @Override
    protected void initData() {
        Log.d(TAG, "initData: " + "...");

        super.initData();

    }
}
