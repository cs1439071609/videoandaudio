package pers.cs.videoandaudio.ui.fragment;


import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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

import java.util.ArrayList;
import java.util.List;

import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.adapter.LocalAudioFragmentAdapter;
import pers.cs.videoandaudio.base.BaseFragment;
import pers.cs.videoandaudio.bean.AudioItem;
import pers.cs.videoandaudio.ui.activity.AudioPlayer1Activity;

/**
 * @author chensen
 *
 * @time 2020/1/6  16:00
 *
 * @desc
 *
 */

public class LocalMusicFragment extends BaseFragment {

    private static final String TAG = LocalMusicFragment.class.getSimpleName();

    private View view;
    private ListView lv_local_video;
    private TextView tv_local_video;
    private ProgressBar pb_local_video;

    private LocalAudioFragmentAdapter mLocalAudioFragmentAdapter;
    private List<AudioItem> mAudioItems;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            if (mAudioItems != null && mAudioItems.size() > 0) {
                mLocalAudioFragmentAdapter = new LocalAudioFragmentAdapter(mContext, mAudioItems);
                lv_local_video.setAdapter(mLocalAudioFragmentAdapter);
                //隐藏文字
                tv_local_video.setVisibility(View.GONE);





            } else {
                tv_local_video.setVisibility(View.VISIBLE);
                tv_local_video.setText("无本地音乐...");
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

//            Intent intent = new Intent(mContext, AudioPlayerActivity.class);
            Intent intent = new Intent(mContext, AudioPlayer1Activity.class);
            Log.d(TAG, "onItemClick: "+"chufa"+position);
            intent.putExtra("position", position);
            startActivity(intent);

        }
    }

    @Override
    protected void initData() {
        Log.d(TAG, "initData: " + "...");

        super.initData();

        getDataFromLocal();
    }


    private void getDataFromLocal() {

        mAudioItems = new ArrayList<>();

        new Thread() {
            @Override
            public void run() {
                super.run();
                //SystemClock.sleep(1000);
                //查询数据
                ContentResolver resolver = mContext.getContentResolver();
//                Uri uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;


                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                //注意：此处使用MediaStore.Video.VideoColumns而不是MediaStore.Video.Media
                String keys[] = {
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.ALBUM_ID};//地址
                Cursor cursor = resolver.query(uri, keys, null, null, null);
                if (cursor != null) {

                    while (cursor.moveToNext()) {

                        AudioItem audioItem = new AudioItem(
                                cursor.getString(0),
                                cursor.getString(1),
                                cursor.getString(2),
                                cursor.getString(4));
                        audioItem.setArtist(cursor.getString(3));

                        mAudioItems.add(audioItem);
                    }

                    cursor.close();

                }
                /*uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
                Cursor cursor1 = resolver.query(uri, keys, null, null, null);
                if (cursor1 != null) {

                    while (cursor1.moveToNext()) {

                        AudioItem audioItem = new AudioItem(
                                cursor1.getString(0),
                                cursor1.getString(1),
                                cursor1.getString(2),
                                cursor1.getString(4));
                        audioItem.setArtist(cursor1.getString(3));
                        mAudioItems.add(audioItem);
                    }

                    cursor1.close();

                }
*/
                //发送消息
                mHandler.sendEmptyMessage(0);

            }
        }.start();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }
}
