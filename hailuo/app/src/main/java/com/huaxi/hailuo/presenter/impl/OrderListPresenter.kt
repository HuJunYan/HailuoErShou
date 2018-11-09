package com.huaxi.hailuo.presenter.impl

import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.base.RxPresenter
import com.huaxi.hailuo.model.bean.MyHttpResponse
import com.huaxi.hailuo.model.bean.OrderListBean
import com.huaxi.hailuo.model.http.ApiManager
import com.huaxi.hailuo.model.http.CommonSubscriber
import com.huaxi.hailuo.presenter.contract.OrderListContract
import com.huaxi.hailuo.util.RequsetUtil
import com.huaxi.hailuo.util.RxUtil
import com.huaxi.hailuo.util.SignUtils
import com.huaxi.hailuo.util.UserUtil
import org.json.JSONObject

/**
 * Created by admin on 2018/1/30.
 */
class OrderListPresenter :RxPresenter<OrderListContract.View>(),OrderListContract.Presenter{
    override fun loadData(now_page: String, page_size: String) {

        val jsonObject = JSONObject()
        jsonObject.put("user_id", UserUtil.getUserId(App.instance))
        jsonObject.put("now_page", now_page)
        jsonObject.put("page_size",page_size)
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)

        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.getMyOrderData(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<OrderListBean>>())
                .compose(RxUtil.handleResult<OrderListBean>())
                .subscribeWith(object : CommonSubscriber<OrderListBean>(mView) {
                    override fun onNext(data: OrderListBean?) {
                        mView?.loadDataComplete(data)
                    }

                    override fun onComplete() {
                        super.onComplete()
                    }
                }))
    }


}