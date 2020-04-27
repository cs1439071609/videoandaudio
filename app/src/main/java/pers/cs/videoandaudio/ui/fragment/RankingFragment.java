package pers.cs.videoandaudio.ui.fragment;


import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.base.BaseFragment;
import pers.cs.videoandaudio.json.BillboardInfo;
import pers.cs.videoandaudio.net.MA;
import pers.cs.videoandaudio.ui.activity.RankPlaylistActivity;
import pers.cs.videoandaudio.utils.OkHttpUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class RankingFragment extends BaseFragment {

    private static final String TAG = RankingFragment.class.getSimpleName();
    private static final Boolean DEBUG = true;
    
    private View mView;
    //内容视图
    private FrameLayout fl_search_hot_word;
    //加载View
    private View loading_view;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RankingAdapter rankingAdapter;
    //热歌榜
    public static int BILLBOARD_HOT_MUSIC = 2;
    private ArrayList<BillboardInfo> items = new ArrayList<>();

    @Override
    protected View initView() {
        mView = View.inflate(mContext, R.layout.fragment_search_hot_word, null);
        fl_search_hot_word = mView.findViewById(R.id.fl_search_hot_word);
        //初始化内容视图为加载View
        loading_view = View.inflate(mContext,R.layout.loading_music_search,null);
        fl_search_hot_word.addView(loading_view);

        return mView;
    }

    View view;
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Log.d(TAG, "setUserVisibleHint: ");
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.fragment_ranking, null, false);
                recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
                linearLayoutManager = new LinearLayoutManager(mContext);
                recyclerView.setLayoutManager(linearLayoutManager);
                rankingAdapter = new RankingAdapter();
                recyclerView.setAdapter(rankingAdapter);
                recyclerView.setHasFixedSize(true);
                loadData();
            }
        }
    }

    @Override
    protected void initData() {
        super.initData();
//        if (view == null) {
            items.clear();
            view = LayoutInflater.from(mContext).inflate(R.layout.fragment_ranking, null, false);
            recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
            linearLayoutManager = new LinearLayoutManager(mContext);
            recyclerView.setLayoutManager(linearLayoutManager);
            rankingAdapter = new RankingAdapter();
            recyclerView.setAdapter(rankingAdapter);
            recyclerView.setHasFixedSize(true);
            loadData();
//        }
    }

    private void loadData() {
        Log.d(TAG, "loadData: ");
        new MyTask().execute(BILLBOARD_HOT_MUSIC,0);
    }

    public class MyTask extends AsyncTask<Integer,Void,Boolean>{

        @Override
        protected Boolean doInBackground(Integer... params) {
            JsonArray array = null;
            try {
                JsonObject jsonObject;
                if(params[1] == 1){
                    Log.d(TAG, "doInBackground: 缓存");
                    jsonObject = OkHttpUtil.getResposeJsonObject1(MA.Billboard.billSongList(params[0], 0, 3),mContext,true);
                }else{
                    Log.d(TAG, "doInBackground: 网络");
                    jsonObject = OkHttpUtil.getResposeJsonObject(MA.Billboard.billSongList(params[0], 0, 3));
                }
                array = jsonObject.get("song_list").getAsJsonArray();

                for (int i = 0; i < array.size(); i++) {
                    BillboardInfo billboardInfo = new BillboardInfo();
                    billboardInfo.title = array.get(i).getAsJsonObject().get("title").toString();
                    billboardInfo.author = array.get(i).getAsJsonObject().get("author").toString();
                    billboardInfo.id = array.get(i).getAsJsonObject().get("artist_id").toString();
                    items.add(billboardInfo);
                }
                return true;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);

            if(success){
                rankingAdapter.updateAdapter(items);
                fl_search_hot_word.removeAllViews();
                fl_search_hot_word.addView(view);
            }else{
//                new MyTask().execute(BILLBOARD_HOT_MUSIC,1);
            }
        }

    }

    class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingViewHolder> {
        ArrayList<BillboardInfo> mList;
        int[] pic = {R.mipmap.ranklist_first, R.mipmap.ranklist_second, R.mipmap.ranklist_third
                , R.mipmap.ranklist_fifth, R.mipmap.ranklist_acg, R.mipmap.ranklist_six};

        public void updateAdapter(ArrayList<BillboardInfo> list) {
            mList = list;
            this.notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RankingAdapter.RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RankingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ranking_recyclerview_adapter, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RankingAdapter.RankingViewHolder holder, int position) {
            BillboardInfo billboardInfo1 = mList.get(position * 3);
            BillboardInfo billboardInfo2 = mList.get(position * 3 + 1);
            BillboardInfo billboardInfo3 = mList.get(position * 3 + 2);
            holder.textView1.setText(billboardInfo1.title + "-" + billboardInfo1.author);
            holder.textView2.setText(billboardInfo2.title + "-" + billboardInfo2.author);
            holder.textView3.setText(billboardInfo3.title + "-" + billboardInfo3.author);
            holder.imageView.setImageResource(pic[2]);
        }

        @Override
        public int getItemCount() {
            return mList == null ? 0 : mList.size() / 3;
        }


        class RankingViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView textView1, textView2, textView3;
            public RankingViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.item_image);
                textView1 = itemView.findViewById(R.id.rank_first_txt);
                textView2 = itemView.findViewById(R.id.rank_second_txt);
                textView3 = itemView.findViewById(R.id.rank_third_txt);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getAdapterPosition() > -1) {
                            Intent intent = new Intent(mContext, RankPlaylistActivity.class);
//                            intent.putExtra("type", mBillList[getAdapterPosition()]);
                            intent.putExtra("pic", pic[getAdapterPosition()]);
                            mContext.startActivity(intent);
                        }
                    }
                });
            }
        }
    }

}
