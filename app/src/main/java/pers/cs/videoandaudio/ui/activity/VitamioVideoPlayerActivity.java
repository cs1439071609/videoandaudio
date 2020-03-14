package pers.cs.videoandaudio.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.bean.VideoItem;
import pers.cs.videoandaudio.ui.View.VideoViewVitamio;
import pers.cs.videoandaudio.utils.NetUtil;
import pers.cs.videoandaudio.utils.TimeUtil;

public class VitamioVideoPlayerActivity extends AppCompatActivity {

    private static final String TAG = VitamioVideoPlayerActivity.class.getSimpleName();

    @BindView(R.id.tv_controller_name)
    TextView tvControllerName;
    @BindView(R.id.img_controller_battery)
    ImageView imgControllerBattery;
    @BindView(R.id.tv_controller_system_time)
    TextView tvControllerSystemTime;

    //音量
    @BindView(R.id.btn_controller_voice)
    ImageView btnControllerVoice;
    @BindView(R.id.change_volume_progress)
    ProgressBar pbVoice;

    @BindView(R.id.change_brightness_progress)
    ProgressBar pbBrightness;

    @BindView(R.id.btn_controller_switch)
    ImageView btnControllerSwitch;

    @BindView(R.id.tv_controller_current)
    TextView tvControllerCurrent;
    @BindView(R.id.seekbar_video)
    SeekBar seekbarVideo;
    @BindView(R.id.tv_controller_time)
    TextView tvControllerTime;


    //image退出
    @BindView(R.id.btn_controller_exit)
    ImageView btnControllerExit;
    @BindView(R.id.btn_controller_pre)
    ImageView btnControllerPre;
    @BindView(R.id.btn_controller_pause)
    ImageView btnControllerPause;
    @BindView(R.id.btn_controller_next)
    ImageView btnControllerNext;
    @BindView(R.id.btn_controller_full)
    ImageView btnControllerFull;

    private VideoViewVitamio video_view;

    private LinearLayout media_controller1;
    private LinearLayout media_controller2;

    private LinearLayout media_brightness;
    private LinearLayout media_volume;

    private LinearLayout media_buffer;
    private TextView tv_media_buffer;
    private ProgressBar pb_loading_qq;
    private ProgressBar pb_loading_ring;



    private TimeUtil mTimeUtil;
    private NetUtil mNetUtil;

    private Uri uri;
    private boolean isNetVideo;

    //是否使用系统监听缓冲
    private boolean isUseSystemBuffer = true ;


    //得到本地视频列表
    private List<VideoItem> mItemList;
    //得到当前播放视频在本地视频中位置
    private int position;

    //更新播放进度消息
    private static final int PROGRESS = 0;
    //隐藏控制栏消息
    private static final int HIDECONTROLLER = 1;
    //延迟隐藏控制栏
    private static final int HIDECONTROLLERDELAY = 3000;
    //更新网速
    private static final int UPDATENETSPEED = 2;
    //更新网速延迟
    private static final int UPDATENETSPEEDDELAY = 2000;
    //隐藏音量
    private static final int HIDEVOLUME = 3;



    //监听电量的广播
    private MyReceiver mMyReceiver;

    //手势识别器
    private GestureDetector mGestureDetector;

    //是否显示控制栏
    private boolean isShowMediaController;

    //是否全屏
    private boolean isFullScreen = false;

    //屏幕宽和高
    private int screenWidth;
    private int screenHeight;
    //视频原宽和高
    private int videoWidth;
    private int videoHeight;

    //屏幕尺寸常量
    private static final int SCREEN_FULL = 1;
    private static final int SCREEN_DEFAULT = 2;


    private AudioManager mAudioManager;
    //当前音量
    private int currentVolume;
    //最大音量
    private int maxVolume;
    //是否静音
    private boolean isMute = false;

    int currentPosition,prePosition;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case UPDATENETSPEED:
                    String speed = mNetUtil.getNetSpeed(VitamioVideoPlayerActivity.this);
                    tv_media_buffer.setText("缓冲中..." + speed);
                    mHandler.removeMessages(UPDATENETSPEED);
                    mHandler.sendEmptyMessageDelayed(UPDATENETSPEED,UPDATENETSPEEDDELAY);
                    break;
                case PROGRESS:

