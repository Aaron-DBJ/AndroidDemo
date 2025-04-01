package com.aaron.hookpractice.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;


public class AutoScrollTextView extends View {

    private Paint textPaint;
    private String[] texts;
    private int currentIndex = 0;
    private float offsetY = 0;
    private float textHeight;
    private ValueAnimator scrollAnimator;
    private ValueAnimator widthAnimator;

    public AutoScrollTextView(Context context) {
        super(context);
        init();
    }

    public AutoScrollTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AutoScrollTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(50); // 设置文本大小
        textPaint.setColor(0xFF000000); // 设置文本颜色

        // 计算文本高度
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        textHeight = fontMetrics.descent - fontMetrics.ascent;

        // 初始化滚动动画
        scrollAnimator = ValueAnimator.ofFloat(0, textHeight + getPaddingBottom() + getPaddingTop());
        scrollAnimator.setDuration(2000); // 设置滚动动画时长
        scrollAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        scrollAnimator.addUpdateListener(animation -> {
            offsetY = (float) animation.getAnimatedValue();
            invalidate();
        });

        // 初始化宽度动画
        widthAnimator = ValueAnimator.ofInt(0, 0);
        widthAnimator.setDuration(500); // 设置宽度动画时长
        widthAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        widthAnimator.addUpdateListener(animation -> {
            int newWidth = (int) animation.getAnimatedValue();
            getLayoutParams().width = newWidth;
            requestLayout();
        });
    }

    public void setTexts(String[] texts) {
        this.texts = texts;
        startScrollAnimation();
    }

    private void startScrollAnimation() {
        if (texts == null || texts.length == 0) return;

        // 停止之前的动画
//        if (scrollAnimator.isRunning()) {
//            scrollAnimator.cancel();
//        }

        // 开始滚动动画
        scrollAnimator.start();

        // 滚动结束后切换到下一个文本
        scrollAnimator.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                currentIndex = (currentIndex + 1) % texts.length;
                offsetY = 0;
                startWidthAnimation();
                invalidate();
            }
        });
    }

    private void startWidthAnimation() {
        if (texts == null || texts.length == 0) return;

        // 停止之前的动画
//        if (widthAnimator.isRunning()) {
//            widthAnimator.cancel();
//        }

        // 计算新文本的宽度
        String newText = texts[currentIndex];
        float newTextWidth = textPaint.measureText(newText);

        // 开始宽度动画
        widthAnimator.setIntValues(getWidth(), (int) newTextWidth + getPaddingLeft() + getPaddingRight());
        widthAnimator.start();

        // 宽度动画结束后重新开始滚动动画
        widthAnimator.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                startScrollAnimation();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 处理宽度
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int width;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            // 计算文本宽度
            float textWidth = texts != null && texts.length > 0 ? textPaint.measureText(texts[currentIndex]) : 0;
            width = (int) (textWidth + getPaddingLeft() + getPaddingRight());
            if (widthMode == MeasureSpec.AT_MOST) {
                width = Math.min(width, widthSize);
            }
        }

        // 处理高度
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int height;

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            // 计算文本高度
            height = (int) (textHeight + getPaddingTop() + getPaddingBottom());
            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, heightSize);
            }
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (texts == null || texts.length == 0) return;

        // 考虑 padding
        float x = getPaddingLeft();
//        float y = getPaddingTop() + textHeight - textPaint.getFontMetrics().top;
        float y = getPaddingTop() + textHeight ;

        // 绘制当前文本
        String currentText = texts[currentIndex];
        canvas.drawText(currentText, x, y - offsetY, textPaint);

        // 绘制下一个文本
        String nextText = texts[(currentIndex + 1) % texts.length];
        canvas.drawText(nextText, x, y - offsetY + textHeight + getPaddingTop() + getPaddingBottom(), textPaint);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // 释放动画资源
        if (scrollAnimator != null) {
            scrollAnimator.cancel();
            scrollAnimator.removeAllListeners();
            scrollAnimator.removeAllUpdateListeners();
        }
        if (widthAnimator != null) {
            widthAnimator.cancel();
            widthAnimator.removeAllListeners();
            widthAnimator.removeAllUpdateListeners();
        }
    }
}