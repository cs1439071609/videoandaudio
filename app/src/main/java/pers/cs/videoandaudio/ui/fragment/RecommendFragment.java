package pers.cs.videoandaudio.ui.fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;

import pers.cs.videoandaudio.MyApplication;
import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.base.BaseFragment;
import pers.cs.videoandaudio.json.RecommendListRecommendInfo;
import pers.cs.videoandaudio.ui.activity.PlaylistActivity;
import pers.cs.videoandaudio.utils.NetUtil;
import pers.cs.videoandaudio.utils.OkHttpUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecommendFragment extends BaseFragment {
    private static final String TAG = RecommendFragment.class.getSimpleName();
    private static final Boolean DEBUG = true;

    private ViewGroup view;
    private LayoutInflater mLayoutInflater;
    private View mRecommendView;
    private LinearLayout mViewContent;
    private View mLoadView, v1;
    private RecyclerView mRecyclerView1;

    private ScrollView scrollview;
    private RecommendAdapter mRecomendAdapter;
    private ArrayList<RecommendListRecommendInfo> mRecomendList = new ArrayList<>();
    private boolean isFromCache = true;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: "+context);
    }

    @Override
    protected View initView() {
        view = (ViewGroup) View.inflate(mContext, R.layout.fragment_recommend, null);

        mLayoutInflater = LayoutInflater.from(mContext);

        mRecommendView = mLayoutInflater.inflate(R.layout.recommend, null, false);
        view.addView(mRecommendView);
        mViewContent = mRecommendView.findViewById(R.id.recommend_layout);

        scrollview = mRecommendView.findViewById(R.id.scrollview);

        String date = Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "";
        TextView dailyText = mRecommendView.findViewById(R.id.daily_text);
        dailyText.setText(date);

        mLoadView = mLayoutInflater.inflate(R.layout.loading_music_search, null, false);
        mViewContent.addView(mLoadView);

        mRecomendAdapter = new RecommendAdapter(null);
        scrollview.fullScroll(View.FOCUS_UP);
        return view;
    }

    @Override
    protected void initData() {
        super.initData();

        requestData();

    }

    private boolean firstLoad = true;
    public void requestData() {
        if(firstLoad) {
//            reloadAdapter();
            Log.d(TAG, "requestData: "+(mContext==null));
            new MyTask(mContext).execute(0);

        }


    }

    class MyTask extends AsyncTask<Integer, Void, Boolean>{

        WeakReference<Context> context;
        public MyTask(Context context) {
            super();
            this.context = new WeakReference<Context>(context);
        }

        @Override
        protected Boolean doInBackground(Integer... params) {
            //推荐电台
            try {

                if(params[0] == 1){
                    isFromCache = true;

                }else{
                    isFromCache = false;
                    Log.d(TAG, "doInBackground: 网络");
                }
                if (NetUtil.isConnectInternet(context.get())) {
                    isFromCache = false;
                    Log.d(TAG, "doInBackground: 缓存");
                }
                JsonObject list = OkHttpUtil.getResposeJsonObject1("http://tingapi.ting.baidu.com/v1/restserver/ting?from=android&version=5.8.1.0&channel=ppzs&operator=3&method=baidu.ting.plaza.index&cuid=89CF1E1A06826F9AB95A34DC0F6AAA14"
                        , context.get(), isFromCache);

                JsonObject object = list.get("result").getAsJsonObject();
                JsonArray radioArray = object.get("radio").getAsJsonObject().get("result").getAsJsonArray();
                JsonArray recommendArray = object.get("diy").getAsJsonObject().get("result").getAsJsonArray();
                JsonArray newAlbumArray = object.get("mix_1").getAsJsonObject().get("result").getAsJsonArray();


                for (int i = 0; i < 6; i++) {
                    mRecomendList.add(MyApplication.gsonInstance().fromJson(recommendArray.get(i), RecommendListRecommendInfo.class));
                    // mNewAlbumsList.add(MainApplication.gsonInstance().fromJson(newAlbumArray.get(i), RecommendListNewAlbumInfo.class));
                    // mRadioList.add(MainApplication.gsonInstance().fromJson(radioArray.get(i), RecommendListRadioInfo.class));
                }
                return true;
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {

            if(success){
                Log.d(TAG, "onPostExecute: "+(mLayoutInflater==null));
                if(context.get() != null){
                    v1 = mLayoutInflater.inflate(R.layout.recommend_playlist, mViewContent, false);

                    mRecyclerView1 = (RecyclerView) v1.findViewById(R.id.recommend_playlist_recyclerview);
                    GridLayoutManager mGridLayoutManager = new GridLayoutManager(mContext, 3);
                    mRecyclerView1.setLayoutManager(mGridLayoutManager);
                    mRecyclerView1.setAdapter(mRecomendAdapter);
                    mRecyclerView1.setHasFixedSize(true);
                    TextView more = (TextView) v1.findViewById(R.id.more);
                    more.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });

                    mRecomendAdapter.update(mRecomendList);
                    mViewContent.removeAllViews();
                    mViewContent.addView(v1);
                    scrollview.smoothScrollTo(0,0);
                    firstLoad = false;
                }

            }else{
                firstLoad = true;
//                new MyTask(mContext).execute(1);
            }

        }

    }

    private void reloadAdapter() {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                mContext = RecommendFragment.this.getActivity();

                //推荐电台
                try {
                    if (NetUtil.isConnectInternet(mContext)) {
                        isFromCache = false;
                    }

                    JsonObject list = OkHttpUtil.getResposeJsonObject1("http://tingapi.ting.baidu.com/v1/restserver/ting?from=android&version=5.8.1.0&channel=ppzs&operator=3&method=baidu.ting.plaza.index&cuid=89CF1E1A06826F9AB95A34DC0F6AAA14"
                            , mContext, isFromCache);

                    JsonObject object = list.get("result").getAsJsonObject();
                    JsonArray radioArray = object.get("radio").getAsJsonObject().get("result").getAsJsonArray();
                    JsonArray recommendArray = object.get("diy").getAsJsonObject().get("result").getAsJsonArray();
                    JsonArray newAlbumArray = object.get("mix_1").getAsJsonObject().get("result").getAsJsonArray();


                    for (int i = 0; i < 6; i++) {
                        mRecomendList.add(MyApplication.gsonInstance().fromJson(recommendArray.get(i), RecommendListRecommendInfo.class));
//                        mNewAlbumsList.add(MainApplication.gsonInstance().fromJson(newAlbumArray.get(i), RecommendListNewAlbumInfo.class));
//                        mRadioList.add(MainApplication.gsonInstance().fromJson(radioArray.get(i), RecommendListRadioInfo.class));
                    }
                    return true;
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                return false;
            }

            @Override
            protected void onPostExecute(Boolean success) {

                if(success){
                    Log.d(TAG, "onPostExecute: "+(mLayoutInflater==null));
                    v1 = mLayoutInflater.inflate(R.layout.recommend_playlist, mViewContent, false);

                    mRecyclerView1 = (RecyclerView) v1.findViewById(R.id.recommend_playlist_recyclerview);
                    GridLayoutManager mGridLayoutManager = new GridLayoutManager(mContext, 3);
                    mRecyclerView1.setLayoutManager(mGridLayoutManager);
                    mRecyclerView1.setAdapter(mRecomendAdapter);
                    mRecyclerView1.setHasFixedSize(true);
                    TextView more = (TextView) v1.findViewById(R.id.more);
                    more.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });

                    mRecomendAdapter.update(mRecomendList);
                    mViewContent.removeAllViews();
                    mViewContent.addView(v1);
                    scrollview.smoothScrollTo(0,0);
                    firstLoad = false;
                }else{
                    firstLoad = true;
                }

            }

        }.execute();
    }

    class RecommendAdapter extends RecyclerView.Adapter<RecommendAdapter.ItemView> {
        private ArrayList<RecommendListRecommendInfo> mList;
        SpannableString spanString;

        public RecommendAdapter(ArrayList<RecommendListRecommendInfo> list) {
            Bitmap b = BitmapFactory.decodeResource(getResources(), R.mipmap.index_icn_earphone);
            ImageSpan imgSpan = new ImageSpan(mContext, b, ImageSpan.ALIGN_BASELINE);
            spanString = new SpannableString("icon");
            spanString.setSpan(imgSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            mList = list;
        }

        public void update(ArrayList<RecommendListRecommendInfo> list) {
            mList = list;
            notifyDataSetChanged();
        }

        @Override
        public ItemView onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            ItemView viewholder = new ItemView(layoutInflater.inflate(R.layout.recommend_playlist_item, parent, false));

            return viewholder;
        }



        @Override
        public void onBindViewHolder(ItemView holder, int position) {
            final RecommendListRecommendInfo info = mList.get(position);

            RequestOptions options = new RequestOptions()
                    .placeholder(R.mipmap.placeholder_disk_300)
                    .error(R.mipmap.placeholder_disk_300)
                    .diskCacheStrategy(DiskCacheStrategy.ALL) ;

            Glide.with(mContext).load(Uri.parse(info.getPic()))
                    .apply(options)
                    .into(holder.art);


            holder.name.setText(info.getTitle());
            holder.count.setText(spanString);

            int count = Integer.parseInt(info.getListenum());
            if (count > 10000) {
                count = count / 10000;
                holder.count.append(" " + count + "万");
            } else {
                holder.count.append(" " + info.getListenum());
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PlaylistActivity.class);
                    intent.putExtra("playlistid", info.getListid());
                    intent.putExtra("islocal", false);
                    intent.putExtra("albumart", info.getPic());
                    intent.putExtra("playlistname", info.getTitle());
                    intent.putExtra("playlistDetail", info.getTag());
                    intent.putExtra("playlistcount", info.getListenum());

                    mContext.startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            if (mList == null) {
                return 0;
            }

            if (mList.size() < 7) {
                return mList.size();
            } else {
                return 6;
            }
        }

        class ItemView extends RecyclerView.ViewHolder {
            private ImageView art;
            private TextView name, count;

            public ItemView(View itemView) {
                super(itemView);
                art = itemView.findViewById(R.id.playlist_art);
                name = itemView.findViewById(R.id.playlist_name);
                count = itemView.findViewById(R.id.playlist_listen_count);
            }
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        firstLoad = true;
        Log.d(TAG, "onDestroyView: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }
}
