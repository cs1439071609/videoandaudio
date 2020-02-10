package pers.cs.videoandaudio.ui.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class AudioPlayerActivity extends AppCompatActivity {

    @BindView(R.id.tv_audio_artist)
    TextView tvAudioArtist;
    @BindView(R.id.tv_audio_name)
    TextView tvAudioName;
    @BindView(R.id.tv_audio_time)
    TextView tvAudioTime;
    @BindView(R.id.seekbar_audio)
    SeekBar seekbarAudio;
    @BindView(R.id.btn_audio_order)
    Button btnAudioOrder;
    @BindView(R.id.btn_audio_pre)
    Button btnAudioPre;
    @BindView(R.id.btn_audio_pause)
    Button btnAudioPause;
    @BindView(R.id.btn_audio_next)
    Button btnAudioNext;
    @BindView(R.id.btn_audio_lyrics)
    Button btnAudioLyrics;
    @BindView(R.id.img_audio_icon)
    ImageView imgAudioIcon;
    private ImageView img_icon;
    private LyricsTextView lyricsTextView;

    private String TAG = AudioPlayerActivity.class.getSimpleName();

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

                        tvAudioTime.setText(mTimeUtil.formatTime(currentPosition) + "/"
                                + mTimeUtil.formatTime(mIMusicPlayerService.getDuration()));

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
                        mIMusicPlayerService.openAudio(position);
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
                btnAudioPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
            }else{
                btnAudioPause.setBackgroundResource(R.drawable.btn_audio_start_selector);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_audio_player);
        ButterKnife.bind(this);

        initView();
        initEvent();
        playAnim();
        initData();
        bindAndStartService();


    }

    private void initEvent() {
        seekbarAudio.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
    }

    class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            try {
                if (fromUser) {
                    mIMusicPlayerService.seekTo(progress);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
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
                //更新歌词
                handler.sendEmptyMessage(UPDATE_LYRICS);
            }else{
                lyricsTextView.setLyrics(null);
            }


        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private void updateUI() {
        try {
            tvAudioArtist.setText(mIMusicPlayerService.getArtist());
            tvAudioName.setText(mIMusicPlayerService.getName());
            seekbarAudio.setMax(mIMusicPlayerService.getDuration());

            handler.sendEmptyMessage(UPDATE_PROGRESS);

        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private void initView() {
        img_icon = findViewById(R.id.img_audio_icon);
        lyricsTextView = findViewById(R.id.lyricsTextView);
    }

    private void playAnim() {
        AnimationDrawable animationDrawable = (AnimationDrawable) img_icon.getBackground();

        if (!animationDrawable.isRunning()) {
            animationDrawable.start();
        }
    }

    @OnClick({R.id.btn_audio_order, R.id.btn_audio_pre, R.id.btn_audio_pause, R.id.btn_audio_next, R.id.btn_audio_lyrics})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_audio_order:
                changePlayMode();
                showPlayMode();
                break;
            case R.id.btn_audio_pre:

                try {
                    if (mIMusicPlayerService != null) {
                        mIMusicPlayerService.pre();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.btn_audio_pause:
                try {
                    if (mIMusicPlayerService != null) {
                        if (mIMusicPlayerService.isPlaying()) {
                            mIMusicPlayerService.pause();
                            btnAudioPause.setBackgroundResource(R.drawable.btn_audio_start_selector);
                        } else {
                            mIMusicPlayerService.start();
                            btnAudioPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
                        }
                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_audio_next:
                try {
                    if (mIMusicPlayerService != null) {
                        mIMusicPlayerService.next();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_audio_lyrics:
                break;
        }
    }

    private void showPlayMode() {
        try {
            if (mIMusicPlayerService != null) {
                int playMode = mIMusicPlayerService.getPlayMode();
                switch (playMode) {
                    case MusicPlayerService.ORDER_NORMAL:
                        btnAudioOrder.setBackgroundResource(R.drawable.btn_audio_order_normal_selector);
                        break;
                    case MusicPlayerService.ORDER_SINGLE:
                        btnAudioOrder.setBackgroundResource(R.drawable.btn_audio_order_single_selector);

                        break;
                    case MusicPlayerService.ORDER_ALL:
                        btnAudioOrder.setBackgroundResource(R.drawable.btn_audio_order_all_selector);

                        break;
                    default:
                        btnAudioOrder.setBackgroundResource(R.drawable.btn_audio_order_normal_selector);

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
                        playMode = MusicPlayerService.ORDER_ALL;
                        Toast.makeText(this, "全部循环", Toast.LENGTH_SHORT).show();
                        break;
                    case MusicPlayerService.ORDER_ALL:
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
}
