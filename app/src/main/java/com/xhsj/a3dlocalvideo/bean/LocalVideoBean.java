package com.xhsj.a3dlocalvideo.bean;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * @author qushaobo
 *
 * 本地视频实体
 */
public class LocalVideoBean implements Serializable {

    String name;
    long size;
    String url;
    long duration;
    private String album;
    public String getName1() {
        return name;
    }
    public void setName1(String name) {
        this.name = name;
    }
    public long getSize1() {
        return size;
    }
    public void setSize1(long size) {
        this.size = size;
    }
    public String getUrl1() {
        return url;
    }
    public void setUrl1(String url) {
        this.url = url;
    }
    public long getDuration1() {
        return duration;
    }
    public void setDuration1(long duration) {
        this.duration = duration;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }
}
