package com.huaxi.hailuo.util;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntegerRes;
import android.widget.Button;
import android.widget.TextView;

import com.huaxi.hailuo.R;


public class TimeCount extends CountDownTimer {
    private Button button;
    private TextView textView;
    private String msg;
    private ColorStateList color;
    private boolean isChangeColor = true;
    private int backgroundRes;

    public TimeCount(long millisInFuture, long countDownInterval) {
        this(null, millisInFuture, countDownInterval, "");
    }

    public TimeCount(TextView textView, long millisInFuture, long countDownInterval, String msg, boolean isChangeColor, @DrawableRes int backgroundRes) {
        this(textView, millisInFuture, countDownInterval, msg);
        this.isChangeColor = isChangeColor;
        this.backgroundRes = backgroundRes;
    }


    public TimeCount(TextView textView, long millisInFuture, long countDownInterval, String msg) {
        super(millisInFuture, countDownInterval);
        if (textView != null) {
            textView.setClickable(false);
        }
        if (textView != null) {
            this.color = textView.getTextColors();
        }
        this.textView = textView;
        this.msg = msg;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        if (textView != null) {
            if (isChangeColor) {
                textView.setBackground(textView.getContext().getResources().getDrawable(R.drawable.shape_login_right_background2));
                textView.setTextColor(Color.WHITE);
                textView.setText(millisUntilFinished / 1000 + "s后重发");
            }
            if (!isChangeColor) {
                textView.setBackground(textView.getContext().getResources().getDrawable(R.drawable.shape_login_right_background2));
                textView.setTextColor(Color.WHITE);
                textView.setText(millisUntilFinished / 1000 + "s后重发");
            }
        }
    }

    @Override
    public void onFinish() {
        if (textView != null) {
            textView.setClickable(true);
            textView.setTextColor(color);
            textView.setText(msg);
            if (backgroundRes != 0) {
                textView.setBackgroundResource(backgroundRes);
            }

            if (!isChangeColor) {
                textView.setBackground(textView.getContext().getResources().getDrawable(R.drawable.shape_verify_code));
            }
        }
    }

    public void finish() {
        mHandler.sendEmptyMessage(0);
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            TimeCount.this.onFinish();
            TimeCount.this.cancel();
        }
    };
}

