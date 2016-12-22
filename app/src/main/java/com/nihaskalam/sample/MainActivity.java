/*
 * Copyright (C) 2016 Nihas Kalam.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nihaskalam.sample;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nihaskalam.progressbuttonlibrary.CircularProgressButton;
import com.nihaskalam.progressbuttonlibrary.OnAnimationUpdateTimeListener;

public class MainActivity extends AppCompatActivity {
    private CircularProgressButton circularButton1, circularButton2, circularButton3, circularButton4, circularButton5;
    private Button setProgressBtn;
    private int progress = 0;
    private TextView percentageTV, progressAmountTV;
    public static final int sweepDuration = 5000;
    private static final String MANUAL_PROGRESS_AMOUNT_KEY = "manualProgressAmount";
    private static final String FIXED_PROGRESS_PERCENTAGE_KEY = "fixedTimeProgressPercentage";
    private static final String CONFIGURATION_CHANGE_KEY = "configurationChange";
    private int fixedTimeProgressPercentage = 0;
    private boolean hasConfigurarationChanged = false;

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
        final int factor = sweepDuration / 100;
        circularButton2.setSweepDuration(sweepDuration);
        percentageTV = (TextView) findViewById(R.id.timeTV);
        circularButton2.setOnAnimationUpdateTimeListener(new OnAnimationUpdateTimeListener() {
            @Override
            public void onAnimationTimeUpdate(int timeElapsed) {
                fixedTimeProgressPercentage = hasConfigurarationChanged ? fixedTimeProgressPercentage + timeElapsed / factor : timeElapsed / factor;
                percentageTV.setText(String.format("%s : %s", "Percentage completed", Integer.toString(fixedTimeProgressPercentage)));
                hasConfigurarationChanged = false;
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
            Toast.makeText(MainActivity.this, "Do after submit button click", Toast.LENGTH_LONG).show();
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
        progressAmountTV.setText(String.format("%s %s", getResources().getString(R.string.progress_amount), Integer.toString(progress)));
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MANUAL_PROGRESS_AMOUNT_KEY, progress);
        outState.putInt(FIXED_PROGRESS_PERCENTAGE_KEY, fixedTimeProgressPercentage);
        outState.putBoolean(CONFIGURATION_CHANGE_KEY, true);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        progress = savedInstanceState.getInt(MANUAL_PROGRESS_AMOUNT_KEY);
        fixedTimeProgressPercentage = savedInstanceState.getInt(FIXED_PROGRESS_PERCENTAGE_KEY);
        hasConfigurarationChanged = savedInstanceState.getBoolean(CONFIGURATION_CHANGE_KEY);
        setProgressText();
    }
}
