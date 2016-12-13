package com.nihaskalam.sample;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.nihaskalam.progressbuttonlibrary.CircularProgressButton;
import com.nihaskalam.progressbuttonlibrary.OnAnimationUpdateTimeListener;

public class MainActivity extends AppCompatActivity {
    private CircularProgressButton circularButton1, circularButton2, circularButton3, circularButton4, circularButton5;
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
        int duration = 5000;
        final int factor = duration / 100;
        circularButton2.setCustomSweepDuration(duration);
        percentageTV = (TextView) findViewById(R.id.timeTV);
        circularButton2.setOnAnimationUpdateTimeListener(new OnAnimationUpdateTimeListener() {
            @Override
            public void onAnimationTimeUpdate(int timeElapsed) {
                percentageTV.setText(String.format("%s : %s", "Percentage completed", Integer.toString(timeElapsed / factor)));
            }
        });



        circularButton3 = (CircularProgressButton) findViewById(R.id.circularButton3);
        circularButton3.setIndeterminateProgressMode(false);
        circularButton3.setStrokeColor(ContextCompat.getColor(this, R.color.colorStroke));
        circularButton3.setCustomProgressMode(true);

        setProgressBtn = (Button) findViewById(R.id.button);

        progressAmountTV = (TextView) findViewById(R.id.progressAmountTV);

        circularButton4 = (CircularProgressButton) findViewById(R.id.circularButton4);
        circularButton4.setIndeterminateProgressMode(true);
        circularButton4.setStrokeColor(ContextCompat.getColor(this, R.color.colorStroke));

        circularButton5 = (CircularProgressButton) findViewById(R.id.circularButton5);
        circularButton5.setIndeterminateProgressMode(true);
        circularButton5.setStrokeColor(ContextCompat.getColor(this, R.color.colorStroke));
    }

    public void onClickFirstBtn(View view) {

        if (circularButton1.isIdle()) {
            circularButton1.showProgress();
        } else if (circularButton1.isErrorOrCompleteOrCancelled()) {
            circularButton1.showIdle();
        } else if (circularButton1.isProgress()) {
            circularButton1.showCancel();
        }
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
        progress = 0;
        setProgressText();
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
        setProgressText();
    }

    public void setProgressText() {
        progressAmountTV.setText(String.format("%s : %s", "Progress completed", Integer.toString(progress)));
    }

    public void onClickFourthBtn(View view) {
        if (circularButton4.isIdle()) {
            circularButton4.showProgress();
        } else if (circularButton4.isErrorOrCompleteOrCancelled()) {
            circularButton4.showIdle();
        } else if (circularButton4.isProgress()) {
            circularButton4.showComplete();
        }

    }

    public void onClickFifthBtn(View view) {
        if (circularButton5.isIdle()) {
            circularButton5.showProgress();
        } else if (circularButton5.isErrorOrCompleteOrCancelled()) {
            circularButton5.showIdle();
        } else if (circularButton5.isProgress()) {
            circularButton5.showError();
        }

    }
}
