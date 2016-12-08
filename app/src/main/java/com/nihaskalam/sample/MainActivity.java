package com.nihaskalam.sample;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.nihaskalam.progressbuttonlibrary.CircularProgressButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample1);

        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.setTitle(R.string.IndeterminateProgressSample);
        }

        final CircularProgressButton circularButton1 = (CircularProgressButton) findViewById(R.id.circularButton1);
        circularButton1.setIndeterminateProgressMode(true);
        circularButton1.setStrokeColor(ContextCompat.getColor(this, R.color.colorStroke));
        circularButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (circularButton1.isIdle()) {
                    circularButton1.showProgress();
                } else if (circularButton1.isErrorOrComplete()) {
                    circularButton1.showIdle();
                } else {
                    circularButton1.showComplete();
                }
            }
        });

        final CircularProgressButton circularButton2 = (CircularProgressButton) findViewById(R.id.circularButton2);
        circularButton2.setIndeterminateProgressMode(false);
        circularButton2.setStrokeColor(ContextCompat.getColor(this, R.color.colorStroke));
        circularButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (circularButton2.isIdle()) {
                    circularButton2.showProgress();
                } else if (circularButton2.isErrorOrComplete()) {
                    circularButton2.showIdle();
                } else {
                    circularButton2.showComplete();
                }
            }
        });
    }
}
