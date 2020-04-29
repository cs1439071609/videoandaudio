package pers.cs.videoandaudio.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import pers.cs.videoandaudio.MyApplication;
import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.base.BaseActivity;
import pers.cs.videoandaudio.handler.HandlerUtil;
import pers.cs.videoandaudio.info.MusicInfo;
import pers.cs.videoandaudio.json.GeDanGeInfo;
import pers.cs.videoandaudio.net.MA;
import pers.cs.videoandaudio.service.MusicPlayer;
import pers.cs.videoandaudio.utils.NetUtil;
import pers.cs.videoandaudio.utils.OkHttpUtil;

public class PlaylistActivity extends BaseActivity {
    private static final String TAG = PlaylistActivity.class.getSimpleName();
    private static final Boolean DEBUG = true;
    @BindView(R.id.album_title)
    TextView albumTitle;
    @BindView(R.id.album_details)
    TextView albumDetails;
    @BindView(R.id.playlist_art)
    ImageView playlistArt;

    private String playlsitId;
    private String albumPath, playlistName, playlistDetail;
    private boolean isLocalPlaylist;
    private String playlistCount;

    private Toolbar toolbar;
    private ActionBar ab;
    private View loadView;
    private FrameLayout loadFrameLayout;

    private LoadNetPlaylistInfo mLoadNetList;
    private ArrayList<GeDanGeInfo> mList = new ArrayList<GeDanGeInfo>();
    private int musicCount;
    private PlaylistDetailAdapter mAdapter;
    private ArrayList<MusicInfo> adapterList = new ArrayList<>();
    private RecyclerView mRecyclerView;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getExtras() != null) {
            isLocalPlaylist = getIntent().getBooleanExtra("islocal", false);
            playlsitId = getIntent().getStringExtra("playlistid");
            albumPath = getIntent().getStringExtra("albumart");
            playlistName = getIntent().getStringExtra("playlistname");
            playlistDetail = getIntent().getStringExtra("playlistDetail");
            playlistCount = getIntent().getStringExtra("playlistcount");

        }
        setContentView(R.layout.activity_playlist);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar1);
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
        showQuickControl(true);
        if (playlistName != null && playlistDetail != null && albumPath != null) {
            albumTitle.setText(playlistName);
            albumDetails.setText(playlistDetail);
            RequestOptions options = new RequestOptions()
                    .placeholder(R.mipmap.placeholder_disk_300)
                    .error(R.mipmap.placeholder_disk_300)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

            Glide.with(this).load(Uri.parse(albumPath))
                    .apply(options)
                    .into(playlistArt);
        }
        mHandler = HandlerUtil.getInstance(this);
        loadFrameLayout = findViewById(R.id.content_play_list);
        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        mAdapter = new PlaylistDetailAdapter(PlaylistActivity.this, adapterList);
        mRecyclerView.setAdapter(mAdapter);
        loadAllLists();
    }

    private void loadAllLists() {


        if (NetUtil.isConnectInternet(this)) {

            loadView = LayoutInflater.from(this).inflate(R.layout.loading_music_search, loadFrameLayout, false);
            //            loadFrameLayout.addView(loadView);
            mLoadNetList = new LoadNetPlaylistInfo();
            mLoadNetList.execute(0);

        }
    }

    class LoadNetPlaylistInfo extends AsyncTask<Integer, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Integer... params) {
            try {
                JsonObject jsonObject;
                if(params[0] == 1){
                    Log.d(TAG, "doInBackground: 缓存");
                    jsonObject = OkHttpUtil.getResposeJsonObject1(MA.GeDan.geDanInfo(playlsitId + ""),MyApplication.context,true);
                }else{
                    Log.d(TAG, "doInBackground: 网络");
                    jsonObject = OkHttpUtil.getResposeJsonObject(MA.GeDan.geDanInfo(playlsitId + ""));
                }

                if (DEBUG) {
                    Log.d(TAG, "LoadNetPlaylistInfo: " + jsonObject.toString());
                }
                JsonArray pArray = jsonObject.get("content").getAsJsonArray();
                playlistDetail = jsonObject.get("desc").getAsString();
                musicCount = pArray.size();
                for (int i = 0; i < musicCount; i++) {
                    GeDanGeInfo geDanGeInfo = MyApplication.gsonInstance().fromJson(pArray.get(i), GeDanGeInfo.class);
                    mList.add(geDanGeInfo);
                    //                    RequestThreadPool.post(new MusicDetailInfoGet(geDanGeInfo.getSong_id(), i, sparseArray));
                }
                //                int tryCount = 0;
                //                while (sparseArray.size() != musicCount && tryCount < 1000 && !isCancelled()) {
                //                    tryCount++;
                //                    try {
                //                        Thread.sleep(30);
                //                    } catch (InterruptedException e) {
                //                        e.printStackTrace();
                //                    }
                //                }


                //                if (sparseArray.size() == musicCount) {
                for (int i = 0; i < mList.size(); i++) {
                    try {
                        MusicInfo musicInfo = new MusicInfo();
                        musicInfo.songId = Integer.parseInt(mList.get(i).getSong_id());
                        musicInfo.musicName = mList.get(i).getTitle();
                        musicInfo.artist = mList.get(i).getAuthor();
                        musicInfo.albumData = mList.get(i).getPic_radio();
//                        musicInfo.setLrc(mList.get(i).get);
                        musicInfo.setIslocal(false);
                        adapterList.add(musicInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean complete) {
            if (DEBUG) {
                Log.d(TAG, "onPostExecute:" + complete);
            }
            if (!complete) {
//                mLoadNetList.execute(1);
            } else {
                mAdapter.updateDataSet(adapterList);

            }
        }

        public void cancleTask() {

            cancel(true);
            //            RequestThreadPool.finish();
            //            Log.e(TAG, " cancled task , + thread" + Thread.currentThread().getName());
        }
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
                        if(MusicPlayer.isPlaying() && MusicPlayer.getCurrentAudioId() == arraylist.get(getAdapterPosition()).getSongId()){
                            startActivity(new Intent(mContext,PlayingActivity.class));
                        }else{
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
                        }
                    }
                }, 70);
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mLoadNetList != null){
            mLoadNetList.cancleTask();
        }
    }
}


