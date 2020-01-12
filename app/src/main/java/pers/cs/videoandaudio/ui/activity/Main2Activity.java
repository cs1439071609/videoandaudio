package pers.cs.videoandaudio.ui.activity;

import android.os.Bundle;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.base.BasePager;
import pers.cs.videoandaudio.ui.Pager.LocalMusicPager;
import pers.cs.videoandaudio.ui.Pager.LocalVideoPager;
import pers.cs.videoandaudio.ui.Pager.NetMusicPager;
import pers.cs.videoandaudio.ui.Pager.NetVideoPager;
import pers.cs.videoandaudio.ui.fragment.ReplaceFragment;

/**
 * @author chensen
 *
 * @time 2020/1/6  21:29
 *
 * @desc 框架2 使用RadioGroup+Pager
 *
 */

public class Main2Activity extends AppCompatActivity {


    private RadioGroup rg_bottom_tag;

    private List<BasePager> mBasePagers;
    private int position = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        initView();
        initPage();
        setListener();

    }

    private void setListener() {
        rg_bottom_tag.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        rg_bottom_tag.check(R.id.rb_local_video);
    }

    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
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

            setFragment();
        }
    }

    private void setFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fl_main_content,new ReplaceFragment(mBasePagers.get(position)));
        ft.commit();
    }


    /**
     * 此时已经执行initView方法
     */
    private void initPage() {
        mBasePagers = new ArrayList<>();
        mBasePagers.add(new LocalVideoPager(this));
        mBasePagers.add(new NetVideoPager(this));
        mBasePagers.add(new LocalMusicPager(this));
        mBasePagers.add(new NetMusicPager(this));

    }

    private void initView() {
        rg_bottom_tag = (RadioGroup) findViewById(R.id.rg_bottom_tag);
    }
}
