package pers.cs.videoandaudio.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.info.MusicInfo;
import pers.cs.videoandaudio.utils.TimeUtil;

/**
 * @author chensen
 *
 * @time 2020/4/27  17:47
 *
 * @desc 音乐详细信息
 *
 */

public class MusicDetailFragment extends DialogFragment {

    private Context mContext;
    private TextView title, name, time, qua, size, path;
    private MusicInfo musicInfo;

    public static MusicDetailFragment newInstance(MusicInfo musicInfo) {
        MusicDetailFragment fragment = new MusicDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("musicinfo", musicInfo);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CustomMoreDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //设置从底部弹出
        WindowManager.LayoutParams params = getDialog().getWindow()
                .getAttributes();
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setAttributes(params);
        if (getArguments() != null) {
            musicInfo = getArguments().getParcelable("musicinfo");
        }

        View view = inflater.inflate(R.layout.fragment_music_detail, container, false);
        title = view.findViewById(R.id.music_detail_title);
        name = view.findViewById(R.id.music_detail_name);
        time = view.findViewById(R.id.music_detail_time);
        //qua = (TextView) view.findViewById(R.id.music_detail_quater);
        size = view.findViewById(R.id.music_detail_size);
        path = view.findViewById(R.id.music_detail_path);

        title.setText(musicInfo.getMusicName());
        name.setText(musicInfo.getArtist() + "-" + musicInfo.getMusicName());
        time.setText(new TimeUtil().formatTime(musicInfo.getDuration()));

        size.setText(musicInfo.getSize() / 1000000 + "M");
        path.setText(musicInfo.getData());
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //设置fragment高度 、宽度
//        int dialogHeight = (int) (mContext.getResources().getDisplayMetrics().heightPixels * 0.40);
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        getDialog().setCanceledOnTouchOutside(true);
    }
}
