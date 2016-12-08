package com.nihaskalam.progressbuttonlibrary;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.StateSet;
import android.view.animation.BounceInterpolator;
import android.widget.Button;

public class CircularProgressButton extends Button {

    public static final int IDLE_STATE_PROGRESS = 0;
    public static final int ERROR_STATE_PROGRESS = -1;
    public static final int SUCCESS_STATE_PROGRESS = 100;
    public static final int INDETERMINATE_STATE_PROGRESS = 50;
    private static final int IDLE_STATE_ANIMATION_DURATION_AFTER_CLICK = 600;

    private StrokeGradientDrawable background;
    private CircularAnimatedDrawable mAnimatedDrawable;

    private ColorStateList mIdleColorState;
    private ColorStateList mCompleteColorState;
    private ColorStateList mErrorColorState;

    private StateListDrawable mIdleStateDrawable;
    private StateListDrawable mCompleteStateDrawable;
    private StateListDrawable mErrorStateDrawable;

    private StateManager mStateManager;
    private State mState;
    private String mIdleText;
    private String mCompleteText;
    private String mErrorText;
    private String mProgressText;

    private int mColorProgress;
    private int mColorIndicator;
    private int mColorIndicatorBackground;
    private int mIconComplete;
    private int mIconError;
    private int mStrokeWidth;
    private int mPaddingProgress;
    private float mCornerRadius;
    private boolean mIndeterminateProgressMode;
    private boolean mConfigurationChanged;
    private int idleStateStrokeColor = -1;
    private int mIdleStateTextColorAfterClick;
    private int mIdleStateBackgroundColorAfterClick;
    private int textSize;

    private enum State {
        PROGRESS, IDLE, COMPLETE, ERROR
    }

    private int mMaxProgress;
    private int mProgress;

    private boolean mMorphingInProgress;

    public CircularProgressButton(Context context) {
        super(context);
        init(context, null);
    }

    public CircularProgressButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircularProgressButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        mStrokeWidth = (int) getContext().getResources().getDimension(R.dimen.pb_stroke_width);

        initAttributes(context, attributeSet);

        mMaxProgress = 100;
        mState = State.IDLE;
        mStateManager = new StateManager(this);

        setText(mIdleText);

