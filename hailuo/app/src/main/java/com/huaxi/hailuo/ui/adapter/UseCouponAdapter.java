package com.huaxi.hailuo.ui.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huaxi.hailuo.R;
import com.huaxi.hailuo.model.bean.UseCoupon;

import java.util.List;

/**
 * Created by admin on 2018/4/3.
 * 还款使用优惠券
 */

public class UseCouponAdapter extends BaseQuickAdapter<UseCoupon.CouponListBean, BaseViewHolder> {

    private boolean isClear = false;

    public UseCouponAdapter(@Nullable List<UseCoupon.CouponListBean> data) {
        super(R.layout.item_use_coupon, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, UseCoupon.CouponListBean item) {

        if (!isClear) {
            if (item.getChose()) {
                helper.setVisible(R.id.is_chosed, true);
            } else {
                helper.setVisible(R.id.is_chosed, false);
            }
        } else {
            helper.setVisible(R.id.is_chosed, false);
        }

        helper.setText(R.id.tv_item_coupons_name, item.getCoupon_name());
        helper.setText(R.id.tv_item_coupons_valid, item.getValidity_time_str());
        helper.setText(R.id.tv_item_coupons_description, item.getCoupon_description());
        helper.setText(R.id.tv_item_tips, item.getCoupon_name_description()).addOnClickListener(R.id.item_use_coupon);
//        SpannableUtils.setTextSizeSpan((TextView) helper.getView(R.id.tv_item_money), item.getCoupon_money_str(), 50f, Color.WHITE);
//        SpannableUtils.setTextSizeSpan((TextView) helper.getView(R.id.tv_item_money),  item.getCoupon_money_str(), 80f, Color.WHITE);

        helper.setText(R.id.tv_item_money_value, item.getCoupon_money_str());
    }

    public void clearChose() {
        isClear = true;
    }
}
