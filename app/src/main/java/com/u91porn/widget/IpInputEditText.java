package com.u91porn.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.sdsmdg.tastytoast.TastyToast;
import com.u91porn.R;
import com.u91porn.utils.RegexUtils;

/**
 * IP地址输入框
 * <p>
 * /*
 * android官方没有提供监听删除事件的接口，所以需要自己实现。
 * 一个最简单的实现方法就是监听edittext的文本变化（addTextChangedListener），
 * 如果edittext内容变少了，就说明进行了删除操作。
 * 但这个方法有个缺陷，在edittext没有内容的时候，监听不到删除事件。
 * 解决办法就是，在edittext的内容最前面加一个看不见且不占空间的字符，
 * 如果检测到这个字符被删掉，就说明进行了删除操作。
 * 这个特殊字符就是"零宽空格"。
 * wiki链接：https://en.wikipedia.org/wiki/Zero-width_space
 * http://cashow.github.io/android-detect-keyboard-delete.html
 *
 * @author flymegoc
 * @date 2018/1/15
 */

public class IpInputEditText extends LinearLayout {

    private AppCompatEditText subIpCompatEditText1;
    private AppCompatEditText subIpCompatEditText2;
    private AppCompatEditText subIpCompatEditText3;
    private AppCompatEditText subIpCompatEditText4;

    private static final int MAX_IP_NUM = 255;
    private static final int SUB_MAX_LENGTH = 4;
    /**
     * // "\uFEFF"是零宽空格，不会引起换行
     * // 需要注意的是，"\u200b"也是零宽空格，但是这个字符会导致换行
     * // 也就是说，如果"\u200b"字符后面的单词超出了该行的剩余长度，
     * // 会导致这个单词换行，在下一行显示
     * // 使用"\uFEFF"就不会出现这种情况
     */
    private static final String ZERO_WIDTH_SPACE = "\uFEFF";
    private static final String POINT_STR = ".";

    public IpInputEditText(Context context) {
        super(context);
        init(context);
    }

    public IpInputEditText(final Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public IpInputEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public IpInputEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(final Context context) {
        //初始化界面
        LayoutInflater.from(context).inflate(R.layout.layout_ip_edittext, this);
        //绑定
        subIpCompatEditText1 = findViewById(R.id.edit1);
        subIpCompatEditText2 = findViewById(R.id.edit2);
        subIpCompatEditText3 = findViewById(R.id.edit3);
        subIpCompatEditText4 = findViewById(R.id.edit4);

        subIpCompatEditText1.setText(ZERO_WIDTH_SPACE);
        subIpCompatEditText2.setText(ZERO_WIDTH_SPACE);
        subIpCompatEditText3.setText(ZERO_WIDTH_SPACE);
        subIpCompatEditText4.setText(ZERO_WIDTH_SPACE);

        //初始化函数
        subIpCompatEditText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                pressTextChange(s, subIpCompatEditText1, null, subIpCompatEditText2);

            }
        });

        subIpCompatEditText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                pressTextChange(s, subIpCompatEditText2, subIpCompatEditText1, subIpCompatEditText3);
            }
        });

        subIpCompatEditText3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                pressTextChange(s, subIpCompatEditText3, subIpCompatEditText2, subIpCompatEditText4);
            }
        });

        subIpCompatEditText4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                pressTextChange(s, subIpCompatEditText4, subIpCompatEditText3, null);
            }
        });
    }

    private void pressTextChange(Editable s, AppCompatEditText currenAppCompatEditText, AppCompatEditText preFouceAppCompatEditText, AppCompatEditText nextFouceAppCompatEditText) {
        String text = s.toString();

        //回退上一个
        if (TextUtils.isEmpty(s)) {
            focusEditViewAndMoveSelection(preFouceAppCompatEditText);
            return;
        }

        if (!text.startsWith(ZERO_WIDTH_SPACE)) {
            text = text.replace(ZERO_WIDTH_SPACE, "");
            text = ZERO_WIDTH_SPACE + text;
            currenAppCompatEditText.setText(text);
            moveSelectionToLast(currenAppCompatEditText);
        }
        //输入未达到最大，但包含点，则说明当前输入完成
        if (text.contains(POINT_STR)) {
            //替换掉点，重新设置文本
            text = text.replace(POINT_STR, "");
            currenAppCompatEditText.setText(text);
            if (!TextUtils.isEmpty(text.replace(ZERO_WIDTH_SPACE, ""))) {
                //聚焦到下一个控件
                focusEditViewAndMoveSelection(nextFouceAppCompatEditText);
            }


            //达到最大，输入完成
        } else if (text.length() >= SUB_MAX_LENGTH) {
            int subIp = Integer.parseInt(text.replace(ZERO_WIDTH_SPACE, ""));
            //检查ip断合法性
            if (subIp >= MAX_IP_NUM) {
                //清空重新输入
                currenAppCompatEditText.setText(ZERO_WIDTH_SPACE);
            } else {
                //验证通过,聚焦到下一个控件
                focusEditViewAndMoveSelection(nextFouceAppCompatEditText);
            }
        }
    }

    /**
     * 将光标移到末尾
     **/
    private void moveSelectionToLast(AppCompatEditText edittext) {
        int length = edittext.getText().length();
        edittext.setSelection(length);
    }

    /**
     * 聚焦控件，并将光标移至文字后
     *
     * @param appCompatEditText CompatEditText
     */
    private void focusEditViewAndMoveSelection(AppCompatEditText appCompatEditText) {
        if (appCompatEditText == null) {
            return;
        }
        appCompatEditText.setFocusable(true);
        appCompatEditText.requestFocus();
        appCompatEditText.setSelection(appCompatEditText.getText().length());

    }

    public String getIpAddressStr() {
        String subIpText1 = subIpCompatEditText1.getText().toString().replace(ZERO_WIDTH_SPACE, "");
        String subIpText2 = subIpCompatEditText2.getText().toString().replace(ZERO_WIDTH_SPACE, "");
        String subIpText3 = subIpCompatEditText3.getText().toString().replace(ZERO_WIDTH_SPACE, "");
        String subIpText4 = subIpCompatEditText4.getText().toString().replace(ZERO_WIDTH_SPACE, "");
        String fullIpText;
        if (TextUtils.isEmpty(subIpText1) || TextUtils.isEmpty(subIpText2) || TextUtils.isEmpty(subIpText3) || TextUtils.isEmpty(subIpText4)) {
            fullIpText = "";
        } else {
            fullIpText = subIpText1 + POINT_STR + subIpText2 + POINT_STR + subIpText3 + POINT_STR + subIpText4;
        }
        return fullIpText;
    }

    public void setIpAddressStr(String ipAddress) {
        if (TextUtils.isEmpty(ipAddress)) {
            subIpCompatEditText1.setText("");
            subIpCompatEditText2.setText("");
            subIpCompatEditText3.setText("");
            subIpCompatEditText4.setText("");
            subIpCompatEditText1.requestFocus();
            return;
        }
        if (RegexUtils.isIP(ipAddress)) {
            String[] ipArr = ipAddress.split("\\.");
            for (int i = 0; i < ipArr.length; i++) {
                switch (i) {
                    case 0:
                        subIpCompatEditText1.setText(ipArr[i]);
                        break;
                    case 1:
                        subIpCompatEditText2.setText(ipArr[i]);
                        break;
                    case 2:
                        subIpCompatEditText3.setText(ipArr[i]);
                        break;
                    case 3:
                        subIpCompatEditText4.setText(ipArr[i]);
                        break;
                    default:
                }
            }
        }
    }
}
