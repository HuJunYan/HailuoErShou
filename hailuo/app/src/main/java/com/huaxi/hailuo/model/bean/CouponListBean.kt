package com.huaxi.hailuo.model.bean

/**
 * Created by wang on 2018/4/2.
 */
data class CouponListBean(var couponList: ArrayList<CouponBean>, var unused_count: Int, var used_count: Int, var past_time_count: Int) {
    data class CouponBean(var coupon_id: String, var coupon_name: String, var coupon_money_str: String,
                          var coupon_name_description: String, var coupon_description: String,
                          var validity_time_str: String, var used_time_str: String){
        constructor() : this("","","","","","","") {}
    }

}
