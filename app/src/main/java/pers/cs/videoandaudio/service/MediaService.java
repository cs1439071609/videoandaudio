package pers.cs.videoandaudio.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.NotificationTarget;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;

import pers.cs.videoandaudio.IMediaAidlInterface;
import pers.cs.videoandaudio.MyApplication;
import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.downmusic.Down;
import pers.cs.videoandaudio.info.MusicInfo;
import pers.cs.videoandaudio.json.MusicFileDownInfo;
import pers.cs.videoandaudio.net.MA;
import pers.cs.videoandaudio.proxy.utils.MediaPlayerProxy;
import pers.cs.videoandaudio.utils.CacheUtils;
import pers.cs.videoandaudio.utils.OkHttpUtil;
import pers.cs.videoandaudio.utils.PreferencesUtility;

public class MediaService extends Service {
    
    private static final String TAG = MediaService.class.getSimpleName();
    private static final Boolean DEBUG = true;

    //外界传入 键
    public static final String CMDNAME = "command";
    //上一曲
    public static final String PREVIOUS_ACTION = "pers.cs.videoandaudio.previous";
    //强制上一曲
    public static final String PREVIOUS_FORCE_ACTION = "pers.cs.videoandaudio.previous.force";
    //暂停/播放 收到的，通知栏发来，
    public static final String TOGGLEPAUSE_ACTION = "pers.cs.videoandaudio.togglepause";
    //喜欢 mNotification
    public static final String LOVE_ACTION = "pers.cs.videoandaudio.love";


    public static final String REPEATMODE_CHANGED = "pers.cs.videoandaudio.repeatmodechanged";
    public static final String QUEUE_CHANGED = "pers.cs.videoandaudio.queuechanged";
    //进度改变
    public static final String POSITION_CHANGED = "pers.cs.videoandaudio.position_changed";
    //歌曲状态改变
    public static final String META_CHANGED = "pers.cs.videoandaudio.meta_changed";
    //歌曲切换
    public static final String MUSIC_CHANGED = "pers.cs.videoandaudio.change_music";
    //播放/暂停 发送到外界
    public static final String PLAYSTATE_CHANGED = "pers.cs.videoandaudio.playstatechanged";

    //暂停
    public static final String PAUSE_ACTION = "pers.cs.videoandaudio.pause";
    //关闭 mNotification
    public static final String STOP_ACTION = "pers.cs.videoandaudio.stop";

    //下一曲
    public static final String NEXT_ACTION = "pers.cs.videoandaudio.next";

    public static final String LRC_UPDATED = "pers.cs.videoandaudio.updatelrc";
    public static final String BUFFER_UP = "pers.cs.videoandaudio.bufferup";
    public static final String MUSIC_LODING = "pers.cs.videoandaudio.loading";
    public static final String REFRESH = "pers.cs.videoandaudio.refresh";

    public static final String LRC_PATH = "/csmusic/lrc/";

    private static final int LRC_DOWNLOADED = -10;
    private static final int FOCUSCHANGE = 5;
    //网络音乐结束标记
    private static final int TRACK_ENDED = 1;
    private static final int TRACK_LOCAL_ENDED = 2;

    //播放模式
    public static final int REPEAT_CURRENT = 1;
    public static final int REPEAT_ALL = 2;
    public static final int SHUFFLE_AUTO = 3;
    private int mRepeatMode = REPEAT_ALL;


    private NotificationManager mNotificationManager;
    private Notification mNotification;
    private int mNotificationId = 1000;

    private AudioManager mAudioManager;

    private MultiPlayer mPlayer;
    //是否正在播放
    private boolean mIsSupposedToBePlaying = false;
    //播放列表信息
    private HashMap<Long, MusicInfo> mPlaylistInfo = new HashMap<>();
    //播放列表id+索引
    private ArrayList<MusicTrack> mPlaylist = new ArrayList<MusicTrack>(100);
    //当前播放索引
    private int mPlayPos = -1;
    //下一首索引
    private int mNextPlayPos = -1;
    //历史记录
    private static LinkedList<Integer> mHistory = new LinkedList<>();


    //当前音乐游标，获取音乐信息
    private Cursor mCursor;
    /**
     * 表结构，本地和网络均为
     */
    private static final String[] PROJECTION = new String[]{
            "audio._id AS _id", MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.MIME_TYPE, MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST_ID
    };
    //内容观察者
    private ContentObserver mMediaStoreObserver;

    private final IBinder mBinder = new ServiceStub(this);

    private MediaPlayerProxy mProxy;

    private HandlerThread mHandlerThread;
    private MusicPlayerHandler mPlayerHandler;

    private int mServiceStartId = -1;


    private RequestLrc mRequestLrc;
    private RequestPlayUrl mRequestUrl;
    private static Handler mUrlHandler;
    private static Handler mLrcHandler;
    private Thread mLrcThread = new Thread(new Runnable() {
        @Override
        public void run() {
            Looper.prepare();
            mLrcHandler = new Handler();
            Looper.loop();
        }
    },"Lrc-Thread");
    private Thread mGetUrlThread = new Thread(new Runnable() {
        @Override
        public void run() {
            Looper.prepare();
            mUrlHandler = new Handler();
            Looper.loop();
        }
    },"Url-Thread");

    class RequestPlayUrl implements Runnable {
        private long id;
        private boolean play;
        private boolean stop;

        public RequestPlayUrl(long id, boolean play) {
            this.id = id;
            this.play = play;
        }
        public void stop() {
            stop = true;
        }
        @Override
        public void run() {
            //音乐的url可能会变。所以此处不用存储的
            String url = null;
//            url = PreferencesUtility.getInstance(MediaService.this).getPlayLink(id);
//            Log.d(TAG, "PreferencesUtility: "+url);

            if (url == null) {
                MusicFileDownInfo song = Down.getUrl(MediaService.this, id + "");
                if (song != null && song.getShow_link() != null) {
                    url = song.getShow_link();
                    PreferencesUtility.getInstance(MediaService.this).setPlayLink(id, url);
                }
            }
            if (url != null) {
                if(DEBUG){
                    Log.d(TAG,"current play url = :" +url);
                }
            } else {
                gotoNext(true);
            }
            if (!stop) {
                mPlayer.setDataSource(url);
//                startProxy();
//                String urlEn = url;
//                //进行缓存
//                //将链接先转为自己的，mediaplayer的setDataSource调用时转到MediaPlayerProxy
//                urlEn = mProxy.getProxyURL(urlEn);
//                mPlayer.setDataSource(urlEn);
            }

            if (play && !stop) {
                play();
            }

        }
    }
    class RequestLrc implements Runnable {

        private MusicInfo musicInfo;
        private boolean stop;

        RequestLrc(MusicInfo info) {
            this.musicInfo = info;
        }

        public void stop() {
            stop = true;
        }

