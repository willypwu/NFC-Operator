package com.willy.nfc.nfcoperator.util;

import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

public class TouchFeedbackHelper {

    public static final long PENDING_DOWN_FEEDBACK_DURATION = 200; // ms

    private class H extends Handler {
        static final int MSG_DOWN_FEEDBACK = 4000;
        static final int MSG_UP_FEEDBACK = 4001;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_DOWN_FEEDBACK:
                    performTouchDownFeedback();
                    break;
                case MSG_UP_FEEDBACK:
                    performTouchUpFeedback();
                    break;
            }
        }
    };

    private H mH;
    private long m_lDownDelay = ViewConfiguration.getTapTimeout();
    private long m_lPendingUpDelay = PENDING_DOWN_FEEDBACK_DURATION;

    public TouchFeedbackHelper(OnTouchFeedbackListener l, long lDownDelay, long lPendingUpDelay) {
        m_lDownDelay = lDownDelay;
        m_onTouchFeedbackListener = l;
        m_lPendingUpDelay = lPendingUpDelay;
        mH = new H();
    }

    public TouchFeedbackHelper(OnTouchFeedbackListener l, long lDownDelay) {
        this(l, lDownDelay, PENDING_DOWN_FEEDBACK_DURATION);
    }

    public TouchFeedbackHelper(OnTouchFeedbackListener l) {
        this(l, ViewConfiguration.getTapTimeout(), PENDING_DOWN_FEEDBACK_DURATION);
    }

    public void onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTouchDownFeedbackDelayed();
                break;
            case MotionEvent.ACTION_CANCEL:
                resetTouchFeedback();
                break;
            case MotionEvent.ACTION_UP:
                startTouchUpFeedback(isTouchInSide(event));
                break;
            default:
                ; //
        }
    }

    private void startTouchDownFeedbackDelayed() {
        if (m_onTouchFeedbackListener != null) {
            mH.sendEmptyMessageDelayed(H.MSG_DOWN_FEEDBACK, m_lDownDelay);
        }
    }

    private void performTouchDownFeedback() {
        if (m_onTouchFeedbackListener != null) {
            m_onTouchFeedbackListener.onTouchDownFeedback();
        }
    }

    private void performPendingTouchDownFeedback() {
        if (m_onTouchFeedbackListener != null) {
            m_onTouchFeedbackListener.onTouchDownFeedback();
            mH.sendEmptyMessageDelayed(H.MSG_UP_FEEDBACK, m_lPendingUpDelay);
        }
    }

    private void startTouchUpFeedback(boolean bInside) {
        if (cancelPendingTouchDown()) {
            if (bInside) performPendingTouchDownFeedback();
        } else {
            performTouchUpFeedback();
        }
    }

    private void performTouchUpFeedback() {
        if (m_onTouchFeedbackListener != null) {
            m_onTouchFeedbackListener.onTouchUpFeedback();
        }
    }

    private void resetTouchFeedback() {
        cancelPendingTouchDown();
        performTouchUpFeedback();
    }

    private boolean cancelPendingTouchDown() {
        if (mH.hasMessages(H.MSG_DOWN_FEEDBACK)) {
            mH.removeMessages(H.MSG_DOWN_FEEDBACK);
            return true;
        }
        return false;
    }

    private Rect m_RectTmp = new Rect();

    private boolean isTouchInSide(MotionEvent ev) {
        if (m_onTouchFeedbackListener instanceof View) {
            View view = (View) m_onTouchFeedbackListener;
            m_RectTmp.set(0, 0, view.getWidth(), view.getHeight());
            return m_RectTmp.contains((int) ev.getX(), (int) ev.getY());
        }
        return true;
    }

    public void setPendingUpDelay(long lUpDelay) {
        m_lPendingUpDelay = lUpDelay;
    }

    public long getPendingUpDelay() {
        return m_lPendingUpDelay;
    }

    private OnTouchFeedbackListener m_onTouchFeedbackListener;

    public interface OnTouchFeedbackListener {
        public void onTouchDownFeedback();

        public void onTouchUpFeedback();
    }
}
