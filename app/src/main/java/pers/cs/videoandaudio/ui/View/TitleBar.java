package pers.cs.videoandaudio.ui.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import pers.cs.videoandaudio.R;

/**
 * @author chensen
 * @time 2020/1/7  20:55
 * @desc
 */
public class TitleBar extends LinearLayout implements View.OnClickListener {

    private View tv_search;
    private View rl_game;
    private View iv_history;
    private Context context;

    public TitleBar(Context context) {
        this(context,null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    /**
     *
     * 当布局文件加载完成时，回调此方法
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        tv_search = getChildAt(1);
        rl_game = getChildAt(2);
        iv_history = getChildAt(3);

        tv_search.setOnClickListener(this);
        rl_game.setOnClickListener(this);
        iv_history.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_search:
                Toast.makeText(context, "搜索", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rl_game:
                Toast.makeText(context, "游戏", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_history:
                Toast.makeText(context, "历史", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
