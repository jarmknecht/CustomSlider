package com.example.jonathan.diy_slider;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.view.View;
import android.widget.TextView;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MySeekBar.MySeekbarListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //instantiate my two thumbed seekbar.
        //final MySeekBar mySeekBar = new MySeekBar(this);
        //Parameters for constructor:
        // float thumbRadius, float vertPadding(vertical padding for thumb to show up on screen), percent of screen widget takes up, int numThumbs, float value at top of ellipse, float value at bottom of ellipse)
        final MySeekBar mySeekBar = new MySeekBar(this, 50f, 3f, 100f, 3, 5f, 1023f);
        mySeekBar.addAsListener(this);

        // add it to the layout.
        FrameLayout layout = (FrameLayout) findViewById (R.id.seekbar_placeholder);
        layout.addView(mySeekBar);
        String strValue = String.valueOf((int) mySeekBar.getMinValue());
        for (int i = 1; i < mySeekBar.getNumThumbs(); i++) {
            strValue += String.valueOf(",\t" + (int)mySeekBar.getMinValue());
        }
        ((TextView) findViewById(R.id.sliderValue)).setText(strValue);


        /*mySeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent e) {
                ((TextView)view.getRootView().findViewById(R.id.sliderValue)).setText(String.valueOf((int)mySeekBar.getCurrValue()));
                //((TextView)view.getRootView().findViewById(R.id.sliderValue)).setText(String.valueOf(mySeekBar.getCurrValue()));
                return false;
            }
        });*/
    }

    @Override
    public void onValueChanged(List<Float> values, MySeekBar mySeekBar) {
        String strValue = String.valueOf((int)values.get(0).floatValue());

        for (int i = 1; i < values.size(); i++) {
            strValue += String.valueOf(",\t" + (int)values.get(i).floatValue());
        }
        ((TextView)findViewById(R.id.sliderValue)).setText(strValue);
    }
}
