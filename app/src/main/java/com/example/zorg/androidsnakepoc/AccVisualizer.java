package com.example.zorg.androidsnakepoc;

/**
 * Created by roy on 26-Dec-14.
 */

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.View;

public class AccVisualizer extends View {

    private static final float RADIUS = 150;
    private Paint accPaint = new Paint();
    private Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float mAccX, mAccY;
    private boolean isSnake;

    public AccVisualizer(StartScreen context) {
        super(context);

        accPaint.setColor(0xff33bb33);
        accPaint.setStrokeWidth(5);
        accPaint.setAntiAlias(true);

        circlePaint.setColor(0xff33bb33);
        circlePaint.setStyle(Style.STROKE);
        circlePaint.setStrokeWidth((float) 5.0);
    }

    public AccVisualizer(SnakeScreen context) {
        super(context);

        accPaint.setColor(0xff33bb33);
        accPaint.setStrokeWidth(5);
        accPaint.setAntiAlias(true);

        circlePaint.setColor(0xff33bb33);
        circlePaint.setStyle(Style.STROKE);
        circlePaint.setStrokeWidth((float) 5.0);

        isSnake = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float midX = getWidth() / 2f;
        float midY = getHeight() / 2f;
        float accX = 0;
        float accY = 0;

        // is snake screen only draw 2 circles
        if (isSnake) {
            // Accelerometer
            accX = midX + mAccX * 50f;
            accY = midY + mAccY * 50f;
            canvas.drawLine(midX, midY, accX, accY, accPaint);
            canvas.drawCircle((float) midX, (float) midY, (float) (RADIUS / 4),
                    circlePaint);
            canvas.drawCircle((float) midX, (float) midY, (float) (RADIUS / 2),
                    circlePaint);
        }
        // if start screen draw 3 circles
        else {
            // Accelerometer
            accX = midX + mAccX * 100f;
            accY = midY + mAccY * 100f;
            canvas.drawLine(midX, midY, accX, accY, accPaint);

            canvas.drawCircle((float) midX, (float) midY, (float) (RADIUS / 2),
                    circlePaint);
            canvas.drawCircle((float) midX, (float) midY, (float) (RADIUS),
                    circlePaint);
            canvas.drawCircle((float) midX, (float) midY,
                    (float) ((6 * RADIUS) / 4), circlePaint);
        }
        invalidate();
    }

    public void setAcceleration(float x, float y) {
        mAccX = x;
        mAccY = y;
    }

}

