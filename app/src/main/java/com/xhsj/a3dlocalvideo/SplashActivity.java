package com.xhsj.a3dlocalvideo;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;


/**
 * 启动页面
 * Created by Jacky on 2017/7/25.
 */
public class SplashActivity extends Activity {
    private final static int TIME_DURING = 2000;
    protected static boolean isPresseHomeKey = false;

    protected static long lastTime = System.currentTimeMillis();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);

        initViews();
    }

    protected void initViews() {
        ImageView companyLogo = (ImageView) findViewById(R.id.companyLogo);
        final ScaleAnimation animation =new ScaleAnimation(1.0f, 1.4f, 1.0f, 1.4f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setFillAfter(true);
        animation.setDuration(2000);//设置动画持续时间
        companyLogo.setAnimation(animation);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setLastTimes(0L);
                setIsPresseHomeKey(true);
                Intent intent = new Intent(SplashActivity.this, VideoListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animation.start();

    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }


    public static void setLastTimes(long times) {
        lastTime = times;
    }

    public static void setIsPresseHomeKey(boolean presseHomeKey) {
        isPresseHomeKey = presseHomeKey;
    }

}
