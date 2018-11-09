package com.huaxi.hailuo.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huaxi.hailuo.R;
import com.huaxi.hailuo.model.bean.SpeekBean;

import java.util.List;

/**
 * Created by zhangliuguang  on 2018/4/28.
 */
public class GridViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<SpeekBean.FeedbackItemBean> mList;
    private String selectorPosition = "";

    public GridViewAdapter(Context context, List<SpeekBean.FeedbackItemBean> mList) {
        this.mContext = context;
        this.mList = mList;

    }

    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mList != null ? mList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return mList != null ? position : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = View.inflate(mContext, R.layout.item_gridview, null);
        TextView textView = (TextView) convertView.findViewById(R.id.tv);
        ImageView iv_chosed = (ImageView) convertView.findViewById(R.id.iv_is_chosed);
        textView.setText(mList.get(position).getFeedback_title());
        //如果当前的position等于传过来点击的position,就去改变他的状态
        if (selectorPosition == mList.get(position).getFeedback_type()) {

            textView.setTextColor(Color.parseColor("#3084fd"));
            textView.setBackgroundResource(R.drawable.shape_item_red);
            iv_chosed.setVisibility(View.VISIBLE);
        } else {
            //其他的恢复原来的状态
            textView.setTextColor(Color.parseColor("#666666"));
            textView.setBackgroundResource(R.drawable.shape_item_gray);
            iv_chosed.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }


    public void changeState(String pos) {
        selectorPosition = pos;
        notifyDataSetChanged();

    }
}
