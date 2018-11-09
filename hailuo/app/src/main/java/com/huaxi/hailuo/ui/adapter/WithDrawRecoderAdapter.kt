package com.huaxi.hailuo.ui.adapter

import android.app.Activity
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.huaxi.hailuo.R
import com.huaxi.hailuo.model.bean.CouponListBean
import com.huaxi.hailuo.model.bean.IncomeDetailOrderBean
import com.huaxi.hailuo.util.SpannableUtils
import com.huaxi.hailuo.util.TintColorUtil
import kotlinx.android.synthetic.main.item_income_detail.view.*
import kotlinx.android.synthetic.main.item_used_coupons_layout.view.*
import kotlinx.android.synthetic.main.item_withdraw_recoder.view.*

/**
 * Created by zhangliuguang on 2018/5/16.
 */
class WithDrawRecoderAdapter(var activity: Activity, var data: MutableList<IncomeDetailOrderBean.Income>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val withDrawViewHolder = holder as WithDrawViewHolder
        withDrawViewHolder.onBindViewHolder(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return WithDrawViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_withdraw_recoder, parent, false))
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    internal inner class WithDrawViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun onBindViewHolder(position: Int) {
            val incomeBean = data[position]
            with(incomeBean) {
                itemView.tv_withdraw_money.text = incomeBean.income_money
                itemView.tv_withdraw_time.text = incomeBean.income_date
                itemView.tv_withdraw_result.text = incomeBean.income_result
            }

        }

    }

}