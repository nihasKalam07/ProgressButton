package com.nihaskalam.sample;

import android.app.ActionBar;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.nihaskalam.progressbuttonlibrary.CircularProgressButton;
import com.nihaskalam.progressbuttonlibrary.OnAnimationUpdateListener;

public class MainActivity extends AppCompatActivity {
    private CircularProgressButton circularButton1, circularButton2, circularButton3;
    private Button setProgressBtn;
    private int progress = 0;
    private TextView percentageTV, progressAmountTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample1);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.IndeterminateProgressSample);
        }
        circularButton1 = (CircularProgressButton) findViewById(R.id.circularButton1);
        circularButton1.setIndeterminateProgressMode(true);
        circularButton1.setStrokeColor(ContextCompat.getColor(this, R.color.colorStroke));

        circularButton2 = (CircularProgressButton) findViewById(R.id.circularButton2);
        circularButton2.setIndeterminateProgressMode(false);
        circularButton2.setStrokeColor(ContextCompat.getColor(this, R.color.colorStroke));
        int duration = 3000;
        final int factor = duration / 100;
        circularButton2.setCustomSweepDuration(duration);
        percentageTV = (TextView) findViewById(R.id.timeTV);
        circularButton2.setOnAnimationUpdateListener(new OnAnimationUpdateListener() {
            @Override
            public void onAnimationTimeUpdate(int time) {

                percentageTV.setText(String.format("%s : %s", "Percentage completed", Integer.toString(time / factor)));
            }
        });

        circularButton3 = (CircularProgressButton) findViewById(R.id.circularButton3);
        circularButton3.setIndeterminateProgressMode(false);
        circularButton3.setStrokeColor(ContextCompat.getColor(this, R.color.colorStroke));
        circularButton3.setCustomProgressMode(true);

        setProgressBtn = (Button) findViewById(R.id.button);

        progressAmountTV = (TextView) findViewById(R.id.progressAmountTV);
    }

    public void onClickFirstBtn(View view) {

        if (circularButton1.isIdle()) {
            circularButton1.showProgress();
        } else if (circularButton1.isErrorOrCompleteOrCancelled()) {
            circularButton1.showIdle();
        } else if (circularButton1.isProgress()) {
            circularButton1.showCancel();
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                circularButton1.showComplete();
            }
        }, 10000);
    }

    public void onClickSecondBtn(View view) {

        if (circularButton2.isIdle()) {
            circularButton2.showProgress();
        } else if (circularButton2.isErrorOrCompleteOrCancelled()) {
            circularButton2.showIdle();
        } else if (circularButton2.isProgress()) {
            circularButton2.showCancel();
        } else {
            circularButton2.showComplete();
        }
    }

    public void onClickThirdBtn(View view) {

        if (circularButton3.isIdle()) {
            circularButton3.showProgress();
        } else if (circularButton3.isErrorOrCompleteOrCancelled()) {
            circularButton3.showIdle();
        } else if (circularButton3.isProgress()) {
            circularButton3.showCancel();
        } else {
            circularButton3.showComplete();
        }
    }

    public void setProgress(View view) {
        if (!circularButton3.isProgress()){
            return;
        }
        if (progress >= 100) {
            progress = 25;
        } else {
            progress += 25;
        }
        circularButton3.setCustomProgress(progress);
        progressAmountTV.setText(String.format("%s : %s", "Progress completed", Integer.toString(progress)));
    }
}
