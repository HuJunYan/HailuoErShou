package com.huaxi.hailuo.ui.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huaxi.hailuo.R;
import com.huaxi.hailuo.model.bean.OrderListBean;
import com.huaxi.hailuo.util.ImageUtil;
import com.huaxi.hailuo.util.MoneyUtils;

import java.util.List;

/**
 * Created by admin on 2018/1/30.
 * 我的订单数据适配器
 */

public class MyOrderListAdapter extends BaseQuickAdapter<OrderListBean.Order_list, BaseViewHolder> {

    private Context mContext;

    public MyOrderListAdapter(@Nullable List<OrderListBean.Order_list> data, Context context) {
        super(R.layout.item_my_order, data);
        this.mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, OrderListBean.Order_list item) {
//        ImageUtil.INSTANCE.loadFitXY(mContext, item.getMobile_icon(), (ImageView) helper.getView(R.id.phone_img));
        helper.setText(R.id.phone_name, item.getMobile_model());
        helper.setText(R.id.time, item.getOrder_term() + "天").addOnClickListener(R.id.detail);
        try {
            helper.setText(R.id.phone_price, MoneyUtils.changeF2Y(item.getAssess_money(), 2) + "元");
            helper.setText(R.id.less_price, MoneyUtils.changeF2Y(item.getPre_money(), 2) + "元");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
