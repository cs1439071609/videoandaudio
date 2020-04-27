package pers.cs.videoandaudio.service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.WeakHashMap;

import pers.cs.videoandaudio.IMediaAidlInterface;
import pers.cs.videoandaudio.info.MusicInfo;
import pers.cs.videoandaudio.utils.CacheUtils;

/**
 * @author chensen
 * @time 2020/4/12  20:22
 * @desc 将播放服务封装
 */
public class MusicPlayer {
    private static final String TAG = MusicPlayer.class.getSimpleName();
    private static final Boolean DEBUG = true;

    //BaseActivity中使用
    public static IMediaAidlInterface mService = null;
    //
    private static final WeakHashMap<Context, ServiceBinder> mConnectionMap;

    private static final long[] sEmptyList;

    static {
        mConnectionMap = new WeakHashMap<>();
        sEmptyList = new long[0];
    }

    /**
     * 绑定服务,每个继承BaseActivity的都会在创建时绑定到服务。
     *
     * @param context
     * @param serviceConnection
     * @return
     */
    public static final ServiceToken bindToService(Context context, ServiceConnection serviceConnection) {
        Activity realActivity = ((Activity) context).getParent();
        if (realActivity == null) {
            realActivity = (Activity) context;
        }
        final ContextWrapper contextWrapper = new ContextWrapper(realActivity);
        //启动服务
        contextWrapper.startService(new Intent(contextWrapper, MediaService.class));

        final ServiceBinder binder = new ServiceBinder(contextWrapper.getApplicationContext(), serviceConnection);
        //绑定服务
        if (contextWrapper.bindService(
                new Intent().setClass(contextWrapper, MediaService.class), binder, 0)) {
            mConnectionMap.put(contextWrapper, binder);
            return new ServiceToken(contextWrapper);
        }

        return null;
    }

    /**
     * 解绑服务，每个继承BaseActivity的都会在销毁时调用。
     *
     * @param token
     */
    public static void unbindFromService(final ServiceToken token) {
        if (token == null) {
            return;
        }
        final ContextWrapper mContextWrapper = token.mContextWrapper;
        final ServiceBinder mBinder = mConnectionMap.remove(mContextWrapper);
        if (mBinder == null) {
            return;
        }
        mContextWrapper.unbindService(mBinder);
        Log.d(TAG, "ServiceTest-MusicPlayer-unbindFromService: " + mConnectionMap.size());
        if (mConnectionMap.isEmpty()) {
            mService = null;
            Log.d(TAG, "ServiceTest-MusicPlayer-unbindFromService:All ");
        }
    }


    public static final class ServiceBinder implements ServiceConnection {

        private final Context mContext;
        private final ServiceConnection mServiceConnection;

