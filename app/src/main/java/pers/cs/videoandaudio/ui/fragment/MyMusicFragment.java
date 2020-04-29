package pers.cs.videoandaudio.ui.fragment;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.adapter.MyMusicFragmentAdapter;
import pers.cs.videoandaudio.base.BaseMusicFragment;
import pers.cs.videoandaudio.bean.MyMusicPlaylistItem;
import pers.cs.videoandaudio.bean.MyMusicTitleInfo;
import pers.cs.videoandaudio.utils.CommonUtils;
import pers.cs.videoandaudio.utils.MusicUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyMusicFragment extends BaseMusicFragment {

    private static final String TAG = MyMusicFragment.class.getSimpleName();
    private static final Boolean DEBUG = true;

    private View view;
    private RecyclerView recyclerView;
    private MyMusicFragmentAdapter mMyMusicFragmentAdapter;
    //前4个
    private List<MyMusicTitleInfo> titleLists = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.fragment_my_music, container, false);

        recyclerView = view.findViewById(R.id.recyclerview_mymusic);
        mMyMusicFragmentAdapter = new MyMusicFragmentAdapter(mContext);
        recyclerView.setAdapter(mMyMusicFragmentAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(mContext,DividerItemDecoration.VERTICAL));
        reloadAdapter();
        return view;
    }

    private void reloadAdapter() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                ArrayList results = new ArrayList();
                setMusicInfo();
                ArrayList<MyMusicPlaylistItem> playlists = new ArrayList<>();
                playlists.add(new MyMusicPlaylistItem(0,"我喜欢的音乐",0,"???","local"));
                ArrayList<MyMusicPlaylistItem> netPlaylists = new ArrayList<>();
                results.addAll(titleLists);
                results.add("创建的歌单");
                results.addAll(playlists);
                if (netPlaylists != null) {
                    results.add("收藏的歌单");
                    results.addAll(netPlaylists);
                }

                if (mMyMusicFragmentAdapter == null) {
                    mMyMusicFragmentAdapter = new MyMusicFragmentAdapter(mContext);
                }
                mMyMusicFragmentAdapter.updateResults(results, playlists, netPlaylists);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (mContext == null)
                    return;
                mMyMusicFragmentAdapter.notifyDataSetChanged();
            }
        }.execute();
    }






    /**
     * 设置我的音乐前几个条目
     */
    private void setMusicInfo() {
        if (CommonUtils.isLollipop() && ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            loadCount(false);
        } else {
            loadCount(true);
        }
    }
    private void loadCount(boolean has) {
        int localMusicCount = 0, recentMusicCount = 0, downLoadCount = 0, artistsCount = 0;
        if (has) {
            try {
                localMusicCount = MusicUtils.queryMusicInfo(mContext, MusicUtils.START_FROM_LOCAL).size();
                recentMusicCount = 0;
                downLoadCount = 0;
                artistsCount = MusicUtils.queryArtist(mContext).size();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setInfo(mContext.getResources().getString(R.string.local_music), localMusicCount, R.drawable.music_icn_local, 0);
        setInfo(mContext.getResources().getString(R.string.recent_play), recentMusicCount, R.drawable.music_icn_recent, 1);
        setInfo(mContext.getResources().getString(R.string.local_manage), downLoadCount, R.drawable.music_icn_dld, 2);
        setInfo(mContext.getResources().getString(R.string.my_artist), artistsCount, R.drawable.music_icn_artist, 3);
    }
    //为info设置数据，并放入mlistInfo
    private void setInfo(String title, int count, int id, int i) {
        MyMusicTitleInfo myMusicTitleInfo = new MyMusicTitleInfo();
        myMusicTitleInfo.setTitle(title);
        myMusicTitleInfo.setCount(count);
        myMusicTitleInfo.setAvatar(id);
        if (titleLists.size() < 4) {
            titleLists.add(new MyMusicTitleInfo());
        }
        //将新的info对象加入到信息列表中
        titleLists.set(i, myMusicTitleInfo);
    }

}