                    //得到当前视频进度
                    int currentPosition = (int) video_view.getCurrentPosition();
                    //设置SeekBar
                    seekbarVideo.setProgress(currentPosition);
                    tvControllerCurrent.setText(mTimeUtil.formatTime(currentPosition));

                    tvControllerSystemTime.setText(getSystemTime());


                    if(isNetVideo){
                        int buffer = video_view.getBufferPercentage();//0~100
                        int secondary = buffer * seekbarVideo.getMax() / 100;
                        Log.e(TAG, "handleMessage：网络视频， "+buffer);

                        seekbarVideo.setSecondaryProgress(secondary);
                    }else{
                        seekbarVideo.setSecondaryProgress(0);
                    }



                    if(!isUseSystemBuffer){
                        if(video_view.isPlaying()){
                            if(currentPosition - prePosition < 500){
                                media_buffer.setVisibility(View.VISIBLE);
                                pb_loading_qq.setVisibility(View.GONE);
                                pb_loading_ring.setVisibility(View.VISIBLE);
                                Log.e(TAG, "handleMessage: "+"用户自定义缓冲");
                            }else{
                                media_buffer.setVisibility(View.GONE);
                            }
                            prePosition = currentPosition;
                        }else{
                            media_buffer.setVisibility(View.GONE);
                        }

                    }else{
                        //防止使用系统缓冲时收到消息缓冲View还存在
                        media_buffer.setVisibility(View.GONE);
                    }




                    //每秒更新一次
                    //先移除消息
                    //注意：不能使用removeCallbackAndMessage，此方法移除所有消息，以后再也不发
                    mHandler.removeMessages(PROGRESS);
                    mHandler.sendEmptyMessageDelayed(PROGRESS, 1000);
                    Log.e(TAG, "handleMessage: " + "还在发啊" + mTimeUtil.formatTime(currentPosition));

                    break;
                case HIDECONTROLLER:
                    hideMediaController();
                    break;
                case HIDEVOLUME:
                    media_volume.setVisibility(View.GONE);
                    break;
            }
        }
    };


    private String getSystemTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Calendar calendar = Calendar.getInstance();
        return sdf.format(calendar.getTime());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //先初始化父类
        super.onCreate(savedInstanceState);
        //必须写这个，初始化加载库文件
        Vitamio.isInitialized(getApplicationContext());
        //设置视频解码监听
