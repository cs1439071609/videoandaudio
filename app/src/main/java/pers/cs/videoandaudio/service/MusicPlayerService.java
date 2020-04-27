package pers.cs.videoandaudio.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pers.cs.videoandaudio.IMusicPlayerService;
import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.info.MusicInfo;
import pers.cs.videoandaudio.ui.activity.AudioPlayer1Activity;
import pers.cs.videoandaudio.utils.CacheUtils;
import pers.cs.videoandaudio.utils.MusicUtils;

public class MusicPlayerService extends Service {

    private static final String TAG = MusicPlayerService.class.getSimpleName();
    private boolean DEBUG =  true;


    public static final String OPENMUSIC = "pers.cs.videoandaudio_OPENMUSIC";
    private NotificationManager notificationManager;

    private MediaPlayer mMediaPlayer;

//    private List<AudioItem> mAudioItems;
    private List<MusicInfo> mAudioItems;

    private int position = -1;
//    private AudioItem mAudioItem;
    private MusicInfo mAudioItem;




    public static final int ORDER_NORMAL = 1;
    public static final int ORDER_SINGLE = 2;
    public static final int ORDER_ALL = 3;
    public static final int ORDER_RANDOM = 4;
    private int playMode = ORDER_NORMAL;
    private AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener = null;


    @Override
    public void onCreate() {
        super.onCreate();

        //或者在getPlayMode()中直接返回
        playMode = CacheUtils.getInt(this,"AudioMode",ORDER_NORMAL);

        //加载音乐列表
        getDataFromLocal();
        //Build.VERSION.SDK_INT表示当前SDK的版本，Build.VERSION_CODES.ECLAIR_MR1为SDK 7版本 ，
        //因为AudioManager.OnAudioFocusChangeListener在SDK8版本开始才有。
//        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.ECLAIR_MR1){
//            mAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
//                @Override
//                public void onAudioFocusChange(int focusChange) {
//                    if(focusChange == AudioManager.AUDIOFOCUS_LOSS){
//                        //失去焦点之后的操作
//                        if(isPlaying()){
//                            pause();
//                        }
//                    }else if(focusChange == AudioManager.AUDIOFOCUS_GAIN){
//                        //获得焦点之后的操作
//                    }
//                }
//            };
//            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//            am.requestAudioFocus(mAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
//
//        }

    }

