package com.huaxi.hailuo.presenter.impl

import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.base.GlobalParams
import com.huaxi.hailuo.base.RxPresenter
import com.huaxi.hailuo.log.LogUtil
import com.huaxi.hailuo.model.bean.*
import com.huaxi.hailuo.model.http.ApiManager
import com.huaxi.hailuo.model.http.CommonSubscriber
import com.huaxi.hailuo.presenter.contract.AssessSuccessContract
import com.huaxi.hailuo.util.*
import org.json.JSONObject

/**
 * Created by admin on 2018/1/16.
 */
class AssessSuccessPresenter : RxPresenter<AssessSuccessContract.View>(), AssessSuccessContract.Presenter {
    override fun getCheckOldUserStatus(channel_type: String, bank_mobile: String, verify_code: String) {
        val jsonObject = JSONObject()
        jsonObject.put(GlobalParams.USER_ID, UserUtil.getUserId(App.instance))
        jsonObject.put("channel_type", channel_type)
        jsonObject.put("bank_mobile", bank_mobile)
        jsonObject.put("verify_code", verify_code)
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)

        addDisposable(ApiManager.checkOldUserStatus(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<GetSmsCodeBean>>())
                .compose(RxUtil.handleResult<GetSmsCodeBean>())
                .subscribeWith(object : CommonSubscriber<GetSmsCodeBean>(mView) {
                    override fun onNext(t: GetSmsCodeBean?) {
                        mView?.getCheckOldUserStatusResult(t)
                    }
                }))
    }

    override fun getAssessSuccessData(mobile_model: String, mobile_memory: String) {

        val jsonObject = JSONObject()
        jsonObject.put(GlobalParams.USER_ID, UserUtil.getUserId(App.instance))
        jsonObject.put("mobile_model", mobile_model)
        jsonObject.put("mobile_memory", mobile_memory)
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)

        addDisposable(ApiManager.assessSuccess(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<AssessSuccessBean>>())
                .compose(RxUtil.handleResult<AssessSuccessBean>())
                .subscribeWith(object : CommonSubscriber<AssessSuccessBean>(mView, false) {
                    override fun onNext(t: AssessSuccessBean?) {
                        mView?.processAssessSuccessData(t)
                    }
                }))
    }

    var genOrderResponse = true

    override fun genOrder(mobile_model: String, mobile_memory: String, channel_type: String, bank_mobile: String) {
        if (!genOrderResponse) {
            LogUtil.e("等待生成订单接口返回")
            ToastUtil.showToast(App.instance, "生成订单中，请稍等...")
            return
        }
        genOrderResponse = false

        val jsonObject = JSONObject()
        jsonObject.put(GlobalParams.USER_ID, UserUtil.getUserId(App.instance))
        jsonObject.put("mobile_model", mobile_model)
        jsonObject.put("mobile_memory", mobile_memory)
        jsonObject.put("channel_type", channel_type)
        jsonObject.put("bank_mobile", bank_mobile)
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.genOrder(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<GenOrderBean>>())
                .compose(RxUtil.handleResult<GenOrderBean>())
                .subscribeWith(object : CommonSubscriber<GenOrderBean>(mView, true) {
                    override fun onNext(t: GenOrderBean?) {
                        mView?.processGenOrderResult(t)
                        genOrderResponse = true
                    }

                    override fun onError(e: Throwable?) {
                        super.onError(e)
                        genOrderResponse = true
                    }

                    override fun onComplete() {
                        super.onComplete()
                        mView?.processGenOrderFinish()
                    }
                }))
    }

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
                        mView?.getVeryCodeResult(data)
                    }
                }))
    }

}