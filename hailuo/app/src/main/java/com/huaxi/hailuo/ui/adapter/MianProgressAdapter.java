package com.huaxi.hailuo.ui.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huaxi.hailuo.R;
import com.huaxi.hailuo.model.bean.FlowBean;
import com.huaxi.hailuo.util.ImageUtil;

import java.util.List;

/**
 * Created by admin on 2018/1/27.
 * 首页回收流程数据适配器
 */

public class MianProgressAdapter extends BaseQuickAdapter<FlowBean,BaseViewHolder> {

    private List<FlowBean> mData;
    private Context mContext;

    public MianProgressAdapter(Context context, @Nullable List<FlowBean> data) {
        super( R.layout.item_main_progress2, data);
        this.mContext = context;
        this.mData = data;

    }


    @Override
    protected void convert(BaseViewHolder helper, FlowBean item) {

        helper.setText(R.id.tv,item.getFlow_name());
        ImageUtil.INSTANCE.loadCache(mContext,item.getFlow_icon(),R.drawable.progerss1, (ImageView) helper.getView(R.id.iv));
        if (helper.getLayoutPosition()==mData.size()-1){
            //最后一个隐藏
            helper.setVisible(R.id.iiv,false);
        }

    }


}
