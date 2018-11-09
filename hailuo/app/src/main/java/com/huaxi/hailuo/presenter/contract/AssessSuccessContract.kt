package com.huaxi.hailuo.presenter.contract

import com.huaxi.hailuo.base.BasePresenter
import com.huaxi.hailuo.base.BaseView
import com.huaxi.hailuo.model.bean.*

/**
 * Created by admin on 2018/1/16.
 */
object AssessSuccessContract {

    interface View : BaseView {
        fun processAssessSuccessData(data: AssessSuccessBean?)

        fun processGenOrderResult(t: GenOrderBean?)
        fun processGenOrderFinish()
        /**
         * 获取验证码完成
         */
        fun getVeryCodeResult(data: SmsBean?)

        fun  getCheckOldUserStatusResult(data:GetSmsCodeBean?)
    }

    interface Presenter : BasePresenter<View> {
        fun getAssessSuccessData(mobile_model: String, mobile_memory: String)
        fun genOrder(mobile_model: String, mobile_memory: String,channel_type:String, bank_mobile:String)
        /**
         * 获取验证码
         */
        fun getVeryCode(mobile:String,type:String)

        //获取checkOldUserStatus
        fun getCheckOldUserStatus(channel_type:String,bank_mobile:String,verify_code:String)
    }
}