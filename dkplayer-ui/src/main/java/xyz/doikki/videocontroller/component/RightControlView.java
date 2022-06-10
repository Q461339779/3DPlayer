package xyz.doikki.videocontroller.component;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import xyz.doikki.videocontroller.LogUtils;
import xyz.doikki.videocontroller.R;
import xyz.doikki.videocontroller.adapter.AnthologyBean;
import xyz.doikki.videocontroller.adapter.MultiTypeAdapter;
import xyz.doikki.videocontroller.adapter.SpeedBean;
import xyz.doikki.videocontroller.adapter.Vistable;
import xyz.doikki.videocontroller.callBack.IVideoInfoCallback;
import xyz.doikki.videocontroller.handler.WeakHandler;
import xyz.doikki.videocontroller.videoinfo.M3U8Seg;
import xyz.doikki.videoplayer.controller.ControlWrapper;
import xyz.doikki.videoplayer.controller.IControlComponent;
import xyz.doikki.videoplayer.player.VideoView;
import xyz.doikki.videoplayer.util.PlayerUtils;

/**
 * 点播底部控制栏
 */
public class RightControlView extends FrameLayout implements IControlComponent, View.OnClickListener {

    private static int preSelection;
    private static int preSpeedPosition;
    private static int mPreResolutionPosition;

    protected ControlWrapper mControlWrapper;


    private RelativeLayout mBottomContainer;
    private RecyclerView recyclerView;

    private static MultiTypeAdapter adapter;


    private LinearLayoutManager manager;
    private Context mContext;

    public static void setList(ArrayList<AnthologyBean> list) {
        AnthologyBeanlist = list;
        mList.addAll(list);
    }

    public static ArrayList<AnthologyBean> AnthologyBeanlist = new ArrayList<AnthologyBean>();
    public static ArrayList<SpeedBean> SpeedBeanlist = new ArrayList<SpeedBean>();
    public static ArrayList<Vistable> mList = new ArrayList<Vistable>();
    public static ArrayList<Vistable> mSpeedList = new ArrayList<Vistable>();
    //清晰度地址集合
    private static List<Vistable> mResolutionList= new ArrayList<Vistable>();
    private static List<M3U8Seg> M3U8SegBeanList= new ArrayList<M3U8Seg>();
    private boolean mIsShowBottomProgress = true;

    public RightControlView(@NonNull Context context) {
        super(context);
        initSpeedList();
        mContext = context;

    }

    public static void setSelectSpeedPrePosition(int position) {
            preSpeedPosition = position;
    }

    public static void setSelectSpeedPosition(int position) {
        mSpeedList.clear();
        SpeedBeanlist.get(preSpeedPosition).setIschecked(false);
        SpeedBeanlist.get(position).setIschecked(true);
        mSpeedList.addAll(SpeedBeanlist);
        adapter.notifyDataSetChanged();
    }

    public static void setSelectResolutionPosition(int position) {
        mResolutionList.clear();
        M3U8SegBeanList.get(mPreResolutionPosition).setIschecked(false);
        M3U8SegBeanList.get(position).setIschecked(true);
        mResolutionList.addAll(M3U8SegBeanList);
        mPreResolutionPosition = position ;
        adapter.notifyDataSetChanged();
    }

    public static void setResolutionList(List<M3U8Seg> urlList,int postion) {
        mPreResolutionPosition = postion;
        M3U8SegBeanList.addAll(urlList);
        mResolutionList.addAll(M3U8SegBeanList);
    }

    private void initSpeedList() {
        String[] speedArr = {"2.0X","1.75X","1.5X","1.25X","1.0X","0.7X","0.5X"};
        float num = 2.0f;
        for (int i = 0; i < 7; i++) {
            SpeedBean speedBean = new SpeedBean();
            speedBean.setName(speedArr[i]);
            if (i==4){
                speedBean.setIschecked(true);
            }
            speedBean.setNum(num);
            num = num-0.25f;
            SpeedBeanlist.add(speedBean);
        }
        mSpeedList.addAll(SpeedBeanlist);

    }

