package pers.cs.videoandaudio.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.bean.MyMusicPlaylistItem;
import pers.cs.videoandaudio.bean.MyMusicTitleInfo;
import pers.cs.videoandaudio.ui.activity.LocalMusicActivity;
import pers.cs.videoandaudio.ui.activity.RecentActivity;

/**
 * @author chensen
 * @time 2020/4/28  21:03
 * @desc 我的音乐
 */
public class MyMusicFragmentAdapter extends RecyclerView.Adapter<MyMusicFragmentAdapter.ItemHolder>{

    private Context mContext;
    private ArrayList itemResults = new ArrayList();

    //创建歌单
    private boolean createdExpanded = true;
    private ArrayList<MyMusicPlaylistItem> playlists = new ArrayList<>();
    //收藏歌单
    private boolean collectExpanded = true;
    private ArrayList<MyMusicPlaylistItem> netplaylists = new ArrayList<>();

    private boolean isLoveList = true;


    public MyMusicFragmentAdapter(Context context) {
        this.mContext = context;
    }

    public void updateResults(ArrayList itemResults, ArrayList<MyMusicPlaylistItem> playlists, ArrayList<MyMusicPlaylistItem> netplaylists) {
        isLoveList = true;
        this.itemResults = itemResults;
        this.playlists = playlists;
        this.netplaylists = netplaylists;
    }
    public void updatePlaylists(ArrayList<MyMusicPlaylistItem> playlists) {
        this.playlists = playlists;
    }

    @Override
    public int getItemCount() {
        if (itemResults == null) {
            return 0;
        }
        if (!createdExpanded && playlists != null) {
            itemResults.removeAll(playlists);
        }
        if (!collectExpanded) {
            itemResults.removeAll(netplaylists);
        }
        return itemResults.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (getItemCount() == 0) {
            return -1;
        }
        if(position == 5 && itemResults.get(5) instanceof MyMusicPlaylistItem){
            return 4;
        }

        if (itemResults.get(position) instanceof MyMusicTitleInfo){
            return 0;
        }
        if (itemResults.get(position) instanceof MyMusicPlaylistItem) {
            return 1;
        }
        if (itemResults.get(position) instanceof String) {
            if (((String) itemResults.get(position)).equals("收藏的歌单")){
                return 3;
            }
        }
        return 2;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                View v0 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mymusic_main, parent, false);
                ItemHolder ml0 = new ItemHolder(v0);
                return ml0;
            case 1:
//                if (isLoveList) {
//                    View v1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mymusic_love, parent, false);
//                    ItemHolder ml1 = new ItemHolder(v1);
//                    return ml1;
//                }
                View v1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mymusic_list, parent, false);
                ItemHolder ml1 = new ItemHolder(v1);
                return ml1;
            case 2:
                View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mymusic_expandable, parent, false);
                ItemHolder ml2 = new ItemHolder(v2);
                return ml2;
            case 3:
                View v3 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mymusic_expandable, parent, false);
                ItemHolder ml3 = new ItemHolder(v3);
                return ml3;
            case 4:
//                if(){
//
//                }
                View v4 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mymusic_love, parent, false);
                ItemHolder ml4 = new ItemHolder(v4);
                return ml4;

        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        switch (getItemViewType(position)) {
            case 0:
                MyMusicTitleInfo myMusicTitleInfo = (MyMusicTitleInfo) itemResults.get(position);
                holder.img_mymusic_main.setImageResource(myMusicTitleInfo.getAvatar());
                holder.tv_mymusic_main_title.setText(myMusicTitleInfo.getTitle());
                holder.tv_mymusic_main_count.setText("(" + myMusicTitleInfo.getCount() + ")");
                setOnListener(holder,position);
                break;
            case 1:
                MyMusicPlaylistItem playlist = (MyMusicPlaylistItem) itemResults.get(position);
//                if (isLoveList) {
//                    if (playlist.getAlbum() != null){
//                        holder.img_mymusic_love.setImageURI(Uri.parse(playlist.getAlbum()));
//                    }
//                    holder.tv_mymusic_love_count.setText(playlist.getSongCount() + "首");
//                    isLoveList = false;
//                }else{
                    if (createdExpanded && playlist.getAuthor().equals("local")) {
                        if (playlist.getAlbum() != null){
                            holder.img_mymusic_list_album.setImageURI(Uri.parse(playlist.getAlbum()));
                        }
                        holder.tv_mymusic_list_title.setText(playlist.getName());
                        holder.tv_mymusic_list_count.setText(playlist.getSongCount() + "首");

                    }
                    if (collectExpanded && !playlist.getAuthor().equals("local")) {
                        if (playlist.getAlbum() != null){
                            holder.img_mymusic_list_album.setImageURI(Uri.parse(playlist.getAlbum()));
                        }
                        holder.tv_mymusic_list_title.setText(playlist.getName());
                        holder.tv_mymusic_list_count.setText(playlist.getSongCount() + "首");
                    }
                    setOnPlaylistListener(holder, position, playlist.getId(), playlist.getAlbum(), playlist.getName());
//                    isLoveList = false;
//                }
                break;
            case 2:
                holder.tv_mymusic_expand_title.setText("创建的歌单" + "(" + playlists.size() + ")");
                holder.img_mymusic_expand.setImageResource(R.drawable.list_icn_arr_right);
                setSectionListener(holder, position);
                break;
            case 3:
                holder.tv_mymusic_expand_title.setText("收藏的歌单" + "(" + netplaylists.size() + ")");
                holder.img_mymusic_expand.setImageResource(R.drawable.list_icn_arr_right);
                setSectionListener(holder, position);
                break;
            case 4:
                if(itemResults.get(position) instanceof MyMusicTitleInfo){
                    MyMusicPlaylistItem playlist1 = (MyMusicPlaylistItem) itemResults.get(position);
                    if (playlist1.getAlbum() != null){
                        holder.img_mymusic_love.setImageURI(Uri.parse(playlist1.getAlbum()));
                    }
                    holder.tv_mymusic_love_count.setText(playlist1.getSongCount() + "首");
                }


                break;
        }
    }

    private void setOnPlaylistListener(ItemHolder itemHolder, final int position, final long playlistid, final String album, final String playlistname) {
        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        Intent intent = new Intent(mContext, PlaylistActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                        intent.putExtra("islocal", true);
//                        intent.putExtra("playlistid", playlistid + "");
//                        intent.putExtra("albumart", albumArt);
//                        intent.putExtra("playlistname", playlistname);
//                        mContext.startActivity(intent);

                    }
                }, 60);
            }
        });

        itemHolder.img_mymusic_expand_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
