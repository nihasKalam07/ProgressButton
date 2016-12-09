package com.nihaskalam.sample;

import android.app.ActionBar;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.nihaskalam.progressbuttonlibrary.CircularProgressButton;
import com.nihaskalam.progressbuttonlibrary.OnAnimationUpdateListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample1);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
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
                } else  {
                    circularButton1.showComplete();
                }

//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        circularButton1.showComplete();
//                    }
//                }, 10000);
            }
        });

        final CircularProgressButton circularButton2 = (CircularProgressButton) findViewById(R.id.circularButton2);
        circularButton2.setIndeterminateProgressMode(false);
        circularButton2.setStrokeColor(ContextCompat.getColor(this, R.color.colorStroke));
        int duration = 5000;
        final int factor = duration/100;
        circularButton2.setCustomSweepDuration(duration);
        circularButton2.setOnAnimationUpdateListener(new OnAnimationUpdateListener() {
            @Override
            public void onAnimationTimeUpdate(int time) {
                TextView textView = (TextView) findViewById(R.id.timeTV);

                textView.setText(String.format("%s : %s", "Percentage completed", Integer.toString(time/factor)));
            }
        });
        circularButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (circularButton2.isIdle()) {
                    circularButton2.showProgress();
                } else if (circularButton2.isErrorOrComplete()) {
                    circularButton2.showIdle();
                } else if (circularButton2.isProgress()) {
                    circularButton2.showCancel();
                } else {
                    circularButton2.showComplete();
                }
            }
        });
    }
}
