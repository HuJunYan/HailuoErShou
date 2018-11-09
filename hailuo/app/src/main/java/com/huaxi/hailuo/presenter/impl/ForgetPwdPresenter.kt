package com.huaxi.hailuo.presenter.impl

import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.base.RxPresenter
import com.huaxi.hailuo.model.bean.GetSmsCodeBean
import com.huaxi.hailuo.model.bean.MyHttpResponse
import com.huaxi.hailuo.model.bean.SetPasswordBean
import com.huaxi.hailuo.model.http.ApiManager
import com.huaxi.hailuo.model.http.CommonSubscriber
import com.huaxi.hailuo.presenter.contract.ForgetPwdContract
import com.huaxi.hailuo.presenter.contract.SetPwdContract
import com.huaxi.hailuo.util.*
import org.json.JSONObject

/**
 * Created by admin on 2018/1/29.
 * 设置密码
 */
class ForgetPwdPresenter : RxPresenter<ForgetPwdContract.View>(), ForgetPwdContract.Presenter {


    /**
     * 更改密码
     */
    override fun changePwd(mobile: String, code: String, new_password: String) {

        val jsonObject = JSONObject()
        jsonObject.put("mobile", mobile)
        jsonObject.put("code", code)
        jsonObject.put("new_password", new_password)
        EncryptUtil.encryptPassword(jsonObject)
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.forgetPwd(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<Any>>())
                .compose(RxUtil.handleResult<Any>())
                .subscribeWith(object : CommonSubscriber<Any>(mView) {
                    override fun onNext(data: Any?) {
                    }

                    override fun onComplete() {
                        super.onComplete()
                        mView?.changeComplete()
                    }
                }))
    }

    override fun getVeryCode(mobile: String, type: String) {

        val jsonObject = JSONObject()

        jsonObject.put("mobile", mobile)
        jsonObject.put("type", type)
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.sendCode(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<Any>>())
                .compose(RxUtil.handleResult<Any>())
                .subscribeWith(object : CommonSubscriber<Any>(mView) {
                    override fun onNext(data: Any?) {
                        mView?.getVeryCodeResult(data!!)
                    }
                }))


    }
}