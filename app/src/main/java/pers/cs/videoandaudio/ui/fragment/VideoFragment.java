package pers.cs.videoandaudio.ui.fragment;


import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.base.BaseFragment;
import pers.cs.videoandaudio.ui.activity.Main3Activity;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFragment extends BaseFragment {
    private final String TAG = VideoFragment.class.getSimpleName();

    private ViewPager vp_video;
    private TabLayout mTabLayout;

    private MyViewPageAdapter pagerAdapter;

    private String[] titles;
    private ArrayList<BaseFragment> fragments;



    @Override
    protected View initView() {
        View view = View.inflate(mContext,R.layout.fragment_video,null);
        vp_video = view.findViewById(R.id.vp_video);

        titles = new String[]{"我的","发现"};
        fragments = new ArrayList<>();
        fragments.add(new LocalVideoFragment());
        fragments.add(new NetVideoFragment());
        pagerAdapter = new MyViewPageAdapter(getChildFragmentManager());
        vp_video.setAdapter(pagerAdapter);

        Main3Activity main3Activity = (Main3Activity) getActivity();
        if(main3Activity != null){
            mTabLayout = main3Activity.findViewById(R.id.tl_main3);
        }

        initTabLayout();
        return view;
    }
    private void initTabLayout() {
        mTabLayout.removeAllTabs();
        for(int i=0;i<titles.length;i++){
            mTabLayout.addTab(mTabLayout.newTab());
        }
        mTabLayout.setupWithViewPager(vp_video,false);
        for(int i=0;i<titles.length;i++){
            mTabLayout.getTabAt(i).setText(titles[i]);
        }
    }
    private int currentItem = 0;
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.d(TAG, "onHiddenChanged: ");
        //
        if(!hidden){
            initTabLayout();
            //            viewPager.setCurrentItem(currentItem);
            mTabLayout.selectTab(mTabLayout.getTabAt(currentItem));
        }else{
            //隐藏
            //            currentItem = viewPager.getCurrentItem();
            currentItem = mTabLayout.getSelectedTabPosition();
        }
    }
    class MyViewPageAdapter extends FragmentPagerAdapter {


        public MyViewPageAdapter(@NonNull FragmentManager fm) {
            super(fm);
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
    }
}
