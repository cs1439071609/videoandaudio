package pers.cs.videoandaudio.base;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import pers.cs.videoandaudio.IMediaAidlInterface;
import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.service.MediaService;
import pers.cs.videoandaudio.service.MusicPlayer;
import pers.cs.videoandaudio.ui.activity.MusicStateListener;
import pers.cs.videoandaudio.ui.fragment.QuickControlsFragment;

import static pers.cs.videoandaudio.service.MusicPlayer.mService;

//导入


/**
 * @author chensen
 * @time 2020/4/12  21:21
 * @desc
 */
public class BaseActivity extends AppCompatActivity implements ServiceConnection {
    private static final String TAG = BaseActivity.class.getSimpleName();
    private static final Boolean DEBUG = false;

    private MusicPlayer.ServiceToken mToken;
    //receiver 接受播放状态变化等
    private PlaybackStatus mPlaybackStatus;
    //BaseMusicFragment
    private ArrayList<MusicStateListener> mMusicListener = new ArrayList<>();
    //底部播放控制栏
    private QuickControlsFragment mQuickControlsFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "ServiceTest-Activity-bindToService: "+this);
        mToken = MusicPlayer.bindToService(this,this);
        mPlaybackStatus = new PlaybackStatus(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter f = new IntentFilter();
        f.addAction(MediaService.META_CHANGED);
        f.addAction(MediaService.MUSIC_CHANGED);
        f.addAction(MediaService.LRC_UPDATED);
        f.addAction(MediaService.BUFFER_UP);
        f.addAction(MediaService.MUSIC_LODING);
        registerReceiver(mPlaybackStatus, f);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        //导入或者使用MusicPlayer.mService
        mService = IMediaAidlInterface.Stub.asInterface(service);
        onMetaChanged();
    }

    public void onMetaChanged() {
        if(DEBUG){
            Log.d(TAG,"onMetaChanged:"+this);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        if(DEBUG){
            Log.d(TAG, "ServiceTest-Activity-onServiceDisconnected: "+this);
        }
        mService = null;
    }

    public void unbindService() {
        if (mToken != null) {
            if(DEBUG){
                Log.d(TAG, "ServiceTest-Activity-unbindService: "+this);

            }
            MusicPlayer.unbindFromService(mToken);
            mToken = null;
        }
    }

    @Override
    protected void onDestroy() {

        unbindService();
        unregisterReceiver(mPlaybackStatus);
        mMusicListener.clear();
        super.onDestroy();
    }

    private final static class PlaybackStatus extends BroadcastReceiver {

        private final WeakReference<BaseActivity> mReference;

        public PlaybackStatus(final BaseActivity activity) {
            mReference = new WeakReference<>(activity);
        }

        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();

            Log.d(TAG, "onReceive: "+ context + "--->"+ mReference.get() + "---" + action);

            BaseActivity baseActivity = mReference.get();
            if (baseActivity != null) {
                if (action.equals(MediaService.MUSIC_CHANGED)) {
                    baseActivity.updateTrack();
                }else if (action.equals(MediaService.META_CHANGED)) {
                    baseActivity.updateTrackInfo();
                } else if (action.equals(MediaService.LRC_UPDATED)) {
                    baseActivity.updateLrc();
                }else if (action.equals(MediaService.BUFFER_UP)) {
                    baseActivity.updateBuffer(intent.getIntExtra("progress", 0));
                }else if (action.equals(MediaService.MUSIC_LODING)) {
                    baseActivity.loading(intent.getBooleanExtra("isloading", false));
                }
            }
        }


    }
    /**
     * @param isloading 歌曲是否加载中
     */
    public void loading(boolean isloading) {
        if(DEBUG){
            Log.d(TAG,"loading:");
        }
    }

    public void updateLrc() {
        if(DEBUG){
            Log.d(TAG,"updateLrc:");
        }
    }
    /**
     * 更新歌曲状态信息
     */
    public void updateTrackInfo() {
        if(DEBUG){
            Log.d(TAG,"updateTrackInfo:");
        }
        Log.d(TAG,"updateTrackInfo:");
        //将事件分发到Fragment
        for (final MusicStateListener listener : mMusicListener) {
            if (listener != null) {
                listener.updateTrackInfo();
            }
        }
    }

    /**
     * 歌曲切换
     */
    public void updateTrack() {
//        StringBuffer err = new StringBuffer();
//        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
//        for (int i = 0; i < stack.length; i++) {
//            err.append("\tat ");
//            err.append(stack[i].toString());
//            err.append("\n");
//        }
//        Log.e(TAG, err.toString());
        if(DEBUG){
            Log.d(TAG,"updateTrack:");
        }
    }
    /**
     * @param p 更新歌曲缓冲进度值，p取值从0~100
     */
    public void updateBuffer(int p) {
        if(DEBUG){
            Log.d(TAG,"updateBuffer:");
        }
    }

    /**
     * @param show 显示或关闭底部播放控制栏
     */
    protected void showQuickControl(boolean show) {
        Log.d(TAG, MusicPlayer.getQueue().length + "");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (show) {
            if (mQuickControlsFragment == null) {
                mQuickControlsFragment = new QuickControlsFragment();
                ft.add(R.id.bottom_container, mQuickControlsFragment).commitAllowingStateLoss();
            } else {
                ft.show(mQuickControlsFragment).commitAllowingStateLoss();
            }
        } else {
            if (mQuickControlsFragment != null)
                ft.hide(mQuickControlsFragment).commitAllowingStateLoss();
        }
    }

    @Override
    protected void onResume() {
        //For Android 8.0+: service may get destroyed if in background too long
//        if(mService == null){
//            mToken = MusicPlayer.bindToService(this, this);
//            Log.d(TAG, "onResume: "+(mService==null)+this);
//        }
        super.onResume();
    }


    public void setMusicStateListener(final MusicStateListener status) {
        if (status == this) {
            throw new UnsupportedOperationException("Override the method, don't add a listener");
        }

        if (status != null) {
            mMusicListener.add(status);
        }
    }
    public void removeMusicStateListener(final MusicStateListener status) {
        if (status != null) {
            mMusicListener.remove(status);
        }
    }
}
