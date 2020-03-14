package pers.cs.videoandaudio.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import org.xutils.common.util.DensityUtil;

import java.util.List;

import cn.jzvd.JzvdStd;
import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.bean.VillageBean;
import pers.cs.videoandaudio.utils.TimeUtil;


/**
 * @author chensen
 * @time 2020/2/13  10:26
 * @desc
 */
public class CloudVillageFragmentAdapter extends BaseAdapter {

    private static final int TYPE_GIF = 0;
    private static final int TYPE_VIDEO = 1;
    private static final int TYPE_IMAGE = 2;
    private static final int TYPE_TEXT = 3;
    private static final int TYPE_AD = 4;


    private Context context;
    private List<VillageBean.VillageItem> villageItems;
    private TimeUtil timeUtil;

    public CloudVillageFragmentAdapter(Context context, List<VillageBean.VillageItem> villageItems) {
        this.context = context;
        this.villageItems = villageItems;
        timeUtil = new TimeUtil();
    }

    @Override
    public int getViewTypeCount() {
        return 5;
    }

    @Override
    public int getItemViewType(int position) {
        int type = -1;
        switch (villageItems.get(position).getType()) {
            case "gif":
                type = TYPE_GIF;
                break;
            case "video":
                type = TYPE_VIDEO;
                break;
            case "image":
                type = TYPE_IMAGE;
                break;
            case "text":
                type = TYPE_TEXT;
                break;
            case "ad":
                type = TYPE_AD;
                break;
            default:
                type = TYPE_AD;
                break;
        }
        return type;
    }

    @Override
    public int getCount() {
        return villageItems == null ? 0 : villageItems.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int type = getItemViewType(position);
        ViewHolder viewHolder;

        if(convertView == null){
            //初始化
            viewHolder = new ViewHolder();

            convertView = initView(convertView, type, viewHolder);
            initCommonView(convertView,viewHolder,type);
            convertView.setTag(viewHolder);

        }else{
            //获取tag
            viewHolder = (ViewHolder) convertView.getTag();
        }

        bindData(position,type,viewHolder);

        return convertView;
    }

    private View initView(View convertView, int type, ViewHolder viewHolder) {
        switch (type){
            case TYPE_GIF:
                convertView = View.inflate(context, R.layout.village_gif_item,null);
                viewHolder.tv_gif_content = convertView.findViewById(R.id.tv_gif_content);
                viewHolder.iv_gif = convertView.findViewById(R.id.iv_gif);

                break;

            case TYPE_VIDEO:
                convertView = View.inflate(context, R.layout.village_video_item,null);
                viewHolder.jzvdStd = convertView.findViewById(R.id.jzvdStd);
                viewHolder.tv_village_play_nums = convertView.findViewById(R.id.tv_village_play_nums);
                viewHolder.tv_village_video_duration = convertView.findViewById(R.id.tv_village_video_duration);
                viewHolder.tv_village_content = convertView.findViewById(R.id.tv_village_content);

                break;
            case TYPE_IMAGE:
                convertView = View.inflate(context, R.layout.village_image_item,null);
                viewHolder.tv_image_content = convertView.findViewById(R.id.tv_image_content);
                viewHolder.iv_image_content = convertView.findViewById(R.id.iv_image_content);


                break;
            case TYPE_TEXT:
                convertView = View.inflate(context, R.layout.village_text_item,null);
                viewHolder.tv_text = convertView.findViewById(R.id.tv_text);
                break;
            case TYPE_AD:
                convertView = View.inflate(context, R.layout.village_ad_item,null);


                break;
        }
        return convertView;
    }

