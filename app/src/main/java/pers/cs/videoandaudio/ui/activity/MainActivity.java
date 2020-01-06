package pers.cs.videoandaudio.ui.activity;

import android.os.Bundle;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.base.BaseFragment;
import pers.cs.videoandaudio.ui.fragment.LocalMusicFragment;
import pers.cs.videoandaudio.ui.fragment.LocalVideoFragment;
import pers.cs.videoandaudio.ui.fragment.NetMusicFragment;
import pers.cs.videoandaudio.ui.fragment.NetVideoFragment;

/**
 * @author chensen
 * @time 2020/1/5  23:44
 * @desc
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
        mFragments.add(new NetMusicFragment());
    }

    private void initView() {
        rg_bottom_tag = (RadioGroup)findViewById(R.id.rg_bottom_tag);

    }


}
