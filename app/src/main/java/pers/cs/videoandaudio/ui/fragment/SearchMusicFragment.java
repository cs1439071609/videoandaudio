package pers.cs.videoandaudio.ui.fragment;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;

import pers.cs.videoandaudio.MyApplication;
import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.base.BaseFragment;
import pers.cs.videoandaudio.info.MusicInfo;
import pers.cs.videoandaudio.json.MusicDetailInfo;
import pers.cs.videoandaudio.json.SearchSongInfo;
import pers.cs.videoandaudio.net.MA;
import pers.cs.videoandaudio.service.MusicPlayer;
import pers.cs.videoandaudio.ui.activity.PlayingActivity;
import pers.cs.videoandaudio.utils.OkHttpUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchMusicFragment extends BaseFragment {
    
    private static final String TAG = SearchMusicFragment.class.getSimpleName();
    private static final Boolean DEBUG = true;

    private View view;
    private RecyclerView recyclerview;

    private ArrayList<SearchSongInfo> songInfos;

    public static SearchMusicFragment newInstance(ArrayList<SearchSongInfo> songResults) {
        
        Bundle args = new Bundle();
        args.putParcelableArrayList("searchMusic",songResults);
        SearchMusicFragment fragment = new SearchMusicFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View initView() {
        view = View.inflate(mContext,R.layout.fragment_search_music,null);
        if (getArguments() != null) {
            songInfos = getArguments().getParcelableArrayList("searchMusic");
        }

        recyclerview = view.findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(mContext));

        MusicAdapter mAdapter = new MusicAdapter(songInfos);
        recyclerview.setAdapter(mAdapter);

        recyclerview.addItemDecoration(new DividerItemDecoration(mContext,DividerItemDecoration.HORIZONTAL));
        return view;
    }
    public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ListItemViewHolder> {

        final static int FIRST_ITEM = 0;
        final static int ITEM = 1;
        private ArrayList<SearchSongInfo> mList;

        public MusicAdapter(ArrayList<SearchSongInfo> list) {
            if (list == null) {
                throw new IllegalArgumentException("model Data must not be null");
            }
            mList = list;
        }

        //更新adpter的数据
        public void updateDataSet(ArrayList<SearchSongInfo> list) {
            this.mList = list;
        }

        @Override
        public ListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            //            if (viewType == FIRST_ITEM)
            //                return new CommonItemViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.common_item, viewGroup, false));

            return new ListItemViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_music_search, viewGroup, false));
        }
        //判断布局类型
        //@Override
        //public int getItemViewType(int position) {
        //  return position == FIRST_ITEM ? FIRST_ITEM : ITEM;
        // }

        @Override
        public int getItemCount() {
            return (null != mList ? mList.size() : 0);
        }

        //将数据与界面进行绑定
        @Override
        public void onBindViewHolder(ListItemViewHolder holder, int position) {
            SearchSongInfo model = mList.get(position);
            holder.mainTitle.setText(model.getTitle());
            holder.title.setText(model.getAuthor());
        }

        public class ListItemViewHolder extends RecyclerView.ViewHolder {
            //ViewHolder
            ImageView moreOverflow, playState;
            TextView mainTitle, title;
            MusicDetailInfo info = null;
            ListItemViewHolder(View view) {
                super(view);
                this.mainTitle = (TextView) view.findViewById(R.id.list_toptext);
                this.title = (TextView) view.findViewById(R.id.list_bottom_text);

                this.moreOverflow = (ImageView) view.findViewById(R.id.list_button);

                moreOverflow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final SearchSongInfo model = mList.get(getAdapterPosition());
//                        Down.downMusic(MainApplication.context, model.getSong_id() + "", model.getTitle(), model.getAuthor());

                        //                        new AlertDialog.Builder(mContext).setTitle("要下载音乐吗").
                        //                                setPositiveButton(mContext.getString(R.string.sure), new DialogInterface.OnClickListener() {
                        //                                    @Override
                        //                                    public void onClick(DialogInterface dialog, int which) {
                        //                                        Down.downMusic(MainApplication.context, model.getSong_id() + "", model.getTitle(), model.getAuthor());
                        //                                        dialog.dismiss();
                        //                                    }
                        //                                }).
                        //                                setNegativeButton(mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        //                                    @Override
                        //                                    public void onClick(DialogInterface dialog, int which) {
                        //                                        dialog.dismiss();
                        //                                    }
                        //                                }).show();
                    }
                });
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final SearchSongInfo model = mList.get(getAdapterPosition());
                        new AsyncTask<Void, Void, Void>() {

                            @Override
                            protected Void doInBackground(Void... params) {
                                MusicInfo musicInfo = new MusicInfo();
                                try {

                                    JsonObject jsonObject = OkHttpUtil.getResposeJsonObject(MA.Song.songBaseInfo(model.getSong_id()))
                                            .get("result").getAsJsonObject().get("items").getAsJsonArray().get(0).getAsJsonObject();
                                    if(DEBUG){
                                        Log.d(TAG,"musicinfo-net:"+MA.Song.songBaseInfo(model.getSong_id()));
                                    }
                                    info = MyApplication.gsonInstance().fromJson(jsonObject, MusicDetailInfo.class);


                                    musicInfo.albumData = info.getPic_small();
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }

                                musicInfo.songId = Integer.parseInt(model.getSong_id());
                                musicInfo.musicName = model.getTitle();
                                musicInfo.artist = model.getAuthor();
                                musicInfo.islocal = false;
                                musicInfo.albumName = model.getAlbum_title();
                                musicInfo.albumId = Integer.parseInt(model.getAlbum_id());
                                musicInfo.artistId = Integer.parseInt(model.getArtist_id());
                                musicInfo.lrc = model.getLrclink();

                                HashMap<Long, MusicInfo> infos = new HashMap<Long, MusicInfo>();
                                long[] list = new long[1];
                                list[0] = musicInfo.songId;
                                infos.put(list[0], musicInfo);
                                MusicPlayer.playAll(infos, list, 0, false);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                startActivity(new Intent(mContext, PlayingActivity.class));
//                                MediaPlayer mMediaPlayer = new MediaPlayer();
//                                try {
//
//                                    mMediaPlayer.setDataSource(info.getSong_source());
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                                try {
//                                    mMediaPlayer.prepare();
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                                mMediaPlayer.start();
                            }
                        }.execute();
                    }
                });

            }


        }
    }


}
