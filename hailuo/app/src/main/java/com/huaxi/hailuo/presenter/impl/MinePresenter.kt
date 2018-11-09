package com.huaxi.hailuo.presenter.impl

import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.base.RxPresenter
import com.huaxi.hailuo.model.bean.MineBean
import com.huaxi.hailuo.model.bean.MyHttpResponse
import com.huaxi.hailuo.model.http.ApiManager
import com.huaxi.hailuo.model.http.CommonSubscriber
import com.huaxi.hailuo.presenter.contract.MineContract
import com.huaxi.hailuo.util.RequsetUtil
import com.huaxi.hailuo.util.RxUtil
import com.huaxi.hailuo.util.SignUtils
import com.huaxi.hailuo.util.UserUtil
import org.json.JSONObject

class MinePresenter : RxPresenter<MineContract.View>(), MineContract.Presenter {

    override fun exitLogin() {


    }

    override fun loadMine() {
        val jsonObject = JSONObject()
        jsonObject.put("user_id", if (UserUtil.isLogin(App.instance)) UserUtil.getUserId(App.instance) else "")
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.getMineUrl(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<MineBean>>())
                .compose(RxUtil.handleResult<MineBean>())
                .subscribeWith(object : CommonSubscriber<MineBean>(mView) {
                    override fun onNext(data: MineBean?) {
                        mView?.showMine(data!!)
                    }
                }))

    }

    override fun checkUserConfig() {

    }

}