        @Override
        public void run() {
            String url = null;
            Log.d(TAG, "run: "+(musicInfo == null));
            if (musicInfo != null && musicInfo.lrc != null) {
                //传递过来的MusicInfo中有歌词链接
                url = musicInfo.lrc;
                Log.d(TAG, "RequestLrc-musicInfo: "+url);
            }else{
                //传递过来的MusicInfo中没有歌词链接
                //进行查找
                try {
                    Log.d(TAG, "RequestLrc-: ");
                    JsonObject jsonObject = OkHttpUtil.getResposeJsonObject(MA.Search.searchLrcPic(musicInfo.musicName, musicInfo.artist));
                    JsonArray array = jsonObject.get("songinfo").getAsJsonArray();
                    int len = array.size();
                    url = null;
                    for (int i = 0; i < len; i++) {
                        url = array.get(i).getAsJsonObject().get("lrclink").getAsString();
                        if (url != null) {
                            break;
                        }
                    }
                    Log.d(TAG, "RequestLrc-netRequest: "+url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //判断是否停止进行
            if (!stop) {
                //下载歌词，并发送消息
                Log.d(TAG, "RequestLrc-run: "+musicInfo.songId+"----------"+url);
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + LRC_PATH + musicInfo.songId);
                String lrc = null;
                try {
                    lrc = OkHttpUtil.getResposeString(url);
                    if (lrc != null && !lrc.isEmpty()) {
                        if (!file.exists())
                            file.createNewFile();
                        writeToFile(file, lrc);
                        mPlayerHandler.sendEmptyMessage(LRC_DOWNLOADED);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private synchronized void writeToFile(File file, String lrc) {
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(lrc.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理一些内部控制事件,
     * 注意：不是主线程
     */
    private static final class MusicPlayerHandler extends Handler{
        private final WeakReference<MediaService> mService;

        private MusicPlayerHandler(final MediaService service, final Looper looper) {
            super(looper);
            mService = new WeakReference<MediaService>(service);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            final MediaService service = mService.get();
            if (service == null) {
                return;
            }
            //接受到消息，继续发出
            synchronized (service) {
                switch (msg.what){
                    case FOCUSCHANGE:
                        switch (msg.arg1) {
                            case AudioManager.AUDIOFOCUS_LOSS:
                            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
//                                if (service.isPlaying()) {
//                                    service.mPausedByTransientLossOfFocus =
//                                            msg.arg1 == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
//                                }
                                service.pause();
                                break;
                            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
//                                removeMessages(FADEUP);
//                                sendEmptyMessage(FADEDOWN);
                                break;
                            case AudioManager.AUDIOFOCUS_GAIN:
                                if (!service.isPlaying()){
//                                        && service.mPausedByTransientLossOfFocus) {
//                                    service.mPausedByTransientLossOfFocus = false;
//                                    mCurrentVolume = 0f;
//                                    service.mPlayer.setVolume(mCurrentVolume);
                                    service.play();
                                } else {
//                                    removeMessages(FADEDOWN);
//                                    sendEmptyMessage(FADEUP);
                                }
                                break;
                            default:
                        }
                        break;
                    case LRC_DOWNLOADED:
                        service.notifyChange(LRC_UPDATED);
                        break;
                    case TRACK_ENDED:
                        if (service.mRepeatMode == REPEAT_CURRENT) {
                            service.seek(0);
                            service.play();
                        } else {
                            service.gotoNext(false);
                        }
                        break;
                    case TRACK_LOCAL_ENDED:
                        service.setAndRecordPlayPos(service.mNextPlayPos);
                        service.closeCursor();
                        service.updateCursor(service.mPlaylist.get(service.mPlayPos).mId);
                        service.setNextTrack();
                        service.notifyChange(META_CHANGED);
                        service.notifyChange(MUSIC_CHANGED);
                        service.updateNotification();
                        break;

                }
            }
        }
    }

    //监听音频资源是否被抢占的监听器
    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            mPlayerHandler.obtainMessage(FOCUSCHANGE, focusChange, 0).sendToTarget();
        }
    };


    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String command = intent.getStringExtra(CMDNAME);
            handleCommandIntent(intent);

        }
    };
    /**
     * 处理外界发来的广播
     */
    private void handleCommandIntent(Intent intent) {
        final String action = intent.getAction();
        final String command = intent.getStringExtra(CMDNAME);
        if (MyApplication.DEBUG){
            Log.d(TAG, "handleCommandIntent: action = " + action + ", command = " + command);
        }

        if (PREVIOUS_ACTION.equals(action) || PREVIOUS_FORCE_ACTION.equals(action)) {
            Log.d(TAG, "handleCommandIntent: PREVIOUS_FORCE_ACTION");
            prev(PREVIOUS_FORCE_ACTION.equals(action));
        }else if (NEXT_ACTION.equals(action)) {
            gotoNext(true);
        }else if (TOGGLEPAUSE_ACTION.equals(action)) {
            if (isPlaying()) {
                pause();
            } else {
                play();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mServiceStartId = startId;

        if (intent != null) {
            handleCommandIntent(intent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void cancelNotification() {
        //mNotificationManager.cancel(hashCode());
        stopForeground(true);
        mNotificationManager.cancel(mNotificationId);
    }

    @SuppressLint("NewApi")
    private Notification getNotification() {
        final int LOVE_FLAG = 0x1;
        final int PREV_FLAG = 0x2;
        final int PAUSE_FLAG = 0x3;
        final int NEXT_FLAG = 0x4;
        final int STOP_FLAG = 0x5;


        final boolean isPlaying = isPlaying();

        if(mNotificationManager == null){
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        RemoteViews remoteViews = new RemoteViews(this.getPackageName(), R.layout.notification_music);
        remoteViews.setTextViewText(R.id.title, getTrackName());
        remoteViews.setTextViewText(R.id.text, getArtistName());

        //解决Android 8.0以上版本的Notification不显示问题
        String channelId ="my channel"; //通知id
        String name="渠道名字";
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel notificationChannel=new NotificationChannel(channelId,name,NotificationManager.IMPORTANCE_LOW);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        //此处action不能是一样的 如果一样的 接受的flag参数只是第一个设置的值
        Intent loveIntent = new Intent(LOVE_ACTION);
        loveIntent.putExtra("FLAG", LOVE_FLAG);
        PendingIntent lovePIntent = PendingIntent.getBroadcast(this, 0, loveIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.iv_pre, lovePIntent);

        Intent prevIntent = new Intent(PREVIOUS_ACTION);
        prevIntent.putExtra("FLAG", PREV_FLAG);
        PendingIntent prevPIntent = PendingIntent.getBroadcast(this, 0, prevIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.iv_pre, prevPIntent);


        Intent pauseIntent = new Intent(TOGGLEPAUSE_ACTION);
        pauseIntent.putExtra("FLAG", PAUSE_FLAG);
        PendingIntent pausePIntent = PendingIntent.getBroadcast(this, 0, pauseIntent, 0);
        remoteViews.setImageViewResource(R.id.iv_pause, isPlaying ? R.drawable.note_btn_pause : R.drawable.note_btn_play);
        remoteViews.setOnClickPendingIntent(R.id.iv_pause, pausePIntent);

        Intent nextIntent = new Intent(NEXT_ACTION);
        nextIntent.putExtra("FLAG", NEXT_FLAG);
        PendingIntent nextPIntent = PendingIntent.getBroadcast(this, 0, nextIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.iv_next, nextPIntent);

        Intent preIntent = new Intent(STOP_ACTION);
        preIntent.putExtra("FLAG", STOP_FLAG);
        PendingIntent prePIntent = PendingIntent.getBroadcast(this, 0, preIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.iv_stop, prePIntent);

        final Intent nowPlayingIntent = new Intent();
        //nowPlayingIntent.setAction("com.wm.remusic.LAUNCH_NOW_PLAYING_ACTION");
        nowPlayingIntent.setComponent(new ComponentName("pers.cs.videoandaudio", "pers.cs.videoandaudio.ui.activity.PlayingActivity"));
        nowPlayingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent clickIntent = PendingIntent.getBroadcast(this, 0, nowPlayingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent click = PendingIntent.getActivity(this, 0, nowPlayingIntent, PendingIntent.FLAG_UPDATE_CURRENT);



        if (mNotification == null) {
            mNotification = new Notification.Builder(this,channelId)
                    .setSmallIcon(R.drawable.notification_music_playing)
                    .setContentIntent(click)
                    .setContent(remoteViews)
                    .setOngoing(true) //设置通知栏不能清除
                    .build();
        }else{
            mNotification.contentView = remoteViews;
        }
        NotificationTarget notificationTarget = new NotificationTarget(this,
                R.id.image,//通知展示的xml对象实例
                remoteViews,//布局中ImageView的id
                mNotification,//通知的对象实例
                mNotificationId);//通知的对象实例对应的id
        Uri uri = null;
        if (getAlbumPath() != null) {
            try {
                uri = Uri.parse(getAlbumPath());
                Log.d(TAG, "getNotification: "+uri.toString());
                if(uri != null){
                    RequestOptions options = new RequestOptions()
                            .placeholder(R.mipmap.placeholder_disk_300)
                            .error(R.mipmap.placeholder_disk_300)
                            .diskCacheStrategy(DiskCacheStrategy.ALL);
                    Glide .with(this).asBitmap().load(uri)
                            .into( notificationTarget );
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return mNotification;
    }

    @Override
    public void onCreate() {
        if(DEBUG){
            Log.d(TAG,"onCreate:");
        }
        super.onCreate();
        //获取音乐链接消息队列
        mGetUrlThread.start();
        //获取歌词链接消息队列
        mLrcThread.start();
        //播放控制消息队列？
        mHandlerThread = new HandlerThread("MusicPlayerHandler",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();
        mPlayerHandler = new MusicPlayerHandler(this, mHandlerThread.getLooper());
        //缓存
        startProxy();

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        mPlayer = new MultiPlayer(this);
        mPlayer.setHandler(mPlayerHandler);

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Log.d(TAG, "onCreate: MusicRepeatMode:"+CacheUtils.getInt(this,"MusicRepeatMode",REPEAT_ALL));
        mRepeatMode = CacheUtils.getInt(this,"MusicRepeatMode",REPEAT_ALL);

        //mMediaStoreObserver = new MediaStoreObserver();
        //注册广播
        final IntentFilter filter = new IntentFilter();
        filter.addAction(TOGGLEPAUSE_ACTION);
        filter.addAction(LOVE_ACTION);
        filter.addAction(PAUSE_ACTION);
        filter.addAction(STOP_ACTION);
        filter.addAction(NEXT_ACTION);
        filter.addAction(PREVIOUS_ACTION);
        filter.addAction(PREVIOUS_FORCE_ACTION);
        registerReceiver(mIntentReceiver, filter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: ");
        stopSelf(mServiceStartId);
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {

        mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        cancelNotification();
        unregisterReceiver(mIntentReceiver);
        super.onDestroy();
    }

    private void open(HashMap<Long, MusicInfo> infos, long[] list, int position){
        synchronized (this) {
            mPlaylistInfo = infos;

            final int listlength = list.length;

            //判断是否为新队列
            boolean newlist = false;
            if (mPlaylist.size() == listlength) {
                for (int i = 0; i < listlength; i++) {
                    if (list[i] != mPlaylist.get(i).mId) {
                        newlist = true;
                        break;
                    }
                }
            }else{
                newlist = true;
            }
            Log.d(TAG, "open: "+newlist);
            //新队列则接替换当前队列，并通知
            if (newlist) {
                addToPlayList(list, -1);
                notifyChange(QUEUE_CHANGED);
            }

            if (position >= 0) {
                mPlayPos = position;
            }else{
                mPlayPos = 0;
            }
            Log.d(TAG, "open: ");
            openCurrentAndNextPlay(true);
            Log.d(TAG, "open: "+Thread.currentThread().getName());
        }
    }

    /**
     * 更新当前播放队列
     * @param list id数组
     * @param position 索引，-1则替换队列
     */
    private void addToPlayList(final long[] list, int position) {
        final int addlen = list.length;

        //小于0直接清空队列，替换为新队列
        if (position < 0) {
            mPlaylist.clear();
            position = 0;
        }

        mPlaylist.ensureCapacity(mPlaylist.size() + addlen);
        if (position > mPlaylist.size()) {
            position = mPlaylist.size();
        }

        final ArrayList<MusicTrack> arrayList = new ArrayList<MusicTrack>(addlen);
        for (int i = 0; i < list.length; i++) {
            //此处
            //arrayList.add(new MusicTrack(list[i], mPlaylist.size()+ i));
            arrayList.add(new MusicTrack(list[i], i));
        }

        mPlaylist.addAll(position, arrayList);

        if (mPlaylist.size() == 0) {
            closeCursor();
            notifyChange(META_CHANGED);
        }
    }

    /**
     *
     * @param play 是否准备下一个
     */
    private void openCurrentAndNextPlay(boolean play) {
        openCurrentAndMaybeNext(play, true);
    }

    private void getLrc(long id) {
        MusicInfo info = mPlaylistInfo.get(id);
        if (info == null) {
            return;
        }
        String lrc = Environment.getExternalStorageDirectory().getAbsolutePath() + LRC_PATH;
        File file = new File(lrc);
        if (!file.exists()) {
            //不存在就建立此目录
            boolean r = file.mkdirs();
        }
        file = new File(lrc + id);
        if (!file.exists()) {
            if (mRequestLrc != null) {
                mRequestLrc.stop();
                mLrcHandler.removeCallbacks(mRequestLrc);
            }
            Log.d(TAG, "getLrc: 请求歌词");
            mRequestLrc = new RequestLrc(info);
            mLrcHandler.postDelayed(mRequestLrc, 70);
        }
    }

    private void play() {
        play(true);
    }

    public void play(boolean createNewNextTrack) {
        int status = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (status != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return;
        }

        if (createNewNextTrack) {
            setNextTrack();
        } else {
            //设置下一曲为-1
            setNextTrack(mNextPlayPos);
        }
        setIsSupposedToBePlaying(true, true);
        mPlayer.start();
        Log.d(TAG, "play: "+Thread.currentThread().getName());

    }


    private void updateNotification() {
        Log.d(TAG, "updateNotification: "+Thread.currentThread().getName());
        startForeground(mNotificationId, getNotification());
//        mNotificationManager.notify(mNotificationId, getNotification());
    }
    /**
     * 设置是否正在播放
     * @param value
     * @param notify
     */
    private void setIsSupposedToBePlaying(boolean value, boolean notify) {
        Log.d(TAG, "setIsSupposedToBePlaying: ");
        if (mIsSupposedToBePlaying != value) {
            mIsSupposedToBePlaying = value;

            if (!mIsSupposedToBePlaying) {
//                scheduleDelayedShutdown();
//                mLastPlayedTime = System.currentTimeMillis();
            }

            if (notify) {
                notifyChange(META_CHANGED);
                notifyChange(PLAYSTATE_CHANGED);
            }
        }
    }

    private void pause() {
        Log.d(TAG, "pause: "+Thread.currentThread().getName());
        synchronized (this) {
            if (mIsSupposedToBePlaying) {
                mPlayer.pause();
                setIsSupposedToBePlaying(false, true);
            }
        }
    }

    /**
     * 播放下一首
     *
     * @param force
     */
    private void gotoNext(boolean force) {
        synchronized (this) {
            if (mPlaylist.size() <= 0) {
                return;
            }

            //设置
            int pos = mNextPlayPos;
            if (pos < 0) {
                pos = getNextPosition(force);
            }
            if (pos < 0) {
                setIsSupposedToBePlaying(false, true);
                return;
            }
            stop(false);
            //设置当前索引=pos
            setAndRecordPlayPos(pos);

            openCurrentAndNext();

            play();

        }
    }

    /**
     * 设置下一首索引为当前索引，并记录
     * @param nextPos
     */
    public void setAndRecordPlayPos(int nextPos) {
        synchronized (this) {

//            if (mShuffleMode != SHUFFLE_NONE) {
//                mHistory.add(mPlayPos);
//                if (mHistory.size() > MAX_HISTORY_SIZE) {
//                    mHistory.remove(0);
//                }
//            }
            mPlayPos = nextPos;
        }
    }

    private void openCurrent() {
        openCurrentAndMaybeNext(false, false);
    }

    private Map getPlayinfos() {
        synchronized (this) {
            return mPlaylistInfo;
        }
    }

    private boolean isPlaying() {
        return mIsSupposedToBePlaying;
    }

    /**
     * 当前播放队列，音乐id
     * @return
     */
    private long[] getQueue() {
        synchronized (this) {
            final int len = mPlaylist.size();
            final long[] list = new long[len];
            for (int i = 0; i < len; i++) {
                list[i] = mPlaylist.get(i).mId;
            }
            return list;
        }
    }

    /**
     * 根据位置获取音乐id
     * @param position
     * @return
     */
    private long getQueueItemAtPosition(int position) {
        synchronized (this) {
            if (position >= 0 && position < mPlaylist.size()) {
                return mPlaylist.get(position).mId;
            }
        }

        return -1;
    }

    /**
     * 当前播放列表大小
     * @return
     */
    public int getQueueSize() {
        synchronized (this) {
            return mPlaylist.size();
        }
    }

    private long duration() {
        if (mPlayer.isInitialized() && mPlayer.isTrackPrepared()) {
            return mPlayer.duration();
        }
        return -1;
    }

    private long position() {
        if (mPlayer.isInitialized() && mPlayer.isTrackPrepared()) {
            try {
                return mPlayer.position();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    private int getSecondPosition() {
        if (mPlayer.isInitialized()) {
            return mPlayer.sencondaryPosition;
        }
        return -1;
    }

    private long seek(long position) {
        if (mPlayer.isInitialized()) {
            if (position < 0) {
                position = 0;
            } else if (position > mPlayer.duration()) {
                position = mPlayer.duration();
            }
            long result = mPlayer.seek(position);
            notifyChange(POSITION_CHANGED);
            return result;
        }
        return -1;
    }
    private long getAudioId() {
        MusicTrack track = getCurrentTrack();
        if (track != null) {
            return track.mId;
        }
        return -1;
    }

    /**
     * 获取播放的MusicTrack
     * @return
     */
    private MusicTrack getCurrentTrack() {
        return getTrack(mPlayPos);
    }

    /**
     * 根据索引获取MusicTrack
     * @param index
     * @return
     */
    private synchronized MusicTrack getTrack(int index) {
        if (index >= 0 && index < mPlaylist.size()) {
            return mPlaylist.get(index);
        }
        return null;
    }

    private long getNextAudioId() {
        synchronized (this) {
            if (mNextPlayPos >= 0 && mNextPlayPos < mPlaylist.size() && mPlayer.isInitialized()) {
                return mPlaylist.get(mNextPlayPos).mId;
            }
        }
        return -1;
    }
    /**
     * 当前播放的是本地音乐？
     * @return
     */
    private boolean isTrackLocal() {
        synchronized (this) {
            MusicInfo info = mPlaylistInfo.get(getAudioId());
            if (info == null) {
                return true;
            }
            return info.islocal;
        }
    }
    /**
     * 当前播放索引
     * @return
     */
    private int getQueuePosition() {
        synchronized (this) {
            return mPlayPos;
        }
    }

    /**
     * 切换歌曲为同一队列不同位置
     *
     * @param index
     */
    private void setQueuePosition(int index) {
        synchronized (this) {
            stop(false);
            mPlayPos = index;
            openCurrentAndNext();
            play();
        }
    }

    private void openCurrentAndNext() {
        openCurrentAndMaybeNext(false, true);
    }


    private void stop(boolean goToIdle) {
        if (mPlayer.isInitialized()) {
            mPlayer.stop();
        }
        closeCursor();
    }

    private void closeCursor() {
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
    }
    private void setRepeatMode(int repeatmode) {
        synchronized (this) {
            mRepeatMode = repeatmode;
            setNextTrack();
            //saveQueue(false);
            notifyChange(REPEATMODE_CHANGED);
        }
    }
    private int getRepeatMode() {
        return mRepeatMode;
    }

    private void prev(boolean forcePrevious) {
        synchronized (this) {
            boolean goPrevious = getRepeatMode() != REPEAT_CURRENT || forcePrevious;
            if(goPrevious){
                int pos = getPreviousPlayPosition(true);
                Log.d(TAG, "prev: "+pos);
                if (pos < 0) {
                    return;
                }
                mNextPlayPos = mPlayPos;
                mPlayPos = pos;
                stop(false);
                openCurrent();
                play(false);
            } else {
                seek(0);
                //重新播放，前边已设置下次音乐，此次不设置
                play(false);
            }
        }
    }

    public int getPreviousPlayPosition(boolean removeFromHistory) {
        synchronized (this) {
            if (mRepeatMode == SHUFFLE_AUTO) {
                //随机播放，则从历史记录中取
                final int histsize = mHistory.size();
                if (histsize == 0) {
                    return -1;
                }
                final Integer pos = mHistory.get(histsize - 1);
                if (removeFromHistory) {
                    mHistory.remove(histsize - 1);
                }
                return pos.intValue();
            } else {
                if (mPlayPos > 0) {
                    return mPlayPos - 1;
                } else {
                    return mPlaylist.size() - 1;
                }
            }
        }
    }



    private static final class ServiceStub extends IMediaAidlInterface.Stub {

        private final WeakReference<MediaService> mService;

        public ServiceStub(MediaService mediaService) {
            mService = new WeakReference<>(mediaService);
        }

        @Override
        public void openFile(final String path) throws RemoteException {
            mService.get().openFile(path);
        }

        @Override
        public void open(final Map infos, final long[] list, final int position)
                throws RemoteException {
            mService.get().open((HashMap<Long, MusicInfo>) infos, list, position);
        }

        @Override
        public void stop() throws RemoteException {
            mService.get().stop();
        }

        @Override
        public void pause() throws RemoteException {
            mService.get().pause();
        }


        @Override
        public void play() throws RemoteException {
            mService.get().play();
        }

        @Override
        public void prev(boolean forcePrevious) throws RemoteException {
            mService.get().prev(forcePrevious);
        }

        @Override
        public void next() throws RemoteException {
            mService.get().gotoNext(true);
        }

        @Override
        public void enqueue(final long[] list, final Map infos, final int action)
                throws RemoteException {
            mService.get().enqueue(list, (HashMap<Long, MusicInfo>) infos, action);
        }

        @Override
        public Map getPlayinfos() throws RemoteException {
            return mService.get().getPlayinfos();
        }

        @Override
        public void moveQueueItem(final int from, final int to) throws RemoteException {
            mService.get().moveQueueItem(from, to);
        }

        @Override
        public void refresh() throws RemoteException {
            mService.get().refresh();
        }

        @Override
        public void playlistChanged() throws RemoteException {
            mService.get().playlistChanged();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return mService.get().isPlaying();
        }

        @Override
        public long[] getQueue() throws RemoteException {
            return mService.get().getQueue();
        }

        @Override
        public long getQueueItemAtPosition(int position) throws RemoteException {
            return mService.get().getQueueItemAtPosition(position);
        }

        @Override
        public int getQueueSize() throws RemoteException {
            return mService.get().getQueueSize();
        }

        @Override
        public int getQueueHistoryPosition(int position) throws RemoteException {
            return mService.get().getQueueHistoryPosition(position);
        }

        @Override
        public int getQueueHistorySize() throws RemoteException {
            return mService.get().getQueueHistorySize();
        }

        @Override
        public int[] getQueueHistoryList() throws RemoteException {
            return mService.get().getQueueHistoryList();
        }

        @Override
        public long duration() throws RemoteException {
            return mService.get().duration();
        }

        @Override
        public long position() throws RemoteException {
            return mService.get().position();
        }

        @Override
        public int secondPosition() throws RemoteException {
            return mService.get().getSecondPosition();
        }

        @Override
        public long seek(final long position) throws RemoteException {
            return mService.get().seek(position);
        }

        @Override
        public void seekRelative(final long deltaInMs) throws RemoteException {
            mService.get().seekRelative(deltaInMs);
        }

        @Override
        public long getAudioId() throws RemoteException {
            return mService.get().getAudioId();
        }

        @Override
        public MusicTrack getCurrentTrack() throws RemoteException {
            return mService.get().getCurrentTrack();
        }

        @Override
        public MusicTrack getTrack(int index) throws RemoteException {
            return mService.get().getTrack(index);
        }

        @Override
        public long getNextAudioId() throws RemoteException {
            return mService.get().getNextAudioId();
        }

        @Override
        public long getPreviousAudioId() throws RemoteException {
            return mService.get().getPreviousAudioId();
        }

        @Override
        public long getArtistId() throws RemoteException {
            return mService.get().getArtistId();
        }

        @Override
        public long getAlbumId() throws RemoteException {
            return mService.get().getAlbumId();
        }

        @Override
        public String getArtistName() throws RemoteException {
            return mService.get().getArtistName();
        }

        @Override
        public String getTrackName() throws RemoteException {
            return mService.get().getTrackName();
        }

        @Override
        public boolean isTrackLocal() throws RemoteException {
            return mService.get().isTrackLocal();
        }

        @Override
        public String getAlbumName() throws RemoteException {
            return mService.get().getAlbumName();
        }

        @Override
        public String getAlbumPath() throws RemoteException {
            return mService.get().getAlbumPath();
        }

        @Override
        public String[] getAlbumPathtAll() throws RemoteException {
            return mService.get().getAlbumPathAll();
        }

        @Override
        public String getPath() throws RemoteException {
            return mService.get().getPath();
        }

        @Override
        public int getQueuePosition() throws RemoteException {
            return mService.get().getQueuePosition();
        }

        @Override
        public void setQueuePosition(final int index) throws RemoteException {
            mService.get().setQueuePosition(index);
        }

        @Override
        public int getShuffleMode() throws RemoteException {
            return mService.get().getShuffleMode();
        }

        @Override
        public void setShuffleMode(final int shufflemode) throws RemoteException {
            mService.get().setShuffleMode(shufflemode);
        }

        @Override
        public int getRepeatMode() throws RemoteException {
            return mService.get().getRepeatMode();
        }

        @Override
        public void setRepeatMode(final int repeatmode) throws RemoteException {
            mService.get().setRepeatMode(repeatmode);
        }

        @Override
        public int removeTracks(final int first, final int last) throws RemoteException {
            return mService.get().removeTracks(first, last);
        }


        @Override
        public int removeTrack(final long id) throws RemoteException {
            return mService.get().removeTrack(id);
        }


        @Override
        public boolean removeTrackAtPosition(final long id, final int position)
                throws RemoteException {
            return mService.get().removeTrackAtPosition(id, position);
        }


        @Override
        public int getMediaMountedCount() throws RemoteException {
            return mService.get().getMediaMountedCount();
        }


        @Override
        public int getAudioSessionId() throws RemoteException {
            return mService.get().getAudioSessionId();
        }


        @Override
        public void setLockscreenAlbumArt(boolean enabled) {
            mService.get().setLockscreenAlbumArt(enabled);
        }

        @Override
        public void exit() throws RemoteException {
            mService.get().exit();
        }

        @Override
        public void timing(int time) throws RemoteException {
            mService.get().timing(time);
        }
    }
    //获取音乐信息
    //艺术家id
    private long getArtistId() {
        synchronized (this) {
            if (mCursor == null) {
                return -1;
            }
            return mCursor.getLong(mCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST_ID));
        }
    }
    //专辑id
    private long getAlbumId() {
        synchronized (this) {
            if (mCursor == null) {
                return -1;
            }
            return mCursor.getLong(mCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ID));
        }
    }
    //艺术家名
    private String getArtistName() {
        synchronized (this) {
            if (mCursor == null) {
                return null;
            }
            return mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST));
        }
    }
    //音乐名
    private String getTrackName() {
        Log.d(TAG, "getTrackName: "+(mCursor == null));

        synchronized (this) {
            if (mCursor == null) {
                return null;
            }
            return mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE));
        }
    }
    //音乐路径
    private String getPath() {
        synchronized (this) {
            if (mCursor == null) {
                return null;
            }
            return mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA));
        }
    }
    //专辑名
    private String getAlbumName() {
        synchronized (this) {
            if (mCursor == null) {
                return null;
            }
            return mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM));
        }
    }
    //专辑路径mime_type？
    private String getAlbumPath() {
        synchronized (this) {
            if (mCursor == null) {
                return null;
            }
            return mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.MIME_TYPE));
        }
    }

    //根据音乐id更新游标
    private void updateCursor(final long trackId) {
        MusicInfo info = mPlaylistInfo.get(trackId);
        if (info != null) {
            MatrixCursor cursor = new MatrixCursor(PROJECTION);
            cursor.addRow(new Object[]{info.songId, info.artist, info.albumName, info.musicName
                    , info.data, info.albumData, info.albumId, info.artistId});
            cursor.moveToFirst();
            mCursor = cursor;
            cursor.close();
        }
    }
    //本地游标
    private void updateCursor(final String selection, final String[] selectionArgs) {
        synchronized (this) {
            closeCursor();
            mCursor = openCursorAndGoToFirst(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    PROJECTION, selection, selectionArgs);
        }
    }
    //内容提供者游标
    private void updateCursor(final Uri uri) {
        synchronized (this) {
            closeCursor();
            mCursor = openCursorAndGoToFirst(uri, PROJECTION, null, null);
        }
    }
    private Cursor openCursorAndGoToFirst(Uri uri, String[] projection,
                                           String selection, String[] selectionArgs) {
        Cursor c = getContentResolver().query(uri, projection,
                selection, selectionArgs, null);
        if (c == null) {
            return null;
        }
        if (!c.moveToFirst()) {
            c.close();
            return null;
        }
        return c;
    }


    private void openCurrentAndMaybeNext(final boolean play, final boolean openNext) {
        synchronized (this) {

            stop(false);
            Log.d(TAG, "openCurrentAndMaybeNext: "+mPlaylist.size()+"-"+mPlaylistInfo.size());
            if (mPlaylist.size() == 0 || mPlaylistInfo.size() == 0 && mPlayPos >= mPlaylist.size()) {
                return;
            }

            final long id = mPlaylist.get(mPlayPos).mId;
            if (mPlaylistInfo.get(id) == null) {
                return;
            }
            updateCursor(id);
            getLrc(id);
            notifyChange(MUSIC_CHANGED);
            updateNotification();
            //
//            boolean shutdown = false;

            Log.d(TAG, "openCurrentAndMaybeNext: "+mPlaylistInfo.get(id).islocal);

            if (!mPlaylistInfo.get(id).islocal) {
                //网络
                if (mRequestUrl != null) {
                    mRequestUrl.stop();
                    mUrlHandler.removeCallbacks(mRequestUrl);
                }
                mRequestUrl = new RequestPlayUrl(id, play);
                mUrlHandler.postDelayed(mRequestUrl, 70);
            }else{
                //本地
                mPlayer.setDataSource(mPlaylistInfo.get(id).getData());
            }

//            if(shutdown){
//
//            }
//            else if(openNext){
//                setNextTrack();
//            }
        }
    }

    private void setNextTrack() {
        setNextTrack(getNextPosition(false));
    }

    /**
     * 设置下一曲，
     * 本地音乐还需设置资源启动下一个媒体播放器
     * 网络音乐url为准备，则不设置
     * @param position
     */
    private void setNextTrack(int position) {
        mNextPlayPos = position;
        if (mNextPlayPos >= 0 && mPlaylist != null && mNextPlayPos < mPlaylist.size()) {
            final long id = mPlaylist.get(mNextPlayPos).mId;
            if (mPlaylistInfo.get(id) != null) {
                if (mPlaylistInfo.get(id).islocal) {
//                    mPlayer.setNextDataSource(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI + "/" + id);
                    mPlayer.setNextDataSource(mPlaylistInfo.get(id).getData());
                } else {
                    mPlayer.setNextDataSource(null);
                }
            }
        } else {
            mPlayer.setNextDataSource(null);
        }
    }
    private int getNextPosition(final boolean force) {
        if (mPlaylist == null || mPlaylist.isEmpty()) {
            return -1;
        }

        if (mRepeatMode == REPEAT_CURRENT) {
            if (mPlayPos < 0) {
                return 0;
            }else if(mPlayPos >= mPlaylistInfo.size()){
                return mPlaylistInfo.size() - 1;
            }
            return mPlayPos;
        }else if(mRepeatMode == REPEAT_ALL){
            if(mPlayPos + 1 >= mPlaylistInfo.size()){
                return 0;
            }
            return mPlayPos + 1;
        }else if(mRepeatMode == SHUFFLE_AUTO){
            int n = new Random().nextInt(mPlaylistInfo.size());
            while(n == mPlayPos){
                n = new Random().nextInt(mPlaylistInfo.size());
            }
            return n;
        }
        return -1;
    }

    private void sendUpdateBuffer(int progress) {
        Intent intent = new Intent(BUFFER_UP);
        intent.putExtra("progress", progress);
        sendBroadcast(intent);
    }
    public void loading(boolean l) {
        Intent intent = new Intent(MUSIC_LODING);
        intent.putExtra("isloading", l);
        sendBroadcast(intent);
    }

    /**
     * 播放出错、完成、准备
     */
    private static final class MultiPlayer implements MediaPlayer.OnErrorListener,MediaPlayer.OnCompletionListener{

        private final WeakReference<MediaService> mService;
        private MediaPlayer mCurrentMediaPlayer = new MediaPlayer();
        private MediaPlayer mNextMediaPlayer;

        private Handler mHandler;
        //初始化数据,是否设置完成
        private boolean mIsInitialized = false;
        //网络请求设置完成，是否开始准备
        private boolean mIsTrackNet = false;
        //是否准备好，是否可以跟踪（网络请求需等待准备完成，本地则设置完成后便可跟踪）
        private boolean mIsTrackPrepared = false;

        private String mNextMediaPath;
        private boolean mIsNextInitialized = false;
        private boolean mIsNextTrackPrepared = false;

        private Handler handler = new Handler();

        private boolean isFirstLoad = true;

        private int sencondaryPosition = 0;

        public MultiPlayer(final MediaService service) {
            mService = new WeakReference<MediaService>(service);
            //http://www.mamicode.com/info-detail-1896844.html
//            mCurrentMediaPlayer.setWakeMode(mService.get(), PowerManager.PARTIAL_WAKE_LOCK);
        }
        public void setHandler(Handler playerHandler) {
            mHandler = playerHandler;
        }
        /**
         * 设置数据
         * @param path
         */
        public void setDataSource(final String path) {
            mIsInitialized = setDataSourceImpl(mCurrentMediaPlayer, path);
            if (mIsInitialized) {
                setNextDataSource(null);
            }
        }
        private boolean setDataSourceImpl(MediaPlayer player, String path) {
            mIsTrackNet = false;
            mIsTrackPrepared = false;
            try {
                //重置MediaPlayer至未初始化状态
                player.reset();
                //指定流媒体类型
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                //注意：内存中音乐地址：/storage/emulated/0/Music/一场恋爱-杨宗纬.flac
                if (path.startsWith("content://")) {
                    Log.d(TAG, "setDataSourceImpl: "+Uri.parse(path).toString());
                    player.setOnPreparedListener(null);
                    //为什么此处使用app上下文，而不是用service的上下文
                    // player.setDataSource(mService.get(), Uri.parse(path));
                    player.setDataSource(MyApplication.context, Uri.parse(path));
                    player.prepare();
                    //也可在准备完成事件监听中设置,此处不设置准备完成监听事件
                    mIsTrackPrepared = true;
                    player.setOnCompletionListener(this);
                } else {
                    Log.d(TAG, "setDataSourceImpl: "+Uri.parse(path).toString());
                    player.setDataSource(path);
                    player.setOnPreparedListener(preparedListener);
                    player.prepareAsync();
                    mIsTrackNet = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            player.setOnErrorListener(this);
            player.setOnBufferingUpdateListener(bufferingUpdateListener);
            return true;
        }

        @SuppressLint("NewApi")
        private void setNextDataSource(final String path) {
            mNextMediaPath = null;
            mIsNextInitialized = false;
            try {
                mCurrentMediaPlayer.setNextMediaPlayer(null);
            } catch (IllegalArgumentException e) {
                Log.i(TAG, "Next media player is current one, continuing");
            } catch (IllegalStateException e) {
                Log.e(TAG, "Media player not initialized!");
                return;
            }
            if (mNextMediaPlayer != null) {
                mNextMediaPlayer.release();
                mNextMediaPlayer = null;
            }
            if (path == null) {
                return;
            }
            mNextMediaPlayer = new MediaPlayer();
            if (mIsNextInitialized = setNextDataSourceImpl(mNextMediaPlayer, path)) {
                mNextMediaPath = path;
                mCurrentMediaPlayer.setNextMediaPlayer(mNextMediaPlayer);
                // mHandler.post(setNextMediaPlayerIfPrepared);

            } else {
                if (mNextMediaPlayer != null) {
                    mNextMediaPlayer.release();
                    mNextMediaPlayer = null;
                }
            }
        }
        private boolean setNextDataSourceImpl(final MediaPlayer player, final String path) {
            mIsNextTrackPrepared = false;
            try {
                player.reset();
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                if (path.startsWith("content://")) {
                    player.setOnPreparedListener(preparedNextListener);
                    player.setDataSource(MyApplication.context, Uri.parse(path));
                    player.prepare();
                } else {
                    Log.d(TAG, "setNextDataSourceImpl: !!!!!!!!!");
                    player.setDataSource(path);
                    player.setOnPreparedListener(preparedNextListener);
                    player.prepare();
                }

            } catch (final IOException todo) {

                return false;
            } catch (final IllegalArgumentException todo) {

                return false;
            }
            player.setOnCompletionListener(this);
            player.setOnErrorListener(this);
            return true;
        }

        MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                if (isFirstLoad) {
                    isFirstLoad = false;
                }
                mp.setOnCompletionListener(MultiPlayer.this);
                mIsTrackPrepared = true;

            }
        };

        MediaPlayer.OnPreparedListener preparedNextListener = new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mIsNextTrackPrepared = true;
//                mService.get().updateNotification();
            }
        };

        MediaPlayer.OnBufferingUpdateListener bufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {

            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                if (sencondaryPosition != 100)
                    mService.get().sendUpdateBuffer(percent);
                sencondaryPosition = percent;
            }
        };


        @Override
        public void onCompletion(MediaPlayer mp) {
            if (mp == mCurrentMediaPlayer && mNextMediaPlayer != null) {
                Log.d(TAG, "onCompletion: "+(mNextMediaPlayer != null));
                mCurrentMediaPlayer.release();
                mCurrentMediaPlayer = mNextMediaPlayer;
                mNextMediaPath = null;
                mNextMediaPlayer = null;
                mHandler.sendEmptyMessage(TRACK_LOCAL_ENDED);
            }else{
                Log.d(TAG, "onCompletion: "+(mNextMediaPlayer != null));
                mHandler.sendEmptyMessage(TRACK_ENDED);
            }
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            switch (what) {
                case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                    Log.d(TAG, "mCurrentMediaPlayer onError: ");
                    final MediaService service = mService.get();
//                    final TrackErrorInfo errorInfo = new TrackErrorInfo(service.getAudioId(),
//                            service.getTrackName());

                    mIsInitialized = false;
                    mIsTrackPrepared = false;
                    mCurrentMediaPlayer.release();
                    mCurrentMediaPlayer = new MediaPlayer();
//                    mCurrentMediaPlayer.setWakeMode(service, PowerManager.PARTIAL_WAKE_LOCK);
//                    Message msg = mHandler.obtainMessage(SERVER_DIED, errorInfo);
//                    mHandler.sendMessageDelayed(msg, 2000);
                    return true;
                default:
                    break;
            }
            return false;
        }

        Runnable startMediaPlayerIfPrepared = new Runnable() {
            @Override
            public void run() {
                if (mIsTrackPrepared) {
                    mCurrentMediaPlayer.start();
                    mService.get().notifyChange(META_CHANGED);
                    mService.get().loading(false);
                }else{
                    handler.postDelayed(startMediaPlayerIfPrepared, 700);
                }
            }
        };
        public void start() {
            if(mIsTrackNet){
                sencondaryPosition = 0;
                mService.get().loading(true);
                handler.postDelayed(startMediaPlayerIfPrepared, 50);
            }else{
                Log.d(TAG, "mIsTrackNet start: "+mIsTrackNet);
            }
//            mService.get().notifyChange(MUSIC_CHANGED);

        }
        public long position() {
            if (mIsTrackPrepared) {
                try {
                    return mCurrentMediaPlayer.getCurrentPosition();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return -1;
        }
        public long secondPosition() {
            if (mIsTrackPrepared) {
                return sencondaryPosition;
            }
            return -1;
        }
        public long duration() {
            if (mIsTrackPrepared) {
                return mCurrentMediaPlayer.getDuration();
            }
            return -1;
        }
        public void release() {
            mCurrentMediaPlayer.release();
        }
        public void pause() {
            handler.removeCallbacks(startMediaPlayerIfPrepared);
            mCurrentMediaPlayer.pause();
        }
        public long seek(final long whereto) {
            mCurrentMediaPlayer.seekTo((int) whereto);
            return whereto;
        }
        public void stop() {

            handler.removeCallbacks(startMediaPlayerIfPrepared);
            mCurrentMediaPlayer.reset();
            mIsInitialized = false;
            mIsTrackPrepared = false;

        }

        public boolean isInitialized() {
            return mIsInitialized;
        }
        public boolean isTrackPrepared() {
            return mIsTrackPrepared;
        }
    }

    private class MediaStoreObserver extends ContentObserver implements Runnable{

        private static final long REFRESH_DELAY = 500;
        private Handler mHandler;

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MediaStoreObserver(Handler handler) {
            super(handler);
            mHandler = handler;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            //取消原线程执行
            mHandler.removeCallbacks(this);
            //开始新线程
            mHandler.postDelayed(this,REFRESH_DELAY);

        }

        @Override
        public void run() {
            refresh();
        }
    }

    /**
     * 缓存
     */
    private void startProxy() {
        if (mProxy == null) {
            mProxy = new MediaPlayerProxy(this);
            mProxy.init();
            mProxy.start();
        }
    }

    /**
     * 通知改变
     * @param what
     */
    private void notifyChange(final String what) {


        final Intent intent = new Intent(what);
        intent.putExtra("id", getAudioId());
        intent.putExtra("artist", getArtistName());
        intent.putExtra("album", getAlbumName());
        intent.putExtra("track", getTrackName());
        intent.putExtra("playing", isPlaying());
        intent.putExtra("albumuri", getAlbumPath());
        intent.putExtra("islocal", isTrackLocal());
        sendStickyBroadcast(intent);

        if (what.equals(PLAYSTATE_CHANGED)) {
            updateNotification();
        }
    }




    private long getPreviousAudioId() {
        synchronized (this) {
            if (mPlayer.isInitialized()) {
                int pos = getPreviousPlayPosition(false);
                if (pos >= 0 && pos < mPlaylist.size()) {
                    return mPlaylist.get(pos).mId;
                }
            }
        }
        return -1;
    }
    private void enqueue(long[] list, HashMap<Long, MusicInfo> infos, int action) {
    }

    private void moveQueueItem(int from, int to) {

    }
    private void playlistChanged() {

    }
    private int getQueueHistoryPosition(int position) {
        synchronized (this) {
            if (position >= 0 && position < mHistory.size()) {
                return mHistory.get(position);
            }
        }
        return -1;
    }
    private int getQueueHistorySize() {
        synchronized (this) {
            return mHistory.size();
        }
    }
    /**
     * 获取历史记录
     * @return
     */
    private int[] getQueueHistoryList() {
        synchronized (this) {
            int[] history = new int[mHistory.size()];
            for (int i = 0; i < mHistory.size(); i++) {
                history[i] = mHistory.get(i);
            }
            return history;
        }
    }
    private void seekRelative(long deltaInMs) {
        synchronized (this) {
            if (mPlayer.isInitialized()) {
                final long newPos = position() + deltaInMs;
                final long duration = duration();
                if (newPos < 0) {
                    prev(true);
                    // seek to the new duration + the leftover position
                    //                    seek(duration() + newPos);
                } else if (newPos >= duration) {
                    gotoNext(true);
                    // seek to the leftover duration
                    //                    seek(newPos - duration);
                } else {
                    seek(newPos);
                }
            }
        }
    }
    /**
     * 所有专辑
     * @return
     */
    private String[] getAlbumPathAll() {
        synchronized (this) {
            try {
                int len = mPlaylistInfo.size();
                String[] albums = new String[len];
                long[] queue = getQueue();
                for (int i = 0; i < len; i++) {
                    albums[i] = mPlaylistInfo.get(queue[i]).albumData;
                }
                return albums;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new String[]{};
        }
    }
    private int getShuffleMode() {
        return 0;
    }
    private void setShuffleMode(int shufflemode) {

    }

    private int removeTracksInternal(int first, int last) {
        synchronized (this) {
            if (last < first) {
                return 0;
            } else if (first < 0) {
                first = 0;
            } else if (last >= mPlaylist.size()) {
                last = mPlaylist.size() - 1;
            }

            final int numToRemove = last - first + 1;

            boolean gotonext = false;

            if (first <= mPlayPos && mPlayPos <= last) {
                //当前播放的也在移除曲目内
                mPlayPos = first;
                gotonext = true;
            } else if (mPlayPos > last) {
                mPlayPos -= numToRemove;
            }

            if (first == 0 && last == mPlaylist.size() - 1) {
                //全部移除
                mPlayPos = -1;
                mNextPlayPos = -1;
                mPlaylist.clear();
                mHistory.clear();
            } else {
                for (int i = 0; i < numToRemove; i++) {
                    mPlaylistInfo.remove(mPlaylist.get(first).mId);
                    mPlaylist.remove(first);
                }

                ListIterator<Integer> positionIterator = mHistory.listIterator();
                while (positionIterator.hasNext()) {
                    int pos = positionIterator.next();
                    if (pos >= first && pos <= last) {
                        positionIterator.remove();
                    } else if (pos > last) {
                        positionIterator.set(pos - numToRemove);
                    }
                }
            }
            if (gotonext) {
                if (mPlaylist.size() == 0) {
                    stop(true);
                    mPlayPos = -1;
                    closeCursor();
                } else {
                    if (mPlayPos >= mPlaylist.size()) {
                        mPlayPos = 0;
                    }
                    final boolean wasPlaying = isPlaying();
                    stop(false);
                    openCurrentAndNext();
                    if (wasPlaying) {
                        play();
                    }
                }
                notifyChange(META_CHANGED);
            }

            return numToRemove;
        }
    }
    /**
     * 移除曲目
     * @param first
     * @param last
     * @return
     */
    private int removeTracks(int first, int last) {
        final int numremoved = removeTracksInternal(first, last);
        if (numremoved > 0) {
            //            notifyChange(QUEUE_CHANGED);
        }
        return numremoved;
    }
    private int removeTrack(long id) {
        int numremoved = 0;
        synchronized (this) {
            for (int i = 0; i < mPlaylist.size(); i++) {
                if (mPlaylist.get(i).mId == id) {
                    numremoved += removeTracksInternal(i, i);
                    i--;
                }
            }

            mPlaylistInfo.remove(id);
        }


        if (numremoved > 0) {
            //            notifyChange(QUEUE_CHANGED);
        }
        return numremoved;
    }
    private boolean removeTrackAtPosition(long id, int position) {
        synchronized (this) {
            if (position >= 0 &&
                    position < mPlaylist.size() &&
                    mPlaylist.get(position).mId == id) {
                mPlaylistInfo.remove(id);
                return removeTracks(position, position) > 0;
            }

        }
        return false;
    }
    private int getMediaMountedCount() {
        return 0;
    }
    private int getAudioSessionId() {
        return 0;
    }
    private void setLockscreenAlbumArt(boolean enabled) {

    }
    private void exit() {

    }
    private void timing(int time) {

    }
    private void stop() {
        stop(true);
    }
    private boolean openFile(String path) {
        if(MyApplication.DEBUG){
            Log.d(TAG, "openFile: path = " + path);
        }
        synchronized (this) {
            if (path == null) {
                return false;
            }
            return false;
        }
    }
    /**
     * 刷新
     */
    private void refresh() {
        notifyChange(REFRESH);
    }
}
