package pers.cs.videoandaudio.ui.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.VideoView;

/**
 * @author chensen
 * @time 2020/1/14  11:43
 * @desc
 */
public class VideoViewCustom extends VideoView {


    public VideoViewCustom(Context context) {
        this(context,null);
    }

    public VideoViewCustom(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public VideoViewCustom(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //没明白调用此句有什么用，上述句子super中已经调用此句
//        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
    }


    /**
     * 设置视频的宽和高
     * @param videoWidth 视频的宽
     * @param videoHeight 视频的高
     */
    public void setVideoSize(int videoWidth,int videoHeight){
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = videoWidth;
        layoutParams.height = videoHeight;
        setLayoutParams(layoutParams);
        //或requestLayout();

    }

}
