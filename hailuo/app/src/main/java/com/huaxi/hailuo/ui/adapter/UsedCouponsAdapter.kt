package com.huaxi.hailuo.ui.adapter

import android.app.Activity
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.huaxi.hailuo.R
import com.huaxi.hailuo.model.bean.CouponListBean
import com.huaxi.hailuo.util.SpannableUtils
import com.huaxi.hailuo.util.TintColorUtil
import kotlinx.android.synthetic.main.item_used_coupons_layout.view.*

/**
 * Created by wang on 2018/4/2.
 */
class UsedCouponsAdapter(var activity: Activity, var data: MutableList<CouponListBean.CouponBean>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val couponsViewHolder = holder as CouponsViewHolder
        couponsViewHolder.onBindViewHolder(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CouponsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_used_coupons_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    internal inner class CouponsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun onBindViewHolder(position: Int) {
            val couponBean = data[position]
            with(couponBean) {
                itemView.rl_un_use_coupons_moneys.background = TintColorUtil.tintDrawable(activity, itemView.rl_un_use_coupons_moneys.background, R.color.edit_text_hint_color)
                itemView.tv_item_money.text = "￥"
                SpannableUtils.setTextSizeSpan(itemView.tv_item_money, coupon_money_str, itemView.tv_item_money_number.textSize, Color.WHITE)
                itemView.tv_item_tips.text = coupon_name_description
                itemView.tv_item_coupons_name.text = coupon_name
                itemView.tv_item_coupons_immediate_use.text =  "$used_time_str\n已使用"
                itemView.tv_item_coupons_valid.text = validity_time_str
                itemView.tv_item_coupons_description.text = coupon_description
            }

        }

    }

}