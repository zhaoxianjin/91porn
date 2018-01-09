package com.u91porn.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 *多行中间显示省略...
 * https://stackoverflow.com/a/39890995
 * @author flymegoc
 * @date 2018/1/8
 */

public class MiddleMultilineTextView extends AppCompatTextView {

    private String SYMBOL = " ... ";
    private final int SYMBOL_LENGTH = SYMBOL.length();

    public MiddleMultilineTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getMaxLines() > 1) {
            int originalLength = getText().length();
            int visibleLength = getVisibleLength();

            if (originalLength > visibleLength) {
                setText(smartTrim(getText().toString(), visibleLength - SYMBOL_LENGTH));
            }
        }
    }

    private String smartTrim(String string, int maxLength) {
        if (string == null) {
            return null;
        }
        if (maxLength < 1) {
            return string;
        }
        if (string.length() <= maxLength) {
            return string;
        }
        if (maxLength == 1) {
            return string.substring(0, 1) + "...";
        }

        int midpoint = (int) Math.ceil(string.length() / 2);
        int toremove = string.length() - maxLength;
        int lstrip = (int) Math.ceil(toremove / 2);
        int rstrip = toremove - lstrip;

        return string.substring(0, midpoint - lstrip) + SYMBOL + string.substring(midpoint + rstrip);
    }

    private int getVisibleLength() {
        int start = getLayout().getLineStart(0);
        int end = getLayout().getLineEnd(getMaxLines() - 1);
        return getText().toString().substring(start, end).length();
    }
}
