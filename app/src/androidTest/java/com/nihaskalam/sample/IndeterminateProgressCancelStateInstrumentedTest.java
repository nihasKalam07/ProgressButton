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

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.internal.util.Checks;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.espresso.Espresso;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;


/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class IndeterminateProgressCancelStateInstrumentedTest {
    private Activity activity;
    private String cancelStateText, idleStateText;
    private static final String emptyString = "";

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);


    @Before
    public void setUp() {
        activity = mActivityRule.getActivity();
        cancelStateText = activity.getResources().getString(R.string.Cancel);
        idleStateText = activity.getResources().getString(R.string.submit);
    }

    @Test
    public void verifyProgressCycleWithCancelStateIcon() throws InterruptedException {
        // Click submit button.

        Espresso.onView(allOf(withId(R.id.circularButton1), withText(idleStateText)))
                .perform(click());
        Thread.sleep(Constants.INDETERMINATE_PROGRESS_DURATION);
        Espresso.onView(withId(R.id.circularButton1)).perform(click());
        Thread.sleep(Constants.MORPH_DURATION);

        // Check the text and compound drawable.
        Espresso.onView(withId(R.id.circularButton1))
                .check(matches(allOf(withText(emptyString), Utils.withCompoundDrawable(R.drawable.ic_action_cross))));
        Utils.clickAndShowIdleState(idleStateText, R.id.circularButton1);
    }


    @Test
    public void verifyProgressCycleWithCancelStateText() throws InterruptedException {
        // Click submit button.
        Espresso.onView(allOf(withId(R.id.circularButton1), withText(idleStateText)))
                .perform(click());
        Thread.sleep(Constants.INDETERMINATE_PROGRESS_DURATION);
        Espresso.onView(withId(R.id.circularButton1)).perform(click());
        Thread.sleep(Constants.MORPH_DURATION);

        // Check the text and compound drawable.
        Espresso.onView(withId(R.id.circularButton1))
                .check(matches(allOf(withText(cancelStateText), not(Utils.withCompoundDrawable(R.drawable.ic_action_cross)))));
        Utils.clickAndShowIdleState(idleStateText, R.id.circularButton1);
    }
}
