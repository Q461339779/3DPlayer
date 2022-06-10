package xyz.doikki.videocontroller.adapter;

import android.view.View;

import xyz.doikki.videocontroller.videoinfo.M3U8Seg;

public interface TypeFactory {
    int type(AnthologyBean anthologyBean);
    int type(SpeedBean speedBean);
    int type(M3U8Seg m3U8Seg);
    BaseViewHolder createViewHolder(int type, View itemView);
}
