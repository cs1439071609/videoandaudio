package pers.cs.videoandaudio.ui.activity;

import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.base.BaseActivity;
import pers.cs.videoandaudio.bean.Lyrics;
import pers.cs.videoandaudio.service.MediaService;
import pers.cs.videoandaudio.service.MusicPlayer;
import pers.cs.videoandaudio.ui.View.LyricsTextView;
import pers.cs.videoandaudio.ui.fragment.PlayQueueFragment;
import pers.cs.videoandaudio.utils.LyricsUtils;
import pers.cs.videoandaudio.utils.TimeUtil;

/**
 * @author chensen
 * @time 2020/4/15  21:45
 * @desc
 */
public class PlayingActivity extends BaseActivity {

    private static final String TAG = PlayingActivity.class.getSimpleName();
    private static final Boolean DEBUG = true;

    @BindView(R.id.tv_audio_time)
    TextView tvAudioTime;
    @BindView(R.id.tv_audio_duration)
    TextView tvAudioDuration;

    @BindView(R.id.seekbar_audio)
    SeekBar seekbarAudio;
    @BindView(R.id.playing_mode)
    ImageView playing_mode;

    @BindView(R.id.playing_pre)
    ImageView playing_pre;
    @BindView(R.id.playing_play)
    ImageView playing_play;
    @BindView(R.id.playing_next)
    ImageView playing_next;
    @BindView(R.id.playing_playlist)
    ImageView playing_playlist;
    @BindView(R.id.tragetlrc)
    TextView tragetlrc;
    @BindView(R.id.rl_playing)
    RelativeLayout rl_playing;
    @BindView(R.id.lrcviewContainer)
    RelativeLayout lrcviewContainer;
    @BindView(R.id.volume_seek)
    SeekBar volume_seek;
    @BindView(R.id.img_music)
    ImageView imgMusic;

    private Toolbar toolbar;
    private ActionBar ab;
    private LyricsTextView lyricsTextView;

    private AudioManager mAudioManager;
    private int maxVolume;
    private int currentVolume;

    private MyOnSeekBarChangeListener mMyOnSeekBarChangeListener;

