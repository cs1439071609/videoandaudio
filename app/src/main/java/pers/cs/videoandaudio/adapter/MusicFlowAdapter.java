package pers.cs.videoandaudio.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.bean.OverFlowItem;
import pers.cs.videoandaudio.info.MusicInfo;

/**
 * @author chensen
 * @time 2020/4/25  14:30
 * @desc
 */
public class MusicFlowAdapter extends RecyclerView.Adapter<MusicFlowAdapter.ListItemViewHolder> implements View.OnClickListener {

    private List<OverFlowItem> mList;
    private MusicInfo musicInfo;
    private Context mContext;

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public MusicFlowAdapter(Context context, List<OverFlowItem> list, MusicInfo info) {
        mList = list;
        musicInfo = info;
        mContext = context;
    }
    @NonNull
    @Override
    public ListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_flow_layout, parent, false);
        ListItemViewHolder vh = new ListItemViewHolder(view);
        //将创建的View注册点击事件
        view.setOnClickListener(this);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemViewHolder holder, int position) {
        OverFlowItem item = mList.get(position);
        holder.title.setText(item.getTitle());
        holder.icon.setImageResource(item.getAvatar());
        holder.itemView.setTag(position + "");
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onClick(View v) {
        if(mOnItemClickListener != null){
            mOnItemClickListener.onItemClick(v, (String) v.getTag());
        }
    }

    public interface OnRecyclerViewItemClickListener{
        void onItemClick(View view, String data);
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener){
        this.mOnItemClickListener = listener;
    }


    public class ListItemViewHolder extends RecyclerView.ViewHolder{
        ImageView icon;
        TextView title;

        public ListItemViewHolder(@NonNull View itemView) {
            super(itemView);
            this.icon = itemView.findViewById(R.id.img_flow_music);
            this.title = itemView.findViewById(R.id.tv_flow_music);
        }
    }
}
