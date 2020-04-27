package pers.cs.videoandaudio.ui.fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pers.cs.videoandaudio.R;
import pers.cs.videoandaudio.adapter.MusicFlowAdapter;
import pers.cs.videoandaudio.bean.OverFlowItem;
import pers.cs.videoandaudio.info.MusicInfo;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoreFragment extends DialogFragment {

    private static final String TAG = MoreFragment.class.getSimpleName();
    private static final Boolean DEBUG = true;
    
    private Context mContext;
    private double heightPercent;

    private TextView tv_title;
    private RecyclerView recyclerview;
    private MusicFlowAdapter mMusicFlowAdapter;

    private MusicInfo musicInfo;

    //弹出的activity列表
    private List<OverFlowItem> mlistInfo = new ArrayList<>();

    public static MoreFragment newInstance(MusicInfo musicInfo) {
        
        Bundle args = new Bundle();
        args.putParcelable("music",musicInfo);
        MoreFragment fragment = new MoreFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CustomMoreDialog);
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //设置从底部弹出
        WindowManager.LayoutParams params = getDialog().getWindow()
                .getAttributes();
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setAttributes(params);

        View view = inflater.inflate(R.layout.fragment_more, container, false);;
        tv_title = view.findViewById(R.id.tv_more_title);
        recyclerview = view.findViewById(R.id.recyclerview_more);
        recyclerview.setLayoutManager(new LinearLayoutManager(mContext,RecyclerView.VERTICAL,false));
        recyclerview.setHasFixedSize(true);
        recyclerview.addItemDecoration(new DividerItemDecoration(mContext,DividerItemDecoration.VERTICAL));


        getInfo();
        initListener();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        int dialogHeight = (int) (mContext.getResources().getDisplayMetrics().heightPixels * heightPercent);
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, dialogHeight);
        getDialog().setCanceledOnTouchOutside(true);
    }

    private void initListener() {
        if(mMusicFlowAdapter != null){
            mMusicFlowAdapter.setOnRecyclerViewItemClickListener(new MusicFlowAdapter.OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, String data) {
                    switch (Integer.parseInt(data)){

                        case 2:
//                            Intent shareIntent = new Intent();
//                            shareIntent.setAction(Intent.ACTION_SEND);
//                            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + musicInfo.getData()));
//                            shareIntent.setType("audio/*");
//                            startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.shared_to)));
                            dismiss();
                            break;
                        case 3:
                            new AlertDialog.Builder(mContext).setTitle("确定删除歌曲吗？")
                                .setPositiveButton("确定",new DialogInterface.OnClickListener(){

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
//                                        Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, musicInfo.getSongId());
//                                        mContext.getContentResolver().delete(uri, null, null);
                                    }
                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dismiss();
                                    }
                                }).show();

                            dismiss();
                            break;
                        case 6:
                            if (musicInfo.islocal) {
                                new AlertDialog.Builder(mContext).setTitle("确定设置为铃声吗").
                                        setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    if (!Settings.System.canWrite(mContext)) {
                                                        Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                                                        intent.setData(Uri.parse("package:" + mContext.getPackageName()));
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        mContext.startActivity(intent);
                                                    } else {
                                                        //有了权限，具体的动作
                                                        Uri ringUri = Uri.parse("file://" + musicInfo.data);
                                                        RingtoneManager.setActualDefaultRingtoneUri(mContext, RingtoneManager.TYPE_RINGTONE, ringUri);
                                                        dialog.dismiss();
                                                        Toast.makeText(mContext, "设置成功",
                                                                Toast.LENGTH_SHORT).show();
                                                        dismiss();
                                                    }
                                                }

                                            }
                                        }).
                                        setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();
                            } else {

                            }

                            break;
                        case 7:
                            MusicDetailFragment detailFrament = MusicDetailFragment.newInstance(musicInfo);
                            detailFrament.show(getActivity().getSupportFragmentManager(), "detail");
                            dismiss();
                            break;

                        default:
                            dismiss();
                            break;
                    }
                }
            });
        }


        recyclerview.setAdapter(mMusicFlowAdapter);
    }

    private void getInfo() {
        musicInfo = getArguments().getParcelable("music");
        heightPercent = 0.6;
        tv_title.setText("歌曲：" + " " + musicInfo.getMusicName());
        setMusicInfo();
        mMusicFlowAdapter = new MusicFlowAdapter(mContext, mlistInfo, musicInfo);
    }

    //设置音乐overflow条目
    private void setMusicInfo() {
        //设置mlistInfo，listview要显示的内容
        setInfo("下一首播放", R.drawable.lay_icn_next);
        setInfo("收藏到歌单", R.drawable.lay_icn_fav);
        setInfo("分享", R.drawable.lay_icn_share);
        setInfo("删除", R.drawable.lay_icn_delete);
        setInfo("歌手：" + musicInfo.getArtist(), R.drawable.lay_icn_artist);
        setInfo("专辑：" + musicInfo.getAlbumName(), R.drawable.lay_icn_alb);
        setInfo("设为铃声", R.drawable.lay_icn_ring);
        setInfo("查看歌曲信息", R.drawable.lay_icn_document);
    }

    //为info设置数据，并放入mlistInfo
    public void setInfo(String title, int id) {
        // mlistInfo.clear();
        OverFlowItem information = new OverFlowItem();
        information.setTitle(title);
        information.setAvatar(id);
        mlistInfo.add(information); //将新的info对象加入到信息列表中
    }
}
