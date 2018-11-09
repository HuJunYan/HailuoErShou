package com.huaxi.hailuo.presenter.impl

import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.base.RxPresenter
import com.huaxi.hailuo.model.bean.MyHttpResponse
import com.huaxi.hailuo.model.bean.SetPasswordBean
import com.huaxi.hailuo.model.bean.UseCoupon
import com.huaxi.hailuo.model.http.ApiManager
import com.huaxi.hailuo.model.http.CommonSubscriber
import com.huaxi.hailuo.presenter.contract.UseCouponContract
import com.huaxi.hailuo.util.*
import org.json.JSONObject

/**
 * Created by admin on 2018/4/3.
 */
class UseCouponPresenter:RxPresenter<UseCouponContract.View>(),UseCouponContract.Presenter{

    override fun loadData(order_id: String?) {

        val jsonObject = JSONObject()
        jsonObject.put("order_id", order_id)
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.getUseCoupon(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<UseCoupon>>())
                .compose(RxUtil.handleResult<UseCoupon>())
                .subscribeWith(object : CommonSubscriber<UseCoupon>(mView) {
                    override fun onNext(data: UseCoupon?) {
                        mView?.loadDataComplete(data)
                    }

                    override fun onComplete() {
                        super.onComplete()
                    }
                }))
    }


}