package com.huaxi.hailuo.presenter.contract

import com.huaxi.hailuo.base.BasePresenter
import com.huaxi.hailuo.base.BaseView
import com.huaxi.hailuo.model.bean.CheckUseCouponBean
import com.huaxi.hailuo.model.bean.CouponListBean

object CouponsContract {

    interface View : BaseView {
        fun processGetCouponListData(data: CouponListBean?, isRefresh: Boolean)
        fun processCheckUseCoupon(data: CheckUseCouponBean?,coupon_id: String)
    }

    interface Presenter : BasePresenter<View> {
        fun getCouponList(type: Int, size: Int, page: Int, isRefresh: Boolean)
        fun checkUseCoupon(coupon_id :String)
    }

}