    IMusicPlayerService.Stub mStub = new IMusicPlayerService.Stub() {

        MusicPlayerService service = MusicPlayerService.this;

        @Override
        public void openAudio(int position) throws RemoteException {

//            positionS = position;
//            Log.d(TAG, "openAudio: "+"hhh"+positionS);
            service.openAudio(position);
        }

        @Override
        public void start() throws RemoteException {

            service.start();
        }

        @Override
        public void pause() throws RemoteException {
            service.pause();
        }

        @Override
        public void stop() throws RemoteException {
            service.stop();
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            return service.getCurrentPosition();
        }

        @Override
        public int getDuration() throws RemoteException {
            return service.getDuration();
        }

        @Override
        public String getName() throws RemoteException {
            return service.getName();
        }

        @Override
        public String getArtist() throws RemoteException {
            return service.getArtist();
        }

        @Override
        public String getAudioPath() throws RemoteException {
            return service.getAudioPath();
        }

        @Override
        public void next() throws RemoteException {
            service.next();
        }

        @Override
        public void pre() throws RemoteException {
            service.pre();
        }

        @Override
        public void setPlayMode(int playMode) throws RemoteException {
            service.setPlayMode(playMode);
        }

        @Override
        public int getPlayMode() throws RemoteException {
            return service.getPlayMode();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return service.isPlaying();
        }

        @Override
        public void seekTo(int position) throws RemoteException {
            mMediaPlayer.seekTo(position);
        }

        @Override
        public int getPosition() throws RemoteException {
            return position;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mStub;
    }

    private void getDataFromLocal() {
        mAudioItems = new ArrayList<>();
        new Thread() {
            @Override
            public void run() {
                super.run();
                //SystemClock.sleep(1000);
                //查询数据
//                ContentResolver resolver = getContentResolver();
////                Uri uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
//                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//                //注意：此处使用MediaStore.Video.VideoColumns而不是MediaStore.Video.Media
//                String keys[] = {
//                        MediaStore.Audio.AudioColumns.DISPLAY_NAME,
//                        MediaStore.Audio.AudioColumns.SIZE,
//                        MediaStore.Audio.AudioColumns.DURATION,
//                        MediaStore.Audio.AudioColumns.ARTIST,
//                        MediaStore.Audio.AudioColumns.DATA};//地址
//                Cursor cursor = resolver.query(uri, keys, null, null, null);
//                if (cursor != null) {
//                    while (cursor.moveToNext()) {
//                        AudioItem audioItem = new AudioItem(
//                                cursor.getString(0),
//                                cursor.getString(1),
//                                cursor.getString(2),
//                                cursor.getString(4));
//                        Log.d(TAG, "run: "+audioItem.getData());
//                        audioItem.setArtist(cursor.getString(3));
//                        mAudioItems.add(audioItem);
//                    }
//                    cursor.close();
//                }

                mAudioItems = MusicUtils.queryMusicInfo(MusicPlayerService.this,"",0);
            }
        }.start();
    }

    private void openAudio(int position){
        this.position = position;
        if(DEBUG){
            Log.d(TAG, "openAudio: "+position);
        }

        if(mAudioItems != null && mAudioItems.size() > 0){
            mAudioItem = mAudioItems.get(position);

            if(mMediaPlayer != null){

                //重置
                mMediaPlayer.reset();
                //释放
                //mMediaPlayer.release();
            }

            try {
                //不管为不为null都重新new
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setOnPreparedListener(new MyOnPreparedListener());
                mMediaPlayer.setOnErrorListener(new MyOnErrorListener());
                mMediaPlayer.setOnCompletionListener(new MyOnCompletionListener());
                mMediaPlayer.setDataSource(mAudioItem.getData());
                if(playMode == ORDER_SINGLE){
                    mMediaPlayer.setLooping(true);
                }else{
                    mMediaPlayer.setLooping(false);
                }

                mMediaPlayer.prepareAsync();



            } catch (IOException e) {
                e.printStackTrace();
            }

        }else{
            Toast.makeText(this, "还没有数据", Toast.LENGTH_SHORT).show();
        }


    }

    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener{

        @Override
        public void onCompletion(MediaPlayer mp) {
            switch (playMode) {
                case ORDER_NORMAL:
                    position++;
                    break;
                case ORDER_SINGLE:
                    break;
                case ORDER_ALL:
                    position++;
                    if (position >= mAudioItems.size()) {
                        position = 0;
                    }
                    break;
                case ORDER_RANDOM:
                    int n = new Random().nextInt(mAudioItems.size());
                    while(n == position){
                        n = new Random().nextInt(mAudioItems.size());
                    }
                    position = n;
                    break;
                default:
                    position++;
                    break;
            }
            openNextAudio();
        }
    }

    class MyOnErrorListener implements MediaPlayer.OnErrorListener{

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            next();
            return true;
        }
    }


    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener{

        @Override
        public void onPrepared(MediaPlayer mp) {
/*
            //使用广播通知Activity获取数据
            Intent intent = new Intent();
            intent.setAction(OPENMUSIC);
            sendBroadcast(intent);
*/
            start();
            //使用EventBus通知Activity获取数据
            EventBus.getDefault().post("");

        }
    }


