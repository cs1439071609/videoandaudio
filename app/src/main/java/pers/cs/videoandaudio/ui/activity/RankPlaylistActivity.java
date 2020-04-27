package pers.cs.videoandaudio.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;

import pers.cs.videoandaudio.MyApplication;
import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.base.BaseActivity;
import pers.cs.videoandaudio.handler.HandlerUtil;
import pers.cs.videoandaudio.info.MusicInfo;
import pers.cs.videoandaudio.json.BillboardJson;
import pers.cs.videoandaudio.net.MA;
import pers.cs.videoandaudio.service.MusicPlayer;
import pers.cs.videoandaudio.utils.OkHttpUtil;

public class RankPlaylistActivity extends BaseActivity {
    
    private static final String TAG = RankPlaylistActivity.class.getSimpleName();
    private static final Boolean DEBUG = true;
    
    private TextView playlistTitleView, playlistDetailView;

    private ActionBar ab;

    private RecyclerView mRecyclerView;

    private Handler mHandler;

    private PlaylistDetailAdapter mAdapter;
    private ArrayList<BillboardJson> mList = new ArrayList<BillboardJson>();
    private ArrayList<MusicInfo> adapterList = new ArrayList<>();

    private String playlistName, playlistDetail;
    private int musicCount;

    private LoadNetPlaylistInfo mLoadNetPlaylistInfo;

//    private ArrayList<BillboardJson> mList = new ArrayList<BillboardJson>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getExtras() != null) {

        }
        setContentView(R.layout.activity_rank_playlist);
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);

            ab = getSupportActionBar();
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeAsUpIndicator(R.drawable.actionbar_back);
            ab.setTitle("歌单");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        setHeaderView();

        mHandler = HandlerUtil.getInstance(this);
        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        mAdapter = new PlaylistDetailAdapter(RankPlaylistActivity.this, adapterList);
        mRecyclerView.setAdapter(mAdapter);
        loadAllLists();
    }

    private void loadAllLists() {
        mLoadNetPlaylistInfo = new LoadNetPlaylistInfo();
        mLoadNetPlaylistInfo.execute(0);
    }

    Runnable showInfo = new Runnable() {
        @Override
        public void run() {
            playlistTitleView.setText(playlistName);
            playlistDetailView.setText(playlistDetail);
        }
    };

    class LoadNetPlaylistInfo extends AsyncTask<Integer, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Integer... params) {
            try {
                JsonObject jsonObject;
                if(params[0] == 1){
                    jsonObject = OkHttpUtil.getResposeJsonObject1(MA.Billboard.billSongList(2, 0, 100),MyApplication.context,true);
                    Log.d(TAG, "doInBackground: 缓存");
                }else{
                    jsonObject = OkHttpUtil.getResposeJsonObject(MA.Billboard.billSongList(2, 0, 100));
                    Log.d(TAG, "doInBackground: 网络");
                }

                JsonArray pArray = jsonObject.get("song_list").getAsJsonArray();

                JsonObject jsonObject1 = jsonObject.get("billboard").getAsJsonObject();

                playlistName = jsonObject1.get("name").getAsString();
                try {
                    playlistDetail = "最近更新:" + jsonObject1.get("update_date").getAsString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mHandler.post(showInfo);

                musicCount = pArray.size();
                for (int i = 0; i < musicCount; i++) {
                    BillboardJson billboardJson = MyApplication.gsonInstance().fromJson(pArray.get(i), BillboardJson.class);
                    mList.add(billboardJson);
                    //                    RequestThreadPool.post(new MusicDetailInfoGet(billboardJson.getSong_id(), i, sparseArray));
                }
                for (int i = 0; i < mList.size(); i++) {
                    MusicInfo musicInfo = new MusicInfo();
                    musicInfo.songId = Integer.parseInt(mList.get(i).getSong_id());
                    musicInfo.musicName = mList.get(i).getTitle();
                    musicInfo.artist = mList.get(i).getArtist_name();
                    musicInfo.islocal = false;
                    musicInfo.albumName = mList.get(i).getAlbum_title();
                    musicInfo.albumId = Integer.parseInt(mList.get(i).getAlbum_id());
                    musicInfo.artistId = Integer.parseInt(mList.get(i).getArtist_id());
                    musicInfo.lrc = mList.get(i).getLrclink();
                    musicInfo.albumData = mList.get(i).getPic_radio();
                    adapterList.add(musicInfo);
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean complete) {
            if (!complete) {
//                mLoadNetPlaylistInfo.execute(1);
            } else {
                mAdapter.updateDataSet(adapterList);
            }
        }

        public void cancleTask() {
            cancel(true);
//            RequestThreadPool.finish();
        }
    }

    private void setHeaderView() {
        playlistTitleView = findViewById(R.id.album_title);
        playlistDetailView = findViewById(R.id.album_details);
    }

    class PlaylistDetailAdapter extends RecyclerView.Adapter<PlaylistDetailAdapter.ItemViewHolder> {
        private ArrayList<MusicInfo> arraylist;
        private Activity mContext;

        public PlaylistDetailAdapter(Activity context, ArrayList<MusicInfo> mList) {
            this.arraylist = mList;
            this.mContext = context;
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            return new ItemViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_playlist_detail, viewGroup, false));
        }


        @Override
        public void onBindViewHolder(final ItemViewHolder itemHolder, final int i) {
            final MusicInfo localItem = arraylist.get(i);
            itemHolder.title.setText(localItem.musicName);
            itemHolder.artist.setText(localItem.artist);
            itemHolder.trackNumber.setText("" + (i + 1));
            itemHolder.menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }

        @Override
        public int getItemCount() {
            return arraylist == null ? 0 : arraylist.size();
        }

        public void updateDataSet(ArrayList<MusicInfo> arraylist) {
            this.arraylist = arraylist;

            this.notifyDataSetChanged();
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            protected TextView title, artist, trackNumber;
            protected ImageView menu;

            public ItemViewHolder(View view) {
                super(view);
                this.title = view.findViewById(R.id.song_title);
                this.artist = view.findViewById(R.id.song_artist);
                this.trackNumber = view.findViewById(R.id.trackNumber);
                this.menu = view.findViewById(R.id.popup_menu);
                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        HashMap<Long, MusicInfo> infos = new HashMap<Long, MusicInfo>();
                        int len = arraylist.size();
                        long[] list = new long[len];
                        for (int i = 0; i < len; i++) {
                            MusicInfo info = arraylist.get(i);
                            list[i] = info.songId;
                            infos.put(list[i], info);
                        }
                        if (getAdapterPosition() >= 0)
                            MusicPlayer.playAll(infos, list, getAdapterPosition(), false);
                        startActivity(new Intent(mContext,PlayingActivity.class));
                    }
                }, 70);
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLoadNetPlaylistInfo.cancleTask();
    }
}
