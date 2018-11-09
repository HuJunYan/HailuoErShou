package com.huaxi.hailuo.ui.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huaxi.hailuo.R;
import com.huaxi.hailuo.model.bean.MyBankListBean;
import com.huaxi.hailuo.util.ImageUtil;

import java.util.List;


/**
 * @author admin
 * @date 2018/1/31
 * 我的银行卡数据适配器
 */

public class MyBankListAdapter extends BaseQuickAdapter<MyBankListBean.BankBean, BaseViewHolder> {

    private Context mContext;

    public MyBankListAdapter(Context context, @Nullable List<MyBankListBean.BankBean> data) {
        super(R.layout.item_my_bank_list, data);
        this.mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, MyBankListBean.BankBean item) {
        helper.setBackgroundRes(R.id.rl_my_card,
                helper.getAdapterPosition() % 2 == 0 ?
                        R.drawable.iv_card_bg_blue : R.drawable.iv_card_bg_red);


        ImageUtil.INSTANCE.loadFitXY(mContext, item.getBank_icon(), (ImageView) helper.getView(R.id.iv_card_icon));
        helper.setText(R.id.card_name, item.getBank_card_name());
        if ("1".equals(item.is_delete())) {
            helper.setVisible(R.id.iv_bind_bank_card, true).addOnClickListener(R.id.iv_bind_bank_card);
        } else {
            helper.setVisible(R.id.iv_bind_bank_card, false);
        }
        StringBuffer buffer = new StringBuffer();
        String cardNum = item.getMobile_card_num();
        buffer.append("**** **** **** ");
        buffer.append(cardNum.substring(cardNum.length() - 4, cardNum.length()));
        helper.setText(R.id.tv_card_num, buffer);
    }
}
