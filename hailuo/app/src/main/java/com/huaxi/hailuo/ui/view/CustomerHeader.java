package com.huaxi.hailuo.ui.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huaxi.hailuo.R;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.util.DensityUtil;

/**
 * Created by wang on 2018/3/20.
 */

public class CustomerHeader extends LinearLayout implements RefreshHeader {
    TextView tv;
    private ValueAnimator va;

    public CustomerHeader(Context context) {
        super(context);
        init();
        va = ValueAnimator.ofInt(0, 100);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                tv.setText(String.valueOf((int) animation.getAnimatedValue()));
            }
        });
    }


    public CustomerHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomerHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        tv = new TextView(getContext());
        tv.setBackgroundColor(getContext().getResources().getColor(R.color.global_bg));
        tv.setHeight(DensityUtil.dp2px(50));
        tv.setGravity(Gravity.CENTER);
        tv.setText("下拉开始刷新");
        addView(tv, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout layout, int height, int extendHeight) {
        va.start();
    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        Log.d("wangchen", "onStateChanged newState= " + newState);
        switch (newState) {
            case None:
            case PullDownToRefresh:
                tv.setText("下拉开始刷新");
                break;
            case Refreshing:
                tv.setText("正在刷新");
                break;
            case ReleaseToRefresh:
                tv.setText("释放立即刷新");
                break;
        }
    }

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        va.cancel();
        if (success) {
            tv.setText("刷新成功");
        }
        return 500;
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;
    }

    /**
     * /下拉时调用
     * @param percent  百分比
     * @param offset   偏移量
     * @param headerHeight header的高度
     * @param extendHeight 扩展高度?
     */
    @Override
    public void onPullingDown(float percent, int offset, int headerHeight, int extendHeight) {
        Log.d("wangchen", "onPullingDown, percent = " + percent
                + ",offset = " + offset
                + ",headerHeight = " + headerHeight
                + ",extendHeight = " + extendHeight);

    }

    /**
     * /释放时调用
     * @param percent  百分比
     * @param offset   偏移量
     * @param headerHeight header的高度
     * @param extendHeight 扩展高度?
     */
    @Override
    public void onReleasing(float percent, int offset, int headerHeight, int extendHeight) {
        Log.d("wangchen", "onReleasing, percent = " + percent
                + ",offset = " + offset
                + ",headerHeight = " + headerHeight
                + ",extendHeight = " + extendHeight);

    }

    @Override
    public void onRefreshReleased(RefreshLayout layout, int headerHeight, int extendHeight) {

    }

    @Override
    public void setPrimaryColors(int... colors) {

    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int extendHeight) {

    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }


    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }


}
