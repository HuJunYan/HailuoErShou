package com.huaxi.hailuo.presenter.impl

import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.base.GlobalParams
import com.huaxi.hailuo.base.RxPresenter
import com.huaxi.hailuo.model.bean.DownRefreshBean
import com.huaxi.hailuo.model.bean.MyHttpResponse
import com.huaxi.hailuo.model.http.ApiManager
import com.huaxi.hailuo.model.http.CommonSubscriber
import com.huaxi.hailuo.presenter.contract.PhoneAssessContract
import com.huaxi.hailuo.util.*
import org.json.JSONObject

/**
 * Created by admin on 2018/1/16.
 */
class PhoneAssessPresenter : RxPresenter<PhoneAssessContract.View>(), PhoneAssessContract.Presenter {
    //导流链接的埋点
    override fun daoliuBuriedPoint(flag: String, result: String) {
        val jsonObject = JSONObject()
        jsonObject.put("flag", flag)
        jsonObject.put("result", result)
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.buriedPoint(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<Any>>())
                .compose(RxUtil.handleResult<Any>())
                .subscribeWith(object : CommonSubscriber<Any>(mView) {
                    override fun onNext(data: Any?) {

                    }
                }))
    }


    override fun submitCredit(choose_mobile: String, choose_memory: String) {
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

                    }

                    override fun onComplete() {
                        super.onComplete()
                        mView?.processSubmitCreditResult()
                    }
                }))
    }

    override fun getRiskWaitingFeedback() {
        val jsonObject = JSONObject()
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)

        addDisposable(ApiManager.riskWaitingFeedback(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<Any>>())
                .compose(RxUtil.handleResult<Any>())
                .subscribeWith(object : CommonSubscriber<Any>(mView) {
                    override fun onNext(t: Any?) {
                        mView?.riskWaitingFeedbackResult(t)
                    }


                }))
    }

    override fun getRefreshData(mobile_model: String, mobile_memory: String) {
        if (mView == null) {
            return
        }
        val jsonObject = JSONObject()
        jsonObject.put(GlobalParams.USER_ID, UserUtil.getUserId(App.instance))
        jsonObject.put("mobile_model", mobile_model)
        jsonObject.put("mobile_memory", mobile_memory)
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)

        addDisposable(ApiManager.downRefresh(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<DownRefreshBean>>())
                .compose(RxUtil.handleResult<DownRefreshBean>())
                .subscribeWith(object : CommonSubscriber<DownRefreshBean>(mView) {
                    override fun onNext(t: DownRefreshBean?) {
                        mView?.processData(t)
                    }

                    override fun onComplete() {
                        super.onComplete()
                        mView?.processSuccessData()
                    }
                }))
    }

}