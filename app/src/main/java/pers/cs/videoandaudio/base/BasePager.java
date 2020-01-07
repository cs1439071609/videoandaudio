package pers.cs.videoandaudio.base;

import android.content.Context;
import android.view.View;

/**
 * @author chensen
 * @time 2020/1/6  20:42
 * @desc 框架2基类
 */
public abstract class BasePager {


    public boolean isInitDate;
    public View mView;
    public final Context mContext;


    public BasePager(Context context){
        this.mContext = context;
        mView = initView();
    }

    public abstract View initView();

    public void initDate(){

    }
}
