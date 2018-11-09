package com.huaxi.hailuo.presenter.contract

import com.huaxi.hailuo.base.BasePresenter
import com.huaxi.hailuo.base.BaseView
import com.huaxi.hailuo.model.bean.DownRefreshBean

/**
 * Created by admin on 2018/1/16.
 */
object PhoneAssessContract {

    interface View : BaseView {
        fun processData(data: DownRefreshBean?)
        fun processSuccessData()
        fun riskWaitingFeedbackResult(t: Any?)
        fun processSubmitCreditResult()
    }

    interface Presenter : BasePresenter<View> {
        fun getRefreshData(mobile_model: String, mobile_memory: String)
        fun getRiskWaitingFeedback()
        fun submitCredit(choose_mobile: String, choose_memory: String)
        //导流埋点
        fun daoliuBuriedPoint(flag:String,result: String)
    }
}