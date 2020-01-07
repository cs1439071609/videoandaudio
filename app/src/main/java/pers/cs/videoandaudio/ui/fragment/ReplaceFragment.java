package pers.cs.videoandaudio.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import pers.cs.videoandaudio.base.BasePager;

/**
 * @author chensen
 * @time 2020/1/6  21:50
 * @desc 框架2中替换
 */
public class ReplaceFragment extends Fragment {
    private static final String TAG = ReplaceFragment.class.getSimpleName();
    private BasePager basePager;

    public ReplaceFragment(BasePager pager) {
        this.basePager=pager;
    }


    /**
     *
     * initView()和initDate()均执行一次
     * 但此方法执行多次
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: " + "...");
        //isInitDate让其数据只初始化一次
        if(basePager != null && !basePager.isInitDate){
            basePager.initDate();
            basePager.isInitDate = true;
        }
        if(basePager != null){
            return basePager.mView;
        }
        return null;
    }

}
