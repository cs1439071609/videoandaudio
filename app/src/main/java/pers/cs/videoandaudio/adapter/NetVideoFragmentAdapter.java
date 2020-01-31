package pers.cs.videoandaudio.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.bean.VideoItem;

/**
 * @author chensen1
 * @time 2020/1/6  17:35
 * @desc
 *
 * Formatter.formatFileSize(context,long sizebyte)
 *
 */
public class NetVideoFragmentAdapter extends BaseAdapter {

    private Context mContext;
    private List<VideoItem> mVideoItems;

    public NetVideoFragmentAdapter(Context context, List<VideoItem> videoItems) {
        mContext = context;
        mVideoItems = videoItems;
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
            convertView = View.inflate(mContext, R.layout.item_net_video,null);
            viewHolder.img_video = convertView.findViewById(R.id.img_net_video);
            viewHolder.tv_video_name = convertView.findViewById(R.id.tv_net_video_name);
            viewHolder.tv_video_desc = convertView.findViewById(R.id.tv_net_video_desc);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //注意：只有findviewbyid在里边，此在外边
        VideoItem videoItem = mVideoItems.get(position);
        viewHolder.tv_video_name.setText(videoItem.getName());
        viewHolder.tv_video_desc.setText(videoItem.getDesc());
        //使用xUtils3请求图片
//        x.image().bind(viewHolder.img_video,videoItem.getImageUrl());

        //使用Glide请求图片
        //加载成功之前占位图
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.video_default)
                .error(R.drawable.video_default)
                .diskCacheStrategy(DiskCacheStrategy.ALL) ;
        Glide.with(mContext).load(videoItem.getImageUrl())
                .apply(options)
                .into(viewHolder.img_video);
        //使用Picasso请求图片
//        Picasso.get().load(videoItem.getImageUrl())
//                .placeholder(R.drawable.video_default)
//                .error(R.drawable.video_default)
//                .into(viewHolder.img_video);

        return convertView;
    }


    static class ViewHolder{
        ImageView img_video;
        TextView tv_video_name;
        TextView tv_video_desc;
    }


}
