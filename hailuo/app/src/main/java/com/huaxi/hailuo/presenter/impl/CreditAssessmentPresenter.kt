package com.huaxi.hailuo.presenter.impl

import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.base.GlobalParams
import com.huaxi.hailuo.base.RxPresenter
import com.huaxi.hailuo.log.LogUtil
import com.huaxi.hailuo.model.bean.CreditAssessBean
import com.huaxi.hailuo.model.bean.LastSmsTimeBean
import com.huaxi.hailuo.model.bean.MyHttpResponse
import com.huaxi.hailuo.model.http.ApiManager
import com.huaxi.hailuo.model.http.CommonSubscriber
import com.huaxi.hailuo.presenter.contract.CreditAssessmentContract
import com.huaxi.hailuo.util.*
import org.json.JSONObject

/**
 * Created by admin on 2018/1/16.
 */
class CreditAssessmentPresenter : RxPresenter<CreditAssessmentContract.View>(), CreditAssessmentContract.Presenter {
    override fun submitPhoneInfo(data: JSONObject, deviceId: String) {
        var jsonObject = JSONObject(data.toString())
        jsonObject.put(GlobalParams.USER_ID, UserUtil.getUserId(App.instance))
        jsonObject.put("device_id", deviceId)
        val jsonObjectSigned = SignUtils.signJsonContainList(jsonObject, GlobalParams.USER_INFO_APP_LIST, GlobalParams.USER_INFO_MESSAGE_LIST)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.savePhoneInfo(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<Any>>())
                .compose(RxUtil.handleResult<Any>())
                .subscribeWith(object : CommonSubscriber<Any>(mView) {
                    override fun onNext(t: Any?) {
                        mView?.processSubmitPhoneInfoResult()
                    }

                }))

    }

    override fun getLastSmsTime(isFirst: Boolean) {
        val jsonObject = JSONObject()
        jsonObject.put(GlobalParams.USER_ID, UserUtil.getUserId(App.instance))
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.getLastSmsTime(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<LastSmsTimeBean>>())
                .compose(RxUtil.handleResult<LastSmsTimeBean>())
                .subscribeWith(object : CommonSubscriber<LastSmsTimeBean>(mView) {
                    override fun onNext(t: LastSmsTimeBean?) {
                        mView?.processLastSmsTime(t, isFirst)
                    }

                }))
    }

    var submitCreditResponse = true

    override fun submitCredit(choose_mobile: String, choose_memory: String) {
        if (!submitCreditResponse) {
            LogUtil.d("正在等待提交认证接口返回...")
            ToastUtil.showToast(App.instance, "正在提交认证，请稍等...")
            return
        }
        submitCreditResponse = false

        val jsonObject = JSONObject()
        jsonObject.put(GlobalParams.USER_ID, UserUtil.getUserId(App.instance))
        val root = Tools.isRoot()
        jsonObject.put("is_root", if (root) "1" else "2");
        val phoneName = Tools.getPhoneName()
        jsonObject.put("mobile_real_model", phoneName);
        val internalToatalSpace = Tools.getInternalToatalSpace(App.instance)
        jsonObject.put("mobile_real_memory", internalToatalSpace)
        jsonObject.put("mobile_model", choose_mobile)
        jsonObject.put("mobile_memory", choose_memory)
        val location = UserUtil.getLocation(App.instance)
        val province = UserUtil.getProvince(App.instance)
        val city = UserUtil.getCity(App.instance)
        val country = UserUtil.getCountry(App.instance)
        val address = UserUtil.getAddress(App.instance)
        jsonObject.put("location", location)
        jsonObject.put("province", province)
        jsonObject.put("city", city)
        jsonObject.put("country", country)
        jsonObject.put("address", address)
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.creditApply(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<Any>>())
                .compose(RxUtil.handleResult<Any>())
                .subscribeWith(object : CommonSubscriber<Any>(mView) {
                    override fun onNext(t: Any?) {
                        mView?.processSubmitCreditResult()
                        submitCreditResponse = true
                    }

                    override fun onError(e: Throwable?) {
                        super.onError(e)
                        submitCreditResponse = true
                    }
                }))
    }

    override fun getCreditAssessData(isNeedJump: Boolean, dialog: Boolean) {
        val jsonObject = JSONObject()
        jsonObject.put(GlobalParams.USER_ID, UserUtil.getUserId(App.instance))
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.creditAssess(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<CreditAssessBean>>())
                .compose(RxUtil.handleResult<CreditAssessBean>())
                .subscribeWith(object : CommonSubscriber<CreditAssessBean>(mView, isShowLoading = dialog) {
                    override fun onNext(t: CreditAssessBean?) {
                        mView?.getCreditAssessResult(t, isNeedJump)
                    }
                }))
    }


}