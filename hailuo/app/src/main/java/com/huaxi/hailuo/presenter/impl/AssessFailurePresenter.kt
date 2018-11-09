package com.huaxi.hailuo.presenter.impl

import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.base.GlobalParams
import com.huaxi.hailuo.base.RxPresenter
import com.huaxi.hailuo.model.bean.DownRefreshBean
import com.huaxi.hailuo.model.bean.MyHttpResponse
import com.huaxi.hailuo.model.http.ApiManager
import com.huaxi.hailuo.model.http.CommonSubscriber
import com.huaxi.hailuo.presenter.contract.AssessFailureContract
import com.huaxi.hailuo.util.RequsetUtil
import com.huaxi.hailuo.util.RxUtil
import com.huaxi.hailuo.util.SignUtils
import com.huaxi.hailuo.util.UserUtil
import org.json.JSONObject

/**
 * Created by admin on 2018/1/16.
 */
class AssessFailurePresenter : RxPresenter<AssessFailureContract.View>(), AssessFailureContract.Presenter {
    //评估失败页面导流埋点
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

    override fun getRefreshData(mobile_model: String, mobile_memory: String) {
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
                        mView?.processSuccessResult(t)
                    }


                }))
    }

    override fun getData(mobile_model: String, mobile_memory: String) {
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
                }))
    }





}