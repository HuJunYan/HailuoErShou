package com.huaxi.hailuo.ui.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaxi.hailuo.R;
import com.huaxi.hailuo.model.bean.AssessSuccessBean;
import com.huaxi.hailuo.util.ImageUtil;

import java.util.List;

/**
 * Created by wang on 2018/2/1.
 */

public class AssessSuccessAdapter extends BaseRecyclerViewAdapter<AssessSuccessBean.RecoveryProcessBean> {
    public AssessSuccessAdapter(Activity context, List<AssessSuccessBean.RecoveryProcessBean> list) {
        super(context, list);
    }

    @Override
    public BaseRecyclerViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_assess_success, parent, false));
    }

    @Override
    protected void onBindHeaderView(View itemView) {

    }

    @Override
    protected void onBindFooterView(View itemView) {

    }


    class ItemHolder extends BaseRecyclerViewHolder {
        ImageView iv_item_assess_success_icon;
        TextView tv_item_assess_success_title;
        TextView tv_item_assess_success_content;

        public ItemHolder(View itemView) {
            super(itemView);
            iv_item_assess_success_icon = (ImageView) itemView.findViewById(R.id.iv_item_assess_success_icon);
            tv_item_assess_success_title = (TextView) itemView.findViewById(R.id.tv_item_assess_success_title);
            tv_item_assess_success_content = (TextView) itemView.findViewById(R.id.tv_item_assess_success_content);
        }

        @Override
        public void onBindViewHolder(int position) {
            AssessSuccessBean.RecoveryProcessBean recoveryProcessBean = mList.get(position);
            ImageUtil.INSTANCE.load(mActivity, recoveryProcessBean.getProcess_icon(), iv_item_assess_success_icon);
            tv_item_assess_success_title.setText(recoveryProcessBean.getProcess_title());
            tv_item_assess_success_content.setText(recoveryProcessBean.getProcess_content());
        }
    }


}
