package pers.cs.videoandaudio.base;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import pers.cs.videoandaudio.ui.activity.MusicStateListener;


/**
 * @author chensen
 * @time 2020/4/28  15:08
 * @desc
 */
public class BaseMusicFragment extends Fragment implements MusicStateListener {

    protected Context mContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((BaseActivity) getActivity()).setMusicStateListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((BaseActivity) getActivity()).removeMusicStateListener(this);
    }

    @Override
    public void updateTrackInfo() {

    }



}
