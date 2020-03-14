package pers.cs.videoandaudio.ui.fragment;


import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.adapter.LocalVideoFragmentAdapter;
import pers.cs.videoandaudio.base.BaseFragment;
import pers.cs.videoandaudio.bean.VideoItem;
import pers.cs.videoandaudio.ui.activity.VitamioVideoPlayerActivity;

/**
 * @author chensen
 * @time 2020/1/6  15:54
 * @desc
 */


public class LocalVideoFragment extends BaseFragment {

    private static final String TAG = LocalVideoFragment.class.getSimpleName();
    private View view;
    private ListView lv_local_video;
    private TextView tv_local_video;
    private ProgressBar pb_local_video;

    private LocalVideoFragmentAdapter mLocalVideoFragmentAdapter;
    private List<VideoItem> mVideoItems;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            if (mVideoItems != null && mVideoItems.size() > 0) {
                mLocalVideoFragmentAdapter = new LocalVideoFragmentAdapter(mContext, mVideoItems);
                lv_local_video.setAdapter(mLocalVideoFragmentAdapter);
                //隐藏文字
                tv_local_video.setVisibility(View.GONE);

            } else {
                tv_local_video.setVisibility(View.VISIBLE);
            }
            pb_local_video.setVisibility(View.GONE);

        }
    };

    @Override
    protected View initView() {
        Log.d(TAG, "initView: " + "...");
        view = View.inflate(mContext, R.layout.fragment_local_video, null);
        tv_local_video = view.findViewById(R.id.tv_local_video);
        lv_local_video = view.findViewById(R.id.lv_local_video);
        pb_local_video = view.findViewById(R.id.pb_local_video);

        lv_local_video.setOnItemClickListener(new MyOnItemClickListener());

        return view;
    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            VideoItem videoItem = mVideoItems.get(position);

            //            调用系统所有播放器-隐示意图
            //            startSystemAll(videoItem.getData());

            //            调用自己的播放器-显示意图 -- 一个播放地址
            //            Intent intent = new Intent(mContext, SystemVideoPlayerActivity.class);
            //            intent.setDataAndType(Uri.parse(videoItem.getData()),"video/*");
            //            startActivity(intent);

            //传递列表数据--对象，序列化
//            Intent intent = new Intent(mContext, SystemVideoPlayerActivity.class);
            Intent intent = new Intent(mContext, VitamioVideoPlayerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("valist", (Serializable) mVideoItems);

            intent.putExtras(bundle);
            intent.putExtra("position", position);
            startActivity(intent);
        }
    }

    /**
     * 调用系统的播放器，Intent-filter与此相同的播放器也可被调
     *
     * @param data
     */
    private void startSystemAll(String data) {
        Intent intent = new Intent();
        intent.setDataAndType(Uri.parse(data), "video/*");
        startActivity(intent);
    }

    @Override
    protected void initData() {
        Log.d(TAG, "initData: " + "...");
        super.initData();

        getDataFromLocal();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        for (int i = 0; i < permissions.length; i++) {
            Log.i("MainActivity", "申请的权限为：" + permissions[i] + ",申请结果：" + grantResults[i]);
        }


    }

    /**
     * 从本地sdcard得到数据
     * 1.遍历sdcard，后缀名
     * 2.从内容提供者中获取
     * 使用MediaScanner。
     * Android系统在SD卡插入或开机后，MediaScanner服务会在后台自动扫描SD上的文件资源，
     * 将SD上的音乐媒体信息加入到MediaStore数据库中
     * <p>
     * <p>
     * 如果是6.0系统，动态获取读取sdcard权限
     */
    private void getDataFromLocal() {

        mVideoItems = new ArrayList<>();
        isGrantExternalRW((Activity) mContext);
        new Thread() {
            @Override
            public void run() {
                super.run();
                //SystemClock.sleep(1000);
                //查询数据
                ContentResolver resolver = mContext.getContentResolver();
//                Uri uri = MediaStore.Video.Media.INTERNAL_CONTENT_URI;
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

                //注意：此处使用MediaStore.Video.VideoColumns而不是MediaStore.Video.Media
                String keys[] = {
                        MediaStore.Video.Media.DISPLAY_NAME,
                        MediaStore.Video.Media.SIZE,
                        MediaStore.Video.Media.DURATION,
                        MediaStore.Video.Media.DATA};//地址
                Cursor cursor = resolver.query(uri, keys, null, null, null);
                if (cursor != null) {

                    while (cursor.moveToNext()) {

                        VideoItem videoItem = new VideoItem(
                                cursor.getString(0),
                                cursor.getString(1),
                                cursor.getString(2),
                                cursor.getString(3));

                        mVideoItems.add(videoItem);
                    }

                    cursor.close();

                }
                uri = MediaStore.Video.Media.INTERNAL_CONTENT_URI;
                Cursor cursor1 = resolver.query(uri, keys, null, null, null);
                if (cursor1 != null) {

                    while (cursor1.moveToNext()) {

                        VideoItem videoItem = new VideoItem(
                                cursor1.getString(0),
                                cursor1.getString(1),
                                cursor1.getString(2),
                                cursor1.getString(3));

                        mVideoItems.add(videoItem);
                    }

                    cursor1.close();

                }
                //发送消息
                mHandler.sendEmptyMessage(0);

            }
        }.start();


    }


    /**
     * 如果版本大于6.0，需要动态获取权限
     *
     * @param activity
     * @return
     */
    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

            return false;
        }

        return true;
    }
}
