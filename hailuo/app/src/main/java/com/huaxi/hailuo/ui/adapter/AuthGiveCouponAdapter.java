package com.huaxi.hailuo.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaxi.hailuo.R;
import com.huaxi.hailuo.model.bean.HomeCouponBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangliuguang  on 2018/5/2.
 */
public class AuthGiveCouponAdapter extends BaseAdapter {
    private Context mContext;
    private List<HomeCouponBean.AuthListBean> mlist;
    private int[] normalImageList = {R.drawable.icon_unauth_1, R.drawable.icon_unauth_2, R.drawable.icon_unauth_3,
            R.drawable.icon_unauth_4, R.drawable.icon_unauth_5};
    private int[] hasAuthedArr = {R.drawable.icon_authed_1, R.drawable.icon_authed_2,
            R.drawable.icon_authed_3, R.drawable.icon_authed_4, R.drawable.icon_authed_5,};

    public AuthGiveCouponAdapter(Context mContext, List<HomeCouponBean.AuthListBean> mlist) {
        this.mContext = mContext;
        this.mlist = mlist;
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_auth_coupon, null);
            holder = new ViewHolder();
            holder.iv_num = (ImageView) convertView.findViewById(R.id.iv_auth_coupon_num);
            holder.tv_auth_content = (TextView) convertView.findViewById(R.id.tv_auth_verify);
            holder.tv_red_packet = (TextView) convertView.findViewById(R.id.tv_totlal_red_packet);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_auth_content.setText(Html.fromHtml(mlist.get(position).getAuth_title()));
        holder.tv_red_packet.setText(Html.fromHtml(mlist.get(position).getAuth_des()));
        holder.iv_num.setImageResource(normalImageList[position]);
        String auth_status = mlist.get(position).getAuth_status();

        Log.e("是否是", "getView: " + auth_status);
        //所有未认证的
        if ("2".equals(auth_status)) {
            holder.iv_num.setImageResource(normalImageList[position]);
            holder.tv_auth_content.setTextColor(mContext.getResources().getColor(R.color.global_txt_black5));
            holder.tv_red_packet.setTextColor(mContext.getResources().getColor(R.color.global_txt_black5));


        } else if ("1".equals(auth_status)
                && (position + 1 < mlist.size())
                && "1".equals(mlist.get(position + 1).getAuth_status())) {
            // 当前为已经认证，并且下一项也为已认证
            holder.iv_num.setImageResource(R.drawable.icon_authed_right);
            holder.tv_auth_content.setTextColor(mContext.getResources().getColor(R.color.global_txt_black4));
            holder.tv_red_packet.setTextColor(mContext.getResources().getColor(R.color.global_txt_black4));
        } else if ("1".equals(auth_status)
                && (position + 1 < mlist.size())
                && "2".equals(mlist.get(position + 1).getAuth_status())) {
            // 当前为已经认证，并且下一项未认证

            holder.iv_num.setImageResource(hasAuthedArr[position]);
            holder.tv_auth_content.setTextColor(mContext.getResources().getColor(R.color.global_txt_black4));
            holder.tv_red_packet.setTextColor(mContext.getResources().getColor(R.color.global_txt_black4));
        } else if (position + 1 == mlist.size() && "1".equals(auth_status)) {
            // 当前为已经认证，无下一项
            holder.iv_num.setImageResource(hasAuthedArr[position]);
            holder.tv_auth_content.setTextColor(mContext.getResources().getColor(R.color.global_txt_black4));
            holder.tv_red_packet.setTextColor(mContext.getResources().getColor(R.color.global_txt_black4));
        }

        return convertView;
    }

    class ViewHolder {
        ImageView iv_num;
        TextView tv_auth_content;
        TextView tv_red_packet;
    }
}
