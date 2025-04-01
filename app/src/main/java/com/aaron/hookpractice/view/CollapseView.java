package com.aaron.hookpractice.view;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.AppCompatTextView;
import android.text.Layout;
import android.text.StaticLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * author: dubojun
 * date: 2025/1/15
 * description:
 **/
public class CollapseView extends AppCompatTextView {
    public static final String SUFFIX_COLLAPSE = "...收起";

    public static final String SUFFIX_EXPAND = "...展开";

    public static final int MAX_LINES = 3;

    protected boolean isCollapse = true;

    public CollapseView(Context context) {
        super(context);
    }

    public CollapseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CollapseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    private Layout layout;
    protected CharSequence originText;
    public void setContent(CharSequence content) {
        post(()->{
            originText = content;
            layout = createStaticLayout(content);

            int lineCount = layout.getLineCount();
            int maxLines = getMaxLines();
            if (lineCount <= maxLines) {
                setText(content);
                return;
            }

            int startPos = layout.getLineStart(maxLines - 1);
            int endPos = layout.getLineEnd(maxLines - 1);

            // 折叠状态，最后一行肯定是满的，直接裁剪
            if (isCollapse) {
                int suffixCount = SUFFIX_EXPAND.length();
                CharSequence showText = content.subSequence(0, endPos - suffixCount) + SUFFIX_EXPAND;
                setText(showText);
                return;
            }

            // 展开状态，最后一行的剩余空间可能够放suffix，也可能不够，分开处理
            setMaxLines(-1);
            float lastLineWidth = layout.getLineWidth(lineCount - 1);
            CharSequence showText = content;
            if (getMeasuredWidth() - lastLineWidth > layout.getPaint().measureText(SUFFIX_COLLAPSE)) {
                 showText += SUFFIX_COLLAPSE;
            } else {
                showText += "\n" + SUFFIX_COLLAPSE;
            }
            setText(showText);
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                int y = (int) event.getY();
                if(layout.getLineForVertical(y) == getMaxLines() -1) {
                    isCollapse = !isCollapse;
                    setMaxLines(-1);
                    setContent(originText);
                }
                break;
        }
        return true;
    }

    private Layout createStaticLayout(CharSequence text) {
        return StaticLayout.Builder.obtain(text, 0, text.length(), getPaint(), getMeasuredWidth()).build();
    }
}
