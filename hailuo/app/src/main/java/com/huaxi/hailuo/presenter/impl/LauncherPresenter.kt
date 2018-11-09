package com.huaxi.hailuo.presenter.impl

import android.app.Activity
import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.base.RxPresenter
import com.huaxi.hailuo.log.LogUtil
import com.huaxi.hailuo.model.bean.MyHttpResponse
import com.huaxi.hailuo.model.bean.UpgradeBean
import com.huaxi.hailuo.model.http.ApiManager
import com.huaxi.hailuo.model.http.CommonSubscriber
import com.huaxi.hailuo.presenter.contract.LauncherContract
import com.huaxi.hailuo.util.*
import org.json.JSONObject

class LauncherPresenter : RxPresenter<LauncherContract.View>(), LauncherContract.Presenter {


    override fun checkUpdate() {
        val jsonObject = JSONObject()
        val device_id = UserUtil.getDeviceId()
        jsonObject.put("device_id", device_id)
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)

        addDisposable(ApiManager.checkUpgrade(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<UpgradeBean>>())
                .compose(RxUtil.handleResult<UpgradeBean>())
                .subscribeWith(object : CommonSubscriber<UpgradeBean>(mView) {
                    override fun onNext(data: UpgradeBean) {
                        mView?.checkUpdateResult(data)
                    }
                }))
    }

}