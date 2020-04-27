package pers.cs.videoandaudio.ui.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.base.BaseFragment;

/**
 * @author chensen
 *
 * @time 2020/1/6  16:00
 *
 * @desc
 *
 */

public class NetMusicFragment extends BaseFragment {

    private static final String TAG = NetMusicFragment.class.getSimpleName();
    private static final Boolean DEBUG = true;

    private int page = 0;

    private View mView;
    private ViewPager viewPager;
    private RecommendFragment recommendFragment;

    private boolean isFirstLoad = true;

    /**
     * onCreateView后执行
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager.setCurrentItem(page);
    }

    @Override
    protected View initView() {
        Log.d(TAG, "initView: " + "...");

        mView = View.inflate(mContext, R.layout.fragment_net_music, null);
        viewPager = mView.findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
            viewPager.setOffscreenPageLimit(2);
        }

        final TabLayout tabLayout =  mView.findViewById(R.id.tabs);
//        tabLayout.setTabTextColors(R.color.text_color, ThemeUtils.getThemeColorStateList(mContext, R.color.theme_color_primary).getDefaultColor());
//        tabLayout.setSelectedTabIndicatorColor(ThemeUtils.getThemeColorStateList(mContext, R.color.theme_color_primary).getDefaultColor());
        tabLayout.setupWithViewPager(viewPager);

        return mView;
    }
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getChildFragmentManager(),FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        recommendFragment = new RecommendFragment();
//        recommendFragment.setChanger(this);
        adapter.addFragment(recommendFragment, "新曲");
//        adapter.addFragment(new AllPlaylistFragment(), "歌单");
        adapter.addFragment(new RankingFragment(), "排行榜");
        viewPager.setAdapter(adapter);

    }

    @Override
    protected void initData() {
        Log.d(TAG, "initData: " + "...");

        super.initData();

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if(DEBUG){
            Log.d(TAG,"setUserVisibleHint:"+isVisibleToUser);
        }
        super.setUserVisibleHint(isVisibleToUser);
        if (recommendFragment == null) {
            return;
        }
        if (isVisibleToUser) {
            if(DEBUG){
                Log.d(TAG,"setUserVisibleHint:");
            }
//            recommendFragment.requestData();

        }
    }




    static class Adapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }


        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}