//                        if (position == 5) {
//                            Toast.makeText(mContext, "此歌单不应删除", Toast.LENGTH_SHORT).show();
//                        } else {
                            new AlertDialog.Builder(mContext).setTitle("确定删除此歌单吗").
                                    setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            Toast.makeText(mContext, "待完成", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    }).
                                    setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).show();
//                        }

                        return true;
                    }
                });
                popupMenu.inflate(R.menu.mymusic_popmenu);
                popupMenu.show();
            }
        });
    }

    private void setOnListener(ItemHolder itemHolder, final int position) {
        switch (position) {
            case 0:
                itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(mContext, LocalMusicActivity.class);
                                intent.putExtra("page_number", 0);
                                mContext.startActivity(intent);
                            }
                        }, 60);
                    }
                });
                break;
            case 1:
                itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                Toast.makeText(mContext, "待开发", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(mContext, RecentActivity.class);
                                mContext.startActivity(intent);
                            }
                        }, 60);
                    }
                });


                break;
            case 2:
                itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "待开发", Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(mContext, DownActivity.class);
//                                mContext.startActivity(intent);
                            }
                        }, 60);

                    }
                });
                break;
            case 3:
                itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext, "待开发", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(mContext, LocalMusicActivity.class);
//                        intent.putExtra("page_number", 1);
//                        mContext.startActivity(intent);

                    }
                });
        }
    }

    private void setSectionListener(final ItemHolder itemHolder, int position) {
        itemHolder.img_mymusic_expand_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "待开发", Toast.LENGTH_SHORT).show();
                //Intent intent = new Intent(mContext, PlaylistManagerActivity.class);
//                mContext.startActivity(intent);
            }
        });
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView img_mymusic_main,img_mymusic_love,img_mymusic_list_album;
        private TextView tv_mymusic_main_title,tv_mymusic_main_count,tv_mymusic_love_count;
        private TextView tv_mymusic_list_title,tv_mymusic_list_count,tv_mymusic_expand_title;
        private ImageView img_mymusic_list_option,img_mymusic_expand,img_mymusic_expand_menu;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            img_mymusic_main = itemView.findViewById(R.id.img_mymusic_main);
            tv_mymusic_main_title = itemView.findViewById(R.id.tv_mymusic_main_title);
            tv_mymusic_main_count = itemView.findViewById(R.id.tv_mymusic_main_count);

            img_mymusic_love = itemView.findViewById(R.id.img_mymusic_love);
            tv_mymusic_love_count = itemView.findViewById(R.id.tv_mymusic_love_count);

            img_mymusic_list_album = itemView.findViewById(R.id.img_mymusic_list_album);
            tv_mymusic_list_title = itemView.findViewById(R.id.tv_mymusic_list_title);
            tv_mymusic_list_count = itemView.findViewById(R.id.tv_mymusic_love_count);
            img_mymusic_list_option = itemView.findViewById(R.id.img_mymusic_list_option);


            img_mymusic_expand = itemView.findViewById(R.id.img_mymusic_expand);
            tv_mymusic_expand_title = itemView.findViewById(R.id.tv_mymusic_expand_title);
            img_mymusic_expand_menu = itemView.findViewById(R.id.img_mymusic_expand_menu);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            ObjectAnimator anim = ObjectAnimator.ofFloat(img_mymusic_expand, "rotation", 90, 0);
            anim.setDuration(100);
            anim.setRepeatCount(0);
            anim.setInterpolator(new LinearInterpolator());

            switch (getItemViewType()) {
                case 2:
                    if (createdExpanded) {
                        itemResults.removeAll(playlists);
                        updateResults(itemResults, playlists, netplaylists);
                        notifyItemRangeRemoved(5, playlists.size());
                        anim.start();

                        createdExpanded = false;
                    } else {
                        itemResults.removeAll(netplaylists);
                        itemResults.remove("收藏的歌单");
                        itemResults.addAll(playlists);
                        itemResults.add("收藏的歌单");
                        itemResults.addAll(netplaylists);
                        updateResults(itemResults, playlists, netplaylists);
                        notifyItemRangeInserted(5, playlists.size());
                        anim.reverse();
                        createdExpanded = true;
                    }

                    break;

                case 3:
                    if (collectExpanded) {
                        itemResults.removeAll(netplaylists);
                        updateResults(itemResults, playlists, netplaylists);
                        int len = playlists.size();
                        notifyItemRangeRemoved(6 + len, netplaylists.size());
                        anim.start();

                        collectExpanded = false;
                    } else {
                        itemResults.addAll(netplaylists);
                        updateResults(itemResults, playlists, netplaylists);
                        int len = playlists.size();
                        notifyItemRangeInserted(6 + len, netplaylists.size());
                        anim.reverse();
                        collectExpanded = true;
                    }
                    break;
            }
        }
    }
}
