package com.xhsj.a3dlocalvideo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xhsj.a3dlocalvideo.Picture;
import com.xhsj.a3dlocalvideo.R;

import java.util.ArrayList;

import xyz.doikki.videocontroller.adapter.AnthologyBean;


/**
 * @auther qushaobo
 *
 * 本地视频适配器
 */
public class LocalVideoAdapert extends RecyclerView.Adapter<LocalVideoAdapert.ViewHolder> {
    private Context context;
    private ArrayList<AnthologyBean> mainVideoBeans;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public LocalVideoAdapert(Context context, ArrayList<AnthologyBean> mainVideoBeans) {
        this.context = context;
        this.mainVideoBeans = mainVideoBeans;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LinearLayout.inflate(context, R.layout.local_video_item, null);
        ViewHolder viewHolder = new ViewHolder(inflate);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final ViewHolder holder1 = (ViewHolder) holder;


        holder1.ablum.setImageBitmap(getVideoThumbnail(mainVideoBeans.get(position).getPath(),400, 200, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND));
        holder1.videoName.setText(mainVideoBeans.get(position).getName());
        holder1.ablum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.OnItemClick(mainVideoBeans ,position);

            }
        });

    }

    //获取视频的缩略图
    private Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图 最大关键帧
//        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
//        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
//                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
//
        // 获取视频的缩略图 第一个关键帧
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoPath);
        bitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);


        return bitmap;
    }


    @Override
    public int getItemCount() {
        return mainVideoBeans.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ablum;
        private TextView videoName;

        public ViewHolder(View itemView) {
            super(itemView);
            ablum = itemView.findViewById(R.id.local_video_img);
            videoName = itemView.findViewById(R.id.video_name);
        }
    }

    public interface OnItemClickListener{
        void OnItemClick(ArrayList<AnthologyBean> localVideoBeans, int position);
    }



}
