package com.xhsj.a3dlocalvideo;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.xhsj.a3dlocalvideo.suface.MySurfaceView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;



//import video.hc.com.videodemo.utils.SurfaceViewOutlineProviderUtil;

public class MainActivity extends AppCompatActivity implements   MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener , MySurfaceView.GestrueListener{

    private static int currentPosition = 0;
    private SurfaceHolder surfaceHolder;
    MediaPlayer mediaPlayer;
    /**
     * 自定义surfaceView
     */
    private MySurfaceView mSurfaceView;
    //    ArrayList<Map<String, String>> urlList = new ArrayList();
    int ivProgressFlag = 0;
    private static int mPrecent = 0;
    private static boolean lunboFlag = false;
    private static int mapPosition = 0;
    public static ArrayList<Map<String, String>> listdata;
    private Button button;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mSurfaceView = findViewById(R.id.surfaceview);
        button = findViewById(R.id.next);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playVideo("");
            }
        });
        mSurfaceView.registerListener(this);
        initView();
        mSurfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playVideo("");
            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initView() {
        mediaPlayer = new MediaPlayer();
        surfaceHolder = mSurfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.i("lylog", "SurfaceHolder 创建");
                playVideo("");
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.i("lylog", "SurfaceHolder 变化时");
                mediaPlayer.setDisplay(surfaceHolder);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.i("lylog", "SurfaceHolder 被销毁");
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    currentPosition = mediaPlayer.getCurrentPosition();
                    mediaPlayer.pause();
                }
            }
        });
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


       // mSurfaceView.setOutlineProvider(new SurfaceViewOutlineProviderUtil(30));
        mSurfaceView.setClipToOutline(true);



    }




    private void playVideo(String url) {
        // 重置mediaPaly,建议在初始滑mediaplay立即调用。
        mediaPlayer.reset();

        // 设置声音效果
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        // 设置播放完成监听
        mediaPlayer.setOnCompletionListener(this);

        // 设置媒体加载完成以后回调函数。
        mediaPlayer.setOnPreparedListener(this);
        // 错误监听回调函数
        mediaPlayer.setOnErrorListener(this);
        // 设置缓存变化监听
        mediaPlayer.setOnBufferingUpdateListener(this);
        //网络请求：
        try {
            //mediaPlayer.setDataSource(url);
            int id = R.raw.class.getDeclaredField("m2").getInt(this);


            Uri videoURI = Uri.parse("android.resource://" + MainActivity.this.getPackageName() + "/" + id);
//                    AssetFileDescriptor afd = getResources().getAssets().openFd("2.mp4");
//                    mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer.setDataSource(MainActivity.this, videoURI);
//            mediaPlayer.setDataSource(path);
            // 设置异步加载视频，包括两种方式 prepare()同步，prepareAsync()异步
            mediaPlayer.setDisplay(surfaceHolder);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }



    private void play(int currentPosition) {
        Log.i("lylog", "currentPosition =  " + currentPosition);
        mediaPlayer.seekTo(currentPosition);
        mediaPlayer.start();
    }

    private void resizeSurfaceView() {
        int w = mediaPlayer.getVideoWidth();
        int h = mediaPlayer.getVideoHeight();
        Display display = getWindowManager().getDefaultDisplay();
        int screenW = display.getWidth();
        int screenH = (int) (screenW * (((float) h) / w));
        mSurfaceView.getHolder().setFixedSize(screenW, screenH);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {



    }

    @Override
    public void onPrepared(MediaPlayer mp) {

        Log.i("lylog", " onPrepared");
        resizeSurfaceView();
        play(currentPosition);
        Log.i("lylog11", "onPrepared canPlay =");
    }








    @Override
    protected void onDestroy() {
        Log.i("lylog1", "onDestroy");
        mediaPlayer.release();
        lunboFlag = false;
        currentPosition = 0;
        mapPosition = 0;
        mPrecent = 0;

        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.d("lylog1", " onPause ");
        currentPosition = mediaPlayer.getCurrentPosition();
        mediaPlayer.stop();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("lylog1", " onResume mPrecent =" + mPrecent);
        // playVideo(beifenTrueUrl);
        mediaPlayer.seekTo(currentPosition);
    }


    @Override
    public void gestrueDerection(int firstX, int endX) {

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }
}
