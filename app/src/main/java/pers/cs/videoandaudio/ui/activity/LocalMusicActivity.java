package pers.cs.videoandaudio.ui.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.base.BaseActivity;
import pers.cs.videoandaudio.ui.fragment.AlbumFragment;
import pers.cs.videoandaudio.ui.fragment.LocalMusicFragment;
import pers.cs.videoandaudio.utils.StatusBarUtil;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_SET_USER_VISIBLE_HINT;

public class LocalMusicActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tab_localmusic)
    TabLayout tabLocalmusic;
    @BindView(R.id.vp_localmusic)
    ViewPager vpLocalmusic;

    private MyViewPageAdapter mMyViewPageAdapter;
    private LocalMusicFragment localMusicFragment;
    private AlbumFragment mAlbumFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_music);
        ButterKnife.bind(this);

        showQuickControl(true);
        StatusBarUtil.changeStatusBarTextImgColor(this,true);

        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
//        ab.setHomeAsUpIndicator(R.drawable.actionbar_back);

        ab.setTitle("我的音乐");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        mMyViewPageAdapter = new MyViewPageAdapter(getSupportFragmentManager(),BEHAVIOR_SET_USER_VISIBLE_HINT);
        localMusicFragment = new LocalMusicFragment();
        mAlbumFragment = new AlbumFragment();
        mMyViewPageAdapter.addFragment(localMusicFragment,"单曲");
        mMyViewPageAdapter.addFragment(mAlbumFragment,"专辑");

        vpLocalmusic.setAdapter(mMyViewPageAdapter);

        tabLocalmusic.setupWithViewPager(vpLocalmusic);
    }



    class MyViewPageAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            mFragmentTitles.add(title);
        }

        public MyViewPageAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }

    }
}
