package pers.cs.videoandaudio.ui.activity;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;
import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.base.BaseFragment;
import pers.cs.videoandaudio.service.MusicPlayerService;
import pers.cs.videoandaudio.ui.fragment.MusicFragment;
import pers.cs.videoandaudio.ui.fragment.VideoFragment;

/**
 * @author chensen
 * @time 2020/3/29  22:40
 * @desc 使用DrawerLayout+Toolbar+NavigationView+TabLayout+ViewPager+Fragment
 */

public class Main3Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final boolean DEBUG = true;
    private static final String TAG = Main3Activity.class.getSimpleName();

    @BindView(R.id.tl_main3)
    TabLayout tlMain3;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.bnv_main3)
    BottomNavigationView bnvMain3;
    @BindView(R.id.nav_main3)
    NavigationView navMain3;
    @BindView(R.id.drawer)
    DrawerLayout drawer;

    //页面索引
    private int position = 0;
    //当前Fragemnt
    private BaseFragment currentFragment;
    //所有Fragment
    private List<BaseFragment> fragments;
    private MusicFragment musicFragment;
    private VideoFragment videoFragment;
    private String[] tags;
    /**
     * 界面设置状态栏字体颜色
     */
    public void changeStatusBarTextImgColor(boolean isBlack) {
        if (isBlack) {
            //设置状态栏黑色字体
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            //恢复状态栏白色字体
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        ButterKnife.bind(this);

        //设置手机应用内部状态栏字体图标为黑色
        changeStatusBarTextImgColor(true);
        initView();
        initEvent();


    }

    private void initEvent() {
        navMain3.setNavigationItemSelectedListener(this);
        bnvMain3.setOnNavigationItemSelectedListener(btvOnNavigationItemSelectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener btvOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.main_music:
                    position = 0;
                    switchFragment(currentFragment,fragments.get(position));
                    return true;
                case R.id.main_video:
                    position = 1;
                    switchFragment(currentFragment,fragments.get(position));
                    return true;
            }
            return false;
        }
    };
    private void switchFragment(BaseFragment from, BaseFragment to) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if(to!= null && from != to){

            currentFragment = to;
            if(!to.isAdded()){
                if (from != null){
                    ft.hide(from);
                }
                ft.add(R.id.fl_content,to,tags[position]);
            }else{
                if (from != null){
                    ft.hide(from);
                }
                ft.show(to);
            }
        }

        ft.commit();

        //好像是异步，如果时间太长，那么在onStart中还不是获取Fragment失败？
        //为什么切换后使用findFragmentByTag()可以立即获取到Fragment？
        //        fm.executePendingTransactions();
    }
    private void initView() {
        setSupportActionBar(toolbar);

//        StatusBarUtil.setTransparentForDrawerLayout(this,drawer);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        tlMain3.setSelectedTabIndicator(null);

        tlMain3.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // 获取 tab 组件
                View view = tab.getCustomView();
                if(view != null){
                    TextView textView = view.findViewById(R.id.tv_item_tab);
                    textView.setTextAppearance(Main3Activity.this,R.style.tab_selected);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // 获取 tab 组件
                View view = tab.getCustomView();
                if(view != null){
                    TextView textView = view.findViewById(R.id.tv_item_tab);
                    textView.setTextAppearance(Main3Activity.this,R.style.tab_selected_no);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        fragments = new ArrayList<>();
        musicFragment = new MusicFragment();
        videoFragment = new VideoFragment();
        fragments.add(musicFragment);
        fragments.add(videoFragment);
        tags = new String[]{"music","video"};
        switchFragment(currentFragment,fragments.get(position));

    }

    /**
     * 返回按钮事件
     * onBackPressed方法和onKeyDown方法同时存在的时候，我们按back键，系统调用的是onKeyDown这个方法，
     * 不会调用onBackPressed方法；
     * 当这两个方法任意存在一个的时候，存在的那个都会被调用。
     */
    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ");
        //监听返回键
        if(drawer != null && drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            if (Jzvd.backPress()) {
                return;
            }
            super.onBackPressed();
        }
    }

    /**
     * 创建菜单
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main3, menu);
        return true;
    }

    /**
     * 菜单点击事件
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_search:

//                Snackbar.make(item.getActionView(), "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(Main3Activity.this, NetSearchWordsActivity.class);
                //取消activity跳转的动画效果
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                Main3Activity.this.startActivity(intent);
                return true;


        }

        return super.onOptionsItemSelected(item);

    }

    /**
     * 侧滑栏菜单点击事件
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:

                break;


        }
        if(drawer != null){
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;

    }
    private boolean isExit = false;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK){
            Log.d(TAG, "onKeyDown: ");
            //如果现在jzvd为全屏，先关闭
            if (Jzvd.backPress()) {
                Log.d(TAG, "onKeyDown:jzvd ");
                return true;
            }
            if(position != 0){
                bnvMain3.setSelectedItemId(R.id.main_music);
                return true;
            } else if (!isExit) {
                isExit = true;
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExit = false;
                    }
                },2000);
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //home back
        JzvdStd.goOnPlayOnResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JzvdStd.goOnPlayOnPause();
        //        Jzvd.releaseAllVideos();

        if(DEBUG){
            Log.d(TAG, "onPause: ");
        }

    }

    @Override
    protected void onDestroy() {

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        stopService(new Intent(this, MusicPlayerService.class));
        super.onDestroy();


    }



}
