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
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.filters.SdkSuppress;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObjectNotFoundException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

/**
 * Created by Nihas Kalam on 22/12/16.
 */

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 19)
public class OrientationChangeInstrumentedTest {
    private static final String BASIC_PACKAGE = "com.nihaskalam.sample";
    private static final int LAUNCH_TIMEOUT = 5000;
    private static final int ORIENTATION_PERSIST_TIME = 3000;
    private UiDevice mDevice;
    private String cancelStateText, idleStateText, doProgressText, completeStateText, progressAmount, errorStateText;
    private Activity activity;
    private static final String emptyString = "";
    private static final String MANUAL_PROGRESS_CHECK_AMOUNT = "50";

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Before
    public void startMainActivityFromHomeScreen() {
        activity = mActivityRule.getActivity();
        cancelStateText = activity.getResources().getString(R.string.Cancel);
        idleStateText = activity.getResources().getString(R.string.submit);
        doProgressText = activity.getResources().getString(R.string.doProgress);
        completeStateText = activity.getResources().getString(R.string.Complete);
        progressAmount = activity.getResources().getString(R.string.progress_amount);
        errorStateText = activity.getResources().getString(R.string.Error);
        // Initialize UiDevice instance
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

    @Test
    public void doOrientationTestForIndeterminateProgressWithIcon() throws RemoteException, InterruptedException, UiObjectNotFoundException {
        doInitialSetUpForIndeterminateProgressTest();
        mDevice.setOrientationLeft();
        Thread.sleep(ORIENTATION_PERSIST_TIME);
        checkIndeterminateProgressCancelCompleteErrorTestWithIcon();
        mDevice.setOrientationNatural();
        Thread.sleep(ORIENTATION_PERSIST_TIME);
        checkIndeterminateProgressCancelCompleteErrorTestWithIcon();

        Utils.clickAndShowIdleState(idleStateText, R.id.circularButton1);
        Utils.clickAndShowIdleState(idleStateText, R.id.circularButton4);
        Utils.clickAndShowIdleState(idleStateText, R.id.circularButton5);
        mDevice.setOrientationRight();
        Thread.sleep(ORIENTATION_PERSIST_TIME);
        checkIndeterminateProgressIdleTestWithText();
        mDevice.setOrientationNatural();
        Thread.sleep(ORIENTATION_PERSIST_TIME);
        checkIndeterminateProgressIdleTestWithText();
    }

    @Test
    public void doOrientationTestForFixedAndManualProgressWithIcon() throws RemoteException, InterruptedException, UiObjectNotFoundException {
        doInitialSetUpForFixedAndManualProgressTest();
        mDevice.setOrientationLeft();
        mDevice.waitForIdle();
        Espresso.onView(withId(R.id.button)).perform(scrollTo());
        Thread.sleep(Constants.MORPH_DURATION);
        checkManualProgressAmount();

        Utils.doProgress(doProgressText);
        Utils.doProgress(doProgressText);
        Thread.sleep(Constants.MORPH_DURATION);
        checkFixedAndManualProgressCompleteWithIcon();

        mDevice.setOrientationNatural();
        Thread.sleep(ORIENTATION_PERSIST_TIME);
        Utils.clickAndShowIdleState(idleStateText, R.id.circularButton2);
        Utils.clickAndShowIdleState(idleStateText, R.id.circularButton3);
        Thread.sleep(Constants.MORPH_DURATION);
    }

    @Test
    public void doOrientationTestForIndeterminateProgressWithText() throws RemoteException, InterruptedException, UiObjectNotFoundException {
        doInitialSetUpForIndeterminateProgressTest();
        mDevice.setOrientationLeft();
        Thread.sleep(ORIENTATION_PERSIST_TIME);
        checkIndeterminateProgressCancelCompleteErrorTestWithText();
        mDevice.setOrientationNatural();
        Thread.sleep(ORIENTATION_PERSIST_TIME);
        checkIndeterminateProgressCancelCompleteErrorTestWithText();

        Utils.clickAndShowIdleState(idleStateText, R.id.circularButton1);
        Utils.clickAndShowIdleState(idleStateText, R.id.circularButton4);
        Utils.clickAndShowIdleState(idleStateText, R.id.circularButton5);
        mDevice.setOrientationRight();
        Thread.sleep(ORIENTATION_PERSIST_TIME);
        checkIndeterminateProgressIdleTestWithText();
        mDevice.setOrientationNatural();
        Thread.sleep(ORIENTATION_PERSIST_TIME);
        checkIndeterminateProgressIdleTestWithText();
    }

    @Test
    public void doOrientationTestForFixedAndManualProgressWithText() throws RemoteException, InterruptedException, UiObjectNotFoundException {
        doInitialSetUpForFixedAndManualProgressTest();
        mDevice.setOrientationLeft();
        mDevice.waitForIdle();
        Espresso.onView(withId(R.id.button)).perform(scrollTo());
        Thread.sleep(Constants.MORPH_DURATION);
        checkManualProgressAmount();

        Utils.doProgress(doProgressText);
        Utils.doProgress(doProgressText);
        Thread.sleep(Constants.MORPH_DURATION);
        checkFixedAndManualProgressCompleteWithText();

        mDevice.setOrientationNatural();
        Thread.sleep(ORIENTATION_PERSIST_TIME);
        Utils.clickAndShowIdleState(idleStateText, R.id.circularButton2);
        Utils.clickAndShowIdleState(idleStateText, R.id.circularButton3);
        Thread.sleep(Constants.MORPH_DURATION);
    }


    private void doInitialSetUpForIndeterminateProgressTest() throws InterruptedException {
        Espresso.onView(allOf(withId(R.id.circularButton1), withText(idleStateText)))
                .perform(click());
        Thread.sleep(Constants.INDETERMINATE_PROGRESS_DURATION);
        Espresso.onView(withId(R.id.circularButton1)).perform(click());
        Thread.sleep(Constants.MORPH_DURATION);

        Espresso.onView(allOf(withId(R.id.circularButton4), withText(idleStateText)))
                .perform(click());
        Thread.sleep(Constants.INDETERMINATE_PROGRESS_DURATION);
        Espresso.onView(withId(R.id.circularButton4)).perform(click());
        Thread.sleep(Constants.MORPH_DURATION);

        Espresso.onView(allOf(withId(R.id.circularButton5), withText(idleStateText)))
                .perform(click());
        Thread.sleep(Constants.INDETERMINATE_PROGRESS_DURATION);
        Espresso.onView(withId(R.id.circularButton5)).perform(click());
        Thread.sleep(Constants.MORPH_DURATION);
    }

    private void checkIndeterminateProgressCancelCompleteErrorTestWithIcon() {
        Espresso.onView(withId(R.id.circularButton1))
                .check(matches(allOf(withText(emptyString), Utils.withCompoundDrawable(R.drawable.ic_action_cross))));
        Espresso.onView(withId(R.id.circularButton4))
                .check(matches(allOf(withText(emptyString), Utils.withCompoundDrawable(R.drawable.ic_action_accept))));
        Espresso.onView(withId(R.id.circularButton5))
                .check(matches(allOf(withText(emptyString), Utils.withCompoundDrawable(R.drawable.ic_action_cancel))));
    }

    private void checkIndeterminateProgressCancelCompleteErrorTestWithText() {
        Espresso.onView(withId(R.id.circularButton1))
                .check(matches(allOf(withText(cancelStateText), not(Utils.withCompoundDrawable(R.drawable.ic_action_cross)))));
        Espresso.onView(withId(R.id.circularButton4))
                .check(matches(allOf(withText(completeStateText), not(Utils.withCompoundDrawable(R.drawable.ic_action_accept)))));
        Espresso.onView(withId(R.id.circularButton5))
                .check(matches(allOf(withText(errorStateText), not(Utils.withCompoundDrawable(R.drawable.ic_action_cancel)))));
    }


    private void checkIndeterminateProgressIdleTestWithText() {
        Espresso.onView(withId(R.id.circularButton1))
                .check(matches(allOf(withText(idleStateText))));
        Espresso.onView(withId(R.id.circularButton4))
                .check(matches(allOf(withText(idleStateText))));
        Espresso.onView(withId(R.id.circularButton5))
                .check(matches(allOf(withText(idleStateText))));
    }

    private void doInitialSetUpForFixedAndManualProgressTest() throws InterruptedException {
        Espresso.onView(withId(R.id.button)).perform(scrollTo());
        // Click submit button.
        Espresso.onView(allOf(withId(R.id.circularButton3), withText(idleStateText)))
                .perform(click());
        Thread.sleep(Constants.SUBMIT_TO_PROGRESS_MORPH_DURATION);
        Utils.doProgress(doProgressText);
        Utils.doProgress(doProgressText);
//        Espresso.onView(withId(R.id.circularButton3)).perform(click());
//        Thread.sleep(Constants.MORPH_DURATION);

        // Click submit button.
        Espresso.onView(allOf(withId(R.id.circularButton2), withText(idleStateText)))
                .perform(click());
        Thread.sleep(Constants.SUBMIT_TO_PROGRESS_MORPH_DURATION);
//        Espresso.onView(withId(R.id.circularButton2)).perform(click());
//        Thread.sleep(Constants.MORPH_DURATION);
    }

    private void checkFixedAndManualProgressCompleteWithIcon() {
        Espresso.onView(withId(R.id.circularButton2))
                .check(matches(allOf(withText(emptyString), Utils.withCompoundDrawable(R.drawable.ic_action_accept))));
        Espresso.onView(withId(R.id.circularButton3))
                .check(matches(allOf(withText(emptyString), Utils.withCompoundDrawable(R.drawable.ic_action_accept))));
    }

    private void checkFixedAndManualProgressCompleteWithText() {
        Espresso.onView(withId(R.id.circularButton2))
                .check(matches(allOf(withText(completeStateText), not(Utils.withCompoundDrawable(R.drawable.ic_action_accept)))));
        Espresso.onView(withId(R.id.circularButton3))
                .check(matches(allOf(withText(completeStateText), not(Utils.withCompoundDrawable(R.drawable.ic_action_accept)))));
    }

    private void checkManualProgressAmount() {
        Espresso.onView(withId(R.id.progressAmountTV))
                .check(matches(allOf(withText(String.format("%s %s", progressAmount, MANUAL_PROGRESS_CHECK_AMOUNT)))));
    }
}
