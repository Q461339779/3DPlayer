package com.xhsj.a3dlocalvideo;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import xyz.doikki.videocontroller.LogUtils;
import xyz.doikki.videocontroller.StandardVideoController;
import xyz.doikki.videocontroller.callBack.IVideoInfoCallback;
import xyz.doikki.videocontroller.component.CompleteView;
import xyz.doikki.videocontroller.component.ErrorView;
import xyz.doikki.videocontroller.component.GestureView;
import xyz.doikki.videocontroller.component.PrepareView;
import xyz.doikki.videocontroller.component.RightControlView;
import xyz.doikki.videocontroller.component.TitleView;
import xyz.doikki.videocontroller.component.VodControlView;
import xyz.doikki.videocontroller.eventbus.MessageEventPostion;
import xyz.doikki.videocontroller.handler.WeakHandler;
import xyz.doikki.videocontroller.videoinfo.M3U8Seg;
import xyz.doikki.videocontroller.videoinfo.VideoInfoParserManager;
import xyz.doikki.videoplayer.ijk.IjkPlayerFactory;
import xyz.doikki.videoplayer.player.VideoView;
import xyz.doikki.videoplayer.util.PlayerUtils;


/**
 * 全屏播放
 */

public class FullScreenActivity extends BaseActivity<VideoView> {

    private StandardVideoController mController;
    private static int mCurrentVideoPosition;//用于自动播放的位置累加
    private TitleView titleView;
    private float mLocalSpeed = 1.0f;
    String videoPath ;

    @Override
    protected View getContentView() {
        mVideoView = new VideoView(this);
        adaptCutoutAboveAndroidP();
        return mVideoView;
    }

    @Override
    protected int getTitleResId() {
        return R.string.app_name;
    }

    @Override
    protected void initView() {
        super.initView();
        if (false){
            videoPath = VideoListActivity.videoPath;
            initPlayer(videoPath);
        }else {
            //初始化
            VideoInfoParserManager.getInstance().parseVideoInfo(mUrl, mVideoInfoCallback);
        }


    }

