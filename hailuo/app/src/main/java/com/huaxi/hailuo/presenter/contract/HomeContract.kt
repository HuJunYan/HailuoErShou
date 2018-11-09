package com.huaxi.hailuo.presenter.contract

import com.huaxi.hailuo.base.BasePresenter
import com.huaxi.hailuo.base.BaseView
import com.huaxi.hailuo.model.bean.AssessBean
import com.huaxi.hailuo.model.bean.HomeCouponBean
import com.huaxi.hailuo.model.bean.HomePageBean
import com.huaxi.hailuo.model.bean.MobileListBean

object HomeContract {

    interface View : BaseView {
        fun processHomeData(data: HomePageBean, isNeedShowDialog: Boolean)
        fun assessComplete(data: AssessBean?)
        fun processMobileList(data:MobileListBean?)
        fun homeCouponDialogResult(data: HomeCouponBean?)
    }

    interface Presenter : BasePresenter<View> {
        fun getHomeData(isNeedShowDialog: Boolean)
        fun assess(mobile_model: String, mobile_memory: String)
        fun upLoadLocation()
        fun getMobileList()
        fun getHomeCouponDialog()
    }

}