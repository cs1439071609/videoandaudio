package pers.cs.videoandaudio.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import java.util.List;

import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.info.MusicInfo;
import pers.cs.videoandaudio.ui.fragment.MoreFragment;

/**
 * @author chensen
 * @time 2020/1/31  21:26
 * @desc
 */
public class LocalAudioFragmentAdapter extends BaseAdapter {
    private static final String TAG = LocalAudioFragmentAdapter.class.getSimpleName();
    private static final Boolean DEBUG = true;

    private Context mContext;
    private List<MusicInfo> mAudioItems;

    private FragmentManager mFragmentManager;


    public LocalAudioFragmentAdapter(Context context, FragmentManager fragmentManager, List<MusicInfo> audioItems) {
        mContext = context;
        mFragmentManager = fragmentManager;
        mAudioItems = audioItems;
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

        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            //最后一个为null
            convertView = View.inflate(mContext, R.layout.item_music,null);
            viewHolder.tv_name = convertView.findViewById(R.id.tv_name);
            viewHolder.tv_artist = convertView.findViewById(R.id.tv_artist);
            viewHolder.img_music_option = convertView.findViewById(R.id.img_music_option);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //注意：只有findviewbyid在里边，此在外边
        MusicInfo audioItem = mAudioItems.get(position);
        viewHolder.tv_name.setText(audioItem.getMusicName());
        if(audioItem.getArtist() != null){
            viewHolder.tv_artist.setText(audioItem.getArtist());
        }
        viewHolder.img_music_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: "+getItem(position));
                MoreFragment morefragment = MoreFragment.newInstance((MusicInfo) getItem(position));
                morefragment.show(mFragmentManager, "music");
            }
        });

        return convertView;
    }


    static class ViewHolder{
        TextView tv_name;
        TextView tv_artist;
        ImageView img_music_option;
    }
}