        public ServiceBinder(Context context, ServiceConnection serviceConnection) {
            mContext = context;
            mServiceConnection = serviceConnection;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IMediaAidlInterface.Stub.asInterface(service);
            Log.d(TAG, "ServiceTest-MusicPlayer-onServiceConnected: " + mService);
            if (mServiceConnection != null) {
                //回调真正连接处
                mServiceConnection.onServiceConnected(name, service);
            }
            initPlaybackServiceWithSettings(mContext);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "ServiceTest-MusicPlayer-onServiceDisconnected: " + mService);
            if (mServiceConnection != null) {
                mServiceConnection.onServiceDisconnected(name);
            }
            mService = null;
        }
    }

    /**
     * BaseActivity中获得，
     */
    public static final class ServiceToken {
        //unbindFromService中使用
        public ContextWrapper mContextWrapper;

        public ServiceToken(ContextWrapper contextWrapper) {
            mContextWrapper = contextWrapper;
        }
    }

    public static void exitService() {
        //        if (mService == null) {
        //            return;
        //        }
        try {
            mConnectionMap.clear();
            Log.e("exitmp", "Destroying service");
            mService.exit();
        } catch (Exception e) {
        }
    }

    /**
     * 播放服务是否还连接
     *
     * @return
     */
    public static final boolean isPlaybackServiceConnected() {
        return mService != null;
    }

    /**
     * @param infos        songid-MusicInfo
     * @param list         songid
     * @param position
     * @param forceShuffle
     */
    public static synchronized void playAll(final HashMap<Long, MusicInfo> infos, final long[] list, int position, final boolean forceShuffle) {
        if (list == null || list.length == 0 || mService == null) {
            return;
        }

        try {
            //第一次播放为-1
            final long currentId = mService.getAudioId();
            long playId = list[position];

            if (position != -1) {
                //播放列表中 当前播放索引,第一次播放为-1
                final int currentQueuePosition = getQueuePosition();
                //第一次播放队列空
                final long[] playlist = getQueue();
                if (Arrays.equals(list, playlist)) {
                    if (currentQueuePosition == position && currentId == list[position]) {
                        Log.d(TAG, "playAll: zj");
                        mService.play();
                        return;
                    } else {
                        Log.d(TAG, "playAll: position");
                        mService.setQueuePosition(position);
                        return;
                    }
                }
            }
            if (position < 0) {
                position = 0;
            }
            mService.open(infos, list, forceShuffle ? -1 : position);
            //            mService.play();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    /**
     * 播放/暂停
     */
    public static void playOrPause() {
        try {
            if (mService != null) {
                Log.d(TAG, "ServiceTest-playOrPause: " + mService.isPlaying());
                if (mService.isPlaying()) {
                    mService.pause();
                } else {
                    mService.play();
                }
            }
        } catch (final Exception ignored) {
        }
    }

    /**
     * @param context
     * @param force   强制上一首
     */
    public static void previous(final Context context, final boolean force) {
        Log.d(TAG, "ServiceTest-previous: " + force + "-" + (context == null));
//        final Intent previous = new Intent();
        final Intent previous = new Intent(context, MediaService.class);
        if (force) {
            previous.setAction(MediaService.PREVIOUS_FORCE_ACTION);
        } else {
            previous.setAction(MediaService.PREVIOUS_ACTION);
        }
//        context.sendBroadcast(previous);
        context.startService(previous);
    }

    /**
     * 播放下一个
     */
    public static void next() {
        if (mService != null) {
            try {
                mService.next();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 是否正在播放
     *
     * @return
     */
    public static final boolean isPlaying() {
        if (mService != null) {
            try {
                return mService.isPlaying();
            } catch (final RemoteException ignored) {
            }
        }
        return false;
    }

    public static void seek(final long position) {
        if (mService != null) {
            try {
                mService.seek(position);
            } catch (final RemoteException ignored) {
            }
        }
    }

    public static final long position() {
        if (mService != null) {
            try {
                return mService.position();
            } catch (final RemoteException ignored) {
            } catch (final IllegalStateException ex) {

            }
        }
        return 0;
    }

    public static final long duration() {
        if (mService != null) {
            try {
                return mService.duration();
            } catch (final RemoteException ignored) {
            } catch (final IllegalStateException ignored) {

            }
        }
        return 0;
    }

    public static String getPath() {
        if (mService == null) {
            return null;
        }
        try {
            return mService.getPath();

        } catch (Exception e) {

        }
        return null;
    }

    public static void stop() {
        try {
            mService.stop();
        } catch (Exception e) {

        }
    }

    /**
     * @return 当前播放队列的音乐id
     */
    public static final long[] getQueue() {
        try {
            if (mService != null) {
                return mService.getQueue();
            } else {
            }
        } catch (final RemoteException ignored) {
        }
        return sEmptyList;
    }

    public static final int getQueuePosition() {
        try {
            if (mService != null) {
                return mService.getQueuePosition();
            }
        } catch (final RemoteException ignored) {
        }
        return 0;
    }

    public static final int secondPosition() {
        if (mService != null) {
            try {
                return mService.secondPosition();
            } catch (final RemoteException ignored) {
            } catch (final IllegalStateException ex) {

            }
        }
        return 0;
    }

    public static final HashMap<Long, MusicInfo> getPlayinfos() {
        try {
            if (mService != null) {
                return (HashMap<Long, MusicInfo>) mService.getPlayinfos();
            } else {
            }
        } catch (final RemoteException ignored) {
        }
        return null;
    }

    /**
     * 网络音乐列表 移除音乐
     *
     * @param id
     * @return
     */
    public static final int removeTrack(final long id) {
        try {
            if (mService != null) {
                return mService.removeTrack(id);
            }
        } catch (final RemoteException ingored) {
        }
        return 0;
    }

    /**
     * 初始化播放-设置
     *
     * @param context
     */
    public static void initPlaybackServiceWithSettings(Context context) {
        setShowAlbumArtOnLockscreen(true);
    }

    /**
     * 设置锁屏显示专辑封面，调用aidl方法
     *
     * @param enabled
     */
    private static void setShowAlbumArtOnLockscreen(boolean enabled) {
        if (mService != null) {
            try {
                mService.setLockscreenAlbumArt(enabled);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 是本地音乐
     *
     * @return
     */
    public static boolean isTrackLocal() {
        try {
            if (mService != null) {
                return mService.isTrackLocal();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @return 音乐名
     */
    public static final String getTrackName() {
        Log.d(TAG, "getTrackName: mService != null?" + (mService != null));
        if (mService != null) {
            try {
                return mService.getTrackName();
            } catch (final RemoteException ignored) {
            }
        }
        return null;
    }

    /**
     * @return 艺术家名
     */
    public static final String getArtistName() {
        if (mService != null) {
            try {
                return mService.getArtistName();
            } catch (final RemoteException ignored) {
            }
        }
        return null;
    }

    public static final long getCurrentArtistId() {
        if (mService != null) {
            try {
                return mService.getArtistId();
            } catch (final RemoteException ignored) {
            }
        }
        return -1;
    }

    /**
     * @return 音乐id
     */
    public static final long getCurrentAudioId() {
        if (mService != null) {
            try {
                return mService.getAudioId();
            } catch (final RemoteException ignored) {
            }
        }
        return -1;
    }

    public static final String getAlbumName() {
        if (mService != null) {
            try {
                return mService.getAlbumName();
            } catch (final RemoteException ignored) {
            }
        }
        return null;
    }

    public static final String getAlbumPath() {
        if (mService != null) {
            try {
                return mService.getAlbumPath();
            } catch (final RemoteException ignored) {
            }
        }
        return null;
    }

    public static final String[] getAlbumPathAll() {
        if (mService != null) {
            try {
                return mService.getAlbumPathtAll();
            } catch (final RemoteException ignored) {
            }
        }
        return null;
    }

    public static final long getCurrentAlbumId() {
        if (mService != null) {
            try {
                return mService.getAlbumId();
            } catch (final RemoteException ignored) {
            }
        }
        return -1;
    }


    //播放模式
    public static final int getRepeatMode() {
        if (mService != null) {
            try {
                return mService.getRepeatMode();
            } catch (final RemoteException ignored) {
            }
        }
        return 0;
    }
    /**
     * 单曲循环
     */
    public static void singleRepeat(Context context) {
        try {
            if (mService != null) {
                int mode = getRepeatMode();
                if (mode != MediaService.REPEAT_CURRENT) {
                    mService.setRepeatMode(MediaService.REPEAT_CURRENT);
                    CacheUtils.putInt(context,"MusicRepeatMode",MediaService.REPEAT_CURRENT);
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 全部循环，顺序播放
     */
    public static void allRepeat(Context context) {
        try {
            if (mService != null) {
                int mode = getRepeatMode();
                if (mode != MediaService.REPEAT_ALL) {
                    mService.setRepeatMode(MediaService.REPEAT_ALL);
                    CacheUtils.putInt(context,"MusicRepeatMode",MediaService.REPEAT_ALL);
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 随机播放
     */
    public static void shuffle(Context context) {
        try {
            if (mService != null) {
                int mode = getRepeatMode();
                if (mode != MediaService.SHUFFLE_AUTO) {
                    mService.setRepeatMode(MediaService.SHUFFLE_AUTO);
                    CacheUtils.putInt(context,"MusicRepeatMode",MediaService.SHUFFLE_AUTO);
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }



}
