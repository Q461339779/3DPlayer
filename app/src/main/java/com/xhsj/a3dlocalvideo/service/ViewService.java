package com.xhsj.a3dlocalvideo.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.xhsj.a3dlocalvideo.MainActivity;
import com.xhsj.a3dlocalvideo.R;
import com.xhsj.a3dlocalvideo.view.FloatWindow;


/**
 *  窗体悬浮服务
 */
public class ViewService extends Service implements FloatWindow.FloatClickListener {


    private WindowManager windowManager;
    private Button button;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        showOver();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        windowManager.removeView(button);
    }

    private void showOver() {

        FloatWindow floatWindow = new FloatWindow(this);
        floatWindow.setLayout(R.layout.fl);
        floatWindow.show();
        floatWindow.setOnFloatListener(this);

    }

    @Override
    public void onFloatClick(View v) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
    }
}
