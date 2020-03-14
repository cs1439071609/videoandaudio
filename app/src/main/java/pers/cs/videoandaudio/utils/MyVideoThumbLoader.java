package pers.cs.videoandaudio.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

import org.xutils.common.util.DensityUtil;

import io.vov.vitamio.ThumbnailUtils;
import io.vov.vitamio.provider.MediaStore;
import pers.cs.videoandaudio.R;

/**
 * @author chensen
 * @time 2020/2/16  20:43
 * @desc
 */
public class MyVideoThumbLoader {

    private LruCache<String, Bitmap> mLruCache;
    private Context mContext;

    public MyVideoThumbLoader(Context context){
        this.mContext = context;

        //设置最大缓存空间为运行时内存的 1/8
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        mLruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //这个方法会在每次存入缓存的时候调用
                //计算一个元素的缓存大小

                return value.getByteCount();
            }
        };
    }

    /**
     * 添加图片到 LruCache
     *
     * @param key
     * @param bitmap
     */
    public void addBitmap(String key, Bitmap bitmap) {
        if (getBitmap(key) == null) {
            mLruCache.put(key, bitmap);
        }
    }

    /**
     * 从缓存中获取图片
     *
     * @param key
     * @return
     */
    public Bitmap getBitmap(String key) {
        return mLruCache.get(key);
    }

    /**
     * 从缓存中删除指定的 Bitmap
     *
     * @param key
     */
    public void removeBitmapFromMemory(String key) {
        mLruCache.remove(key);
    }


    public void showThumbByAsynctask(String path, ImageView imgview) {

        if (getBitmap(path) == null) {
            // 异步加载
            new MyBobAsynctack(imgview, path).execute();
        } else {
            imgview.setImageBitmap(getBitmap(path));
        }

    }


    private class MyBobAsynctack extends AsyncTask<String, Void, Bitmap> {
        private ImageView imgView;
        private String path;

        public MyBobAsynctack(ImageView imageView, String path) {
            this.imgView = imageView;
            this.path = path;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {


            //得到bitmap有可能为空，当视频是mkv格式时，就会为null，所以加个判断，设置默认的图片
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(mContext,path, MediaStore.Video.Thumbnails.MICRO_KIND);

            if(bitmap == null){
                bitmap = BitmapFactory.decodeResource(mContext.getResources(),
                        R.drawable.video_default_icon);
            }
            //直接对Bitmap进行缩略操作，最后一个参数定义为OPTIONS_RECYCLE_INPUT ，来回收资源
            Bitmap bitmap2 = ThumbnailUtils.extractThumbnail(bitmap, DensityUtil.dip2px(100), DensityUtil.dip2px(80),
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            // 加入缓存中
            if (getBitmap(path) == null) {
                addBitmap(path, bitmap2);
            }
            return bitmap2;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (imgView.getTag().equals(path)) {
                // 通过 Tag可以绑定 图片地址和imageView，这是解决Listview加载图片错位的解决办法之一
                imgView.setImageBitmap(bitmap);
            }

        }
    }
}
