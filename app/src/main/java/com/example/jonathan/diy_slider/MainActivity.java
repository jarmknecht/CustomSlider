package com.example.jonathan.diy_slider;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //instantiate my two thumbed seekbar.
        MySeekBar mySeekBar = new MySeekBar(this);

        // add it to the layout.
        FrameLayout layout = (FrameLayout) findViewById (R.id.seekbar_placeholder);
        layout.addView(mySeekBar);


    }
}