        initIdleStateDrawable();
        setBackgroundCompat(mIdleStateDrawable);
    }

    private void initErrorStateDrawable() {
        int colorPressed = getPressedColor(mErrorColorState);

        StrokeGradientDrawable drawablePressed = createDrawable(colorPressed);
        mErrorStateDrawable = new StateListDrawable();

        mErrorStateDrawable.addState(new int[]{android.R.attr.state_pressed}, drawablePressed.getGradientDrawable());
        mErrorStateDrawable.addState(StateSet.WILD_CARD, background.getGradientDrawable());
    }

    private void initCompleteStateDrawable() {
        int colorPressed = getPressedColor(mCompleteColorState);

        StrokeGradientDrawable drawablePressed = createDrawable(colorPressed);
        mCompleteStateDrawable = new StateListDrawable();

        mCompleteStateDrawable.addState(new int[]{android.R.attr.state_pressed}, drawablePressed.getGradientDrawable());
        mCompleteStateDrawable.addState(StateSet.WILD_CARD, background.getGradientDrawable());
    }

    private void initIdleStateDrawable() {
        int colorNormal = getNormalColor(mIdleColorState);
        int colorPressed = getPressedColor(mIdleColorState);
        int colorFocused = getFocusedColor(mIdleColorState);
        int colorDisabled = getDisabledColor(mIdleColorState);
        if (background == null) {
            background = createDrawable(colorNormal);
        }

        StrokeGradientDrawable drawableDisabled = createDrawable(colorDisabled);
        StrokeGradientDrawable drawableFocused = createDrawable(colorFocused);
        StrokeGradientDrawable drawablePressed = createDrawable(colorPressed);
        mIdleStateDrawable = new StateListDrawable();

        mIdleStateDrawable.addState(new int[]{android.R.attr.state_pressed}, drawablePressed.getGradientDrawable());
        mIdleStateDrawable.addState(new int[]{android.R.attr.state_focused}, drawableFocused.getGradientDrawable());
        mIdleStateDrawable.addState(new int[]{-android.R.attr.state_enabled}, drawableDisabled.getGradientDrawable());
        mIdleStateDrawable.addState(StateSet.WILD_CARD, background.getGradientDrawable());
    }

    private int getNormalColor(ColorStateList colorStateList) {
        return colorStateList.getColorForState(new int[]{android.R.attr.state_enabled}, 0);
    }

    private int getPressedColor(ColorStateList colorStateList) {
        return colorStateList.getColorForState(new int[]{android.R.attr.state_pressed}, 0);
    }

    private int getFocusedColor(ColorStateList colorStateList) {
        return colorStateList.getColorForState(new int[]{android.R.attr.state_focused}, 0);
    }

    private int getDisabledColor(ColorStateList colorStateList) {
        return colorStateList.getColorForState(new int[]{-android.R.attr.state_enabled}, 0);
    }

    private StrokeGradientDrawable createDrawable(int color) {
        GradientDrawable drawable = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.pb_background).mutate();
        drawable.setColor(color);
        drawable.setCornerRadius(mCornerRadius);
        StrokeGradientDrawable strokeGradientDrawable = new StrokeGradientDrawable(drawable);
        strokeGradientDrawable.setStrokeColor(color);
        strokeGradientDrawable.setStrokeWidth(mStrokeWidth);

        return strokeGradientDrawable;
    }

    @Override
    protected void drawableStateChanged() {
        if (mState == State.COMPLETE) {
            initCompleteStateDrawable();
            setBackgroundCompat(mCompleteStateDrawable);
        } else if (mState == State.IDLE) {
            initIdleStateDrawable();
            setBackgroundCompat(mIdleStateDrawable);
        } else if (mState == State.ERROR) {
            initErrorStateDrawable();
            setBackgroundCompat(mErrorStateDrawable);
        }

        if (mState != State.PROGRESS) {
            super.drawableStateChanged();
        }
    }

    private void initAttributes(Context context, AttributeSet attributeSet) {
        String xmlProvidedSize = attributeSet.getAttributeValue("http://schemas.android.com/apk/res/android", "textSize");
        String[] parts = xmlProvidedSize.split("\\.");
        String part1 = parts[0];
        textSize = new Integer(part1);
        if (textSize < 0) {
            float sourceTextSize = getTextSize();
            textSize = (int) (sourceTextSize / getContext().getResources().getDisplayMetrics().density);
        }
        TypedArray attr = getTypedArray(context, attributeSet, R.styleable.CircularProgressButton);
        if (attr == null) {
            return;
        }
        try {

            mIdleText = attr.getString(R.styleable.CircularProgressButton_pb_textIdle);
            mCompleteText = attr.getString(R.styleable.CircularProgressButton_pb_textComplete);
            mErrorText = attr.getString(R.styleable.CircularProgressButton_pb_textError);
            mProgressText = attr.getString(R.styleable.CircularProgressButton_pb_textProgress);

            mIconComplete = attr.getResourceId(R.styleable.CircularProgressButton_pb_iconComplete, 0);
            mIconError = attr.getResourceId(R.styleable.CircularProgressButton_pb_iconError, 0);
            mCornerRadius = attr.getDimension(R.styleable.CircularProgressButton_pb_cornerRadius, 0);
            mPaddingProgress = attr.getDimensionPixelSize(R.styleable.CircularProgressButton_pb_paddingProgress, 0);
            mIndeterminateProgressMode = attr.getBoolean(R.styleable.CircularProgressButton_pb_isIndeterminate, false);

            int blue = getColor(R.color.pb_blue);
            int white = getColor(R.color.pb_white);
            int grey = getColor(R.color.pb_grey);

            int idleStateSelector = attr.getResourceId(R.styleable.CircularProgressButton_pb_selectorIdle,
                    R.color.pb_idle_state_selector);
            mIdleColorState = ContextCompat.getColorStateList(context, idleStateSelector);

            int completeStateSelector = attr.getResourceId(R.styleable.CircularProgressButton_pb_selectorComplete,
                    R.color.pb_complete_state_selector);
            mCompleteColorState = ContextCompat.getColorStateList(context, completeStateSelector);

            int errorStateSelector = attr.getResourceId(R.styleable.CircularProgressButton_pb_selectorError,
                    R.color.pb_error_state_selector);
            mErrorColorState = ContextCompat.getColorStateList(context, errorStateSelector);

            mColorProgress = attr.getColor(R.styleable.CircularProgressButton_pb_colorProgress, white);
            mColorIndicator = attr.getColor(R.styleable.CircularProgressButton_pb_colorIndicator, blue);
            mColorIndicatorBackground =
                    attr.getColor(R.styleable.CircularProgressButton_pb_colorIndicatorBackground, grey);
            mIdleStateTextColorAfterClick = attr.getColor(R.styleable.CircularProgressButton_pb_textColorAfterClick, getNormalColor(mIdleColorState));
            mIdleStateBackgroundColorAfterClick = attr.getColor(R.styleable.CircularProgressButton_pb_backgroundColorAfterClick, getPressedColor(mIdleColorState));
        } finally {
            attr.recycle();
        }
    }

    protected int getColor(int id) {
        return ContextCompat.getColor(getContext(), id);
    }

    protected TypedArray getTypedArray(Context context, AttributeSet attributeSet, int[] attr) {
        return context.obtainStyledAttributes(attributeSet, attr, 0, 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mProgress > 0 && mState == State.PROGRESS && !mMorphingInProgress) {
            drawProgress(canvas);
        }
    }

    private void drawProgress(Canvas canvas) {
        if (mAnimatedDrawable == null) {
            int offset = (getWidth() - getHeight()) / 2;
            mAnimatedDrawable = new CircularAnimatedDrawable(mColorIndicator, mStrokeWidth, mIndeterminateProgressMode);
            if (!mIndeterminateProgressMode) {
                mAnimatedDrawable.setListener(getDeterminateProgressBarCompleteStateListener());
            }
            int left = offset + mPaddingProgress;
            int right = getWidth() - offset - mPaddingProgress;
            int bottom = getHeight() - mPaddingProgress;
            int top = mPaddingProgress;
            mAnimatedDrawable.setBounds(left, top, right, bottom);
            mAnimatedDrawable.setCallback(this);
            mAnimatedDrawable.start();
        } else {
            mAnimatedDrawable.draw(canvas);
        }
    }


    public boolean isIndeterminateProgressMode() {
        return mIndeterminateProgressMode;
    }

    public void setIndeterminateProgressMode(boolean indeterminateProgressMode) {
        this.mIndeterminateProgressMode = indeterminateProgressMode;
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return who == mAnimatedDrawable || super.verifyDrawable(who);
    }

    private MorphingAnimation createMorphing() {
        mMorphingInProgress = true;

        MorphingAnimation animation = new MorphingAnimation(this, background);
        animation.setFromCornerRadius(mCornerRadius);
        animation.setToCornerRadius(mCornerRadius);

        animation.setFromWidth(getWidth());
        animation.setToWidth(getWidth());

        if (mConfigurationChanged) {
            animation.setDuration(MorphingAnimation.DURATION_INSTANT);
        } else {
            animation.setDuration(MorphingAnimation.DURATION_NORMAL);
        }

        mConfigurationChanged = false;

        return animation;
    }

    private MorphingAnimation createProgressMorphing(float fromCorner, float toCorner, int fromWidth, int toWidth) {
        mMorphingInProgress = true;

        MorphingAnimation animation = new MorphingAnimation(this, background);
        animation.setFromCornerRadius(fromCorner);
        animation.setToCornerRadius(toCorner);

        animation.setPadding(mPaddingProgress);

        animation.setFromWidth(fromWidth);
        animation.setToWidth(toWidth);

        if (mConfigurationChanged) {
            animation.setDuration(MorphingAnimation.DURATION_INSTANT);
        } else {
            animation.setDuration(MorphingAnimation.DURATION_NORMAL);
        }

        mConfigurationChanged = false;

        return animation;
    }

    private void morphToProgress() {
        setClickable(false);

        int duration = 0;
        if (!mConfigurationChanged) {
            animateIdleStateButtonAfterClick();
            duration = IDLE_STATE_ANIMATION_DURATION_AFTER_CLICK;
        }
        mConfigurationChanged = false;

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setWidth(getWidth());
                setText(mProgressText);

                MorphingAnimation animation = createProgressMorphing(mCornerRadius, getHeight(), getWidth(), getHeight());

                animation.setFromColor(getNormalColor(mIdleColorState));
                animation.setToColor(mColorProgress);

                animation.setFromStrokeColor(idleStateStrokeColor == -1 ? getNormalColor(mIdleColorState) : idleStateStrokeColor);
                animation.setToStrokeColor(mColorIndicatorBackground);

                animation.setListener(getProgressStateListener());
                animation.start();
            }
        }, duration);

    }

    private OnAnimationEndListener getProgressStateListener() {
        return new OnAnimationEndListener() {
            @Override
            public void onAnimationEnd() {
                setClickable(true);
                mMorphingInProgress = false;
                mState = State.PROGRESS;

                mStateManager.checkState(CircularProgressButton.this);
            }
        };
    }

    private void morphProgressToComplete() {
        MorphingAnimation animation = createProgressMorphing(getHeight(), mCornerRadius, getHeight(), getWidth());

        animation.setFromColor(mColorProgress);
        animation.setToColor(getNormalColor(mCompleteColorState));

        animation.setFromStrokeColor(mColorIndicator);
        animation.setToStrokeColor(getNormalColor(mCompleteColorState));

        animation.setListener(getCompleteStateListener());

        animation.start();

    }

    private void morphIdleToComplete() {
        MorphingAnimation animation = createMorphing();

        animation.setFromColor(getNormalColor(mIdleColorState));
        animation.setToColor(getNormalColor(mCompleteColorState));

        animation.setFromStrokeColor(getNormalColor(mIdleColorState));
        animation.setToStrokeColor(getNormalColor(mCompleteColorState));

        animation.setListener(getCompleteStateListener());

        animation.start();

    }

    private OnAnimationEndListener getCompleteStateListener() {
        return new OnAnimationEndListener() {
            @Override
            public void onAnimationEnd() {
                if (mIconComplete != 0) {
                    setText(null);
                    setIcon(mIconComplete);
                } else {
                    setText(mCompleteText);
                }
                mMorphingInProgress = false;
                mState = State.COMPLETE;

                mStateManager.checkState(CircularProgressButton.this);
            }
        };
    }

    private void morphCompleteToIdle() {
        MorphingAnimation animation = createMorphing();

        animation.setFromColor(getNormalColor(mCompleteColorState));
        animation.setToColor(getNormalColor(mIdleColorState));

        animation.setFromStrokeColor(getNormalColor(mCompleteColorState));
        animation.setToStrokeColor(idleStateStrokeColor == -1 ? getNormalColor(mIdleColorState) : idleStateStrokeColor);

        animation.setListener(getIdleStateListener());

        animation.start();

    }

    private void morphErrorToIdle() {
        MorphingAnimation animation = createMorphing();

        animation.setFromColor(getNormalColor(mErrorColorState));
        animation.setToColor(getNormalColor(mIdleColorState));

        animation.setFromStrokeColor(getNormalColor(mErrorColorState));
        animation.setToStrokeColor(idleStateStrokeColor == -1 ? getNormalColor(mIdleColorState) : idleStateStrokeColor);

        animation.setListener(getIdleStateListener());

        animation.start();

    }

    private OnAnimationEndListener getIdleStateListener() {
        return new OnAnimationEndListener() {
            @Override
            public void onAnimationEnd() {
                removeIcon();
                setText(mIdleText);
                mMorphingInProgress = false;
                mState = State.IDLE;

                mStateManager.checkState(CircularProgressButton.this);
            }
        };
    }

    private void morphIdleToError() {
        MorphingAnimation animation = createMorphing();

        animation.setFromColor(getNormalColor(mIdleColorState));
        animation.setToColor(getNormalColor(mErrorColorState));

        animation.setFromStrokeColor(getNormalColor(mIdleColorState));
        animation.setToStrokeColor(getNormalColor(mErrorColorState));

        animation.setListener(getErrorStateListener());

        animation.start();

    }

    private void morphProgressToError() {
        MorphingAnimation animation = createProgressMorphing(getHeight(), mCornerRadius, getHeight(), getWidth());

        animation.setFromColor(mColorProgress);
        animation.setToColor(getNormalColor(mErrorColorState));

        animation.setFromStrokeColor(mColorIndicator);
        animation.setToStrokeColor(getNormalColor(mErrorColorState));
        animation.setListener(getErrorStateListener());

        animation.start();
    }

    private OnAnimationEndListener getErrorStateListener() {
        return new OnAnimationEndListener() {
            @Override
            public void onAnimationEnd() {
                if (mIconError != 0) {
                    setText(null);
                    setIcon(mIconError);
                } else {
                    setText(mErrorText);
                }
                mMorphingInProgress = false;
                mState = State.ERROR;

                mStateManager.checkState(CircularProgressButton.this);
            }
        };
    }

    private void morphProgressToIdle() {
        MorphingAnimation animation = createProgressMorphing(getHeight(), mCornerRadius, getHeight(), getWidth());

        animation.setFromColor(mColorProgress);
        animation.setToColor(getNormalColor(mIdleColorState));

        animation.setFromStrokeColor(mColorIndicator);
        animation.setToStrokeColor(getNormalColor(mIdleColorState));
        animation.setListener(new OnAnimationEndListener() {
            @Override
            public void onAnimationEnd() {
                removeIcon();
                setText(mIdleText);
                mMorphingInProgress = false;
                mState = State.IDLE;

                mStateManager.checkState(CircularProgressButton.this);
            }
        });

        animation.start();
    }

    private void setIcon(int icon) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), icon);//getResources().getDrawable(icon);
        if (drawable != null) {
            int padding = (getWidth() / 2) - (drawable.getIntrinsicWidth() / 2);
            setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
            setPadding(padding, 0, 0, 0);
        }
    }

    protected void removeIcon() {
        setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        setPadding(0, 0, 0, 0);
    }

    /**
     * Set the View's background. Masks the API changes made in Jelly Bean.
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public void setBackgroundCompat(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(drawable);
        } else {
            setBackgroundDrawable(drawable);
        }
    }

    public void setProgress(int progress) {
        mProgress = progress;

        if (mMorphingInProgress || getWidth() == 0) {
            return;
        }

        mStateManager.saveProgress(this);

        if (mProgress >= mMaxProgress) {
            if (mState == State.PROGRESS) {
                morphProgressToComplete();
            } else if (mState == State.IDLE) {
                morphIdleToComplete();
            }
        } else if (mProgress > IDLE_STATE_PROGRESS) {
            if (mState == State.IDLE) {
                morphToProgress();
            } else if (mState == State.PROGRESS) {
                invalidate();
            }
        } else if (mProgress == ERROR_STATE_PROGRESS) {
            if (mState == State.PROGRESS) {
                morphProgressToError();
            } else if (mState == State.IDLE) {
                morphIdleToError();
            }
        } else if (mProgress == IDLE_STATE_PROGRESS) {
            if (mState == State.COMPLETE) {
                morphCompleteToIdle();
            } else if (mState == State.PROGRESS) {
                morphProgressToIdle();
            } else if (mState == State.ERROR) {
                morphErrorToIdle();
            }
        }
    }

    public int getProgress() {
        return mProgress;
    }

    public void setBackgroundColor(int color) {
        background.getGradientDrawable().setColor(color);
    }

    public void setStrokeColor(int color) {
        background.setStrokeColor(color);
        idleStateStrokeColor = color;
    }

    public String getIdleText() {
        return mIdleText;
    }

    public String getCompleteText() {
        return mCompleteText;
    }

    public String getErrorText() {
        return mErrorText;
    }

    public void setIdleText(String text) {
        mIdleText = text;
    }

    public void setCompleteText(String text) {
        mCompleteText = text;
    }

    public void setErrorText(String text) {
        mErrorText = text;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            setProgress(mProgress);
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.mProgress = mProgress;
        savedState.mIndeterminateProgressMode = mIndeterminateProgressMode;
        savedState.mConfigurationChanged = true;

        return savedState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState) {
            SavedState savedState = (SavedState) state;
            mProgress = savedState.mProgress;
            mIndeterminateProgressMode = savedState.mIndeterminateProgressMode;
            mConfigurationChanged = savedState.mConfigurationChanged;
            super.onRestoreInstanceState(savedState.getSuperState());
            setProgress(mProgress);
        } else {
            super.onRestoreInstanceState(state);
        }
    }


    static class SavedState extends BaseSavedState {

        private boolean mIndeterminateProgressMode;
        private boolean mConfigurationChanged;
        private int mProgress;

        public SavedState(Parcelable parcel) {
            super(parcel);
        }

        private SavedState(Parcel in) {
            super(in);
            mProgress = in.readInt();
            mIndeterminateProgressMode = in.readInt() == 1;
            mConfigurationChanged = in.readInt() == 1;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(mProgress);
            out.writeInt(mIndeterminateProgressMode ? 1 : 0);
            out.writeInt(mConfigurationChanged ? 1 : 0);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {

            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    public void showProgress() {
        if (getProgress() == 0) {
            setProgress(50);
        }
    }

    public void showIdle() {
        if (getProgress() == 100 || getProgress() == -1)
            setProgress(0);
    }

    public void showComplete() {
        setProgress(100);
    }

    public void showError() {
        setProgress(-1);
    }

    public boolean isIdle() {
        return (getProgress() == 0 ? true : false);
    }

    public boolean isErrorOrComplete() {
        return ((getProgress() == 100 || getProgress() == -1) ? true : false);
    }

    private OnAnimationEndListener getDeterminateProgressBarCompleteStateListener() {
        return new OnAnimationEndListener() {
            @Override
            public void onAnimationEnd() {
                mAnimatedDrawable = null;
                showComplete();
            }
        };

    }

    private void animateIdleStateButtonAfterClick() {
        int textColorChangeDuration = 10;
        ObjectAnimator colorAnim = ObjectAnimator.ofInt(this, "textColor", getNormalColor(this.getTextColors()), mIdleStateTextColorAfterClick);
        colorAnim.setDuration(textColorChangeDuration);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.start();

        ObjectAnimator colorAnim1 = ObjectAnimator.ofInt(this, "textColor", mIdleStateTextColorAfterClick, getNormalColor(this.getTextColors()));
        colorAnim1.setDuration(0);
        colorAnim1.setStartDelay(IDLE_STATE_ANIMATION_DURATION_AFTER_CLICK - textColorChangeDuration);
        colorAnim1.setEvaluator(new ArgbEvaluator());
        colorAnim1.setInterpolator(new BounceInterpolator());
        colorAnim1.start();

        ObjectAnimator bgAnim = ObjectAnimator.ofInt(this, "backgroundColor", getNormalColor(mIdleColorState), mIdleStateBackgroundColorAfterClick);
        bgAnim.setDuration(0);
        bgAnim.setEvaluator(new ArgbEvaluator());
        bgAnim.start();

        int textSizeAnimationDuration = 150;
        ValueAnimator animator = ValueAnimator.ofFloat(textSize, textSize - 4);
        animator.setDuration(textSizeAnimationDuration);
        animator.setRepeatCount(1);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedValue = (float) valueAnimator.getAnimatedValue();
                setTextSize(animatedValue);
            }
        });

        animator.start();
    }
}
