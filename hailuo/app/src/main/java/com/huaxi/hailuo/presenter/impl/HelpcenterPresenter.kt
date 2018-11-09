package com.huaxi.hailuo.presenter.impl

import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.base.RxPresenter
import com.huaxi.hailuo.model.bean.HelpCenterBean
import com.huaxi.hailuo.model.bean.MyHttpResponse
import com.huaxi.hailuo.model.http.ApiManager
import com.huaxi.hailuo.model.http.CommonSubscriber
import com.huaxi.hailuo.presenter.contract.HelpCenterContract
import com.huaxi.hailuo.util.RequsetUtil
import com.huaxi.hailuo.util.RxUtil
import com.huaxi.hailuo.util.SignUtils
import com.huaxi.hailuo.util.UserUtil
import org.json.JSONObject

/**
 * Created by admin on 2018/1/30.
 */
class HelpcenterPresenter : RxPresenter<HelpCenterContract.View>(), HelpCenterContract.Presenter {

    /**
     * 获取订单数据
     */
    override fun getData(type: String) {

        val jsonObject = JSONObject()
        jsonObject.put("user_id", UserUtil.getUserId(App.instance))
        jsonObject.put("type", type)
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.helpCenter(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<HelpCenterBean>>())
                .compose(RxUtil.handleResult<HelpCenterBean>())
                .subscribeWith(object : CommonSubscriber<HelpCenterBean>(mView) {
                    override fun onNext(data: HelpCenterBean?) {
                        mView?.processData(data)
                    }
                }))
    }
}