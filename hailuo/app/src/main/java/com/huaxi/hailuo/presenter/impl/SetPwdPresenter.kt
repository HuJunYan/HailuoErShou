package com.huaxi.hailuo.presenter.impl

import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.base.RxPresenter
import com.huaxi.hailuo.model.bean.MyHttpResponse
import com.huaxi.hailuo.model.bean.SetPasswordBean
import com.huaxi.hailuo.model.http.ApiManager
import com.huaxi.hailuo.model.http.CommonSubscriber
import com.huaxi.hailuo.presenter.contract.SetPwdContract
import com.huaxi.hailuo.util.*
import org.json.JSONObject

/**
 * Created by admin on 2018/1/29.
 * 设置密码
 */
class SetPwdPresenter : RxPresenter<SetPwdContract.View>(), SetPwdContract.Presenter {
    override fun setPwd(pwd: String, confirmPwd: String) {

        val jsonObject = JSONObject()
        jsonObject.put("user_id", UserUtil.getUserId(App.instance))
        jsonObject.put("password", pwd)
        jsonObject.put("confirm_password", confirmPwd)
        EncryptUtil.encryptTwoPassword(jsonObject)
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.setPassword(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<SetPasswordBean>>())
                .compose(RxUtil.handleResult<SetPasswordBean>())
                .subscribeWith(object : CommonSubscriber<SetPasswordBean>(mView) {
                    override fun onNext(data: SetPasswordBean?) {
                        mView?.setPwdComplete(data)
                    }

                    override fun onComplete() {
                        super.onComplete()
                        mView?.finishActivity()
                    }
                }))
    }

}