    //使用@TargetApi annotaion， 使高版本API的代码在低版本SDK不报错
    @TargetApi(Build.VERSION_CODES.O)
    private void start(){
        mMediaPlayer.start();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //解决Android 8.0以上版本的Notification不显示问题
        String channelId ="my channel"; //通知id
        String name="渠道名字";
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel notificationChannel=new NotificationChannel(channelId,name,NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Intent intent = new Intent(this, AudioPlayer1Activity.class);
        intent.putExtra("notification",true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,2,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        //注意；此处用到channelId
        Notification notification = new Notification.Builder(this,channelId)
                .setSmallIcon(R.drawable.notification_music_playing)
                .setContentIntent(pendingIntent)
                .setContentTitle("手机影音")
                .setContentText("正在播放：" + getName())
                .setOngoing(true) //设置通知栏不能清除
                .build();
        notificationManager.notify(1,notification);

    }

    private void pause(){
        mMediaPlayer.pause();

//        notificationManager.cancel(1);
    }

    private void stop(){

    }

    private int getCurrentPosition(){
        return mMediaPlayer.getCurrentPosition();
    }

    private int getDuration(){

        return mMediaPlayer.getDuration();
    }

    private String getName(){
        return mAudioItem.getMusicName();
    }

    private String getArtist(){
        return mAudioItem.getArtist();
    }

    private String getAudioPath(){
        return mAudioItem.getData();
    }

    private void next(){
        //根据播放模式，设置下一个位置
        setNextPosition();

        //根据当前的播放模式和下标位置去播放音频
        openNextAudio();
    }

    private void openNextAudio() {
        switch (playMode){
            case ORDER_NORMAL:
                //顺序播放，最后一个播完停止
                if(position < mAudioItems.size()){
                    openAudio(position);
                }else{
                    position = mAudioItems.size() - 1;
                }
                break;
            case ORDER_SINGLE:
            case ORDER_ALL:
            case ORDER_RANDOM:
                openAudio(position);
                break;
            default:
                if(position < mAudioItems.size()){
                    openAudio(position);
                }else{
                    position = mAudioItems.size() - 1;
                }
                break;
        }
    }

    private void setNextPosition() {
        switch (playMode){
            case ORDER_NORMAL:
                position++;
                break;
            case ORDER_SINGLE:
            case ORDER_ALL:
            case ORDER_RANDOM:
                position++;
                if(position >= mAudioItems.size()){
                    position = 0;
                }
                break;
            default:
                position++;
                break;
        }
    }

    private void pre(){
        //根据播放模式，设置上一个位置
        setPrePosition();

        //根据当前的播放模式和下标位置去播放音频
        openPreAudio();
    }

    private void openPreAudio() {
        switch (playMode){
            case ORDER_NORMAL:
                //顺序播放，最后一个播完停止
                if(position >= 0){
                    openAudio(position);
                }else{
                    position = 0;
                }
                break;
            case ORDER_SINGLE:
            case ORDER_ALL:
            case ORDER_RANDOM:
                openAudio(position);
                break;
            default:
                if(position >= 0){
                    openAudio(position);
                }else{
                    position = 0;
                }
                break;
        }
    }

    private void setPrePosition() {
        switch (playMode){
            case ORDER_NORMAL:
                position--;
                break;
            case ORDER_SINGLE:
            case ORDER_ALL:
            case ORDER_RANDOM:
                position--;
                if(position < 0){
                    position = mAudioItems.size() - 1;
                }
                break;
            default:
                position--;
                break;
        }
    }

    private void setPlayMode(int playMode){
        this.playMode = playMode;
        if(playMode == ORDER_SINGLE){
            mMediaPlayer.setLooping(true);
        }else{
            mMediaPlayer.setLooping(false);
        }
        CacheUtils.putInt(this,"AudioMode",playMode);
    }

    private int getPlayMode(){
        return playMode;
//        return  CacheUtils.getInt(this,"AudioMode",ORDER_NORMAL);
    }

    private boolean isPlaying(){
        return mMediaPlayer.isPlaying();
    }

    @Override
    public void onDestroy() {
        if(mMediaPlayer != null){

            mMediaPlayer.stop();
            mMediaPlayer.release();
        }

        super.onDestroy();

    }
}
