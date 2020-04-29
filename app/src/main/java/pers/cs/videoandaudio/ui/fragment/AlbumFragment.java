package pers.cs.videoandaudio.ui.fragment;


import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.base.BaseMusicFragment;
import pers.cs.videoandaudio.json.AlbumInfo;
import pers.cs.videoandaudio.service.MusicPlayer;
import pers.cs.videoandaudio.utils.MusicUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumFragment extends BaseMusicFragment {

    private View mView;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private AlbumAdapter mAlbumAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_album, container, false);
        recyclerView = mView.findViewById(R.id.recyclerview_album);
        layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        mAlbumAdapter = new AlbumAdapter(null);
        recyclerView.setAdapter(mAlbumAdapter);

        reloadAdapter();
        return mView;
    }

    private void reloadAdapter() {
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
                List<AlbumInfo> albumList = MusicUtils.queryAlbums(mContext);
                mAlbumAdapter.updateDataSet(albumList);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mAlbumAdapter.notifyDataSetChanged();
            }
        }.execute();
    }


    public class AlbumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<AlbumInfo> mList;

        public AlbumAdapter(List<AlbumInfo> list) {
            mList = list;
        }

        //更新adpter的数据
        public void updateDataSet(List<AlbumInfo> list) {
            this.mList = list;
        }


        //创建新View，被LayoutManager调用
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            return new ListItemViewHolder(LayoutInflater.from(viewGroup.getContext()).
                    inflate(R.layout.item_ablum, viewGroup, false));
        }



        //将数据与界面进行绑定
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            AlbumInfo model = mList.get(position);
            ((ListItemViewHolder) holder).tv_name.setText(model.getTitle().toString());
            ((ListItemViewHolder) holder).tv_text.setText(model.getSong_num() + "首 " + model.getAuthor());
            ((ListItemViewHolder) holder).img_ablum.setImageURI(Uri.parse(model.getPic_small() + ""));
            //根据播放中歌曲的专辑名判断当前专辑条目是否有播放的歌曲
            if (MusicPlayer.getArtistName() != null && MusicPlayer.getAlbumName().equals(model.getTitle())) {
                ((ListItemViewHolder) holder).img_option.setImageResource(R.drawable.song_play_icon);
            } else {
                ((ListItemViewHolder) holder).img_option.setImageResource(R.drawable.list_icn_more);
            }

        }

        //条目数量
        @Override
        public int getItemCount() {
            return mList == null ? 0 : mList.size();
        }

        //ViewHolder
        public class ListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            ImageView img_ablum,img_option;
            TextView tv_name, tv_text;

            ListItemViewHolder(View view) {
                super(view);
                this.img_ablum = view.findViewById(R.id.img_ablum);
                this.img_option = view.findViewById(R.id.img_ablum_option);
                this.tv_name = view.findViewById(R.id.tv_ablum_name);
                this.tv_text = view.findViewById(R.id.tv_ablum_text);


                img_option.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        MoreFragment morefragment = MoreFragment.newInstance(mList.get(getAdapterPosition()).album_id + "", IConstants.ALBUMOVERFLOW);
//                        morefragment.show(getFragmentManager(), "album");
                    }
                });
                view.setOnClickListener(this);

            }

            @Override
            public void onClick(View v) {

            }

        }
    }
}
