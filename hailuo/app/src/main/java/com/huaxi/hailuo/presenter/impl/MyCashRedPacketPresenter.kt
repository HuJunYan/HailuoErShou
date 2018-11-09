package com.huaxi.hailuo.presenter.impl

import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.base.RxPresenter
import com.huaxi.hailuo.model.bean.*
import com.huaxi.hailuo.model.http.ApiManager
import com.huaxi.hailuo.model.http.CommonSubscriber
import com.huaxi.hailuo.presenter.contract.ForgetPwdContract
import com.huaxi.hailuo.presenter.contract.InviteFriendsContract
import com.huaxi.hailuo.presenter.contract.MyCashRedPacketContract
import com.huaxi.hailuo.presenter.contract.SetPwdContract
import com.huaxi.hailuo.util.*
import org.json.JSONObject

/**
 * Created by admin on 2018/1/29.
 * 设置密码
 */
class MyCashRedPacketPresenter : RxPresenter<MyCashRedPacketContract.View>(), MyCashRedPacketContract.Presenter {
    //现金红包提现
    override fun myCashWithdraw(withdraw_money: String, code: String) {
        val jsonObject = JSONObject()
        jsonObject.put("withdraw_money", withdraw_money)
        jsonObject.put("code", code)
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.get_withdraw(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<Any>>())
                .compose(RxUtil.handleResult<Any>())
                .subscribeWith(object : CommonSubscriber<Any>(mView) {
                    override fun onNext(data: Any?) {
                        mView?.myCashWithdrawResult(data)
                    }
                }))
    }

    override fun sendCodeToRedPacket(mobile: String, type: String) {
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
                        mView?.sendCodeToRedPacketResult(data)
                    }
                }))
    }

    override fun myCashRedPacket() {
        val jsonObject = JSONObject()

        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.getMyRedPacket(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<CashRedPacketBean>>())
                .compose(RxUtil.handleResult<CashRedPacketBean>())
                .subscribeWith(object : CommonSubscriber<CashRedPacketBean>(mView) {
                    override fun onNext(data: CashRedPacketBean?) {
                        mView?.myCashRedPacketCompelete(data)
                    }
                }))
    }


}