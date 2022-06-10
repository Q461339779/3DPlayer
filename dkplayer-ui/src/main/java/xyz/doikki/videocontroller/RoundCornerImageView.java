package xyz.doikki.videocontroller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
public class RoundCornerImageView extends ImageView {
    private Path path;
    private int mWidth,mHeight;
    private float radius;

    public RoundCornerImageView(Context context) {
        this(context,null);
    }

    public RoundCornerImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RoundCornerImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        path=new Path();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth=MeasureSpec.getSize(widthMeasureSpec);
        mHeight=MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if(getBackground()!=null){
                radius=((GradientDrawable)getBackground()).getCornerRadius();
                path.addRoundRect(0,0,mWidth,mHeight,60,60, Path.Direction.CW);
                canvas.clipPath(path);
            }
        }
        super.onDraw(canvas);
    }
}

