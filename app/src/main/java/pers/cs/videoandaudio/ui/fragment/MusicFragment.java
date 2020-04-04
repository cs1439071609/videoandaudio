package pers.cs.videoandaudio.ui.fragment;


import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
public class MusicFragment extends BaseFragment {

    private final String TAG = MusicFragment.class.getSimpleName();

    private ViewPager vp_music;
    private TabLayout mTabLayout;

    private  MyViewPageAdapter pagerAdapter;

    private String[] titles;
    private ArrayList<BaseFragment> fragments;

    @Override
    protected View initView() {
        View view = View.inflate(mContext,R.layout.fragment_music,null);

        vp_music = view.findViewById(R.id.vp_music);

        titles = new String[]{"我的","云村","发现"};
        fragments = new ArrayList<>();
        fragments.add(new LocalMusicFragment());
        fragments.add(new CloudVillageFragment());
        fragments.add(new NetMusicFragment());
        pagerAdapter = new MyViewPageAdapter(getChildFragmentManager());
        vp_music.setAdapter(pagerAdapter);


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
        mTabLayout.setupWithViewPager(vp_music,false);
        for(int i=0;i<titles.length;i++){
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            tab.setCustomView(R.layout.item_tab);
            TextView textView = tab.getCustomView().findViewById(R.id.tv_item_tab);
            textView.setText(titles[i]);
            if (i == 0) {
                // 设置第一个tab的TextView是被选择的样式
                textView.setTextAppearance(mContext,R.style.tab_selected);
            }
        }

    }

    @Override
    protected void initData() {
        super.initData();

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
