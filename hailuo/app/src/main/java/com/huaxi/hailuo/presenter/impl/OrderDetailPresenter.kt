package com.huaxi.hailuo.presenter.impl

import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.base.RxPresenter
import com.huaxi.hailuo.model.bean.MyHttpResponse
import com.huaxi.hailuo.model.bean.OrderDetailBean
import com.huaxi.hailuo.model.http.ApiManager
import com.huaxi.hailuo.model.http.CommonSubscriber
import com.huaxi.hailuo.presenter.contract.OrderDetailContract
import com.huaxi.hailuo.util.RequsetUtil
import com.huaxi.hailuo.util.RxUtil
import com.huaxi.hailuo.util.SignUtils
import com.huaxi.hailuo.util.UserUtil
import org.json.JSONObject

/**
 * Created by admin on 2018/2/1.
 */
class OrderDetailPresenter:RxPresenter<OrderDetailContract.View>(),OrderDetailContract.Presenter{
    override fun getOrderDetail(order_id: String?) {

        val jsonObject = JSONObject()
        jsonObject.put("user_id", UserUtil.getUserId(App.instance))
        jsonObject.put("order_id", order_id)
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)

        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.getOrderDetail(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<OrderDetailBean>>())
                .compose(RxUtil.handleResult<OrderDetailBean>())
                .subscribeWith(object : CommonSubscriber<OrderDetailBean>(mView) {
                    override fun onNext(data: OrderDetailBean?) {
                        mView?.getOrderDetailResult(data)
                    }
                }))
    }

}