//        if (!LibsChecker.checkVitamioLibs(this))
//            return;
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_vitamio_video_player);
        ButterKnife.bind(this);

        Log.e(TAG, "onCreate: "+"万能播放器");
        initView();
        setListener();

        initData();

        setVideoView();

    }

    private void initView() {
        video_view = findViewById(R.id.videoview);

        media_controller1 = findViewById(R.id.media_controller1);
        media_controller2 = findViewById(R.id.media_controller2);
        media_buffer = findViewById(R.id.media_loading);
        tv_media_buffer = findViewById(R.id.tv_media_buffer);
        pb_loading_qq = findViewById(R.id.pb_loading_qq);
        pb_loading_ring = findViewById(R.id.pb_loading_ring);

        media_brightness = findViewById(R.id.media_brightness);
        media_volume = findViewById(R.id.media_volume);



        hideMediaController();

        media_buffer.setVisibility(View.GONE);


    }

    private void setListener() {
        //准备好的监听
        video_view.setOnPreparedListener(new MyOnPreparedListener());

        //播放出错的监听
        video_view.setOnErrorListener(new MyOnErrorListener());

        //播放完成的监听
        video_view.setOnCompletionListener(new MyOnCompletionListener());

        seekbarVideo.setOnSeekBarChangeListener(new VideoOnSeekBarChangeListener());

//        pbVoice.setOnSeekBarChangeListener(new VoiceOnSeekBarChangeListener());

        if(isUseSystemBuffer){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                video_view.setOnInfoListener(new MyOnInfoListener());
            }
        }else{
            mHandler.sendEmptyMessage(UPDATENETSPEED);
        }

    }

    class MyOnInfoListener implements MediaPlayer.OnInfoListener{

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {

            switch (what){
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    media_buffer.setVisibility(View.VISIBLE);
                    pb_loading_qq.setVisibility(View.GONE);
                    pb_loading_ring.setVisibility(View.VISIBLE);

                    Log.e(TAG, "onInfo: "+"系统");
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    media_buffer.setVisibility(View.GONE);
                    break;
            }
            return true;
        }
    }

    class VoiceOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                if (progress > 0) {
                    isMute = false;
                } else {
                    isMute = true;
                }
                //flags为1显示系统音量条
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                currentVolume = progress;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mHandler.removeMessages(HIDECONTROLLER);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mHandler.sendEmptyMessageDelayed(HIDECONTROLLER, HIDECONTROLLERDELAY);
        }
    }

    class VideoOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        /**
         * @param seekBar
         * @param progress
         * @param fromUser 如果改变是由用户引起，为true
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            if (fromUser) {
                video_view.seekTo(progress);
            }
        }

        //当手指触碰的时候回调此方法
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

            mHandler.removeMessages(HIDECONTROLLER);
        }

        //当手指离开的时候回调此方法
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

            mHandler.sendEmptyMessageDelayed(HIDECONTROLLER, HIDECONTROLLERDELAY);
        }
    }

    private void setVideoView() {
        //设置系统控制面板
        //video_view.setMediaController(new MediaController(this));
    }

    @OnClick({R.id.btn_controller_switch, R.id.btn_controller_exit, R.id.btn_controller_pre, R.id.btn_controller_pause, R.id.btn_controller_next, R.id.btn_controller_full})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_controller_switch:
                
                switchSystemPlayer();
                
                break;
            case R.id.btn_controller_exit:
                finish();
                break;
            case R.id.btn_controller_pre:

                playPreVideo();
                break;
            case R.id.btn_controller_pause:

                startAndPause();

                break;
            case R.id.btn_controller_next:

                playNextVideo();

                break;
            case R.id.btn_controller_full:
                setFullScreenOrDefault();
                break;
        }

        mHandler.removeMessages(HIDECONTROLLER);
        mHandler.sendEmptyMessageDelayed(HIDECONTROLLER, HIDECONTROLLERDELAY);

    }

    private void switchSystemPlayer() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("万能播放器提醒你");
        builder.setMessage("当有花屏时可以切换到系统播放器");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startSystemVideoPlayer();
            }
        });
        builder.setNegativeButton("取消",null);
        builder.show();
    }

    private void startSystemVideoPlayer() {
        Log.e(TAG, "startSystemVideoPlayer: "+"启动系统播放器");
        if(video_view != null){
            video_view.stopPlayback();
        }
        Intent intent = new Intent(this,SystemVideoPlayerActivity.class);

        if(mItemList != null && mItemList.size() >0){
            //传递视频列表
            Bundle bundle = new Bundle();
            bundle.putSerializable("valist", (Serializable) mItemList);

            intent.putExtras(bundle);

            //视频的列表中的某条位置
            intent.putExtra("position",position);


        }else if(uri != null){
            intent.setData(uri);
        }

        startActivity(intent);

        finish();
    }

    private void setFullScreenOrDefault() {
        if (isFullScreen) {
            setVideoType(SCREEN_DEFAULT);
        } else {
            setVideoType(SCREEN_FULL);
        }
    }

    private void startAndPause() {
        if (video_view.isPlaying()) {
            video_view.pause();

            btnControllerPause.setImageResource(R.drawable.ic_player_start);
        } else {
            video_view.start();

            btnControllerPause.setImageResource(R.drawable.ic_player_pause);

        }
    }

    /**
     * 只有本地列表才有上一个下一个。
     * 从其他软件跳转不能播上一个下一个
     */
    private void playPreVideo() {
        if (mItemList != null && mItemList.size() > 0) {
            position--;
            if (position >= 0) {
                media_buffer.setVisibility(View.VISIBLE);
                pb_loading_qq.setVisibility(View.VISIBLE);
                pb_loading_ring.setVisibility(View.GONE);

                VideoItem videoItem = mItemList.get(position);
                tvControllerName.setText(videoItem.getName());
                video_view.setVideoPath(videoItem.getData());
                isNetVideo = mNetUtil.isNetVideo(videoItem.getData());
            }
        }
        setButtonState();
    }

    private void playNextVideo() {
        if (mItemList != null && mItemList.size() > 0) {
            position++;
            if (position < mItemList.size()) {
                media_buffer.setVisibility(View.VISIBLE);
                pb_loading_qq.setVisibility(View.VISIBLE);
                pb_loading_ring.setVisibility(View.GONE);

                VideoItem videoItem = mItemList.get(position);
                tvControllerName.setText(videoItem.getName());
                video_view.setVideoPath(videoItem.getData());
                isNetVideo = mNetUtil.isNetVideo(videoItem.getData());
            }
        }
        setButtonState();
    }

    private void setButtonState() {
        if (mItemList != null && mItemList.size() > 0) {
            if (mItemList.size() == 1) {
                setButtonEnabled(false, false);
            } else {
                if (position == 0) {
                    setButtonEnabled(false, true);
                } else if (position == mItemList.size() - 1) {
                    setButtonEnabled(true, false);
                } else {
                    setButtonEnabled(true, true);
                }
            }
        } else {
            setButtonEnabled(false, false);
        }
    }

    private void setButtonEnabled(boolean isPreEnabled, boolean isNextEnabled) {

        if (isPreEnabled) {
            btnControllerPre.setBackgroundResource(R.drawable.ic_button_prev);
            btnControllerPre.setEnabled(true);
        } else {
            btnControllerPre.setEnabled(false);
        }

        if (isNextEnabled) {
            btnControllerNext.setBackgroundResource(R.drawable.ic_button_next);
            btnControllerNext.setEnabled(true);
        } else {
            btnControllerNext.setEnabled(false);
        }
    }


    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            playNextVideo();
        }
    }

    class MyOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            /**
             * 1.播放视频格式不支持。 --利用万能播放器
             * 2.播放网络视频时，网络中断。
             * ----第一种，如果网络确实断了，提示。
             * ----第二种，如果网络断断续续，重新播放，3次不行提示。
             * 3.播放的时候本地文件中间有空白。
             */

            showErrorDialog();
            //播放异常，则停止播放，防止弹窗使界面阻塞
            video_view.stopPlayback();
            Toast.makeText(VitamioVideoPlayerActivity.this, "bobobo", Toast.LENGTH_SHORT).show();
            //返回true
            return true;
        }
    }

    private void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("抱歉，无法播放该视频");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

        //当底层解码准备好回调
        @Override
        public void onPrepared(MediaPlayer mp) {
            Log.e(TAG, "onPrepared: "+"ssssssssssss");
            mp.setPlaybackSpeed(1.0f);
            videoWidth = mp.getVideoWidth();
            videoHeight = mp.getVideoHeight();

            video_view.start();

            //或使用mp.getDuration()
            int duration = (int) video_view.getDuration();
            //设置视频总长度
            seekbarVideo.setMax(duration);
            tvControllerTime.setText(mTimeUtil.formatTime(duration));

            mHandler.sendEmptyMessage(PROGRESS);

            setVideoType(SCREEN_DEFAULT);

            media_buffer.setVisibility(View.GONE);

            //拖动完成的监听
//            mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
//                @Override
//                public void onSeekComplete(MediaPlayer mp) {
//
//                }
//            });
        }
    }

    private void setVideoType(int type) {

        switch (type) {
            case SCREEN_FULL:
                //1.设置屏幕大小
                video_view.setVideoSize(screenWidth, screenHeight);
                //2.设置按钮状态
                btnControllerFull.setImageResource(R.drawable.ic_zoom_original);

                isFullScreen = true;
                break;

            case SCREEN_DEFAULT:
                int mVideoWidth = videoWidth;
                int mVideoHeight = videoHeight;
                int width = screenWidth;
                int height = screenHeight;

                //当视频宽与屏幕宽比值<视频高与屏幕高比值
                //将视频显示高为屏幕高，视频显示宽：屏幕宽=视频原高：屏幕高
                if (mVideoWidth * height < width * mVideoHeight) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }

                video_view.setVideoSize(width, height);
                btnControllerFull.setImageResource(R.drawable.ic_zoom_stretch);
                isFullScreen = false;
                break;
        }
    }


    private void getUri() {
        //从第三方软件传过来需要
        uri = getIntent().getData();

        //获取软件列表和位置
        mItemList = (List<VideoItem>) getIntent().getSerializableExtra("valist");
        position = getIntent().getIntExtra("position", 0);

        isNetVideo = false;

        if (mItemList != null && mItemList.size() > 0) {
            VideoItem videoItem = mItemList.get(position);
            tvControllerName.setText(videoItem.getName());
            video_view.setVideoPath(videoItem.getData());
            isNetVideo = mNetUtil.isNetVideo(videoItem.getData());

        } else if (uri != null) {
            tvControllerName.setText(uri.toString());
            isNetVideo = mNetUtil.isNetVideo(uri.toString());
            video_view.setVideoURI(uri);
        } else {
            Toast.makeText(this, "播放出错", Toast.LENGTH_SHORT).show();
        }
        video_view.requestFocus();
        setButtonState();
    }

    private void setBattery(int level) {

        if (level <= 10) {
            imgControllerBattery.setImageResource(R.drawable.battery_10);
        } else if (level <= 20) {
            imgControllerBattery.setImageResource(R.drawable.battery_20);
        } else if (level <= 50) {
            imgControllerBattery.setImageResource(R.drawable.battery_50);
        } else if (level <= 80) {
            imgControllerBattery.setImageResource(R.drawable.battery_80);
        } else if (level <= 100) {
            imgControllerBattery.setImageResource(R.drawable.battery_100);
        } else {
            imgControllerBattery.setImageResource(R.drawable.battery_100);
        }

    }


    private void showMediaController() {

        media_controller1.setVisibility(View.VISIBLE);
        media_controller2.setVisibility(View.VISIBLE);
        isShowMediaController = true;
    }

    private void hideMediaController() {
        media_controller1.setVisibility(View.GONE);
        media_controller2.setVisibility(View.GONE);

        isShowMediaController = false;
    }

    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //主线程
            int level = intent.getIntExtra("level", 0);
            setBattery(level);
        }
    }


    private void initData() {

        mTimeUtil = new TimeUtil();
        mNetUtil = new NetUtil();

        //得到屏幕宽和高
        //过时方法
        //        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        //        screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        //得到当前音量和最大音量
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        pbVoice.setMax(maxVolume);
        pbVoice.setProgress(currentVolume);

        getUri();

        mMyReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        //注册电量广播
        registerReceiver(mMyReceiver, intentFilter);

        //实例化手势识别器
        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                startAndPause();
            }


            //onDoubleTapEvent双击时触发两次
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                setFullScreenOrDefault();
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (isShowMediaController) {

                    hideMediaController();
                    mHandler.removeMessages(HIDECONTROLLER);
                } else {

                    showMediaController();
                    mHandler.sendEmptyMessageDelayed(HIDECONTROLLER, HIDECONTROLLERDELAY);
                }

                return super.onSingleTapConfirmed(e);
            }


        });

    }



    /**
     * 释放资源的时候先释放子类，再释放父类
     * 所以后super
     */
    @Override
    protected void onDestroy() {
        if (mMyReceiver != null) {
            unregisterReceiver(mMyReceiver);
            mMyReceiver = null;
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }

        super.onDestroy();

    }


    private float startX, startY;
    private float stopX,stopY;
    //滚动总距离-屏幕高
    private float touchRang;
    //滚动距离
    private float distanceY;
    //滚动开始时的音量。不要用currentVolume，因为滚动过程中此音量不断变化
    private int mScrollStartVolume;
    private static final int THRESHOLD = 10;
    //滑动开始屏幕亮度
    private float mGestureDownBrightness;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //将事件传递给手势识别器
        mGestureDetector.onTouchEvent(event);

        stopX = event.getX();
        stopY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                //                touchRang = screenWidth < screenHeight ? screenWidth :screenHeight;
                touchRang = Math.min(screenWidth, screenHeight);
                //mScrollStartVolume = currentVolume;
                mScrollStartVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                mGestureDownBrightness = getWindow().getAttributes().screenBrightness;
                if (mGestureDownBrightness == -1) {
                    try {
                        mGestureDownBrightness = Settings.System.getFloat(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS) / 255;
                    } catch (Settings.SettingNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                Log.e(TAG, "onTouchEvent: "+mGestureDownBrightness);
//                mHandler.removeMessages(HIDECONTROLLER);
                break;
            case MotionEvent.ACTION_MOVE:

                distanceY = startY - stopY;

                if(Math.abs(distanceY) >= THRESHOLD){
                    if(stopX > screenWidth/2){
                        //改变声音
                        media_volume.setVisibility(View.VISIBLE);

                        float change = distanceY / touchRang * maxVolume;

                        //音量应该大于0小于maxVolume
                        currentVolume = Math.min(maxVolume, Math.max(0, (int) (mScrollStartVolume + change)));

                        if (change != 0) {
                            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
                            pbVoice.setProgress(currentVolume);
                        }

                    }else{
                        //改变亮度
                        media_brightness.setVisibility(View.VISIBLE);

                        float deltaY = distanceY;

                        float deltaBrightness = deltaY * 3 / touchRang;
                        float newBrightness = mGestureDownBrightness + deltaBrightness;
                        newBrightness = Math.max(0, Math.min(newBrightness, 1));
                        float newBrightnessPercentage = newBrightness;
                        WindowManager.LayoutParams params = getWindow().getAttributes();
                        params.screenBrightness = newBrightnessPercentage;
                        getWindow().setAttributes(params);

                        int newBrightnessProgress = (int) (100f * newBrightnessPercentage);
                        pbBrightness.setProgress(newBrightnessProgress);
                        Log.e(TAG, "onTouchEvent: "+newBrightnessProgress);


                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                media_volume.setVisibility(View.GONE);
                media_brightness.setVisibility(View.GONE);
//                mHandler.sendEmptyMessageDelayed(HIDECONTROLLER, HIDECONTROLLERDELAY);
                break;

        }


        return super.onTouchEvent(event);
    }
    /**
     * 设置屏幕亮度
     * 0 最暗
     * 1 最亮
     */
    public void setBrightness(float brightness) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = lp.screenBrightness + brightness / 255.0f;
        if (lp.screenBrightness > 1) {
            lp.screenBrightness = 1;

            //startVibrator();


        } else if (lp.screenBrightness < 0.1) {
            lp.screenBrightness = (float) 0.1;
            //startVibrator();
        }
        getWindow().setAttributes(lp);

        //        float sb = lp.screenBrightness;
        //        brightnessTextView.setText((int) Math.ceil(sb * 100) + "%");
    }

    private void startVibrator() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        long[] pattern = { 10, 200 }; // OFF/ON/OFF/ON...
        vibrator.vibrate(pattern, -1);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        //监听物理按键音量增减
        if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
            media_volume.setVisibility(View.VISIBLE);

            currentVolume--;
            currentVolume = currentVolume > 0 ? currentVolume : 0;
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
            pbVoice.setProgress(currentVolume);
            if (currentVolume == 0) {
                isMute = true;
            } else {
                isMute = false;
            }

            mHandler.removeMessages(HIDEVOLUME);
            mHandler.sendEmptyMessageDelayed(HIDEVOLUME,HIDECONTROLLERDELAY);
            return true;
        }else if(keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            media_volume.setVisibility(View.VISIBLE);

            currentVolume++;
            currentVolume = currentVolume < maxVolume ? currentVolume : maxVolume;
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
            pbVoice.setProgress(currentVolume);

            mHandler.removeMessages(HIDEVOLUME);
            mHandler.sendEmptyMessageDelayed(HIDEVOLUME,HIDECONTROLLERDELAY);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