    public RightControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RightControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }


    {
        setVisibility(GONE);
        LayoutInflater.from(getContext()).inflate(getLayoutId(), this, true);
        mBottomContainer = findViewById(R.id.bottom_container);
        recyclerView = (RecyclerView) findViewById(R.id.right_menu_control_recyclerview);

    }

    public static void setSelectPosition(int position) {
        mList.clear();
        AnthologyBeanlist.get(preSelection).setIschecked(false);
        AnthologyBeanlist.get(position).setIschecked(true);
        mList.addAll(AnthologyBeanlist);
        adapter.notifyDataSetChanged();


    }

    public static void setSelectPrePosition(int position) {
        preSelection = position;

    }

    protected int getLayoutId() {
        return R.layout.dkplayer_layout_right_menu_control_view;
    }


    @Override
    public void attach(@NonNull ControlWrapper controlWrapper) {
        mControlWrapper = controlWrapper;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {

    }

    //定义从右侧进入的动画效果
    protected Animation inFromRightAnimation() {
        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(300);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }

    //定义从左侧进入的动画效果
    protected Animation inFromLeftAnimation() {
        Animation inFromLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromLeft.setDuration(500);
        inFromLeft.setInterpolator(new AccelerateInterpolator());
        return inFromLeft;
    }

    //定义从左侧退出的动画效果
    protected Animation outToRightAnimation() {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(500);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }

    // 定义从左侧进入的动画效果
    protected Animation inFromLeftAnimation2() {
        Animation inFromLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.7f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        inFromLeft.setDuration(500);
        inFromLeft.setFillAfter(true);
        inFromLeft.setInterpolator(new AccelerateInterpolator());
        return inFromLeft;
    }


    //定义从左侧退出的动画效果
    protected Animation outToLeftAnimation2() {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        outtoLeft.setDuration(500);
        outtoLeft.setFillAfter(true);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }


    @Override
    public void onPlayStateChanged(int playState) {

//        switch (playState) {
//            case VideoView.STATE_IDLE:
//            case VideoView.STATE_PLAYBACK_COMPLETED:
//                setVisibility(GONE);
//                Log.i("播放完成","播放完成。。。。");
//
//                break;
//            case VideoView.STATE_START_ABORT:
//            case VideoView.STATE_PREPARING:
//            case VideoView.STATE_PREPARED:
//            case VideoView.STATE_ERROR:
//                setVisibility(GONE);
//                break;
//            case VideoView.STATE_PLAYING:
//
//                if (mIsShowBottomProgress) {
//                    if (mControlWrapper.isShowing()) {
//
//                        mBottomContainer.setVisibility(VISIBLE);
//                    } else {
//                        mBottomContainer.setVisibility(GONE);
//                    }
//                } else {
//                    mBottomContainer.setVisibility(GONE);
//                }
//                setVisibility(VISIBLE);
//                //开始刷新进度
//                mControlWrapper.startProgress();
//                break;
//            case VideoView.STATE_PAUSED:
//                break;
//            case VideoView.STATE_BUFFERING:
//            case VideoView.STATE_BUFFERED:
//                break;
//        }


    }

    @Override
    public void onPlayerStateChanged(int playerState) {
        switch (playerState) {
            case VideoView.PLAYER_NORMAL:
                break;
            case VideoView.PLAYER_FULL_SCREEN:
                break;
        }

        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity != null && mControlWrapper.hasCutout()) {
            int orientation = activity.getRequestedOrientation();
            int cutoutHeight = mControlWrapper.getCutoutHeight();
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                mBottomContainer.setPadding(0, 0, 0, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                mBottomContainer.setPadding(cutoutHeight, 0, 0, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                mBottomContainer.setPadding(0, 0, cutoutHeight, 0);
            }
        }
    }

    @Override
    public void setProgress(int duration, int position) {


    }

    @Override
    public void onLockStateChanged(boolean isLocked) {
        onVisibilityChanged(!isLocked, null);
    }

    @Override
    public void onMaskStateChanged(boolean isMasked, String flag) {
        if (isMasked) {
            setVisibility(VISIBLE);
            mBottomContainer.setVisibility(VISIBLE);
            Animation animation = inFromRightAnimation();
            mBottomContainer.startAnimation(animation);
            manager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(manager);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setHasFixedSize(false);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            if (flag.equals("speed")) {

                adapter = new MultiTypeAdapter(mSpeedList, mContext);

            } else if (flag.equals("anthology")) {

                adapter = new MultiTypeAdapter(mList, mContext);

            } else {
                adapter = new MultiTypeAdapter(mResolutionList, mContext);
            }
            recyclerView.setAdapter(adapter);


        } else {
            mBottomContainer.setVisibility(GONE);
            Animation animation = outToRightAnimation();
            mBottomContainer.startAnimation(animation);


        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.fullscreen) {
            toggleFullScreen();
        } else if (id == R.id.iv_play) {
            mControlWrapper.togglePlay();
        }
    }

    /**
     * 横竖屏切换
     */
    private void toggleFullScreen() {
        Activity activity = PlayerUtils.scanForActivity(getContext());
        mControlWrapper.toggleFullScreen(activity);
        // 下面方法会根据适配宽高决定是否旋转屏幕
//        mControlWrapper.toggleFullScreenByVideoSize(activity);
    }






}
