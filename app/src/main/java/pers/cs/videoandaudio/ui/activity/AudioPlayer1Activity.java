package pers.cs.videoandaudio.ui.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pers.cs.videoandaudio.IMusicPlayerService;
import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.bean.Lyrics;
import pers.cs.videoandaudio.service.MusicPlayerService;
import pers.cs.videoandaudio.ui.View.LyricsTextView;
import pers.cs.videoandaudio.utils.LyricsUtils;
import pers.cs.videoandaudio.utils.TimeUtil;

/**
 * @author chensen
 * @time 2020/1/31  22:02
 * @desc
 */

public class AudioPlayer1Activity extends AppCompatActivity {


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

    private int maxVolume;
    private int currentVolume;

    private LyricsTextView lyricsTextView;

    private Toolbar toolbar;
    private ActionBar ab;

    private String TAG = AudioPlayer1Activity.class.getSimpleName();

    //更新进度
    private static final int UPDATE_PROGRESS = 1;
    //更新歌词
    private static final int UPDATE_LYRICS = 2;


    //服务的代理类，通过他可以调用服务的方法
    private IMusicPlayerService mIMusicPlayerService;
    private int position;

    private Intent serviceIntent;

    private MyReceiver myReceiver;

    private TimeUtil mTimeUtil;

    private AudioManager mAudioManager;

