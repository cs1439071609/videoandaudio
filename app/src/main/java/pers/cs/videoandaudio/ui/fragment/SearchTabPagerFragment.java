package pers.cs.videoandaudio.ui.fragment;


import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;

import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.base.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchTabPagerFragment extends BaseFragment {

    //内容视图
    private FrameLayout fl_search_hot_word;
    //加载View
    private View loading_view;

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
        return view;
    }

}
