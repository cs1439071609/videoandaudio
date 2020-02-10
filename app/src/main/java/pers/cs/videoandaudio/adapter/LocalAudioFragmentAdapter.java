package pers.cs.videoandaudio.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.bean.AudioItem;
import pers.cs.videoandaudio.utils.TimeUtil;

/**
 * @author chensen
 * @time 2020/1/31  21:26
 * @desc
 */
public class LocalAudioFragmentAdapter extends BaseAdapter {

    private Context mContext;
    private List<AudioItem> mAudioItems;
    private TimeUtil mTimeUtil;

    public LocalAudioFragmentAdapter(Context context, List<AudioItem> audioItems) {
        mContext = context;
        mAudioItems = audioItems;
        mTimeUtil = new TimeUtil();
    }


    @Override
    public int getCount() {
        return mAudioItems == null ? 0:mAudioItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mAudioItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LocalVideoFragmentAdapter.ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new LocalVideoFragmentAdapter.ViewHolder();
            //最后一个为null
            convertView = View.inflate(mContext, R.layout.item_video,null);
            viewHolder.img_video_frame = convertView.findViewById(R.id.img_video_frame);
            viewHolder.tv_video_name = convertView.findViewById(R.id.tv_video_name);
            viewHolder.tv_video_time = convertView.findViewById(R.id.tv_video_time);
            viewHolder.tv_video_size = convertView.findViewById(R.id.tv_video_size);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (LocalVideoFragmentAdapter.ViewHolder) convertView.getTag();
        }

        //注意：只有findviewbyid在里边，此在外边
        AudioItem audioItem = mAudioItems.get(position);
        viewHolder.tv_video_name.setText(audioItem.getSimpleName());
        if(audioItem.getTime() != null){
            viewHolder.tv_video_time.setText(mTimeUtil.formatTime(Integer.parseInt(audioItem.getTime())));
        }

        viewHolder.tv_video_size.setText(Formatter.formatFileSize(mContext,Integer.parseInt(audioItem.getSize())));

        viewHolder.img_video_frame.setImageResource(R.drawable.music_default_bg);
        return convertView;
    }


    static class ViewHolder{
        ImageView img_video_frame;
        TextView tv_video_name;
        TextView tv_video_time;
        TextView tv_video_size;
    }
}
