package pers.cs.videoandaudio.ui.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import butterknife.BindView;
import butterknife.ButterKnife;
import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.ui.fragment.SearchHotWordFragment;
import pers.cs.videoandaudio.ui.fragment.SearchTabPagerFragment;
import pers.cs.videoandaudio.ui.fragment.SearchWords;

public class NetSearchWordsActivity extends AppCompatActivity implements SearchWords {

    private final String TAG = NetSearchWordsActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private SearchView mSearchView;
    //键盘管理
    private InputMethodManager inputMethodManager;
    /**
     * 界面设置状态栏字体颜色
     */
    public void changeStatusBarTextImgColor(boolean isBlack) {
        if (isBlack) {
            //设置状态栏黑色字体
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            //恢复状态栏白色字体
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_search_words);
        ButterKnife.bind(this);
        //设置手机应用内部状态栏字体图标为黑色
        changeStatusBarTextImgColor(true);
        //设置Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //初始化内容视图
        SearchHotWordFragment searchHotWordFragment = new SearchHotWordFragment();
        searchHotWordFragment.searchWords(NetSearchWordsActivity.this);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fl_search,searchHotWordFragment);
        ft.commit();

        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.music_search,menu);
        //获取SearchView对象
        MenuItem searchItem = menu.findItem(R.id.music_search_menu);
        //展开
        searchItem.expandActionView();

//        searchItem.collapseActionView();
        mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setQueryHint(getResources().getString(R.string.search_net_music));

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                finish();
                return true;
            }
        });

//        mSearchView.setMinimumWidth(getWindow().getAttributes().width);
        //提交按钮
        mSearchView.setSubmitButtonEnabled(true);

//        mSearchView.setIconifiedByDefault(true);
//        mSearchView.setIconified(false);
//        mSearchView.onActionViewExpanded();
        //默认展开

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                SearchTabPagerFragment fragment = SearchTabPagerFragment.newInstance(0, query);
                //调用commit方法时，系统系判断状态（mStateSaved）是否已经保存，
                // 如果已经保存，则抛出"Can not perform this action after onSaveInstanceState"异常，
                // 而用commitAllowingStateLoss方法则不会这样
                ft.replace(R.id.fl_search, fragment).commitAllowingStateLoss();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }



    /**
     * https://blog.csdn.net/xyz_lmn/article/details/12517911
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }

        return onTouchEvent(ev);
//        return super.dispatchTouchEvent(ev);
    }

    /**
     *
     * @param v
     * @param event
     * @return false在EditText内不隐藏
     */
    public  boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = { 0, 0 };
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode  == KeyEvent.KEYCODE_BACK)
        {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

//            case R.id.music_search_menu:
//                Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
//                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSearch(String str) {
        mSearchView.setQuery(str,true);
//        mSearchView.setSubmitButtonEnabled(false);
    }

}
