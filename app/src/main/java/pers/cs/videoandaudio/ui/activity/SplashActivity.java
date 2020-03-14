package pers.cs.videoandaudio.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.utils.PermissionHelper;

/**
 * @author chensen
 *
 * @time 2020/1/5  21:36
 *
 * @desc
 *
 * SplashActivity、WelcomeActivity、LaucherActivity
 */

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();
    private Handler handler = new Handler();

    private PermissionHelper mPermissionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        // 当系统为6.0以上时，需要申请权限
        mPermissionHelper = new PermissionHelper(this);
        mPermissionHelper.setOnApplyPermissionListener(new PermissionHelper.OnApplyPermissionListener() {
            @Override
            public void onAfterApplyAllPermission() {
                Log.i(TAG, "All of requested permissions has been granted, so run app logic.");
                runApp();
            }
        });

        if (Build.VERSION.SDK_INT < 23) {
            // 如果系统版本低于23，直接跑应用的逻辑
            Log.d(TAG, "The api level of system is lower than 23, so run app logic directly.");
            runApp();
        } else {
            // 如果权限全部申请了，那就直接跑应用逻辑
            if (mPermissionHelper.isAllRequestedPermissionGranted()) {
                Log.d(TAG, "All of requested permissions has been granted, so run app logic directly.");
                runApp();
            } else {
                // 如果还有权限为申请，而且系统版本大于23，执行申请权限逻辑
                Log.i(TAG, "Some of requested permissions hasn't been granted, so apply permissions first.");
                mPermissionHelper.applyPermissions();
            }
        }



    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPermissionHelper.onActivityResult(requestCode, resultCode, data);
    }

    private void runApp() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //注意：此方法在主线程中执行，Handler在哪个线程中new，它就在哪个线程执行。
                startMainActivity();

                Log.d(TAG, "run: 当前线程名称：" + Thread.currentThread().getName());
            }
        },2000);
    }

    //不设置单例模式，让其启动一次,设置标志
    private boolean isStartMain = false;
    private void startMainActivity() {
//        if(!isStartMain) {
//            isStartMain = true;
            Intent intent = new Intent(this, MainActivity.class);
//            Intent intent = new Intent(this, Main2Activity.class);

            startActivity(intent);

            finish();
//        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "run: onTouchEvent：" + event.getAction());
        startMainActivity();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        //防止刚启动时点击退出后，启动。
        //因为handler没有移除消息，2秒后便启动MainActivity。
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();

    }
}
