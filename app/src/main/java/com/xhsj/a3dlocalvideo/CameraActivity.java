package com.xhsj.a3dlocalvideo;


import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.xhsj.a3dlocalvideo.adapter.LocalVideoAdapert;
import com.xhsj.a3dlocalvideo.bean.LocalVideoBean;
import com.xhsj.a3dlocalvideo.suface.MySurfaceView;
import com.xhsj.a3dlocalvideo.suface.NlCamera;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CameraActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener, MySurfaceView.GestrueListener {
    private Button surfaceBtn;
    private NlCamera cameraLeft, cameraRight;
    private TextureView textureViewLeft, textureViewRight;
    private SurfaceView svCamL = null;
    private SurfaceView svCamR = null;
    //-------------
    private MediaPlayer mediaPlayer;
    private int currentPosition;//当前视频播放的位置
    private boolean beleft = true;
    private boolean beProess = false;
    private List<String> filePaths = new ArrayList<>();
    int playIndex = 0;
    String filepath = "/storage/emulated/0/DCIM/3dVideo/man.mp4";
    private Button button;
    /**
     * 左边相机prv的宽
     */
    private int mCameraLeftWith;
    /**
     * 左边相机prv的高
     */
    private int mCameraLeftHeight;
    /**
     * 右边相机prv的宽
     */
    private int mCameraRightWith;
    /**
     * 右边相机prv的高
     */
    private int mCameraRightHeight;
    /**
     * 抽屉按钮
     */
    private ImageView iv;
    /**
     * 抽屉列表
     */
    private RecyclerView recyclerView;

    /**
     * 自定义surfaceView
     */
    private MySurfaceView mSurfaceView;
    private SurfaceHolder surfaceHolder;
    private RelativeLayout relativeLayout;

    private static final int TRANS_TO_YUV = 0;
    private static final int YUV_READER = 1;


    static {
        System.loadLibrary("native-lib");
    }

    private SurfaceView sfv;
    private int iDispWidthL;
    private int iDispHeightL;
    private int iDispWidthR;
    private int iDispHeightR;
    private int[] iArrPrevDataL;
    private int[] iArrPrevDataR;
    private boolean isChanging;
    private SeekBar seekbar;


    private NlCamera camera;
    private byte[] nv21data;
    private LocalVideoAdapert mLocalVideoAdapert;
    private int dataPostion;
    private ArrayList<LocalVideoBean> mVideoDataList;

    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);
        mSurfaceView = findViewById(R.id.surfaceview);
        relativeLayout = findViewById(R.id.relativeLayout);
        mSurfaceView.registerListener(this);
        Intent intent = getIntent();
        if (null != intent) {
            dataPostion = intent.getIntExtra("beansPostion", 0);
            playIndex = dataPostion;
            mVideoDataList = (ArrayList<LocalVideoBean>) intent.getSerializableExtra("beans");
        }else {
            mVideoDataList = new ArrayList<>();
            LocalVideoBean localVideoBean = new LocalVideoBean();
            localVideoBean.setName1("");
            LocalVideoBean localVideoBean1 = new LocalVideoBean();
            localVideoBean.setName1("");
            LocalVideoBean localVideoBean2 = new LocalVideoBean();
            localVideoBean.setName1("");
            LocalVideoBean localVideoBean3 = new LocalVideoBean();
            localVideoBean.setName1("");
            LocalVideoBean localVideoBean4= new LocalVideoBean();
            localVideoBean.setName1("");
            mVideoDataList.add(localVideoBean);
            mVideoDataList.add(localVideoBean1);
            mVideoDataList.add(localVideoBean2);
            mVideoDataList.add(localVideoBean3);
            mVideoDataList.add(localVideoBean4);
        }

        cameraLeft = new NlCamera();
        cameraRight = new NlCamera();
        if (cameraRight == null) {
            Log.e("camera1", "camera1");
        } else {
            Log.e("camera1", "camera111111");
        }


