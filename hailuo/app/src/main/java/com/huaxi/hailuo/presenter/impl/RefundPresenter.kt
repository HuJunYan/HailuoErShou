package com.huaxi.hailuo.presenter.impl

import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.base.RxPresenter
import com.huaxi.hailuo.log.LogUtil
import com.huaxi.hailuo.model.bean.ConfirRefundBean
import com.huaxi.hailuo.model.bean.MyHttpResponse
import com.huaxi.hailuo.model.bean.RefundInfoBean
import com.huaxi.hailuo.model.bean.SmsBean
import com.huaxi.hailuo.model.http.ApiManager
import com.huaxi.hailuo.model.http.CommonSubscriber
import com.huaxi.hailuo.presenter.contract.ReFundContract
import com.huaxi.hailuo.util.*
import org.json.JSONObject

/**
 * Created by admin on 2018/2/1.
 * 退款presenter
 */
class RefundPresenter : RxPresenter<ReFundContract.View>(), ReFundContract.Presenter {
    override fun getVeryCode(mobile: String, type: String) {
        val jsonObject = JSONObject()

        jsonObject.put("mobile", mobile)
        jsonObject.put("type", type)
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.sendSmsCode(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<SmsBean>>())
                .compose(RxUtil.handleResult<SmsBean>())
                .subscribeWith(object : CommonSubscriber<SmsBean>(mView) {
                    override fun onNext(data: SmsBean?) {
                        mView?.getVeryCodeResult()
                    }
                }))
    }

    override fun loadInitData(order_id: String?, mCouponId: String) {
        val jsonObject = JSONObject()
        jsonObject.put("user_id", UserUtil.getUserId(App.instance))
        jsonObject.put("order_id", order_id)
        jsonObject.put("coupon_id", mCouponId)

        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.getRefundInfo(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<RefundInfoBean>>())
                .compose(RxUtil.handleResult<RefundInfoBean>())
                .subscribeWith(object : CommonSubscriber<RefundInfoBean>(mView) {
                    override fun onNext(data: RefundInfoBean?) {
                        mView?.loadInitComplete(data!!)
                    }

                    override fun onComplete() {
                        super.onComplete()
                        mView?.finishActivity()
                    }
                }))

    }

    var confirmRefundResponse = true

    override fun confirmRefund(order_id: String?, coupon_id: String, verify_code: String, channel_type: String, bank_mobile: String) {
        if (!confirmRefundResponse) {
            LogUtil.e("等待还款接口返回")
            ToastUtil.showToast(App.instance, "退款中，请稍等...")
            return
        }
        confirmRefundResponse = false

        val jsonObject = JSONObject()
        jsonObject.put("user_id", UserUtil.getUserId(App.instance))
        jsonObject.put("order_id", order_id)
        jsonObject.put("coupon_id", coupon_id)
        jsonObject.put("verify_code", verify_code)
        jsonObject.put("channel_type", channel_type)
        jsonObject.put("bank_mobile", bank_mobile)
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.confirmRefund(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<ConfirRefundBean>>())
                .compose(RxUtil.handleResult<ConfirRefundBean>())
                .subscribeWith(object : CommonSubscriber<ConfirRefundBean>(mView, true) {
                    override fun onNext(data: ConfirRefundBean?) {
                        mView?.confirmRefundComplete(data)
                        confirmRefundResponse = true
                    }

                    override fun onError(e: Throwable?) {
                        super.onError(e)
                        confirmRefundResponse = true
                    }
                }))

    }

}