    private void bindData(int position, int type, ViewHolder viewHolder) {
        VillageBean.VillageItem villageItem = villageItems.get(position);

        bindData(viewHolder,villageItem);

        switch (type){
            case TYPE_GIF:
                viewHolder.tv_gif_content.setText(villageItem.getText());

                VillageBean.VillageItem.GifBean gifbean = villageItem.getGif();
                if(gifbean != null && gifbean.getImages() != null && gifbean.getImages().size() > 0){

                    RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL);
                    Glide.with(context).load(gifbean.getImages().get(0))
                            .apply(options)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(GlideException e, Object o, Target<Drawable> target, boolean b) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
                                    if (drawable instanceof GifDrawable) {
                                        //加载一次
                                        ((GifDrawable)drawable).setLoopCount(1);
                                    }
                                    return false;
                                }
                            }).into(viewHolder.iv_gif);
                }
                break;
            case TYPE_VIDEO:
                viewHolder.tv_village_content.setText(villageItem.getText());

                VillageBean.VillageItem.VideoBean videoBean = villageItem.getVideo();

                if(videoBean != null){
                    if(videoBean.getVideo() != null && videoBean.getVideo().size() > 0){
                        viewHolder.jzvdStd.setUp(videoBean.getVideo().get(0),"", JzvdStd.SCREEN_NORMAL);
                        Glide.with(context).load(videoBean.getThumbnail_link().get(0)).into(viewHolder.jzvdStd.thumbImageView);
                    }
                }
                viewHolder.tv_village_play_nums.setText("" + videoBean.getPlaycount() + "次播放");
                viewHolder.tv_village_video_duration.setText("" + timeUtil.formatTime(videoBean.getDuration()*1000));
                viewHolder.jzvdStd.positionInList = position;


                break;
            case TYPE_IMAGE:
                VillageBean.VillageItem.ImageBean imageBean = villageItem.getImage();

                if(imageBean != null && imageBean.getBig() != null && imageBean.getBig().size() > 0){

                    int height = imageBean.getHeight()<= DensityUtil.getScreenHeight()*0.75?imageBean.getHeight(): (int) (DensityUtil.getScreenHeight() * 0.75);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(DensityUtil.getScreenWidth(),height);
                    viewHolder.iv_image_content.setLayoutParams(layoutParams);


                    Glide.with(context).load(imageBean.getBig().get(0))

                            .into(viewHolder.iv_image_content);
                }
                viewHolder.tv_image_content.setText(villageItem.getText());

                break;
            case TYPE_TEXT:
                viewHolder.tv_text.setText(villageItem.getText());
                break;
            case TYPE_AD:


                break;
        }
    }

    private void bindData(ViewHolder viewHolder, VillageBean.VillageItem villageItem) {

        VillageBean.VillageItem.UBean uBean = villageItem.getU();
        if(uBean != null){
            Glide.with(context).load(uBean.getHeader().get(0))
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(viewHolder.iv_headpic);
            viewHolder.tv_village_name.setText(uBean.getName());
            viewHolder.tv_village_time.setText(villageItem.getPasstime());
        }

        List<VillageBean.VillageItem.TagsBean> tagsBeans = villageItem.getTags();
        if(tagsBeans != null && tagsBeans.size() > 0){
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < tagsBeans.size(); i++) {
                buffer.append(tagsBeans.get(i).getName() + " ");
            }
            viewHolder.tv_kind.setText(buffer.toString());
        }

        if(villageItem.getTop_comments() != null && villageItem.getTop_comments().size() > 0){
            viewHolder.rl_top_comment.setVisibility(View.VISIBLE);
            VillageBean.VillageItem.TopCommentsBean topCommentsBean = villageItem.getTop_comments().get(0);
            VillageBean.VillageItem.UBean uBean1 = topCommentsBean.getU();
            Glide.with(context).load(uBean1.getHeader().get(0))
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(viewHolder.iv_comment_icon);
            viewHolder.tv_comment_name.setText(uBean1.getName());
            viewHolder.tv_comment_content.setText(topCommentsBean.getContent());
            if(topCommentsBean.getLike_count() > 999){
                viewHolder.tv_comment_zan.setText("999+");
            }else{
                viewHolder.tv_comment_zan.setText("" + topCommentsBean.getLike_count());
            }

        }else{
            viewHolder.rl_top_comment.setVisibility(View.GONE);
        }



        if(villageItem.getUp() == 0){
            viewHolder.tv_ding_number.setText("赞");
        }else{
            viewHolder.tv_ding_number.setText("" + villageItem.getUp());
        }
        if(villageItem.getDown() == 0){
            viewHolder.tv_cai_number.setText("踩");
        }else{
            viewHolder.tv_cai_number.setText("" + villageItem.getDown());
        }
        if(Integer.parseInt(villageItem.getComment()) == 0){
            viewHolder.tv_chart_number.setText("评论");
        }else{
            viewHolder.tv_chart_number.setText("" + villageItem.getComment());
        }
        if(villageItem.getForward() == 0){
            viewHolder.tv_share_number.setText("分享");
        }else{
            viewHolder.tv_share_number.setText("" + villageItem.getForward());
        }


    }

    private void initCommonView(View convertView, ViewHolder viewHolder, int type) {
        switch (type){
            case TYPE_GIF:
            case TYPE_VIDEO:
            case TYPE_IMAGE:
            case TYPE_TEXT:
                viewHolder.iv_headpic = convertView.findViewById(R.id.iv_headpic);
                viewHolder.tv_village_name = convertView.findViewById(R.id.tv_village_name);
                viewHolder.tv_village_time = convertView.findViewById(R.id.tv_village_time);
                viewHolder.iv_village_more = convertView.findViewById(R.id.iv_village_more);

                viewHolder.tv_kind = convertView.findViewById(R.id.tv_kind);


                viewHolder.rl_top_comment = convertView.findViewById(R.id.rl_top_comment);
                viewHolder.iv_comment_icon = convertView.findViewById(R.id.iv_comment_icon);
                viewHolder.tv_comment_name = convertView.findViewById(R.id.tv_comment_name);
                viewHolder.tv_comment_content = convertView.findViewById(R.id.tv_comment_content);
                viewHolder.tv_comment_zan = convertView.findViewById(R.id.tv_comment_zan);
                viewHolder.iv_comment_zan = convertView.findViewById(R.id.iv_comment_zan);

                viewHolder.iv_ding = convertView.findViewById(R.id.iv_ding);
                viewHolder.tv_ding_number = convertView.findViewById(R.id.tv_ding_number);
                viewHolder.iv_cai = convertView.findViewById(R.id.iv_cai);
                viewHolder.tv_cai_number = convertView.findViewById(R.id.tv_cai_number);
                viewHolder.tv_chart_number = convertView.findViewById(R.id.tv_chart_number);
                viewHolder.iv_chart = convertView.findViewById(R.id.iv_chart);
                viewHolder.tv_share_number = convertView.findViewById(R.id.tv_share_number);
                viewHolder.iv_share = convertView.findViewById(R.id.iv_share);

                break;
            case TYPE_AD:
                break;
        }
    }

    static class ViewHolder{

        //head
        ImageView iv_headpic;
        TextView tv_village_name;
        TextView tv_village_time;
        ImageView iv_village_more;
        //foot
        TextView tv_kind;

        RelativeLayout rl_top_comment;
        ImageView iv_comment_icon;
        TextView tv_comment_name;
        TextView tv_comment_content;
        TextView tv_comment_zan;
        ImageView iv_comment_zan;

        ImageView iv_ding;
        TextView tv_ding_number;
        ImageView iv_cai;
        TextView tv_cai_number;
        ImageView iv_chart;
        TextView tv_chart_number;
        ImageView iv_share;
        TextView tv_share_number;
        //video
        JzvdStd jzvdStd;
        TextView tv_village_content;
        TextView tv_village_play_nums;
        TextView tv_village_video_duration;

        //image
        TextView tv_image_content;
        ImageView iv_image_content;

        //gif
        TextView tv_gif_content;
        ImageView iv_gif;

        //Text
        TextView tv_text;

    }
}
