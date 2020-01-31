package pers.cs.videoandaudio.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import io.vov.vitamio.ThumbnailUtils;
import io.vov.vitamio.provider.MediaStore;
import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.bean.VideoItem;
import pers.cs.videoandaudio.utils.TimeUtil;

/**
 * @author chensen1
 * @time 2020/1/6  17:35
 * @desc
 *
 * Formatter.formatFileSize(context,long sizebyte)
 *
 */
public class LocalVideoFragmentAdapter extends BaseAdapter {

    private Context mContext;
    private List<VideoItem> mVideoItems;
    private TimeUtil mTimeUtil;

    public LocalVideoFragmentAdapter(Context context, List<VideoItem> videoItems) {
        mContext = context;
        mVideoItems = videoItems;
        mTimeUtil = new TimeUtil();
    }

    @Override
    public int getCount() {
        return mVideoItems == null ? 0:mVideoItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mVideoItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            //最后一个为null
            convertView = View.inflate(mContext, R.layout.item_video,null);
            viewHolder.img_video_frame = convertView.findViewById(R.id.img_video_frame);
            viewHolder.tv_video_name = convertView.findViewById(R.id.tv_video_name);
            viewHolder.tv_video_time = convertView.findViewById(R.id.tv_video_time);
            viewHolder.tv_video_size = convertView.findViewById(R.id.tv_video_size);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //注意：只有findviewbyid在里边，此在外边
        VideoItem videoItem = mVideoItems.get(position);
        viewHolder.tv_video_name.setText(videoItem.getSimpleName());
        viewHolder.tv_video_time.setText(mTimeUtil.formatTime(Integer.parseInt(videoItem.getTime())));
        viewHolder.tv_video_size.setText(Formatter.formatFileSize(mContext,Integer.parseInt(videoItem.getSize())));

//        Bitmap bm = ThumbnailUtils.createVideoThumbnail(cursor.getString(3), MediaStore.Video.Thumbnails.MINI_KIND);

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.video_default)
                .error(R.drawable.video_default)
                .diskCacheStrategy(DiskCacheStrategy.ALL) ;

        Glide.with(mContext).load(ThumbnailUtils.createVideoThumbnail(mContext,videoItem.getData(), MediaStore.Video.Thumbnails.MINI_KIND))
                .apply(options)
                .into(viewHolder.img_video_frame);
        return convertView;
    }


    static class ViewHolder{
        ImageView img_video_frame;
        TextView tv_video_name;
        TextView tv_video_time;
        TextView tv_video_size;
    }


}
