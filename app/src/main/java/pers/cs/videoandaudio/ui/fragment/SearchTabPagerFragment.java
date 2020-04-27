package pers.cs.videoandaudio.ui.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import pers.cs.videoandaudio.MyApplication;
import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.base.BaseFragment;
import pers.cs.videoandaudio.json.AlbumInfo;
import pers.cs.videoandaudio.json.SearchArtistInfo;
import pers.cs.videoandaudio.json.SearchSongInfo;
import pers.cs.videoandaudio.net.MA;
import pers.cs.videoandaudio.utils.OkHttpUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchTabPagerFragment extends BaseFragment {

    private static final String TAG = SearchTabPagerFragment.class.getSimpleName();
    private static final Boolean DEBUG = true;

    //内容视图
    private FrameLayout fl_search_hot_word;
    //加载View
    private View loading_view;
    //搜索关键词
    private String key;
    //页面：单曲、歌手、专辑
    private int page_number;
    private ArrayList<SearchSongInfo> songResults = new ArrayList<>();
    private ArrayList<SearchArtistInfo> artistResults = new ArrayList<>();
    private ArrayList<AlbumInfo> albumResults = new ArrayList<>();

    public static SearchTabPagerFragment newInstance(int page, String key) {

        Bundle args = new Bundle();
        args.putInt("page_number", page);
        args.putString("key", key);
        SearchTabPagerFragment fragment = new SearchTabPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected View initView() {

        View view = View.inflate(mContext,R.layout.fragment_search_hot_word,null);
        fl_search_hot_word = view.findViewById(R.id.fl_search_hot_word);
        //初始化内容视图为加载View
        loading_view = View.inflate(mContext,R.layout.loading_music_search,null);
        fl_search_hot_word.addView(loading_view);

        if (getArguments() != null) {
            key = getArguments().getString("key");
            page_number = getArguments().getInt("page_number");
        }
        search(key);
        return view;
    }

    private void search(String key) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {

                try {
                    JsonObject jsonObject = OkHttpUtil.getResposeJsonObject(MA.Search.searchMerge(key,1,10));

                    if(jsonObject != null){
                        if(DEBUG){
                            Log.d(TAG,"doInBackground:"+jsonObject.toString());
                        }
                        if(jsonObject != null) {
                            JsonObject resultObject = jsonObject.getAsJsonObject("result");
                            //专辑
                            JsonObject albumObject = resultObject.getAsJsonObject("album_info");
                            JsonArray albumList = albumObject.getAsJsonArray("album_list");
                            if(albumList != null){
                                for (JsonElement element : albumList) {
                                    AlbumInfo albumInfo = MyApplication.gsonInstance().fromJson(element, AlbumInfo.class);
                                    albumResults.add(albumInfo);
                                }
                            }

                            //艺术家
                            JsonObject artistObject = resultObject.getAsJsonObject("artist_info");
                            JsonArray artistList = artistObject.getAsJsonArray("artist_list");
                            if(artistList != null){
                                for (JsonElement element : artistList) {
                                    SearchArtistInfo artistInfo = MyApplication.gsonInstance().fromJson(element, SearchArtistInfo.class);
                                    artistResults.add(artistInfo);
                                }
                            }

                            //歌曲
                            JsonObject songObject = resultObject.getAsJsonObject("song_info");
                            JsonArray songList = songObject.getAsJsonArray("song_list");
                            if(songList != null){
                                for (JsonElement element : songList) {
                                    SearchSongInfo songInfo = MyApplication.gsonInstance().fromJson(element, SearchSongInfo.class);
                                    songResults.add(songInfo);
                                }
                            }

                            if(DEBUG){
                                Log.d(TAG,"doInBackground:------------------------");
                            }
                            return true;
                        }
                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                }

//                JsonElement jsonElement =
//                MyApplication.gsonInstance().fromJson();

                return false;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if(aBoolean){

                    try{
                        View view = View.inflate(mContext,R.layout.fragment_search_tab_pager,null);
                        TabLayout tabs = view.findViewById(R.id.tabs);
                        ViewPager viewPager = view.findViewById(R.id.viewpager);
                        if(viewPager != null){
                            //https://blog.csdn.net/MoLiao2046/article/details/103899277
                            MyViewPageAdapter myViewPageAdapter = new MyViewPageAdapter(getChildFragmentManager(),FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
                            myViewPageAdapter.addFragment(SearchMusicFragment.newInstance(songResults), "单曲");
                            viewPager.setAdapter(myViewPageAdapter);

                        }
                        tabs.setupWithViewPager(viewPager);
                        tabs.selectTab(tabs.getTabAt(page_number));

                        fl_search_hot_word.removeAllViews();
                        fl_search_hot_word.addView(view);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
        }.execute();
    }

    //离开释放资源
    class MyViewPageAdapter extends FragmentStatePagerAdapter {
        private final List<BaseFragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();
        public MyViewPageAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }
        public void addFragment(BaseFragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}
