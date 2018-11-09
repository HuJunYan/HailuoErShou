package com.huaxi.hailuo.ui.adapter

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.huaxi.hailuo.R
import com.huaxi.hailuo.model.bean.CouponListBean
import com.huaxi.hailuo.model.bean.IncomeDetailOrderBean
import com.huaxi.hailuo.util.SpannableUtils
import kotlinx.android.synthetic.main.item_income_detail.view.*
import kotlinx.android.synthetic.main.item_un_use_coupons_layout.view.*

/**
 * Created by zhangliuguang on 2018/5/17.
 */
class IncomeOrderAdapter(var data: MutableList<IncomeDetailOrderBean.Income>, val onClick: (CouponListBean.CouponBean) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val detailViewHolder = holder as IncomeOrderViewHolder
        detailViewHolder.onBindViewHolder(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return IncomeOrderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_income_detail, parent, false))
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    internal inner class IncomeOrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun onBindViewHolder(position: Int) {
            val detailOrderBean = data[position]
//            with(detailOrderBean) {
                itemView.tv_income_money.text = detailOrderBean.income_money
                itemView.tv_invite_order.text = detailOrderBean.income_des
                itemView.tv_income_time.text = detailOrderBean.income_date
//            }

        }

    }

}