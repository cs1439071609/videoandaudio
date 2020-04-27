package pers.cs.videoandaudio.ui.fragment;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.info.MusicInfo;
import pers.cs.videoandaudio.service.MusicPlayer;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayQueueFragment extends DialogFragment implements View.OnClickListener {

    private static final String TAG = PlayQueueFragment.class.getSimpleName();
    private static final Boolean DEBUG = true;

    private Context mContext;
    private TextView tv_add;
    private TextView tv_clear_all;
    private TextView tv_size;
    private RecyclerView recyclerView;
    private ArrayList<MusicInfo> playlist;
    private PlaylistAdapter mPlaylistAdapter;

    private MusicInfo musicInfo;
    private int currentlyPlayingPosition = 0;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置样式
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CustomMoreDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //设置从底部弹出
        WindowManager.LayoutParams params = getDialog().getWindow()
                .getAttributes();
        params.gravity = Gravity.BOTTOM;
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setAttributes(params);

        View view = inflater.inflate(R.layout.fragment_play_queue, container, false);
        tv_add = view.findViewById(R.id.tv_playlist_addto);
        tv_clear_all = view.findViewById(R.id.tv_playlist_clearall);
        recyclerView = view.findViewById(R.id.recyclerView_play_list);
        tv_size = view.findViewById(R.id.tv_playlist);
        tv_add.setOnClickListener(this);
        tv_clear_all.setOnClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setHasFixedSize(true);

        new loadSongs().execute();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //设置fragment高度 、宽度
        int dialogHeight = (int) (mContext.getResources().getDisplayMetrics().heightPixels * 0.6);
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, dialogHeight);
        getDialog().setCanceledOnTouchOutside(true);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_playlist_addto:
                Toast.makeText(mContext, "收藏测试", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_playlist_clearall:
                Toast.makeText(mContext, "清空测试", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    private class loadSongs extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            if (mContext != null) {
                HashMap<Long, MusicInfo> play = MusicPlayer.getPlayinfos();
                if (play != null && play.size() > 0) {
                    long[] queue = MusicPlayer.getQueue();
                    int len = queue.length;
                    playlist = new ArrayList<>();
                    for (int i = 0; i < len; i++) {
                        playlist.add(play.get(queue[i]));
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (playlist != null && playlist.size() > 0) {
                mPlaylistAdapter = new PlaylistAdapter(playlist);
                recyclerView.setAdapter(mPlaylistAdapter);
                recyclerView.addItemDecoration(new DividerItemDecoration(mContext,DividerItemDecoration.VERTICAL));
                tv_size.setText("播放列表"+"(" + playlist.size() + ")");

                for (int i = 0; i < playlist.size(); i++) {
                    MusicInfo info = playlist.get(i);
                    if (info != null && MusicPlayer.getCurrentAudioId() == info.songId) {
                        recyclerView.scrollToPosition(i);
                    }
                }
            }
        }
    }

    class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ItemViewHolder> {

        private ArrayList<MusicInfo> list;

        public PlaylistAdapter(ArrayList<MusicInfo> list){
            this.list = list;
        }

        public void updateDataSet(ArrayList<MusicInfo> list){
            this.list = list;
            this.notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_fragment_playlist, parent, false));
        }

        @Override
        public int getItemCount() {
            return playlist == null ? 0 : playlist.size();
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            musicInfo = list.get(position);
            if(musicInfo != null){
                holder.musicName.setText(musicInfo.getMusicName());
                holder.musicArtist.setText("-" + musicInfo.getArtist());
                if(MusicPlayer.getCurrentAudioId() == musicInfo.getSongId()){
                    holder.isplay.setVisibility(View.VISIBLE);
                    currentlyPlayingPosition = position;
                }else{
                    holder.isplay.setVisibility(View.GONE);
                }
            }else{
                holder.isplay.setVisibility(View.GONE);
            }
        }

        class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            ImageView isplay;
            ImageView delete;
            TextView musicName;
            TextView musicArtist;
            public ItemViewHolder(@NonNull View itemView) {
                super(itemView);
                this.isplay = itemView.findViewById(R.id.img_isplay);
                this.delete = itemView.findViewById(R.id.img_playlist_delete);
                this.musicName = itemView.findViewById(R.id.tv_playlist_name);
                this.musicArtist = itemView.findViewById(R.id.tv_playlist_artist);

                itemView.setOnClickListener(this);
                this.delete.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                    }
                });
            }

            @Override
            public void onClick(View v) {
                int index = getAdapterPosition();
                if(index == -1){

                    return;
                }

                MusicPlayer.setQueuePosition(index);

                notifyItemChanged(currentlyPlayingPosition);
                notifyItemChanged(index);

            }
        }
    }
}