    //是否来自通知
    private boolean fromNotification;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_LYRICS:
                    try {
                        lyricsTextView.setCurrentPosition(mIMusicPlayerService.getCurrentPosition());

                        handler.removeMessages(UPDATE_LYRICS);
                        handler.sendEmptyMessage(UPDATE_LYRICS);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case UPDATE_PROGRESS:

                    try {
                        //获取当前进度
                        int currentPosition = mIMusicPlayerService.getCurrentPosition();

                        tvAudioTime.setText(mTimeUtil.formatTime(currentPosition));

                        seekbarAudio.setProgress(currentPosition);


                        handler.removeMessages(UPDATE_PROGRESS);
                        handler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 1000);


                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    break;
            }

        }
    };

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "onServiceConnected: ");
            mIMusicPlayerService = IMusicPlayerService.Stub.asInterface(service);
            if (mIMusicPlayerService != null) {
                try {
                    if (!fromNotification) {
                        if(mIMusicPlayerService.getPosition() == position){
                            showLyrics();
                            updateUI();
                        }else{
                            mIMusicPlayerService.openAudio(position);

                            Log.d(TAG, "onServiceConnected: "+position);
                        }

                    } else {
                        //从通知栏进入更新UI
                        showLyrics();
                        updateUI();

                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                //服务连接成功就更新播放模式
                showPlayMode();
                showStartOrPause();
                Log.d(TAG, "onServiceConnected: "+position);
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (mIMusicPlayerService != null) {
                try {
                    mIMusicPlayerService.stop();
                    mIMusicPlayerService = null;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
        }
    };


    private void showStartOrPause() {
        try {
            Log.e(TAG, "showStartOrPause: "+mIMusicPlayerService.isPlaying());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            if(mIMusicPlayerService.isPlaying()){
                playing_play.setImageResource(R.drawable.play_rdi_btn_pause);
            }else{
                playing_play.setImageResource(R.drawable.play_rdi_btn_play);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_audio_playing);
        ButterKnife.bind(this);


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

        initView();
        initEvent();
        initData();
        bindAndStartService();


    }

    private void initEvent() {
        seekbarAudio.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());

        volume_seek.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
    }

    class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            if(seekBar == volume_seek){
                if (fromUser) {

                    //flags为1显示系统音量条
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                    currentVolume = progress;
                }
            }else if(seekBar == seekbarAudio){

                try {
                    if (fromUser) {
                        mIMusicPlayerService.seekTo(progress);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
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


    private void bindAndStartService() {
        serviceIntent = new Intent(this, MusicPlayerService.class);
        serviceIntent.setAction("pers.cs.videoandaudio_OPENMUSIC");
        bindService(serviceIntent, conn, Context.BIND_AUTO_CREATE);
        //设置后只能启动一次服务
        startService(serviceIntent);


    }

    private void initData() {
        //得到当前音量和最大音量
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volume_seek.setMax(maxVolume);
        volume_seek.setProgress(currentVolume);


        fromNotification = getIntent().getBooleanExtra("notification", false);

        //        if(!fromNotification){
        position = getIntent().getIntExtra("position", 0);
        //        }

        mTimeUtil = new TimeUtil();

        //使用EventBus代替广播
        EventBus.getDefault().register(this);

       /*
        //使用广播通知音频准备好
        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicPlayerService.OPENMUSIC);
        registerReceiver(myReceiver, filter);
        */
    }

    //EventBus接收消息方法
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(String msg){
        Log.e(TAG, "onGetMessage: ");
        showLyrics();
        updateUI();
        showStartOrPause();
    }


    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            //音频准备后获取信息，更新UI
            showLyrics();


            updateUI();

        }
    }

    private void showLyrics() {
        try {

            LyricsUtils lyricsUtils = new LyricsUtils();
            String path = mIMusicPlayerService.getAudioPath();
            String filePath = path.substring(0,path.lastIndexOf("."));
            Log.e(TAG, "showLyrics: "+filePath);
            File file = new File(filePath + ".lrc");
            if(!file.exists()){
                file = new File(filePath + ".txt");
                if(!file.exists()){
                    String SDPATH = Environment.getExternalStorageDirectory().getPath() + "//";
                    file = new File(SDPATH +"/Music/Musiclrc/"
                            + mIMusicPlayerService.getName().replace(" ","")
                            + ".lrc");
                    Log.e(TAG, "showLyrics: "+file.getAbsolutePath());
                }
            }

            List<Lyrics> lyricsList = lyricsUtils.readLyricFile(file);
            if(lyricsList != null && lyricsList.size() > 0){
                lyricsTextView.setLyrics(lyricsList);
                tragetlrc.setVisibility(View.GONE);
                //更新歌词
                handler.sendEmptyMessage(UPDATE_LYRICS);
            }else{
                lyricsTextView.setLyrics(null);
                tragetlrc.setVisibility(View.VISIBLE);
            }


        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private void updateUI() {
        try {

            ab.setTitle(mIMusicPlayerService.getName());
            ab.setSubtitle(mIMusicPlayerService.getArtist());
            tvAudioDuration.setText(mTimeUtil.formatTime(mIMusicPlayerService.getDuration()));

            seekbarAudio.setMax(mIMusicPlayerService.getDuration());

            handler.removeMessages(UPDATE_PROGRESS);
            handler.sendEmptyMessage(UPDATE_PROGRESS);

        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private void initView() {

        lyricsTextView = findViewById(R.id.lyricsTextView);
    }



    @OnClick({R.id.playing_mode, R.id.playing_pre, R.id.playing_play, R.id.playing_next, R.id.playing_playlist,R.id.rl_playing,R.id.lrcviewContainer})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.playing_mode:
                changePlayMode();
                showPlayMode();
                break;
            case R.id.playing_pre:

                try {
                    if (mIMusicPlayerService != null) {
                        mIMusicPlayerService.pre();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.playing_play:
                try {
                    if (mIMusicPlayerService != null) {
                        if (mIMusicPlayerService.isPlaying()) {
                            mIMusicPlayerService.pause();
                            playing_play.setImageResource(R.drawable.play_rdi_btn_play);
                        } else {
                            mIMusicPlayerService.start();
                            playing_play.setImageResource(R.drawable.play_rdi_btn_pause);
                        }
                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.playing_next:
                try {
                    if (mIMusicPlayerService != null) {
                        mIMusicPlayerService.next();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.playing_playlist:
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

    private void showPlayMode() {
        try {
            if (mIMusicPlayerService != null) {
                int playMode = mIMusicPlayerService.getPlayMode();
                switch (playMode) {
                    case MusicPlayerService.ORDER_NORMAL:
                        playing_mode.setImageResource(R.drawable.play_icn_loop_prs);
                        break;
                    case MusicPlayerService.ORDER_SINGLE:
                        playing_mode.setImageResource(R.drawable.play_icn_one);

                        break;
                    case MusicPlayerService.ORDER_RANDOM:
                        playing_mode.setImageResource(R.drawable.play_icn_shuffle);

                        break;
                    default:
                        playing_mode.setImageResource(R.drawable.play_icn_loop_prs);

                        break;
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void changePlayMode() {
        try {
            if (mIMusicPlayerService != null) {
                int playMode = mIMusicPlayerService.getPlayMode();
                switch (playMode) {
                    case MusicPlayerService.ORDER_NORMAL:
                        playMode = MusicPlayerService.ORDER_SINGLE;
                        Toast.makeText(this, "单曲循环", Toast.LENGTH_SHORT).show();
                        break;
                    case MusicPlayerService.ORDER_SINGLE:
                        playMode = MusicPlayerService.ORDER_RANDOM;
                        Toast.makeText(this, "随机循环", Toast.LENGTH_SHORT).show();
                        break;
                    case MusicPlayerService.ORDER_RANDOM:
                        playMode = MusicPlayerService.ORDER_NORMAL;
                        Toast.makeText(this, "顺序循环", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        playMode = MusicPlayerService.ORDER_NORMAL;
                        Toast.makeText(this, "顺序循环", Toast.LENGTH_SHORT).show();
                        break;
                }
                mIMusicPlayerService.setPlayMode(playMode);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
/*

        if (myReceiver != null) {
            unregisterReceiver(myReceiver);
            myReceiver = null;
        }
*/
        //使用EventBus代替广播
        EventBus.getDefault().unregister(this);

        handler.removeCallbacksAndMessages(null);

        if (conn != null) {
            unbindService(conn);
            conn = null;
        }

        super.onDestroy();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        if (item.getItemId() == R.id.menu_share) {
//            MusicInfo musicInfo = MusicUtils.getMusicInfo(PlayingActivity.this, MusicPlayer.getCurrentAudioId());
            try {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("content://" + mIMusicPlayerService.getAudioPath()));
                shareIntent.setType("audio/*");
                this.startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.shared_to)));
            } catch (RemoteException e) {
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
