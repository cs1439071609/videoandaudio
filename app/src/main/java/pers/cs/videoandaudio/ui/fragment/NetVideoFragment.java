package pers.cs.videoandaudio.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.adapter.NetVideoFragmentAdapter;
import pers.cs.videoandaudio.base.BaseFragment;
import pers.cs.videoandaudio.bean.VideoItem;
import pers.cs.videoandaudio.ui.View.XListView;
import pers.cs.videoandaudio.ui.activity.SystemVideoPlayerActivity;
import pers.cs.videoandaudio.utils.CacheUtils;
import pers.cs.videoandaudio.utils.Constants;
import pers.cs.videoandaudio.utils.TimeUtil;

/**
 * @author chensen
 *
 * @time 2020/1/6  16:05
 *
 * @desc
 *
 */

public class NetVideoFragment extends BaseFragment {


    private static final String TAG = NetVideoFragment.class.getSimpleName();
    private View mView;

    private TextView tv_net_video;
    private XListView lv_net_video;
    private ProgressBar pb_net_video;

    private NetVideoFragmentAdapter mNetVideoFragmentAdapter;
    private List<VideoItem> mItemList;

    //加载更多
    private boolean isLoadMore = false;


    @Override
    protected View initView() {
        Log.d(TAG, "initView: " + "...");
        mView = View.inflate(mContext, R.layout.fragment_net_video, null);

        x.view().inject(NetVideoFragment.this,mView);

        tv_net_video = mView.findViewById(R.id.tv_net_video);
        lv_net_video = mView.findViewById(R.id.lv_net_video);
        pb_net_video = mView.findViewById(R.id.pb_net_video);

        lv_net_video.setOnItemClickListener(new MyOnItemClickListener());
        lv_net_video.setPullLoadEnable(true);
        lv_net_video.setXListViewListener(new MyIXListViewListener());

        return mView;
    }

    class MyIXListViewListener implements XListView.IXListViewListener {

        @Override
        public void onRefresh() {
            getDataFromNet();
            onLoad();
        }

        @Override
        public void onLoadMore() {
            getMoreDataFromNet();
        }
    }

    private void getMoreDataFromNet() {
        RequestParams requestParams = new RequestParams(Constants.NET_URL);
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: "+result);

                isLoadMore = true;
                //主线程
                processData(result);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: "+ex.getMessage());

                isLoadMore = false;

                lv_net_video.stopLoadMore();

            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.e(TAG, "onCancelled: "+cex.getMessage());
                isLoadMore = false;


            }

            @Override
            public void onFinished() {
                Log.e(TAG, "onFinishedaaa: ");
                isLoadMore = false;

            }
        });

    }

    private void onLoad() {
        lv_net_video.stopRefresh();
        lv_net_video.stopLoadMore();
        lv_net_video.setRefreshTime(new TimeUtil().getSystemTime());
    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            //VideoItem videoItem = mItemList.get(position);

            //            调用系统所有播放器-隐示意图
            //            startSystemAll(videoItem.getData());

            //            调用自己的播放器-显示意图 -- 一个播放地址
            //            Intent intent = new Intent(mContext, SystemVideoPlayerActivity.class);
            //            intent.setDataAndType(Uri.parse(videoItem.getData()),"video/*");
            //            startActivity(intent);

            //传递列表数据--对象，序列化
            Intent intent = new Intent(mContext, SystemVideoPlayerActivity.class);
            //            Intent intent = new Intent(mContext, VitamioVideoPlayerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("videolist", (Serializable) mItemList);

            intent.putExtras(bundle);
            intent.putExtra("position", position-1);
            startActivity(intent);
        }
    }

    @Override
    protected void initData() {
        Log.d(TAG, "initData: " + "...");
        super.initData();

        String saveJson = CacheUtils.getString(mContext,Constants.NET_URL);
        if(!TextUtils.isEmpty(saveJson)){
            processData(saveJson);
        }
        getDataFromNet();


    }

    private void getDataFromNet() {
        //联网请求在子线程
        RequestParams requestParams = new RequestParams(Constants.NET_URL);
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: "+result);

                CacheUtils.putString(mContext,Constants.NET_URL,result);
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


    private void processData(String json) {

        if(!isLoadMore){
            mItemList = parseJson(json);

            showData();

        }else{

            isLoadMore = false;
            mItemList.addAll(parseJson(json));
            mNetVideoFragmentAdapter.notifyDataSetChanged();
            onLoad();
        }



    }

    private void showData() {
        if (mItemList != null && mItemList.size() > 0) {
            mNetVideoFragmentAdapter = new NetVideoFragmentAdapter(mContext, mItemList);
            lv_net_video.setAdapter(mNetVideoFragmentAdapter);
            //隐藏文字
            tv_net_video.setVisibility(View.GONE);

        } else {
            tv_net_video.setVisibility(View.VISIBLE);
        }
        pb_net_video.setVisibility(View.GONE);
    }

    /**
     * 解决Json数据：
     * 1.用系统接口解析json数据
     * 2.使用第三方解析工具Gson(Google)，fastjson(alibaba)
     * @param json
     * @return
     */
    private List<VideoItem> parseJson(String json) {
        List<VideoItem> list = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            //使用jsonObject.getJSONArray("trailers")时，如果"trailers"不存在会崩溃。
            JSONArray jsonArray = jsonObject.optJSONArray("trailers");
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObjectItem = jsonArray.optJSONObject(i);

                    if(jsonObjectItem != null){
                        VideoItem videoItem = new VideoItem();

                        String movieName = jsonObjectItem.getString("movieName");
                        videoItem.setName(movieName);
                        String coverImg = jsonObjectItem.getString("coverImg");
                        videoItem.setImageUrl(coverImg);
                        String hightUrl = jsonObjectItem.getString("hightUrl");
                        videoItem.setData(hightUrl);
                        String videoDesc = jsonObjectItem.getString("videoTitle");
                        videoItem.setDesc(videoDesc);
                        list.add(videoItem);
                    }
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
