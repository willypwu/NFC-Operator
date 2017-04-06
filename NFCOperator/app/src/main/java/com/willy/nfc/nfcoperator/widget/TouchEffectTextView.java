package com.willy.nfc.nfcoperator.widget;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.TextView;

import com.willy.nfc.nfcoperator.R;
import com.willy.nfc.nfcoperator.util.TouchFeedbackHelper;

public class TouchEffectTextView extends TextView  implements TouchFeedbackHelper.OnTouchFeedbackListener {
    private TouchFeedbackHelper m_touchFeedbackHelper = new TouchFeedbackHelper(this);
    private boolean m_bDrawOvelrlay = false;
    private boolean mbIgnoreTouchEffect = false;

    private int mTouchEffectColor;
    private int mRestColor;
    private int mDefaultImageColor;

    private boolean mEnable = true;

    public TouchEffectTextView(Context context) {
        this(context, null);
    }

    public TouchEffectTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TouchEffectTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClickable(true);
        setGravity(Gravity.CENTER);
        mTouchEffectColor = getResources().getColor(R.color.colorAccent);
        mRestColor = getResources().getColor(R.color.action_bar_item_text_color);
        mDefaultImageColor = getResources().getColor(R.color.touch_effect_icon_default_color);;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mEnable) {
            m_touchFeedbackHelper.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onTouchDownFeedback() {
        if (m_bDrawOvelrlay || mbIgnoreTouchEffect)
            return;

        setColor(mTouchEffectColor);
    }

    @Override
    public void onTouchUpFeedback() {
        if (!m_bDrawOvelrlay || mbIgnoreTouchEffect)
            return;

        reset();
    }

    protected Drawable getCurrentDrawable() {
        Drawable[] drawables = getCompoundDrawables();
        for (int i = 0; i < drawables.length; ++i) {
            if (drawables[i] != null) {
                return drawables[i];
            }
        }
        return null;
    }

    public void disable() {
        mEnable = false;
        setClickable(mEnable);
        ignoreTouchEffect();
        setColor(getResources().getColor(android.R.color.darker_gray));
    }

    public void enable() {
        mEnable = true;
        setClickable(mEnable);
        reset();
    }

    public void setTouchEffectColor(int color) {
        mTouchEffectColor = color;
    }

    public void setRestColor(int color) {
        mRestColor = color;
    }

    public void setColor(int color) {
        m_bDrawOvelrlay = true;

        Drawable currentDrawable = getCurrentDrawable();
        if(currentDrawable != null) {
            currentDrawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        }
        setTextColor(color);

        invalidate();
    }

    public void ignoreTouchEffect() {
        mbIgnoreTouchEffect = true;
    }

    public void reset() {
        mEnable = true;
        setClickable(mEnable);
        m_bDrawOvelrlay = false;
        mbIgnoreTouchEffect = false;

        setIconToDefaultColor();
        setTextColor(mRestColor);

        invalidate();
    }

    public void setIconToDefaultColor() {
        Drawable currentDrawable = getCurrentDrawable();
        if (currentDrawable != null) {
            currentDrawable.setColorFilter(mDefaultImageColor, PorterDuff.Mode.MULTIPLY);
        }
    }
}
