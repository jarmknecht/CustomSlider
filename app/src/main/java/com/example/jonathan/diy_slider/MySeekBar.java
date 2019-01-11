package com.example.jonathan.diy_slider;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by mike on 6/14/2017.
 * see https://github.com/anothem/android-range-seek-bar/blob/master/rangeseekbar/src/main/java/org/florescu/android/rangeseekbar/RangeSeekBar.java
 */

public class MySeekBar extends View
{

    private PointF circleCenter;
    private PointF viewTopLeft;
    private PointF viewBottomRight;
    private boolean isMoving;
    private float radiusOfThumb;
    private float topPadding;
    private float currValue;
    private float minValue = 0;
    private float maxValue = 100;
    Paint myPaint;
    Paint movingPaint;

    public MySeekBar(Context context) {
        super(context);
        //circleCenter = new PointF(0f,0f);
        viewTopLeft = new PointF(this.getLeft(),this.getRight());
        viewBottomRight = new PointF(this.getRight(),this.getBottom());
        isMoving = false;
        myPaint = new Paint();
        myPaint.setColor(0xff101010);
        myPaint.setAntiAlias(true);
        myPaint.setTextSize(90f);

        movingPaint = new Paint();
        movingPaint.setColor(0xffff0000);
        movingPaint.setAntiAlias(true);
        movingPaint.setTextSize(90f);

        radiusOfThumb = 50f;
        topPadding = radiusOfThumb + 10f;

        circleCenter = new PointF(viewTopLeft.x + radiusOfThumb, viewTopLeft.y + topPadding);

        invalidate();
    }


    @Override
    protected synchronized void onDraw (Canvas canvas)
    {
        viewTopLeft = new PointF(this.getLeft(),this.getTop());
        viewBottomRight = new PointF(this.getRight(),this.getBottom());
        Log.d ("seek","on draw");
        super.onDraw(canvas);

        drawLineFromPoints (new PointF(viewTopLeft.x, viewTopLeft.y + topPadding),
                new PointF(viewBottomRight.x, viewTopLeft.y + topPadding),canvas,myPaint);

        if (isMoving) {
            canvas.drawCircle(circleCenter.x, circleCenter.y, radiusOfThumb, movingPaint);
        }
        else {
            canvas.drawCircle(circleCenter.x, circleCenter.y, radiusOfThumb, myPaint);
        }
        //Draws a line from point leftmost x value at the y pixel plus the padding to the point
        // at the end of the screen at the same height (topLefty + padding)

    }

    private void drawLineFromPoints(PointF viewTopLeft, PointF viewBottomRight, Canvas canvas, Paint myPaint) {
        canvas.drawLine(viewTopLeft.x,viewTopLeft.y,viewBottomRight.x,viewBottomRight.y,myPaint);

    }

    @Override
    public boolean onTouchEvent (MotionEvent event)
    {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                // get the location of the finger down.
                if (event.getY() <= topPadding + radiusOfThumb) {
                    isMoving = true;
                    if (event.getX() < viewTopLeft.x + radiusOfThumb) {
                        circleCenter.x = viewTopLeft.x + radiusOfThumb;
                    }
                    else if (event.getX() > viewBottomRight.x - radiusOfThumb) {
                        circleCenter.x = viewBottomRight.x - radiusOfThumb;
                    }
                    else {
                        circleCenter.x = event.getX();
                    }
                    currValue = event.getX();
                    circleCenter.y = viewTopLeft.y + topPadding;
                }
                // draw it on the screen.
                break;
            case MotionEvent.ACTION_UP:
                isMoving = false;

                break;
        }


        invalidate(); // make sure we force a redraw.
        return true;
    }

    public float getCurrValue() {
        return this.currValue;
    }
}
