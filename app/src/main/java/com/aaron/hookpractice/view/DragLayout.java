package com.aaron.hookpractice.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

import com.aaron.hookpractice.utils.ScreenUtils;


public class DragLayout extends FrameLayout {
    public static final String TAG = "DragLayout";
    private ViewDragHelper mDragHelper;
    private int mDragOriginX;
    private int mDragOriginY;

    public DragLayout(@NonNull Context context) {
        this(context, null);
    }

    public DragLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDragHelper = ViewDragHelper.create(this, mDragCallback);
//        setOnTouchListener(this);
    }

    /**
     * 事件拦截和处理交由ViewDragHelper
     *
     * @param ev
     * @return
     */
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
////        return mDragHelper.shouldInterceptTouchEvent(ev);
//
//        boolean intercepted = false;
//        float mlastX = 0, mlastY = 0;
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                intercepted = false;
//                mlastX = ev.getX();
//                mlastY = ev.getY();
//                break;
//
//            case MotionEvent.ACTION_MOVE:
//                //计算移动距离 判定是否滑动
//                float dx = ev.getX() - mlastX;
//                float dy = ev.getY() - mlastY;
//
//                intercepted = Math.abs(dx) > MOVE_SLOP || Math.abs(dy) > MOVE_SLOP;
//                Log.d(TAG, "onInterceptTouchEvent: ACTION_MOVE: intercepted = " + intercepted);
//                break;
//
//            case MotionEvent.ACTION_UP:
////                intercepted = false;
//                break;
//        }
//        Log.d(TAG, "onInterceptTouchEvent: " + intercepted);
//        return intercepted;
////        return true;
////        return super.onInterceptTouchEvent(ev);
//    }

    private boolean mIsDragging;
    private float mLastX;
    private float mLastY;
    private static final float MOVE_SLOP = 10;
//    private static final float MOVE_SLOP = ViewConfiguration.get().getScaledTouchSlop();

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        mDragHelper.processTouchEvent(event);
//        super.onTouchEvent(event);
//        Log.d(TAG, "onTouchEvent: " + event.toString());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsDragging = false;
                ViewParent parent = getParent();
                // 让父view不要拦截悬浮窗的触摸事件
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(true);
                }
                mLastX = event.getX();
                mLastY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = event.getX() - mLastX;
                float deltaY = event.getY() - mLastY;

//                Log.d(TAG, "onTouchEvent: left = " + getX() + "; top = " + getY());
//                Log.d(TAG, "onTouchEvent: deltaX = " + deltaX + "; deltaY = " + deltaY);
                float destX = 0f, destY = 0f;
                // 判定为拖动
                if (Math.abs(deltaX) > MOVE_SLOP || Math.abs(deltaY) > MOVE_SLOP) {
                    mIsDragging = true;

                    destX = getX() + deltaX;
                    destY = getY() + deltaY;
                    if (destX < 0) {
                        destX = 0;
                    }

                    if (destX + getWidth() > ScreenUtils.getScreenWidth(getContext())) {
                        destX = ScreenUtils.getScreenWidth(getContext()) - getWidth();
                    }

                    if (destY < ScreenUtils.getStatusBarHeight(getContext())) {
                        destY = ScreenUtils.getStatusBarHeight(getContext());
                    }

                    if (destY + getHeight() > ScreenUtils.getScreenHeight(getContext())) {
                        destY = ScreenUtils.getScreenHeight(getContext()) - getHeight();
                    }

                    setX(destX);
                    setY(destY);
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                int centerX = ScreenUtils.getScreenWidth(getContext()) / 2;
                int halfViewWidth = getWidth() / 2;
                if (mIsDragging) {
                    if (event.getRawX() + halfViewWidth <= centerX) {
                        this.animate().setInterpolator(new AccelerateDecelerateInterpolator())
                                .setDuration(200)
                                .x(0)
                                .start();
                    } else {
                        this.animate().setInterpolator(new AccelerateDecelerateInterpolator())
                                .setDuration(200)
                                .x(ScreenUtils.getScreenWidth(getContext()) - getWidth())
                                .start();
                    }
                } else {
                    performClick();
                }
                mIsDragging = false;
                break;
            default:
                break;
        }

        // 是拖动状态就自己消费触摸时间，不是继续传递触摸事件
        return true;
//        return mIsDragging || super.onTouchEvent(event);
    }

    private ViewDragHelper.Callback mDragCallback = new ViewDragHelper.Callback() {
        /**
         * 返回true才能捕获view并处理；返回false则不会回调后续方法，如onViewCaptured等
         * @param child
         * @param pointerId
         * @return
         */
        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            Log.d(TAG, "try Capture " + child + "pointerId = " + pointerId);
            return true;
        }

        @Override
        public void onViewCaptured(@NonNull View capturedChild, int activePointerId) {
            mDragOriginX = capturedChild.getLeft();
            mDragOriginY = capturedChild.getTop();
            Log.d(TAG, "Captured " + capturedChild + "activePointerId = " + activePointerId);
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            mDragHelper.settleCapturedViewAt(mDragOriginX, mDragOriginY);
//            mDragHelper.smoothSlideViewTo(releasedChild, mDragOriginX, mDragOriginY);
//            invalidate();
            int desX = 0, desY = releasedChild.getTop();
            if (releasedChild.getLeft() > getWidth() / 2) {
                desX = getWidth() - releasedChild.getWidth();
            }
            mDragHelper.smoothSlideViewTo(releasedChild, desX, desY);
            invalidate();
            Log.d(TAG, " Release view " + releasedChild);
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            //限定子view在屏幕范围内拖动，水平方向不能超出屏幕范围
            int rightEdge = getWidth() - child.getWidth();
            if (left <= 0) {
                left = 0;
            }
            return Math.min(left, rightEdge);
        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            //限定子view在屏幕范围内拖动，竖直方向不能超出屏幕范围
            int bottomEdge = getHeight() - child.getHeight();
            if (top <= 0) {
                top = 0;
            }
            return Math.min(top, bottomEdge);
        }
    };

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mDragHelper != null && mDragHelper.continueSettling(true)) {
            invalidate();
        }
    }
}