    //更新歌词
    private static final int UPDATE_LYRICS = 2;
    List<Lyrics> lyricsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_playing);
        ButterKnife.bind(this);

        lyricsTextView = findViewById(R.id.lyricsTextView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ab = getSupportActionBar();
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeAsUpIndicator(R.drawable.actionbar_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
        mMyOnSeekBarChangeListener = new MyOnSeekBarChangeListener();
        seekbarAudio.setOnSeekBarChangeListener(mMyOnSeekBarChangeListener);
        volume_seek.setOnSeekBarChangeListener(mMyOnSeekBarChangeListener);

        //从第三方软件传过来需要
        Uri uri = getIntent().getData();
        Log.d(TAG, "onCreate: "+(uri==null ? "" : uri.getPath()));
        if(uri != null){
            MusicPlayer.openFile(uri.getPath());
            MusicPlayer.playOrPause();
        }else{
            initView();
        }

        initData();


    }

    private void initData() {
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        volume_seek.setMax(maxVolume);
        volume_seek.setProgress(currentVolume);
    }

    private void initView() {
        ab.setTitle(MusicPlayer.getTrackName());
        ab.setSubtitle(MusicPlayer.getArtistName());
        long duration = MusicPlayer.duration();
        tvAudioDuration.setText(new TimeUtil().formatTime((int) duration));
        seekbarAudio.setMax((int) duration);
        seekbarAudio.setProgress((int) MusicPlayer.position());

        findLyrics();
    }

    @Override
    public void onMetaChanged() {
        super.onMetaChanged();
        Log.d(TAG, "onMetaChanged: ");
        initView();
    }

    public void updateLrc() {
        Log.d(TAG, "updateLrc: updateLrc"+MusicPlayer.getCurrentAudioId());
        findLyrics();
    }

    private void findLyrics() {
        tragetlrc.setVisibility(View.VISIBLE);
        LyricsUtils lyricsUtils = new LyricsUtils();
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/csmusic/lrc/" + MusicPlayer.getCurrentAudioId());
        String path = MusicPlayer.getPath();
        Log.d(TAG, "findLyrics: "+path);
        if(!file.exists() && path != null) {
            file = new File(path.substring(0,path.lastIndexOf(".")) + ".lrc");
        }
        lyricsList = lyricsUtils.readLyricFile(file);
        if (lyricsList != null && lyricsList.size() > 0) {
            lyricsTextView.setLyrics(lyricsList);
            tragetlrc.setVisibility(View.GONE);
            //更新歌词
        } else {
            lyricsTextView.setLyrics(null);
            tragetlrc.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        if (item.getItemId() == R.id.menu_share) {

            //            MusicInfo musicInfo = MusicUtils.getMusicInfo(PlayingActivity.this, MusicPlayer.getCurrentAudioId());

            try {
                String str = MusicPlayer.getPath();
                if (str != null) {
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + str));
                    shareIntent.setType("audio/*");
                    this.startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.shared_to)));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.playing_menu, menu);
        return true;

    }

    class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            if (seekBar == volume_seek) {
                if (fromUser) {
                    //flags为1显示系统音量条
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                    currentVolume = progress;
                }
            } else if (seekBar == seekbarAudio) {
                if (fromUser) {
                    MusicPlayer.seek((long) progress);
                }
                if (lyricsList != null) {
                    lyricsTextView.setCurrentPosition(progress);
                }

            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    @OnClick({R.id.playing_mode, R.id.playing_pre, R.id.playing_play, R.id.playing_next, R.id.playing_playlist, R.id.rl_playing, R.id.lrcviewContainer})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.playing_mode:
                changePlayMode();
                showPlayMode();
                break;
            case R.id.playing_pre:
                MusicPlayer.previous(PlayingActivity.this, true);
                break;
            case R.id.playing_play:
                if (MusicPlayer.isPlaying()) {
                    playing_play.setImageResource(R.drawable.play_rdi_btn_play);
                } else {
                    playing_play.setImageResource(R.drawable.play_rdi_btn_pause);
                }

                MusicPlayer.playOrPause();
                break;
            case R.id.playing_next:
                MusicPlayer.next();
                break;
            case R.id.playing_playlist:
                PlayQueueFragment playQueueFragment = new PlayQueueFragment();
                playQueueFragment.show(getSupportFragmentManager(), "playlistframent");
                break;
            case R.id.rl_playing:
                rl_playing.setVisibility(View.INVISIBLE);
                lrcviewContainer.setVisibility(View.VISIBLE);
                break;
            case R.id.lrcviewContainer:
                rl_playing.setVisibility(View.VISIBLE);
                lrcviewContainer.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private void changePlayMode() {
        int playMode = MusicPlayer.getRepeatMode();
        switch (playMode) {
            case MediaService.REPEAT_ALL:
                MusicPlayer.singleRepeat(this);
                break;
            case MediaService.REPEAT_CURRENT:
                MusicPlayer.shuffle(this);
                break;
            case MediaService.SHUFFLE_AUTO:
                MusicPlayer.allRepeat(this);
                break;
            default:
                MusicPlayer.allRepeat(this);
                break;
        }
    }
    private void showPlayMode() {
        int playMode = MusicPlayer.getRepeatMode();
        switch (playMode) {
            case MediaService.REPEAT_ALL:
                playing_mode.setImageResource(R.drawable.play_icn_loop_prs);
                break;
            case MediaService.REPEAT_CURRENT:
                playing_mode.setImageResource(R.drawable.play_icn_one);
                break;
            case MediaService.SHUFFLE_AUTO:
                playing_mode.setImageResource(R.drawable.play_icn_shuffle);
                break;
            default:
                playing_mode.setImageResource(R.drawable.play_icn_loop_prs);
                break;
        }
    }

    @Override
    public void updateTrack() {
        super.updateTrack();
        Log.d(TAG, "updateTrack: "+MusicPlayer.getCurrentAudioId());

        initView();
//        lyricsTextView.setCurrentPosition(0);

        showPlayMode();


        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.placeholder_disk_play_program)
                .error(R.drawable.placeholder_disk_play_program)
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(this).load(MusicPlayer.getAlbumPath())
                .apply(options)
                .into(imgMusic);
        //mDuration.setText(MusicUtils.makeShortTimeString(PlayingActivity.this.getApplication(), MusicPlayer.duration() / 1000));
    }

    @Override
    public void updateTrackInfo() {
        super.updateTrackInfo();
        Log.d(TAG, "updateTrackInfo: "+MusicPlayer.getCurrentAudioId());
        if (MusicPlayer.isPlaying()) {
            playing_play.setImageResource(R.drawable.play_rdi_btn_pause);
            seekbarAudio.removeCallbacks(mUpdateProgress);
            seekbarAudio.postDelayed(mUpdateProgress, 200);
        } else {
            playing_play.setImageResource(R.drawable.play_rdi_btn_play);
            seekbarAudio.removeCallbacks(mUpdateProgress);
        }

    }

    @Override
    public void updateBuffer(int p) {
        super.updateBuffer(p);
        Log.d(TAG, "updateBuffer: "+p);
        seekbarAudio.setSecondaryProgress((int) (MusicPlayer.duration() * p));
    }

    @Override
    public void loading(boolean isloading) {
        super.loading(isloading);
        Log.d(TAG, "loading: "+isloading);
        if(!isloading){
            playing_play.setEnabled(true);
            seekbarAudio.setEnabled(true);
            long duration = MusicPlayer.duration();
            tvAudioDuration.setText(new TimeUtil().formatTime((int) duration));
            seekbarAudio.setMax((int) duration);
        }else{
            playing_play.setEnabled(false);
            seekbarAudio.setEnabled(false);
        }
    }

    private Runnable mUpdateProgress = new Runnable() {
        @Override
        public void run() {
            if (seekbarAudio != null) {
                long position = MusicPlayer.position();
                tvAudioTime.setText(new TimeUtil().formatTime((int) position));
                seekbarAudio.setProgress((int) position);
                if (MusicPlayer.isPlaying()) {
                    seekbarAudio.postDelayed(mUpdateProgress, 200);
                } else {
                    seekbarAudio.removeCallbacks(mUpdateProgress);
                }
            }
        }
    };


    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }


    @Override
    protected void onDestroy() {
        seekbarAudio.removeCallbacks(mUpdateProgress);
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: "+this);
//        seekbarAudio.removeCallbacks(mUpdateProgress);
//        seekbarAudio.postDelayed(mUpdateProgress, 200);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        //监听物理按键音量增减
        if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
            currentVolume--;
            currentVolume = currentVolume > 0 ? currentVolume : 0;
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
            volume_seek.setProgress(currentVolume);
            return true;
        }else if(keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            currentVolume++;
            currentVolume = currentVolume < maxVolume ? currentVolume : maxVolume;
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
            volume_seek.setProgress(currentVolume);
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}
