package pers.cs.videoandaudio.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;

import pers.cs.videoandaudio.R;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash);

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
//            Intent intent = new Intent(this, MainActivity.class);
            Intent intent = new Intent(this, Main2Activity.class);

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
