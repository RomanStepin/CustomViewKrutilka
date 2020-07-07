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
    int segmentCount;
    int colorTheme;
    int Color1;
    int Color2;
    int ColorGrad;
    float radius;
    Picture picture;
    Canvas canvas;
    float rotate_angle;
    Executor executor;

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
        rotate_angle = 0;
        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        float height = (float)this.getHeight();
        float width = (float)this.getWidth();
        float alpha = (float)360/segmentCount;
        if (radius <= 0) {
            if (width > height) radius = height/2;
            else radius = width/2; }
        float diameter = radius * 2;

        super.onDraw(canvas);
        this.canvas = canvas;


        picture = new Picture();
        picture.beginRecording((int)width,(int)height);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GRAY);
        RectF oval = new RectF(0,0, diameter, diameter);
        canvas.rotate(rotate_angle, oval.centerX(), oval.centerY());
        canvas.drawOval(oval,paint);

        float d_alpha = 0;
        for (int i = 0; i < segmentCount; i++) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.RED);
            paint.setStrokeWidth(2);
            if (i % 2 > 0)
                paint.setShader(new RadialGradient(oval.centerX(), oval.centerY(), radius/3, Color1, ColorGrad, Shader.TileMode.MIRROR));
            else
                paint.setShader(new RadialGradient(oval.centerX(), oval.centerY(), radius/3, Color2, ColorGrad, Shader.TileMode.MIRROR));
            canvas.drawArc(oval, d_alpha, alpha, true, paint);
            paint.setShader(null);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.BLACK);
            canvas.drawArc(oval, d_alpha, alpha, true, paint);
            d_alpha += alpha;
        }
        picture.endRecording();
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
                    invalidate();
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
