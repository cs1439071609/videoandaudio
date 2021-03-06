package pers.cs.videoandaudio.ui.fragment;


import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;
import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.adapter.CloudVillageFragmentAdapter;
import pers.cs.videoandaudio.base.BaseFragment;
import pers.cs.videoandaudio.bean.VillageBean;
import pers.cs.videoandaudio.utils.CacheUtils;
import pers.cs.videoandaudio.utils.Constants;

public class CloudVillageFragment extends BaseFragment {

    private static final String TAG = CloudVillageFragment.class.getSimpleName();

    private View view;
    private ListView lv_village;
    private TextView tv_village;
    private ProgressBar pb_village;

    private List<VillageBean.VillageItem> villageItems;
    private CloudVillageFragmentAdapter mCloudVillageFragmentAdapter;


    @Override
    protected View initView() {
        view = View.inflate(mContext,R.layout.fragment_cloud_village,null);
//        x.view().inject(CloudVillageFragment.this,view);
        lv_village = view.findViewById(R.id.lv_village);
        tv_village = view.findViewById(R.id.tv_village);
        pb_village = view.findViewById(R.id.pb_village);

        lv_village.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //                Jzvd.onScrollReleaseAllVideos(view, firstVisibleItem, visibleItemCount, totalItemCount);

                if (JzvdStd.CURRENT_JZVD == null) return;
                int lastVisibleItem = firstVisibleItem + visibleItemCount;
                int currentPlayPosition = Jzvd.CURRENT_JZVD.positionInList;
                //                Log.e(TAG, "onScrollReleaseAllVideos: " +
                //                        currentPlayPosition + " " + firstVisibleItem + " " + currentPlayPosition + " " + lastVisibleItem);
                if (currentPlayPosition >= 0) {
                    if ((currentPlayPosition < firstVisibleItem || currentPlayPosition > (lastVisibleItem - 1))) {
                        if (JzvdStd.CURRENT_JZVD.screen != JzvdStd.SCREEN_FULLSCREEN) {
                            JzvdStd.releaseAllVideos();//为什么最后一个视频横屏会调用这个，其他地方不会
                        }
                    }
                }
            }
        });

        return view;
    }

    @Override
    protected void initData() {
        super.initData();

        String saveJson = CacheUtils.getString(mContext,Constants.NET_ALL_RES_URL);
        if(!TextUtils.isEmpty(saveJson)){
            processData(saveJson);
        }
        getDataFromNet();

    }

    private void getDataFromNet() {
        RequestParams params = new RequestParams(Constants.NET_ALL_RES_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: "+result);
                CacheUtils.putString(mContext,Constants.NET_ALL_RES_URL,result);
                //主线程
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: "+ex.getMessage());
                showData();

            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.e(TAG, "onCancelled: "+cex.getMessage());

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void showData() {
        if (villageItems != null && villageItems.size() > 0) {
            mCloudVillageFragmentAdapter = new CloudVillageFragmentAdapter(mContext, villageItems);
            lv_village.setAdapter(mCloudVillageFragmentAdapter);
            //隐藏文字
            tv_village.setVisibility(View.GONE);

        } else {
            tv_village.setVisibility(View.VISIBLE);
        }
        pb_village.setVisibility(View.GONE);
    }

    private void processData(String json) {
        VillageBean villageBean = parseJson(json);
        villageItems = villageBean.getList();
        showData();
    }

    private VillageBean parseJson(String json) {
        return new Gson().fromJson(json,VillageBean.class);
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        //hide、show变化重置
        //Fragment切换时重置
//        Jzvd.releaseAllVideos();
        if(hidden){
            JzvdStd.goOnPlayOnPause();
        }else{
            JzvdStd.goOnPlayOnResume();
        }


    }
}
