package pers.cs.videoandaudio.ui.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.List;

import pers.cs.videoandaudio.bean.Lyrics;
import pers.cs.videoandaudio.utils.DensityUtil;

/**
 * @author chensen
 * @time 2020/2/8  12:24
 * @desc
 */
public class LyricsTextView extends TextView {

    //组件宽和高
    private int width;
    private int height;

    //歌词高
    private float lyricsHeight = 20;

    private Paint paint;
    private Paint greenPaint;

    //歌词列表
    private List<Lyrics> mLyricsList;
    //当前进度
    private long currentPosition = 0;
    //当前歌词
    private int currentLyric = 0;

    //设置歌词列表
    public void setLyrics(List<Lyrics> lyrics){
        this.mLyricsList = lyrics;
    }

    public void setCurrentPosition(int currentPosition){
        this.currentPosition = currentPosition;
        //重绘
        //主线程使用
        invalidate();
        //子线程使用
//        postInvalidate();
    }

    public LyricsTextView(Context context) {
        this(context,null);
    }

    public LyricsTextView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LyricsTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView(context);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        this.height = h;
    }

    private void initView(Context context) {
        lyricsHeight = DensityUtil.dip2px(context,30);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setTextSize(DensityUtil.dip2px(context,16));
        paint.setTextAlign(Paint.Align.CENTER);

        greenPaint = new Paint();
        greenPaint.setAntiAlias(true);
        greenPaint.setColor(Color.GREEN);
        greenPaint.setTextSize(DensityUtil.dip2px(context,16));
        greenPaint.setTextAlign(Paint.Align.CENTER);

        //模拟歌词
//        mLyricsList = new ArrayList<>();
//        for (int i = 0; i < 10000; i++) {
//            Lyrics lyrics = new Lyrics();
//            lyrics.setSleepTime(1000);
//            lyrics.setTimePoint(i*1000);
//            lyrics.setContent(i+"aaaaaaaaaa"+i);
//            mLyricsList.add(lyrics);
//        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(mLyricsList != null && mLyricsList.size() >0){

            //确定当前歌词的位置，移至setCurrentPosition()也可以
            for (int i = 0; i < mLyricsList.size(); i++) {
                long position = mLyricsList.get(i).getTimePoint();

                if(i+1<mLyricsList.size()){
                    long nextPoint = mLyricsList.get(i + 1).getTimePoint();
                    if(currentPosition >= position && currentPosition < nextPoint){
                        currentLyric = i;
                        break;
                    }
                }else if(i+1 == mLyricsList.size()){
                    if(currentPosition >= position){
                        currentLyric = i;
                        break;

                    }
                }
            }


            //往上推移
            long sleepTime = mLyricsList.get(currentLyric).getSleepTime();
            //当前某句歌词已播放时间：这句歌词所需时间 = 平移距离：某句歌词高度
            float time = currentPosition - mLyricsList.get(currentLyric).getTimePoint();
            float dy;
            if(sleepTime == 0){
                dy = 0;
            }else{
                dy = time / mLyricsList.get(currentLyric).getSleepTime() * lyricsHeight;
            }
            canvas.translate(0,-dy);
//            canvas.translate(0,-(lyricsHeight + dy));
/*

            if(currentLyric != mLyricsList.size() - 1){
                //当前某句歌词已播放时间：这句歌词所需时间 = 平移距离：某句歌词高度
                float time = currentPosition - mLyricsList.get(currentLyric).getTimePoint();
                float dy = time / mLyricsList.get(currentLyric).getSleepTime() * lyricsHeight;
                canvas.translate(0,-dy);
            }

*/

            String text;
            //前边部分
            float currentHeight = height / 2;
            for (int i = currentLyric - 1; i >= 0; i--) {
                text = mLyricsList.get(i).getContent();
                currentHeight = currentHeight - lyricsHeight;
                if(currentHeight < 0){
                    break;
                }
                canvas.drawText(text,width/2,currentHeight,paint);
            }
            //当前歌词
            text = mLyricsList.get(currentLyric).getContent();
            canvas.drawText(text,width/2,height / 2,greenPaint);

            //后边部分
            currentHeight = height / 2;
            for (int i = currentLyric + 1; i < mLyricsList.size(); i++) {
                currentHeight = currentHeight + lyricsHeight;
                text = mLyricsList.get(i).getContent();
                if(currentHeight > height){
                    break;
                }
                canvas.drawText(text,width/2,currentHeight,paint);
            }
        }else{
            canvas.drawText("没有歌词",width/2,height/2,greenPaint);
        }

    }
}
