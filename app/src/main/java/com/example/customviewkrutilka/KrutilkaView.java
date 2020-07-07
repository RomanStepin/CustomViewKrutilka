package com.example.customviewkrutilka;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class KrutilkaView extends View
{
   private int segmentCount;
    private int colorTheme;
    private int Color1;
    private int Color2;
    private int ColorGrad;
    private float radius;
    private float rotate_angle;
    private Executor executor;
    Paint paint;
    RectF oval;
    float segment_alpha;
    float d_alpha;
    RadialGradient radialGradient1;
    RadialGradient radialGradient2;


    public KrutilkaView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Log.d("LOG", "KrutilkaView");
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.KrutilkaView);
        segmentCount = typedArray.getInt(R.styleable.KrutilkaView_segmentCount, 1);
        colorTheme = typedArray.getInt(R.styleable.KrutilkaView_colorTheme, 0);
        switch (colorTheme)
        {
            case 0:
                Color1 = Color.BLUE;
                Color2 = Color.RED;
                ColorGrad = Color.GRAY;
                break;
            case 1:
                Color1 = Color.BLACK;
                Color2 = Color.WHITE;
                ColorGrad = Color.GRAY;
                break;
            case 2:
                Color1 = Color.BLACK;
                Color2 = Color.RED;
                ColorGrad = Color.GRAY;
                break;
        }
        radius = typedArray.getFloat(R.styleable.KrutilkaView_radius,-1);
        if (radius <= 0) {
            if (this.getWidth() > this.getHeight()) radius = this.getHeight()/2;            // если радиус не задан - берем минимальную сторону / 2
            else radius = this.getWidth()/2; }
        paint = new Paint();
        oval = new RectF(0,0, radius*2, radius*2);
        radialGradient1 = new RadialGradient(oval.centerX(), oval.centerY(), radius/3, Color1, ColorGrad, Shader.TileMode.MIRROR);
        radialGradient2 = new RadialGradient(oval.centerX(), oval.centerY(), radius/3, Color2, ColorGrad, Shader.TileMode.MIRROR);
        rotate_angle = 0;
        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        segment_alpha = (float)360/segmentCount;                                            // угол сегмента

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GRAY);
        canvas.rotate(rotate_angle, oval.centerX(), oval.centerY());
        canvas.drawOval(oval, paint);
        d_alpha = 0;
        for (int i = 0; i < segmentCount; i++) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.RED);
            paint.setStrokeWidth(2);
            if (i % 2 > 0)
                paint.setShader(radialGradient1);
            else
                paint.setShader(radialGradient2);
            canvas.drawArc(oval, d_alpha, segment_alpha, true, paint);
            paint.setShader(null);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.BLACK);
            canvas.drawArc(oval, d_alpha, segment_alpha, true, paint);
            d_alpha += segment_alpha;
        }
    }

    public void rotate(final float angle)
    {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                double speed = 3;
                double delta = 3;
                float absAngle = Math.abs(angle);
                for (int i = 0; i < absAngle; i++) {
                    rotate_angle += (angle/absAngle);
                    postInvalidate();
                    try {
                        TimeUnit.MILLISECONDS.sleep((long)speed);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (i == absAngle * 2/3) speed += delta;
                    if (i == absAngle * 3/4) speed += delta;
                    if (i == absAngle * 4/5) speed += delta;
                    if (i == absAngle * 5/6) speed += delta;
                    if (i == absAngle * 6/7) speed += delta;
                }
            }
        });
    }


}
