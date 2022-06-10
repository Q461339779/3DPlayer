package xyz.doikki.videocontroller.callBack;


import java.util.List;

import xyz.doikki.videocontroller.videoinfo.M3U8Seg;

public interface IVideoInfoCallback {
  void onVideoType(String contentType, String name);
  void onMutipleVideo(List<M3U8Seg> urlList);
  void onFailed(Exception e);
}
