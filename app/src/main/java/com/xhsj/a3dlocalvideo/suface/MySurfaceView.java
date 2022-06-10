package com.xhsj.a3dlocalvideo.suface;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;


/**
 * Created by ly on 2019/6/4.
 */

public class MySurfaceView extends SurfaceView {

    private GestrueListener gestrueListener;

    public interface GestrueListener {
        void gestrueDerection(int firstX, int endX);
    }

    public MySurfaceView(Context context) {
        super(context);
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void registerListener(GestrueListener gestrueListener) {
        this.gestrueListener = gestrueListener;
    }

    int firstX = 0, endX = 0;
    boolean onceFullGestureFlag = true;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("lylog_Dis", " ACTION_DOWN ");
                firstX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("lylog_Dis", " ACTION_MOVE ");
                break;
            case MotionEvent.ACTION_UP:
                onceFullGestureFlag = false;
                Log.d("lylog_Dis", " ACTION_UP ");
                endX = x;
                break;
        }

        if (!onceFullGestureFlag) {
            gestrueListener.gestrueDerection(firstX, endX);
            onceFullGestureFlag = true;
        }

        return true;
    }
}
