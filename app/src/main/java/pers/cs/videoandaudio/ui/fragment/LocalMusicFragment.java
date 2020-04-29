package pers.cs.videoandaudio.ui.fragment;


import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.adapter.LocalAudioFragmentAdapter;
import pers.cs.videoandaudio.base.BaseMusicFragment;
import pers.cs.videoandaudio.handler.HandlerUtil;
import pers.cs.videoandaudio.info.MusicInfo;
import pers.cs.videoandaudio.service.MusicPlayer;
import pers.cs.videoandaudio.ui.activity.PlayingActivity;
import pers.cs.videoandaudio.utils.MusicUtils;

/**
 * @author chensen
 *
 * @time 2020/1/6  16:00
 *
 * @desc 本地音乐
 *
 */

public class LocalMusicFragment extends BaseMusicFragment {

    private static final String TAG = LocalMusicFragment.class.getSimpleName();

    private View view;
    private TextView tv_local_music;
    private ProgressBar pb_local_music;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private Adapter mAdapter;

    private ListView lv_local_music;
    private LocalAudioFragmentAdapter mLocalAudioFragmentAdapter;

    private ArrayList<MusicInfo> mMusicInfos;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_local_music, container, false);
        tv_local_music = view.findViewById(R.id.tv_local_music);
        lv_local_music = view.findViewById(R.id.lv_local_music);
        pb_local_music = view.findViewById(R.id.pb_local_music);

        mRecyclerView = view.findViewById(R.id.recyclerview_local_music);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new Adapter(null);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));


        //        lv_local_music.setOnItemClickListener(new MyOnItemClickListener());

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDataFromLocal();
    }



    private void getDataFromLocal() {
        mMusicInfos = new ArrayList<>();
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                mMusicInfos = MusicUtils.queryMusicInfo(mContext,"",MusicUtils.START_FROM_LOCAL);


                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (mMusicInfos != null && mMusicInfos.size() > 0) {
//                    mLocalAudioFragmentAdapter = new LocalAudioFragmentAdapter(mContext, getFragmentManager(),mMusicInfos);
//                    lv_local_music.setAdapter(mLocalAudioFragmentAdapter);
                    mAdapter.updateDataSet(mMusicInfos);
                    mAdapter.notifyDataSetChanged();

                    //隐藏文字
                    tv_local_music.setVisibility(View.GONE);

                } else {
                    tv_local_music.setVisibility(View.VISIBLE);
                    tv_local_music.setText("无本地音乐...");
                }
                pb_local_music.setVisibility(View.GONE);
            }
        }.execute();
    }

    @Override
    public void updateTrackInfo() {
        super.updateTrackInfo();
//        mAdapter.notifyItemChanged(MusicPlayer.getQueuePosition()+1);
        mAdapter.notifyDataSetChanged();
    }

    public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        final static int FIRST_ITEM = 0;
        final static int ITEM = 1;

        private ArrayList<MusicInfo> mList;

        PlayMusic playMusic;
        Handler handler;

        public Adapter(ArrayList<MusicInfo> list) {
            handler = HandlerUtil.getInstance(mContext);
            mList = list;
        }

        //更新adpter的数据
        public void updateDataSet(ArrayList<MusicInfo> list) {
            this.mList = list;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            if (viewType == FIRST_ITEM)
                return new CommonItemViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.header_play, viewGroup, false));

            else {
                return new ListItemViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_music, viewGroup, false));
            }
        }

        //判断布局类型
        @Override
        public int getItemViewType(int position) {
            return position == 0 ? FIRST_ITEM : ITEM;
        }

        //将数据与界面进行绑定
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MusicInfo model = null;
            if (position > 0) {
                model = mList.get(position - 1);
            }
            if (holder instanceof ListItemViewHolder) {

                ((ListItemViewHolder) holder).tv_name.setText(model.getMusicName());
                ((ListItemViewHolder) holder).tv_artist.setText(model.getArtist());

                //判断该条目音乐是否在播放
                if (MusicPlayer.getCurrentAudioId() == model.getSongId()) {
                    ((ListItemViewHolder) holder).img_play_state.setVisibility(View.VISIBLE);
                } else {
                    ((ListItemViewHolder) holder).img_play_state.setVisibility(View.GONE);
                }

            } else if (holder instanceof CommonItemViewHolder) {
                ((CommonItemViewHolder) holder).tv_play_number.setText("(共" + mList.size() + "首)");
                ((CommonItemViewHolder) holder).img_select.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

            }
        }

        @Override
        public int getItemCount() {
            return (null != mList ? mList.size() + 1 : 0);
        }


        public class CommonItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView tv_play_number;
            ImageView img_select;

            CommonItemViewHolder(View view) {
                super(view);
                this.tv_play_number = view.findViewById(R.id.tv_play_number);
                this.img_select = view.findViewById(R.id.img_select);
                view.setOnClickListener(this);
            }

            public void onClick(View v) {
                if (playMusic != null)
                    handler.removeCallbacks(playMusic);
                if (getAdapterPosition() > -1) {
                    playMusic = new PlayMusic(0);
                    handler.postDelayed(playMusic, 70);
                }
            }

        }


        public class ListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            //ViewHolder
            ImageView img_music_option;
            TextView tv_name, tv_artist;
            ImageView img_play_state;


            ListItemViewHolder(View view) {
                super(view);
                this.img_play_state = view.findViewById(R.id.img_play_state);
                this.img_music_option = view.findViewById(R.id.img_music_option);
                this.tv_name = view.findViewById(R.id.tv_name);
                this.tv_artist = view.findViewById(R.id.tv_artist);


                img_music_option.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MoreFragment morefragment = MoreFragment.newInstance(mList.get(getAdapterPosition() - 1));
                        morefragment.show(getFragmentManager(), "music");
                    }
                });
                view.setOnClickListener(this);

            }

            @Override
            public void onClick(View v) {
                if(MusicPlayer.isPlaying() && MusicPlayer.getCurrentAudioId() == mMusicInfos.get(getAdapterPosition() - 1).getSongId()){
                    Intent intent = new Intent(mContext, PlayingActivity.class);
                    startActivity(intent);
                }else{
                    if (playMusic != null)
                        handler.removeCallbacks(playMusic);
                    if (getAdapterPosition() > -1) {
                        playMusic = new PlayMusic(getAdapterPosition() - 1);
                        handler.postDelayed(playMusic, 70);
                    }
                }

            }

        }

        class PlayMusic implements Runnable {
            int position;

            public PlayMusic(int position) {
                this.position = position;
            }

            @Override
            public void run() {
                long[] list = new long[mList.size()];
                HashMap<Long, MusicInfo> infos = new HashMap();
                for (int i = 0; i < mList.size(); i++) {
                    MusicInfo info = mList.get(i);
                    list[i] = info.songId;
                    info.islocal = true;
                    info.albumData = MusicUtils.getAlbumArtUri(info.albumId) + "";
                    infos.put(list[i], mList.get(i));
                }
                if (position > -1)
                    MusicPlayer.playAll(infos, list, position, false);

            }
        }
    }



    class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            //            Intent intent = new Intent(mContext, AudioPlayerActivity.class);
            //            Intent intent = new Intent(mContext, AudioPlayer1Activity.class);
            //            Log.d(TAG, "onItemClick: "+"chufa"+position);
            //            intent.putExtra("position", position);
            //            startActivity(intent);

            if(MusicPlayer.isPlaying() && MusicPlayer.getCurrentAudioId() == mMusicInfos.get(position).getSongId()){
                Intent intent = new Intent(mContext, PlayingActivity.class);
                startActivity(intent);
            }else{
                HashMap<Long, MusicInfo> infos = new HashMap<Long, MusicInfo>();
                int len = mMusicInfos.size();
                long[] list = new long[len];
                for (int i = 0; i < len; i++) {
                    MusicInfo info = mMusicInfos.get(i);
                    list[i] = info.songId;
                    infos.put(list[i], info);
                }
                MusicPlayer.playAll(infos, list, position, false);
            }


        }
    }
    /**
     * 废弃
     */
    private void getDataFromLocal1() {

        mMusicInfos = new ArrayList<>();

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

//                        AudioItem audioItem = new AudioItem(
//                                cursor.getString(0),
//                                cursor.getString(1),
//                                cursor.getString(2),
//                                cursor.getString(4));
//                        audioItem.setArtist(cursor.getString(3));
//
//                        mMusicInfos.add(audioItem);
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
                        mMusicInfos.add(audioItem);
                    }

                    cursor1.close();

                }
*/
                //发送消息
//                mHandler.sendEmptyMessage(0);

            }
        }.start();
    }

    /**
     * 废弃
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            if (mMusicInfos != null && mMusicInfos.size() > 0) {
                mLocalAudioFragmentAdapter = new LocalAudioFragmentAdapter(mContext, getFragmentManager(),mMusicInfos);
                lv_local_music.setAdapter(mLocalAudioFragmentAdapter);
                //隐藏文字
                tv_local_music.setVisibility(View.GONE);
            } else {
                tv_local_music.setVisibility(View.VISIBLE);
                tv_local_music.setText("无本地音乐...");
            }
            pb_local_music.setVisibility(View.GONE);

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }
}
