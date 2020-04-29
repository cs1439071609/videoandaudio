package pers.cs.videoandaudio.ui.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.base.BaseActivity;
import pers.cs.videoandaudio.info.MusicInfo;
import pers.cs.videoandaudio.service.MusicPlayer;
import pers.cs.videoandaudio.utils.MusicUtils;
import pers.cs.videoandaudio.utils.StatusBarUtil;

public class RecentActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerview_recent)
    RecyclerView recyclerviewRecent;
    @BindView(R.id.tv_recent_no)
    TextView tvRecentNo;

    private LinearLayoutManager layoutManager;
    private int currentlyPlayingPosition = 0;

//    private List<Song> mList;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("最近播放");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        StatusBarUtil.changeStatusBarTextImgColor(this, true);

        layoutManager = new LinearLayoutManager(this);
        recyclerviewRecent.setLayoutManager(layoutManager);
        recyclerviewRecent.setHasFixedSize(true);

        Toast.makeText(this, "待开发", Toast.LENGTH_SHORT).show();
        new loadSongs().execute("");
    }

    private class loadSongs extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            tvRecentNo.setVisibility(View.VISIBLE);
            //            recyclerviewRecent.setAdapter(mAdapter);
            recyclerviewRecent.addItemDecoration(new DividerItemDecoration(RecentActivity.this, DividerItemDecoration.VERTICAL));
        }

    }

    public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final static int FIRST_ITEM = 0;
        private final static int ITEM = 1;
        private List<MusicInfo> mList;


        public ItemAdapter(List<MusicInfo> list) {
            if (list == null) {
                throw new IllegalArgumentException("model Data must not be null");
            }
            this.mList = list;
        }

        //更新adpter的数据
        public void updateDataSet(List<MusicInfo> list) {
            this.mList = list;
            notifyDataSetChanged();
        }

        //判断布局类型
        @Override
        public int getItemViewType(int position) {
            return position == 0 ? FIRST_ITEM : ITEM;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == FIRST_ITEM) {
                return new CommonItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.header_play, parent, false));
            } else {
                return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            if (holder instanceof CommonItemViewHolder) {
                ((CommonItemViewHolder) holder).tv_play_number.setText("(共" + mList.size() + "首)");
                ((CommonItemViewHolder) holder).img_select.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            } else if (holder instanceof ItemViewHolder) {
                ((ItemViewHolder) holder).tv_artist.setText("");
                ((ItemViewHolder) holder).tv_name.setText("");
                //判断该条目音乐是否在播放
                if (MusicPlayer.getCurrentAudioId() == 1) {
                    ((ItemViewHolder) holder).img_play_state.setVisibility(View.VISIBLE);
                    currentlyPlayingPosition = position;
                } else {
                    ((ItemViewHolder) holder).img_play_state.setVisibility(View.GONE);
                }
            }

        }

        @Override
        public int getItemCount() {
            return (null != mList ? mList.size() + 1 : 0);
        }

        public class CommonItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView tv_play_number;
            ImageView img_select;

            public CommonItemViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_play_number = itemView.findViewById(R.id.tv_play_number);
                img_select = itemView.findViewById(R.id.img_select);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                long[] list = new long[mList.size()];
                HashMap<Long, MusicInfo> infos = new HashMap();
                for (int i = 0; i < mList.size(); i++) {
                    //                    MusicInfo info = MusicUtils.getMusicInfo(RecentActivity.this, mList.get(i).id);
                    MusicInfo info = null;
                    list[i] = info.songId;
                    info.islocal = true;
                    info.albumData = MusicUtils.getAlbumArtUri(info.albumId) + "";
                    infos.put(list[i], info);
                }
                MusicPlayer.playAll(infos, list, 0, false);
            }
        }


        class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            ImageView img_music_option, img_play_state;
            TextView tv_name, tv_artist;

            public ItemViewHolder(@NonNull View itemView) {
                super(itemView);
                img_music_option = itemView.findViewById(R.id.img_music_option);
                img_play_state = itemView.findViewById(R.id.img_play_state);
                tv_name = itemView.findViewById(R.id.tv_name);
                tv_artist = itemView.findViewById(R.id.tv_artist);
                img_music_option.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //                        MoreFragment morefragment = MoreFragment.newInstance(mList.get(getAdapterPosition() - 1).id + "", IConstants.MUSICOVERFLOW);
                        //                        morefragment.show(getSupportFragmentManager(), "music");
                    }
                });
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                long[] list = new long[mList.size()];
                HashMap<Long, MusicInfo> infos = new HashMap();
                for (int i = 0; i < mList.size(); i++) {
                    //                    MusicInfo info = MusicUtils.getMusicInfo(RecentActivity.this, mList.get(i).id);
                    MusicInfo info = null;
                    list[i] = info.songId;
                    info.islocal = true;
                    info.albumData = MusicUtils.getAlbumArtUri(info.albumId) + "";
                    infos.put(list[i], info);
                }
                if (getAdapterPosition() > 0) {
                    MusicPlayer.playAll(infos, list, getAdapterPosition() - 1, false);
                }

            }
        }
    }
}
