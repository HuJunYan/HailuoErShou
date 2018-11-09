package com.huaxi.hailuo.presenter.contract

import com.huaxi.hailuo.base.BasePresenter
import com.huaxi.hailuo.base.BaseView
import com.huaxi.hailuo.model.bean.ConfirRefundBean
import com.huaxi.hailuo.model.bean.GetSmsCodeBean
import com.huaxi.hailuo.model.bean.MyHttpResponse
import com.huaxi.hailuo.model.bean.RefundInfoBean

/**
 * Created by admin on 2018/2/1.
 * 确认退款
 */
class ReFundContract{

    interface View:BaseView{

        fun loadInitComplete(data:RefundInfoBean?)

        fun finishActivity()

        fun confirmRefundComplete(data:ConfirRefundBean?)
        /**
         * 获取验证码完成
         */
        fun getVeryCodeResult()

    }

    interface Presenter:BasePresenter<ReFundContract.View>{

        fun loadInitData(order_id: String?, mCouponId: String)

        fun confirmRefund(order_id:String?,coupon_id:String,verify_code:String,channel_type:String,bank_mobile:String)
        /**
         * 获取验证码
         */
        fun getVeryCode(mobile:String,type:String)
    }
}