//        filePaths.add("m1.mp4");
//        filePaths.add("m2.mp4");
//        filePaths.add("m3.mp4");
        filePaths.add("2.mp4");


        //sfv = (SurfaceView) findViewById(R.id.surfaceview);
        surfaceBtn = (Button) findViewById(R.id.surfaceviewBtn);
        textureViewLeft = findViewById(R.id.textureView1);
        textureViewRight = findViewById(R.id.textureView2);
        Log.e("camera2220", "打开");
        getCameraInstance();
        init3DCamera();
        initView();
        initSurfaceViewClick();
    }


    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void init3DCamera() {
        //打开摄像头
        if (checkReadPermission(Manifest.permission.CAMERA, 1)) {
            try {
                mCameraLeftWith = cameraLeft.sizePrev.getWidth();
                mCameraLeftHeight = cameraLeft.sizePrev.getHeight();
                mCameraRightWith = cameraRight.sizePrev.getWidth();
                mCameraRightHeight = cameraRight.sizePrev.getHeight();
            } catch (final Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CameraActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }

        }
        cameraLeft.SetPreviewTexture(textureViewLeft);
        cameraRight.SetPreviewTexture(textureViewRight);

        svCamL = findViewById(R.id.sv_cam1);
        svCamR = findViewById(R.id.sv_cam2);
        /*svCamL.getHolder().addCallback(new SurfaceHolder.Callback2() {
            @Override
            public void surfaceRedrawNeeded(SurfaceHolder surfaceHolder) {
                iDispWidthL = surfaceHolder.getSurfaceFrame().width();
                iDispHeightL = surfaceHolder.getSurfaceFrame().height();
                iArrPrevDataL = new int[iDispWidthL * iDispHeightL];
            }

            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                iDispWidthL = surfaceHolder.getSurfaceFrame().width();
                iDispHeightL = surfaceHolder.getSurfaceFrame().height();
                iArrPrevDataL = new int[iDispWidthL * iDispHeightL];
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                iDispWidthL = surfaceHolder.getSurfaceFrame().width();
                iDispHeightL = surfaceHolder.getSurfaceFrame().height();
                iArrPrevDataL = new int[iDispWidthL * iDispHeightL];
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            }
        });

        Log.e("camera2223", "打开");


        svCamR.getHolder().addCallback(new SurfaceHolder.Callback2() {
            @Override
            public void surfaceRedrawNeeded(SurfaceHolder surfaceHolder) {
                iDispWidthR = surfaceHolder.getSurfaceFrame().width();
                iDispHeightR = surfaceHolder.getSurfaceFrame().height();
                iArrPrevDataR = new int[iDispWidthR * iDispHeightR];
            }

            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                iDispWidthR = surfaceHolder.getSurfaceFrame().width();
                iDispHeightR = surfaceHolder.getSurfaceFrame().height();
                iArrPrevDataR = new int[iDispWidthR * iDispHeightR];
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                iDispWidthR = surfaceHolder.getSurfaceFrame().width();
                iDispHeightR = surfaceHolder.getSurfaceFrame().height();
                iArrPrevDataR = new int[iDispWidthR * iDispHeightR];
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            }
        });*/
        cameraLeft.camera.setPreviewCallback(new Camera.PreviewCallback() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onPreviewFrame(final byte[] bytes, Camera camera) {
                if ((beleft) && (!beProess)) {
                    beProess = true;

                    Log.i("Arrays.toString", Arrays.toString(bytes));
                    Log.i("Arrays.toStringle2", (bytes.length) + "");
                    Log.i("Arrays.toStringw", cameraLeft.sizePrev.getWidth() + "");
                    Log.i("Arrays.toStringh", cameraLeft.sizePrev.getHeight() + "");
                    sendData(bytes, true, mCameraLeftWith, mCameraLeftHeight, iArrPrevDataL);

                    Log.e("onPreviewFrametrue", bytes + "");

//                    if (svCamL != null) {
//                        Canvas canvas = svCamL.getHolder().lockCanvas();
//                        Log.e("onPreviewFrametrue1", bytes + "");
//                        canvas.drawBitmap(iArrPrevDataL, 0, iDispWidthL, 0, 0, iDispWidthL, iDispHeightL, true, null);
//                        Log.e("onPreviewFrametrue2", bytes + "");
//                        svCamL.getHolder().unlockCanvasAndPost(canvas);
//                        Log.e("onPreviewFrametrue3", bytes + "");
//                    }

                    beleft = false;
                    beProess = false;
                }
            }


        });


        cameraRight.camera.setPreviewCallback(new Camera.PreviewCallback() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onPreviewFrame(final byte[] bytes, Camera camera) {

                if ((!beleft) && (!beProess)) {
                    beProess = true;
                    Log.e("onPreviewFramefalse", bytes + "");
                    sendData(bytes, false, mCameraRightWith, mCameraRightHeight, iArrPrevDataR);


//                            if (svCamR != null) {
//                                Canvas canvas = svCamR.getHolder().lockCanvas();
//                                Log.e("onPreviewFramefalse1", bytes + "");
//                                canvas.drawBitmap(iArrPrevDataR, 0, iDispWidthR, 0, 0, iDispWidthR, iDispHeightR, true, null);
//                                Log.e("onPreviewFramefalse2", bytes + "");
//                                svCamR.getHolder().unlockCanvasAndPost(canvas);
//                                Log.e("onPreviewFramefalse3", bytes + "");
//
//                            }
                    beleft = true;
                    beProess = false;
                }
            }
        });

        Log.i("faceFilePathin", "1111111111");
        String faceFilePath = getFilesDir().getAbsolutePath() + "/lbpcascade_frontalface_improved.xml";
        Log.i("faceFilePathout", "faceFilePath");
        File faceFile = new File(faceFilePath);
        Log.e("camera2226", faceFilePath);
        if (!faceFile.exists()) {
            Log.e("camera2225", "打开");
            try {
                InputStream in = getResources().openRawResource(R.raw.lbpcascade_frontalface_improved);
                int lenght = in.available();
                byte[] buffer = new byte[lenght];
                in.read(buffer);

                FileOutputStream out = new FileOutputStream(faceFile);
                out.write(buffer);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (setCascadeClassifier(faceFilePath) != 0) {
            Log.e("setCascadeClassifier1", "setCascadeClassifier");
            finish();
            Log.e("setCascadeClassifier", "setCascadeClassifier");
        }


        String strStereoParamFilePath = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/Calibration/dualcam_3_0.data";//getFilesDir().getAbsolutePath() + "/StereoParams.data";
        Log.e("fileStereoParam1", strStereoParamFilePath);
        File fileStereoParam = new File(strStereoParamFilePath);
        if (!fileStereoParam.exists()) {
            fileStereoParam.getParentFile().mkdirs();
            Log.e("fileStereoParam1", "fileStereoParam");
            finish();
            Log.e("fileStereoParam", "fileStereoParam");
        }

        if (loadCalibrateParam(strStereoParamFilePath) < 0) {
            Log.e("loadCalibrateParam1", "loadCalibrateParam");
            finish();
            Log.e("loadCalibrateParam", "loadCalibrateParam");
        }
        init3D();


    }


    /**
     * 判断是否有某项权限
     *
     * @param string_permission 权限
     * @param request_code      请求码
     * @return
     */
    @SuppressLint("NewApi")
    public boolean checkReadPermission(String string_permission, int request_code) {
        boolean flag = false;
        if (checkSelfPermission(string_permission) == PackageManager.PERMISSION_GRANTED) {//已有权限
            flag = true;
        } else {//申请权限
            requestPermissions(new String[]{string_permission}, request_code);
        }
        return flag;
    }

    /**
     * 检查权限后的回调
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (permissions.length != 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {//失败
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CameraActivity.this, "请权限后再试", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {//成功
                    cameraLeft.Open(0);
                    cameraRight.Open(1);
                }
                break;
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            cameraLeft.Resume();
            cameraRight.Resume();
            init3D();
            Toast.makeText(getApplicationContext(), "3D模式已开启", Toast.LENGTH_SHORT).show();
            Log.i("3D模式已开启", "3D模式已开启");
        } else {
            cameraLeft.Pause();
            cameraRight.Pause();
            onInit();
            Toast.makeText(getApplicationContext(), "3D模式已关闭", Toast.LENGTH_SHORT).show();
            Log.i("3D模式已关闭", "3D模式已关闭");
        }
    }

    /**
     * 处理相机开启的线程
     * onPreviewFrame 接收帧数据在相机开启的线程
     * 会出现阻塞UI现象
     */
    private class CameraHandlerThread extends HandlerThread {
        Handler mHandler;

        public CameraHandlerThread(String name) {
            super(name);
            start();
            mHandler = new Handler(getLooper());
        }

        synchronized void notifyCameraOpened() {
            notify();
        }

        void openCamera() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    openCameraOriginal();
                    notifyCameraOpened();
                }
            });
            try {
                wait();
            } catch (InterruptedException e) {

            }
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        private void openCameraOriginal() {
            try {
                cameraLeft.Open(0);
                cameraRight.Open(1);

            } catch (Exception e) {

            }
        }
    }

    public void getCameraInstance() {
        CameraHandlerThread mThread = new CameraHandlerThread("camera thread");
        synchronized (mThread) {
            mThread.openCamera();
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initView() {
        mediaPlayer = new MediaPlayer();
        surfaceHolder = mSurfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.i("lylog", "SurfaceHolder 创建");

                playVideo("m2");
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




    private void playVideo(String urlname) {
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
            int id = R.raw.class.getDeclaredField(urlname).getInt(this);


            Uri videoURI = Uri.parse("android.resource://" + CameraActivity.this.getPackageName() + "/" + id);
//                    AssetFileDescriptor afd = getResources().getAssets().openFd("2.mp4");
//                    mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer.setDataSource(CameraActivity.this, videoURI);
//            mediaPlayer.setDataSource(path);
            // 设置异步加载视频，包括两种方式 prepare()同步，prepareAsync()异步
            mediaPlayer.setDisplay(surfaceHolder);
            mediaPlayer.prepareAsync();
            mediaPlayer.setLooping(true);
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
        mediaPlayer.release();
        currentPosition = 0;
        cameraLeft.Destroy();
        cameraRight.Destroy();
        onInit();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.d("lylog1", " onPause ");
        currentPosition = mediaPlayer.getCurrentPosition();
        mediaPlayer.stop();
        cameraLeft.Pause();
        cameraRight.Pause();
        onInit();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraLeft.Resume();
        cameraRight.Resume();
        init3D();
        mediaPlayer.seekTo(currentPosition);
    }


    @Override
    public void gestrueDerection(int firstX, int endX) {

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

    }

    /**
     * 初始化surfaceView监听
     */
    public void initSurfaceViewClick(){
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("onClick","onClick0");
                if (null != mVideoDataList){
                    Log.i("onClick","onClick1");
                    playIndex++;
                    if (playIndex >= mVideoDataList.size()) {
                        playIndex = 0;
                    }
                    Log.i("onClickplayIndex",playIndex+"");
                    playVideo(mVideoDataList.get(playIndex).getName1());
                }


            }
        });
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    //    //调用底层
    private native static int sendData(byte[] bytes, boolean dataFlag, int width, int height, int[] iArrPrevData);

    private native int setCascadeClassifier(String strFaceFilePath);

    private native int loadCalibrateParam(String strStereoParamFile);

    private native int init3D();

    private native int onInit();

}
