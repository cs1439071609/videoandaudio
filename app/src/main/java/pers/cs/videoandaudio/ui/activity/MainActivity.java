package pers.cs.videoandaudio.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.base.BaseFragment;
import pers.cs.videoandaudio.ui.fragment.CloudVillageFragment;
import pers.cs.videoandaudio.ui.fragment.LocalMusicFragment;
import pers.cs.videoandaudio.ui.fragment.LocalVideoFragment;
import pers.cs.videoandaudio.ui.fragment.NetVideoFragment;

/**
 * @author chensen
 * @time 2020/1/5  23:44
 * @desc 框架1 使用RadioGroup+Fragment
 */


public class MainActivity extends AppCompatActivity {

    private RadioGroup rg_bottom_tag;

    private List<BaseFragment> mFragments;
    private int position = 0;
    private Fragment mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);


        initView();
        initFragment();
        setListener();


    }

    private void setListener() {
        rg_bottom_tag.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        rg_bottom_tag.check(R.id.rb_local_video);
    }

    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener{


        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.rb_local_video:
                    position = 0;
                    break;
                case R.id.rb_net_video:
                    position = 1;
                    break;
                case R.id.rb_local_music:
                    position = 2;
                    break;
                case R.id.rb_net_music:
                    position = 3;
                    break;
                default:
                    position = 0;
                    break;
            }

            BaseFragment to = getFragment();
            switchFragment(mCurrentFragment,to);
        }

        private BaseFragment getFragment() {
            return mFragments.get(position);
        }

        private void switchFragment(Fragment from, BaseFragment to) {

            //此处实际上from永远不能和to相等，因为只有点击的radioButton改变，才会执行此方法。
            if(from != to){
                mCurrentFragment = to;
                FragmentTransaction mFragmentTransaction = getSupportFragmentManager().beginTransaction();
                if(from != null){
                    mFragmentTransaction.hide(from);
                }
                if(!to.isAdded()){
                    mFragmentTransaction.add(R.id.fl_main_content,mCurrentFragment);
                }else{
                    mFragmentTransaction.show(mCurrentFragment);
                }
                mFragmentTransaction.commit();
            }
        }
    }

    private void initFragment() {
        mFragments = new ArrayList<>();
        mFragments.add(new LocalVideoFragment());
        mFragments.add(new NetVideoFragment());
        mFragments.add(new LocalMusicFragment());
        mFragments.add(new CloudVillageFragment());
    }

    private void initView() {
        rg_bottom_tag = (RadioGroup)findViewById(R.id.rg_bottom_tag);

    }

    private boolean isExit = false;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(position != 0){
                rg_bottom_tag.check(R.id.rb_local_video);
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
}
