package pers.cs.videoandaudio.ui.fragment;


import android.os.AsyncTask;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.base.BaseFragment;
import pers.cs.videoandaudio.net.MA;
import pers.cs.videoandaudio.utils.DensityUtil;
import pers.cs.videoandaudio.utils.NetUtil;
import pers.cs.videoandaudio.utils.OkHttpUtil;
import pers.cs.videoandaudio.widget.WidgetController;

/**
 * @author chensen
 *
 * @time 2020/4/1  20:00
 *
 * @desc 音乐搜索页面热词
 *
 */

public class SearchHotWordFragment extends BaseFragment implements View.OnClickListener{

    //内容视图
    private FrameLayout fl_search_hot_word;
    //加载View
    private View loading_view;
    //搜索热词接口
    private SearchWords searchWords;
    //是否从缓存中获取热词,默认为是
    private boolean isFromCache = true;

    //热词个数
    private static final int HOT_WORDS_NUM = 10;
    //热词数组
    private String[] texts = new String[HOT_WORDS_NUM];
    //搜索历史记录
    private RecyclerView recyclerView;

    private ArrayList<TextView> views = new ArrayList<>();

    @Override
    protected View initView() {
        View view = View.inflate(mContext,R.layout.fragment_search_hot_word, null);
        fl_search_hot_word = view.findViewById(R.id.fl_search_hot_word);
        //初始化内容视图为加载View
        loading_view = View.inflate(mContext,R.layout.loading_music_search,null);
        fl_search_hot_word.addView(loading_view);

        //加载热词
        loadWords();

        return view;
    }

    /**
     * 加载热词
     */
    private void loadWords() {
        new AsyncTask<Boolean, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Boolean... booleans) {
                if(NetUtil.isConnectInternet(mContext)){
                    isFromCache = false;
                }
                /**
                 * "error_code": 22000,
                 * "result": [
                 * {
                 * "strong": 1,
                 * "word": "我们在一起",
                 * "linktype": 1,
                 * "linkurl": "674169896"
                 * },
                 * ……]
                 */
                JSONObject jsonObject = OkHttpUtil.getResposeJsonObject(MA.Search.hotWord(), mContext, isFromCache);
                try {
                //比较getJSONArray(String key) 和optJSONArray（）
                //getJSONArray遇到是null或者不是jsonarray抛异常
                //optJSONArray返回空
                if(jsonObject != null){
                    JSONArray jsonArray = jsonObject.optJSONArray("result");
                    if(jsonArray != null){
                        for (int i = 0; i < HOT_WORDS_NUM; i++) {
                            if(i == jsonArray.length()){
                                break;
                            }
                            texts[i] = jsonArray.getJSONObject(i).getString("word");
                        }

                        return true;
                    }
                }
                } catch (JSONException e) {
                    e.printStackTrace();
                    return false;
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                if (!success && mContext == null) {
                    return;
                }
                View view = View.inflate(mContext,R.layout.music_hot_word, null);
                recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
                recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

                TextView text1 = (TextView) view.findViewById(R.id.text1);
                TextView text2 = (TextView) view.findViewById(R.id.text2);
                TextView text3 = (TextView) view.findViewById(R.id.text3);
                TextView text4 = (TextView) view.findViewById(R.id.text4);
                TextView text5 = (TextView) view.findViewById(R.id.text5);
                TextView text6 = (TextView) view.findViewById(R.id.text6);
                text1.setOnClickListener(SearchHotWordFragment.this);
                text2.setOnClickListener(SearchHotWordFragment.this);
                text3.setOnClickListener(SearchHotWordFragment.this);
                text4.setOnClickListener(SearchHotWordFragment.this);
                text5.setOnClickListener(SearchHotWordFragment.this);
                text6.setOnClickListener(SearchHotWordFragment.this);
                text1.setText(texts[0]);
                text2.setText(texts[1]);
                text3.setText(texts[2]);
                text4.setText(texts[3]);
                text5.setText(texts[4]);
                text6.setText(texts[5]);
                views.add(text1);
                views.add(text2);
                views.add(text3);
                views.add(text4);
                views.add(text5);
                views.add(text6);
                int w = mContext.getResources().getDisplayMetrics().widthPixels;
                int xdistance = -1;
                int ydistance = 0;
                int distance = DensityUtil.dip2px(mContext, 16);
                for (int i = 0; i < 6; i++) {
                    if (xdistance == -1) {
                        xdistance = 0;
                        WidgetController.setLayout(views.get(i), xdistance, ydistance);
                        continue;
                    }
                    xdistance += WidgetController.getWidth(views.get(i - 1)) + distance;
                    if (xdistance + WidgetController.getWidth(views.get(i)) + distance > w) {
                        xdistance = -1;
                        ydistance += DensityUtil.dip2px(mContext, 50);
                        i--;
                        continue;
                    }
                    WidgetController.setLayout(views.get(i), xdistance, ydistance);
                }
                fl_search_hot_word.removeAllViews();
                fl_search_hot_word.addView(view);
            }

        }.execute();
    }

    //将本类中SearchWords对象设置为Activity中对象
    public void searchWords(SearchWords searchWords) {
        this.searchWords = searchWords;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.text1:
                searchWords.onSearch(texts[0]);
                break;
            case R.id.text2:
                searchWords.onSearch(texts[1]);
                break;
            case R.id.text3:
                searchWords.onSearch(texts[2]);
                break;
            case R.id.text4:
                searchWords.onSearch(texts[3]);
                break;
            case R.id.text5:
                searchWords.onSearch(texts[4]);
                break;
            case R.id.text6:
                searchWords.onSearch(texts[5]);
                break;
        }
    }

    /**
     * SearchWords接口方法
     * 此处为了接受RecyclerLayout中最近搜索记录热词
     * @param str
     */
//    @Override
//    public void onSearch(String str) {
//
//    }
}
