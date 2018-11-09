package com.huaxi.hailuo.presenter.impl

import android.text.TextUtils
import cn.jpush.android.api.JPushInterface
import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.base.RxPresenter
import com.huaxi.hailuo.model.bean.*
import com.huaxi.hailuo.model.http.ApiManager
import com.huaxi.hailuo.model.http.CommonSubscriber
import com.huaxi.hailuo.presenter.contract.LoginConract
import com.huaxi.hailuo.util.*
import org.json.JSONObject

/**
 * Created by admin on 2017/12/28.
 * 登录逻辑操作
 */
class LoginPresenter : RxPresenter<LoginConract.View>(), LoginConract.Presenter {
    override fun signUpAgreement() {

        val jsonObject = JSONObject()
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.signUpAgreement(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<SignUpAgreementBean>>())
                .compose(RxUtil.handleResult<SignUpAgreementBean>())
                .subscribeWith(object : CommonSubscriber<SignUpAgreementBean>(mView) {
                    override fun onNext(data: SignUpAgreementBean?) {
                        mView?.signUpAgreementResult(data!!)
                    }

                }))

    }

    override fun login(phone: String, password: String, code: String, type: Int) {

        val channel_id = Utils.getChannelId()

        val jsonObject = JSONObject()
        jsonObject.put("mobile", phone)
        jsonObject.put("password", password)
        jsonObject.put("code", code)
        jsonObject.put("type", type)
        jsonObject.put("channel_id", channel_id)
        var jpushId = UserUtil.getUserJPushId(App.instance)
        if (TextUtils.isEmpty(jpushId)) {
            jpushId = JPushInterface.getRegistrationID(App.instance)
        }
        jsonObject.put("push_id", jpushId)
        EncryptUtil.encryptPassword(jsonObject)
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.signIn(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<LoginBean>>())
                .compose(RxUtil.handleResult<LoginBean>())
                .subscribeWith(object : CommonSubscriber<LoginBean>(mView,true) {
                    override fun onNext(data: LoginBean?) {
                        UserUtil.saveUserJPushId(App.instance, jpushId)
                        mView?.loginCompelete(data!!)
                    }

                    override fun onComplete() {
                        super.onComplete()
                        mView?.finishActivity()
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
                        mView?.getVeryCodeResult(data)
                    }
                }))


    }

    /**
     * 注册
     * 不要问我为啥写这里，因为。。。
     */
    override fun regist(mobile: String, code: String, password: String) {

        val channel_id = Utils.getChannelId()

        val jsonObject = JSONObject()

        jsonObject.put("mobile", mobile)
        jsonObject.put("code", code)
        jsonObject.put("password", password)
        jsonObject.put("channel_id", channel_id)
        EncryptUtil.encryptPassword(jsonObject)
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.signUp(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<RegistLoginBean>>())
                .compose(RxUtil.handleResult<RegistLoginBean>())
                .subscribeWith(object : CommonSubscriber<RegistLoginBean>(mView!!) {
                    override fun onNext(data: RegistLoginBean?) {
                        mView?.registComplete(data)

                    }

                    override fun onComplete() {
                        super.onComplete()
                        mView?.finishActivity()
                    }
                }))
    }

    /**
     * 更改密码
     */
    override fun changePwd(mobile: String, code: String, new_password: String) {

        val jsonObject = JSONObject()
        jsonObject.put("user_id", UserUtil.getUserId(App.instance))
        jsonObject.put("mobile", mobile)
        jsonObject.put("code", code)
        jsonObject.put("new_password", new_password)
        EncryptUtil.encryptPassword(jsonObject)
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.reSetpwd(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<Any>>())
                .compose(RxUtil.handleResult<Any>())
                .subscribeWith(object : CommonSubscriber<Any>(mView!!) {
                    override fun onNext(data: Any?) {
                        mView?.changeComplete()
                    }

                    override fun onComplete() {
                        super.onComplete()
                        mView?.finishActivity()
                    }
                }))
    }


}
