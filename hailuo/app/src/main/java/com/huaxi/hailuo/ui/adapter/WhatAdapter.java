package com.huaxi.hailuo.ui.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huaxi.hailuo.R;

import java.util.List;

/**
 * Created by admin on 2018/2/2.
 */

public class WhatAdapter extends BaseQuickAdapter<String,BaseViewHolder> {

    private List<String> mList;
    public WhatAdapter( @Nullable List<String> data) {
        super(R.layout.item_dialog_what, data);
        this.mList = data;
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_content,item);

    }
}