    private void initPlayer(String videoPath) {
        mVideoView.setPlayerFactory(IjkPlayerFactory.create());
        mVideoView.startFullScreen();
        
        //mVideoView.setUrl(videoPath);
        //mVideoView.setUrl("http://vfx.mtime.cn/Video/2019/03/14/mp4/190314223540373995.mp4");
        mVideoView.setUrl(VideoListActivity.videoPath);
        //mVideoView.setUrl("http://videoconverter.vivo.com.cn/201706/655_1498479540118.mp4.f10.m3u8");
        mController = new StandardVideoController(this);
        mController.addControlComponent(new CompleteView(this));
        mController.addControlComponent(new ErrorView(this));
        mController.addControlComponent(new PrepareView(this));

        titleView = new TitleView(this);
        // 我这里改变了返回按钮的逻辑，我不推荐这样做，我这样只是为了方便，
        // 如果你想对某个组件进行定制，直接将该组件的代码复制一份，改成你想要的样子
        titleView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        mCurrentVideoPosition = VideoListActivity.videopostion;
        titleView.setTitle(VideoListActivity.listPictures.get(VideoListActivity.videopostion).getName());
        mController.addControlComponent(titleView);
        VodControlView vodControlView = new VodControlView(this);
        // 我这里隐藏了全屏按钮并且调整了边距，我不推荐这样做，我这样只是为了方便，
        // 如果你想对某个组件进行定制，直接将该组件的代码复制一份，改成你想要的样子
        vodControlView.findViewById(R.id.fullscreen).setVisibility(View.GONE);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) vodControlView.findViewById(R.id.total_time).getLayoutParams();
        lp.rightMargin = PlayerUtils.dp2px(this, 16);
        mController.addControlComponent(vodControlView);
        mController.addControlComponent(new RightControlView(this));
        mController.addControlComponent(new GestureView(this));
        mVideoView.setVideoController(mController);
        mVideoView.setScreenScaleType(VideoView.SCREEN_SCALE_16_9);
        //监听播放结束
        mVideoView.addOnStateChangeListener(new VideoView.SimpleOnStateChangeListener() {

            @Override
            public void onPlayStateChanged(int playState) {
                if (playState == VideoView.STATE_PLAYBACK_COMPLETED) {
                    if (mCurrentVideoPosition == VideoListActivity.listPictures.size() - 1) {
                        finish();
                    }
                    if (VideoListActivity.listPictures != null && mCurrentVideoPosition < VideoListActivity.listPictures.size() - 1) {
                        Log.i("mCurrentVideoPosition0000", mCurrentVideoPosition + "");
                        RightControlView.AnthologyBeanlist.get(mCurrentVideoPosition).setIschecked(false);//播放完的设置为不选中
                        if (mCurrentVideoPosition >= VideoListActivity.listPictures.size() - 1)
                            return;
                        mCurrentVideoPosition++;
                        RightControlView.AnthologyBeanlist.get(mCurrentVideoPosition).setIschecked(true);//当前自动播放位置数据设置选中
                        mVideoView.release();
                        //重新设置数据
                        mVideoView.setUrl(VideoListActivity.listPictures.get(mCurrentVideoPosition).getPath());
                        titleView.setTitle(VideoListActivity.listPictures.get(mCurrentVideoPosition).getName());
                        mVideoView.setVideoController(mController);
                        mVideoView.setScreenScaleType(VideoView.SCREEN_SCALE_16_9);
                        //开始播放
                        mVideoView.start();

                    }
                } else if (playState == VideoView.STATE_PLAYING) {
                    mVideoView.setSpeed(mLocalSpeed);
                }
            }
        });
        mVideoView.start();
        EventBus.getDefault().register(this);
    }

    private void adaptCutoutAboveAndroidP() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
    }


    @Override
    public void onBackPressed() {
        if (!mController.isLocked()) {
            finish();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEventPostion event) {

        if (event.getFlag().equals("Speed")) {
            mLocalSpeed = event.getSpeed();
            mVideoView.setSpeed(mLocalSpeed);
        } else if (event.getFlag().equals("Anthology")) {
            if (VideoListActivity.listPictures != null) {
                Log.i("mCurrentVideoPosition0", mCurrentVideoPosition + "");
                RightControlView.AnthologyBeanlist.get(mCurrentVideoPosition).setIschecked(false);//播放完的设置为不选中

                mCurrentVideoPosition = event.getPostion();
                Log.i("mCurrentVideoPosition1", mCurrentVideoPosition + "");
                if (mCurrentVideoPosition >= VideoListActivity.listPictures.size()) return;
                RightControlView.AnthologyBeanlist.get(mCurrentVideoPosition).setIschecked(true);//当前自动播放位置数据设置选中
                mVideoView.release();
                //重新设置数据
                mVideoView.setUrl(VideoListActivity.listPictures.get(mCurrentVideoPosition).getPath());
                titleView.setTitle(VideoListActivity.listPictures.get(mCurrentVideoPosition).getName());
                mVideoView.setVideoController(mController);
                mVideoView.setScreenScaleType(VideoView.SCREEN_SCALE_16_9);
                //开始播放
                mVideoView.start();
            }
        }else if (event.getFlag().equals("Resolution")){
            /**设置清晰度切换*/
            mVideoView.setUrl(event.getmResolutionUrl());
            mVideoView.replay(false);
        }else if (event.getFlag().equals("screen_shot")){
            /**截图操作*/
            Bitmap bitmap = mVideoView.doScreenShot();
            StandardVideoController.setScreenShotBitmap(bitmap,this);
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    // --------------------------------------------------------------------------------------
    private String mUrl = "http://videoconverter.vivo.com.cn/201706/655_1498479540118.mp4.main.m3u8";
    private static final int MSG_MULTIPLE_VIDEO = 2;

    private IVideoInfoCallback mVideoInfoCallback = new IVideoInfoCallback() {
        @Override
        public void onVideoType(String contentType, String name) {

        }

        @Override
        public void onMutipleVideo(List<M3U8Seg> urlList) {
            LogUtils.e("onMutipleVideo : size="+urlList.size());
            Message message = Message.obtain();
            message.what = MSG_MULTIPLE_VIDEO;
            message.obj = urlList;
            mHandler.sendMessage(message);
        }

        @Override
        public void onFailed(Exception e) {
            e.printStackTrace();
            LogUtils.e("onFailed, e="+e.getMessage());
        }
    };


    private WeakHandler mHandler = new WeakHandler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@android.support.annotation.NonNull Message msg) {
            if (msg.what == MSG_MULTIPLE_VIDEO) {
                List<M3U8Seg> urlList = (List<M3U8Seg>) msg.obj;
                initVideoResolutions(urlList);
            }
            return true;
        }
    });


    private void initVideoResolutions(List<M3U8Seg> urlList) {
        int position ;
        if (urlList.size() == 1) {
            urlList.get(0).setIschecked(true);
            position = 0;
            initPlayer(urlList.get(0).getUrl());
        }else {
            urlList.get(1).setIschecked(true);
            initPlayer(urlList.get(1).getUrl());
            position = 1;
        }
        RightControlView.setResolutionList(urlList,position);

    }


    private void changeResolution(int position) {
//        if (mResolutionPosition != position) {
//            mResolutionPosition = position;
//            String url = mSegList.get(position - 1).getUrl();
//            if (mPlayer.isPlaying()) {
//                mPlayer.reset();
//                try {
//                    mPlayer.setDataSource(this, Uri.parse(url));
//                } catch (Exception e) {
//                    return;
//                }
//                mPlayer.setSurface(mSurface);
//                mPlayer.prepareAsync();
//            }
//        } else {
//            Toast.makeText(this, "正在观看", Toast.LENGTH_SHORT).show();
//        }
    }
}

