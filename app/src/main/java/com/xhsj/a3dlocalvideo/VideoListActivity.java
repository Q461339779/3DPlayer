package com.xhsj.a3dlocalvideo;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.xhsj.a3dlocalvideo.adapter.LocalVideoAdapert;
import xyz.doikki.videocontroller.util.Permission;

import java.io.File;
import java.util.ArrayList;

import xyz.doikki.videocontroller.adapter.AnthologyBean;
import xyz.doikki.videocontroller.component.RightControlView;


public class VideoListActivity extends AppCompatActivity implements LocalVideoAdapert.OnItemClickListener {

    private RecyclerView recyclerView;
    private LocalVideoAdapert mLocalVideoAdapert;
    /**
     * 加载等待框
     */
    private Dialog progressDialog;

    private AlertDialog dialog;
    public static ArrayList<AnthologyBean> listPictures;
    public static String videoPath;
    public static int videopostion;//提供给播放页面的视频数据位置



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_list);
        initViews();
        if (Permission.checkPermission(this)){
            loadVaule();
        }

//        if (Permission.isPermissionGranted(VideoListActivity.this)) {
//            loadVaule();
//        }


    }


    /**
     * 初始化布局控件
     */
    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.local_video_recyclerView);

    }


    /**
     * 列表监听
     *
     * @param localVideoBeans
     */
    @Override
    public void OnItemClick(ArrayList<AnthologyBean> localVideoBeans, int position) {
        localVideoBeans.get(position).setIschecked(true);//设置选集蒙版选中
        listPictures = localVideoBeans;
        videoPath = localVideoBeans.get(position).getPath();
        videopostion = position;
        RightControlView.setList(localVideoBeans);
        startActivity(new Intent(VideoListActivity.this, FullScreenActivity.class));
    }

    public void showDialog() {
        if (progressDialog == null) {
            progressDialog = new Dialog(this, R.style.progress_dialog);
            progressDialog.setContentView(R.layout.dialog);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            TextView msg = (TextView) progressDialog.findViewById(R.id.id_tv_loadingmsg);
            msg.setText("视频检索中");
        }
        progressDialog.show();

    }

    public void diamissDialog() {

        progressDialog.dismiss();

    }


    private String video_path = "/sdcard/DCIM/3d/";


    private void loadVaule() {
        File file = new File(video_path);
        //判断文件夹是否存在，如果不存在就创建一个
        if (!file.exists()) {
            file.mkdirs();
        }
        File[] files = file.listFiles();
        listPictures = new ArrayList<AnthologyBean>();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()&&files[i].getName().contains(".")){
                AnthologyBean anthologyBean = new AnthologyBean();
                anthologyBean.setPath(files[i].getPath());
                anthologyBean.setName(files[i].getName());
                listPictures.add(anthologyBean);
            }

        }

        Message msg = new Message();
        msg.what = 0;
        msg.obj = listPictures;
        handler.sendMessage(msg);

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);

            if (msg.what == 0) {

                recyclerView.setLayoutManager(new GridLayoutManager(VideoListActivity.this, 3));
                mLocalVideoAdapert = new LocalVideoAdapert(VideoListActivity.this, listPictures);
                mLocalVideoAdapert.setOnItemClickListener(VideoListActivity.this);
                recyclerView.setAdapter(mLocalVideoAdapert);

            }
        }

    };



    private static final String[] VIDEO_PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    /**
     * 用户权限 申请 的回调方法
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Permission.REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                    boolean b = shouldShowRequestPermissionRationale(permissions[0]);
                    // 以前是!b
                    if (b) {
                        // 用户还是想用我的 APP 的
                        // 提示用户去应用设置界面手动开启权限
                        showDialogTipUserGoToAppSettting();
                    } else {
                        finish();
                    }
                } else {
                    loadVaule();
                }
            }
        }
    }


    /**
     * 提示用户去应用设置界面手动开启权限
     */
    private void showDialogTipUserGoToAppSettting() {

        dialog = new AlertDialog.Builder(this)
                .setTitle("存储权限不可用")
                .setMessage("请在-应用设置-权限-中，允许应用使用存储权限来保存用户数据")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 跳转到应用设置界面
                        goToAppSetting();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        finish();
                    }
                }).setCancelable(false).show();
    }


    /**
     * 跳转到当前应用的设置界面
     */
    private void goToAppSetting() {
        Intent intent = new Intent();

        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);

        startActivityForResult(intent, 123);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //权限管理
        if (requestCode == 123) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 检查该权限是否已经获取
                int i = ContextCompat.checkSelfPermission(this, VIDEO_PERMISSIONS[0]);
                // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                if (i != PackageManager.PERMISSION_GRANTED) {
                    // 提示用户应该去应用设置界面手动开启权限
                    showDialogTipUserGoToAppSettting();
                } else {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
