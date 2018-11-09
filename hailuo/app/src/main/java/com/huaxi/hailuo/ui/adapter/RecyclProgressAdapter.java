package com.huaxi.hailuo.ui.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huaxi.hailuo.R;
import com.huaxi.hailuo.model.bean.OrderDetailBean;

import java.util.List;

/**
 * Created by admin on 2018/2/1.
 * 订单进度适配器
 */

public class RecyclProgressAdapter extends BaseQuickAdapter<OrderDetailBean.StatusListBean, BaseViewHolder> {
    private List<OrderDetailBean.StatusListBean> mList;

    public List<OrderDetailBean.StatusListBean> getList() {
        return mList;
    }

    public void setList(List<OrderDetailBean.StatusListBean> list) {
        mList = list;
    }

    public RecyclProgressAdapter(@Nullable List<OrderDetailBean.StatusListBean> data) {
        super(R.layout.item_recycl_progress, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, OrderDetailBean.StatusListBean item) {

        //控制线
        if (helper.getLayoutPosition() == 1) {
            helper.setVisible(R.id.dot_top, false);
            helper.setImageResource(R.id.iv_dot, R.drawable.round_deep);
        } else if (helper.getLayoutPosition() == mList.size()) {
            helper.setVisible(R.id.dot_bottom, false);
        } else {
            helper.setVisible(R.id.dot_top, true);
            helper.setVisible(R.id.dot_bottom, true);
            helper.setImageResource(R.id.iv_dot, R.drawable.round_grey);
        }

        helper.setGone(R.id.status_desc, !TextUtils.isEmpty(item.getStatus_des()));
        helper.setText(R.id.status_desc, item.getStatus_des());

        helper.setText(R.id.status, item.getStatus_title());
        helper.setText(R.id.status_time, item.getStatus_date());
    }
}
