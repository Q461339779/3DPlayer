package xyz.doikki.videocontroller.videoinfo;

import xyz.doikki.videocontroller.adapter.TypeFactory;
import xyz.doikki.videocontroller.adapter.Vistable;

public class M3U8Seg implements Vistable {
  private String mUrl;
  private String mResolution = "未知";

  public M3U8Seg(String url) {
    mUrl = url;
  }

  public M3U8Seg(String url, String resolution) {
    mUrl = url;
    mResolution = resolution;
  }

  public String getUrl() {
    return mUrl;
  }

  public void setResolution(String resolution) {
    mResolution = resolution;
  }

  public String getResolution() {
    return mResolution;
  }

  private boolean ischecked = false;

  public boolean isIschecked() {
    return ischecked;
  }

  public void setIschecked(boolean ischecked) {
    this.ischecked = ischecked;
  }

  @Override
  public int type(TypeFactory factory) {
    return factory.type(this);
  }
}
