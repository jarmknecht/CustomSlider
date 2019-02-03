package com.example.jonathan.diy_slider;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.icu.util.Measure;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mike on 6/14/2017.
 * see https://github.com/anothem/android-range-seek-bar/blob/master/rangeseekbar/src/main/java/org/florescu/android/rangeseekbar/RangeSeekBar.java
 *
 *
 * My seek bar displays an array of the values with the first value always belonging to the same thumb, second with second thumb etc
 *
 */

public class MySeekBar extends View
{

    private List<PointF> circleCenters;
    private Integer selectedCircle = null;
    private List<Float> values;
    private PointF viewTopLeft;
    private PointF viewBottomRight;
    private boolean isMoving;
    private float radiusOfThumb;
    private float topPadding;
    private float currValue;
    private float minValue = 0;
    private float maxValue = 100;
    private float thumbPadding = 4;
    private float vertPadding;
    private int numThumbs;
    private float percentOfScreen;
    private ArrayList<MySeekbarListener> listeners;
    Paint myPaint;
    Paint movingPaint;
    Paint arcPaint;

    public interface MySeekbarListener {
        public void onValueChanged(List<Float> values, MySeekBar mySeekBar);
    }

    public MySeekBar(Context context, float thumbRadius, float vertPadding, float percentOfScreen,
                     int numThumbs, float minValue, float maxValue) {
        super(context);
        //circleCenter = new PointF(0f,0f);
        this.numThumbs = numThumbs;
        this.percentOfScreen = percentOfScreen;
        listeners = new ArrayList<>();
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

        arcPaint = new Paint();
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setColor(0xff101010);
        arcPaint.setStrokeWidth(5f);
        arcPaint.setAntiAlias(true);
        arcPaint.setTextSize(90f);

        radiusOfThumb = thumbRadius;
        topPadding = radiusOfThumb + thumbPadding;

        this.minValue = minValue;
        this.maxValue = maxValue;
        this.vertPadding = vertPadding;

        circleCenters = new ArrayList<>();
        values = new ArrayList<>();

        for (int i = 0; i < numThumbs; i++) {
            values.add(minValue);
            circleCenters.add(new PointF(viewTopLeft.x, viewTopLeft.y));

        }
        //circleCenter = new PointF(viewTopLeft.x + radiusOfThumb, viewTopLeft.y + topPadding);

        invalidate();
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = 200;
        if (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED) {
            width = (int) (MeasureSpec.getSize(widthMeasureSpec) * percentOfScreen / 100);
        }

        int height = (int) (radiusOfThumb * 2f + vertPadding * 2);
        if (MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.UNSPECIFIED) {
            height = MeasureSpec.getSize(heightMeasureSpec);
        }

        setMeasuredDimension(width, height);
    }


    @Override
    protected synchronized void onDraw (Canvas canvas)
    {
        viewTopLeft = new PointF(this.getLeft(),this.getTop());
        viewBottomRight = new PointF(this.getRight(),this.getBottom());
        Log.d ("seek","on draw");
        super.onDraw(canvas);

        for (PointF point : circleCenters) {
            canvas.drawCircle(point.x, point.y, radiusOfThumb, myPaint);
        }
        float widthOfCurve = this.getRight();
        float heightOfCurve = this.getBottom() * 2;
        //drawLineFromPoints (viewTopLeft, viewBottomRight,canvas,myPaint);
        //left of arc(width), top of arc, right or arc (width), bottom of arc (height), startAngle, sweepAngle, useCenter, paint
        canvas.drawArc(-widthOfCurve, 0, widthOfCurve, heightOfCurve, 270f, 90f,false, arcPaint);

        if (selectedCircle != null) {
            if (isMoving) {
                canvas.drawCircle(circleCenters.get(selectedCircle).x, circleCenters.get(selectedCircle).y, radiusOfThumb, movingPaint);
            } else {
                canvas.drawCircle(circleCenters.get(selectedCircle).x, circleCenters.get(selectedCircle).y, radiusOfThumb, myPaint);
            }
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
                selectedCircle = getSelectedCircle(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                // get the location of the finger down.
                if (selectedCircle != null) {
                    isMoving = true;
                    float xVal = event.getX();
                    float yVal = event.getY();

                    if (event.getX() < viewTopLeft.x) {
                        xVal = viewTopLeft.x;
                    }

                    if (event.getY() > viewBottomRight.y) {
                        yVal = viewBottomRight.y;
                    }

                    calculateThumbPos(xVal, yVal, selectedCircle);

                    for (MySeekbarListener listener : listeners) {
                        listener.onValueChanged(values, this);
                    }
                }
                // draw it on the screen.
                break;
            case MotionEvent.ACTION_UP:
                isMoving = false;
                selectedCircle = null;
                break;
        }


        invalidate(); // make sure we force a redraw.
        return true;
    }

    public void addAsListener(MySeekbarListener listener) {
        listeners.add(listener);
    }

    private void calculateThumbPos(float x, float y, Integer selectedCircle) {
        // this is the vector from o (origin topLeft and bottomRight value) to p (x, y) v = p - o
        PointF vector = new PointF(x - this.viewTopLeft.x, y - this.viewBottomRight.y);

        double magnitudeOfVector = Math.sqrt(Math.pow(vector.x, 2) + Math.pow(vector.y, 2));

        // normalizes the vector by dividing by its magnitude/length
        vector.x /= magnitudeOfVector;
        vector.y /= magnitudeOfVector;

        double theta = Math.atan(Math.abs(vector.y) / Math.abs(vector.x));

        float radiusX = viewBottomRight.x - viewTopLeft.x;
        float radiusY = viewBottomRight.y - viewTopLeft.y;

        double radius = (radiusX * radiusY) / Math.sqrt((Math.pow(radiusX, 2) * Math.pow(Math.sin(theta), 2)
        + Math.pow(radiusY, 2) * Math.pow(Math.cos(theta), 2)));

        PointF t = new PointF((float)(this.viewTopLeft.x + (vector.x * radius)), (float)(this.viewBottomRight.y +
                (vector.y * radius)));

        circleCenters.get(selectedCircle).x = t.x;
        circleCenters.get(selectedCircle).y = t.y;
        //times theta by 180 to get degrees
        values.set(selectedCircle, (float)((maxValue + (minValue - maxValue) * (theta * 180f / (Math.PI * 90f)))));
    }


    private double getDistanceFromCircle(float x, float y, PointF circle) {
        return Math.sqrt(Math.pow(x - circle.x, 2) +
                Math.pow(y - circle.y, 2));  //Uses the distance formula
    }

    private Integer getSelectedCircle(float x, float y) {
        double minDistance = 99;
        Integer circle = null;

        for (int i = 0; i < circleCenters.size(); i++) {
            double distance = getDistanceFromCircle(x, y, circleCenters.get(i));
            if (distance < minDistance && distance < radiusOfThumb + thumbPadding) {
                minDistance = distance;
                circle = i;
            }
        }

        return circle;
    }

    public float getRadiusOfThumb(){
        return radiusOfThumb;
    }

    public void setRadiusOfThumb(float radiusOfThumb) {
        this.radiusOfThumb = radiusOfThumb;
        invalidate();
    }

    public float getMinValue() {
        return minValue;
    }

    public void setMinValue(float minValue) {
        this.minValue = minValue;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    public float getCurrValue() {
        return this.currValue;
    }

    public int getNumThumbs() {
        return this.numThumbs;
    }
}
