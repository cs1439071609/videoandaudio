package pers.cs.videoandaudio.ui.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.base.BaseMusicFragment;
import pers.cs.videoandaudio.service.MusicPlayer;
import pers.cs.videoandaudio.ui.activity.PlayingActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuickControlsFragment extends BaseMusicFragment implements View.OnClickListener {

    private static final String TAG = QuickControlsFragment.class.getSimpleName();
    private static final Boolean DEBUG = true;

    private Context mContext;
    private View view;
    private LinearLayout ll_controls;
    private TextView tv_name;
    private TextView tv_artist;
    private ImageView img_album;
    private ImageView img_play;
    private ImageView img_next;
    private ImageView img_list;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_quick_controls, container, false);

        initView(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initView(View view) {
        tv_name = view.findViewById(R.id.tv_controls_name);
        tv_artist = view.findViewById(R.id.tv_controls_singer);
        img_album = view.findViewById(R.id.img_controls_album);
        img_play = view.findViewById(R.id.img_controls_play);
        img_next = view.findViewById(R.id.img_controls_next);
        img_list = view.findViewById(R.id.img_controls_list);
        ll_controls = view.findViewById(R.id.ll_controls);
        img_play.setOnClickListener(this);
        img_next.setOnClickListener(this);
        img_list.setOnClickListener(this);
        ll_controls.setOnClickListener(this);
//        view.setOnClickListener;

        setBtnEnabled();


    }

    private void setBtnEnabled() {
        if(MusicPlayer.getQueue() == null || MusicPlayer.getQueue().length == 0){
            Log.d(TAG, "initView: ");
//            ll_controls.setOnClickListener(null);
//            img_play.setEnabled(false);
//            img_next.setEnabled(false);
//            img_list.setEnabled(false);
            view.setVisibility(View.GONE);
        }else{
//            ll_controls.setOnClickListener(this);
//            img_play.setEnabled(true);
//            img_next.setEnabled(true);
//            img_list.setEnabled(true);
            if(view.getVisibility() != View.VISIBLE){
                view.setVisibility(View.VISIBLE);
                Animation animation = AnimationUtils.loadAnimation(mContext,R.anim.dialog_in_bottom);
                view.startAnimation(animation);
            }

        }
    }

    private void initData() {

        updateNowPlaying();
    }

    private void updateNowPlaying() {
        tv_name.setText(MusicPlayer.getTrackName());
        tv_artist.setText(MusicPlayer.getArtistName());

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.placeholder_disk_210)
                .error(R.drawable.placeholder_disk_210)
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(mContext).load(MusicPlayer.getAlbumPath())
                .apply(requestOptions)
                .into(img_album);

    }

    public void updateState() {
        if (MusicPlayer.isPlaying()) {
            img_play.setImageResource(R.drawable.playbar_btn_pause);
        } else {
            img_play.setImageResource(R.drawable.playbar_btn_play);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_controls_play:
                img_play.setImageResource(MusicPlayer.isPlaying() ? R.drawable.playbar_btn_pause
                        : R.drawable.playbar_btn_play);
                MusicPlayer.playOrPause();
                break;
            case R.id.img_controls_next:
                MusicPlayer.next();
                break;
            case R.id.img_controls_list:
                PlayQueueFragment playQueueFragment = new PlayQueueFragment();
                playQueueFragment.show(getFragmentManager(), "playqueueframent");
                break;

            case R.id.ll_controls:
                Intent intent = new Intent(mContext, PlayingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                break;
        }
    }

    @Override
    public void updateTrackInfo() {
        super.updateTrackInfo();
        updateNowPlaying();
        updateState();
        setBtnEnabled();
    }
}
