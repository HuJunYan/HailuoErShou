package com.huaxi.hailuo.presenter.impl

import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.base.RxPresenter
import com.huaxi.hailuo.model.bean.AssessBean
import com.huaxi.hailuo.model.bean.HomePageBean
import com.huaxi.hailuo.model.bean.MobileListBean
import com.huaxi.hailuo.model.bean.MyHttpResponse
import com.huaxi.hailuo.model.http.ApiManager
import com.huaxi.hailuo.model.http.CommonSubscriber
import com.huaxi.hailuo.presenter.contract.HomeContract
import com.huaxi.hailuo.presenter.contract.VerifyHomeContract
import com.huaxi.hailuo.util.*
import org.json.JSONObject

class VerifyHomePresenter : RxPresenter<VerifyHomeContract.View>(), VerifyHomeContract.Presenter {

    override fun getHomeData(isNeedShowDialog: Boolean) {
        val jsonObject = JSONObject()

        jsonObject.put("user_id", UserUtil.getUserId(App.instance))
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.homePage(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<HomePageBean>>())
                .compose(RxUtil.handleResult<HomePageBean>())
                .subscribeWith(object : CommonSubscriber<HomePageBean>(mView) {
                    override fun onNext(data: HomePageBean?) {
                        mView?.processHomeData(data, true)
                    }
                }))

    }

    /**
     * 评估
     */
    override fun assess(mobile_model: String, mobile_memory: String) {
        val jsonObject = JSONObject()
        jsonObject.put("user_id", UserUtil.getUserId(App.instance))
        jsonObject.put("mobile_model", mobile_model)
        jsonObject.put("mobile_memory", mobile_memory)

        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.assess(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<AssessBean>>())
                .compose(RxUtil.handleResult<AssessBean>())
                .subscribeWith(object : CommonSubscriber<AssessBean>(mView!!) {
                    override fun onNext(data: AssessBean?) {
                        mView?.assessComplete(data!!)
                    }
                }))

    }

    override fun upLoadLocation() {
        val jsonObject = JSONObject()
        jsonObject.put("user_id", UserUtil.getUserId(App.instance))
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
        addDisposable(ApiManager.userLocation(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<Any>>())
                .compose(RxUtil.handleResult<Any>())
                .subscribeWith(object : CommonSubscriber<Any>(mView!!, false) {
                    override fun onNext(data: Any?) {
                    }
                }))
    }

    override fun getMobileList() {
        val jsonObject = JSONObject()
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.getMobileList(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<MobileListBean>>())
                .compose(RxUtil.handleResult<MobileListBean>())
                .subscribeWith(object : CommonSubscriber<MobileListBean>(mView!!) {
                    override fun onNext(data: MobileListBean?) {
                        mView?.processMobileList(data)
                    }
                }))

    }

}