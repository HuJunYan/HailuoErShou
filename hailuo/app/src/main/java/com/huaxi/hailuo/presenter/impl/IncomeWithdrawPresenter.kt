package com.huaxi.hailuo.presenter.impl

import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.base.RxPresenter
import com.huaxi.hailuo.model.bean.*
import com.huaxi.hailuo.model.http.ApiManager
import com.huaxi.hailuo.model.http.CommonSubscriber
import com.huaxi.hailuo.presenter.contract.*
import com.huaxi.hailuo.util.*
import org.json.JSONObject

/**
 * Created by admin on 2018/1/29.
 * 设置密码
 */
class IncomeWithdrawPresenter : RxPresenter<IncomeWithdrawContract.View>(), IncomeWithdrawContract.Presenter {
    override fun myCashRedPacketMoneyList(type: String, current_page: String, page_size: String,isRefresh:Boolean) {
        val jsonObject = JSONObject()
        jsonObject.put("type", type)
        jsonObject.put("current_page", current_page)
        jsonObject.put("page_size", page_size)
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.getMyRedPacketMoneyList(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<IncomeDetailOrderBean>>())
                .compose(RxUtil.handleResult<IncomeDetailOrderBean>())
                .subscribeWith(object : CommonSubscriber<IncomeDetailOrderBean>(mView) {
                    override fun onNext(data: IncomeDetailOrderBean?) {
                        mView?.myCashRedPacketMoneyListResult(data,isRefresh)
                    }
                }))
    }


}