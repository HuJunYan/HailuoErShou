package com.huaxi.hailuo.presenter.impl

import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.base.RxPresenter
import com.huaxi.hailuo.model.bean.GetSmsCodeBean
import com.huaxi.hailuo.model.bean.InviteFriendBean
import com.huaxi.hailuo.model.bean.MyHttpResponse
import com.huaxi.hailuo.model.bean.SetPasswordBean
import com.huaxi.hailuo.model.http.ApiManager
import com.huaxi.hailuo.model.http.CommonSubscriber
import com.huaxi.hailuo.model.http.TianShenApiManager
import com.huaxi.hailuo.presenter.contract.ForgetPwdContract
import com.huaxi.hailuo.presenter.contract.InviteFriendsContract
import com.huaxi.hailuo.presenter.contract.SetPwdContract
import com.huaxi.hailuo.util.*
import org.json.JSONObject

/**
 * Created by admin on 2018/1/29.
 * 设置密码
 */
class InviteFriendsPresenter : RxPresenter<InviteFriendsContract.View>(), InviteFriendsContract.Presenter {
    //邀请好友的埋点
    override fun inviteFriendsBuridPoint(flag: String, result: String) {
        val jsonObject = JSONObject()
        jsonObject.put("flag", flag)
        jsonObject.put("result", result)
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.buriedPoint(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<Any>>())
                .compose(RxUtil.handleResult<Any>())
                .subscribeWith(object : CommonSubscriber<Any>(mView) {
                    override fun onNext(data: Any?) {
                        mView?.inviteFriendsBuridPointResult(data)
                    }
                }))
    }

    override fun inviteFriends() {
        val jsonObject = JSONObject()
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.getInvitaionFriends(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<InviteFriendBean>>())
                .compose(RxUtil.handleResult<InviteFriendBean>())
                .subscribeWith(object : CommonSubscriber<InviteFriendBean>(mView) {
                    override fun onNext(data: InviteFriendBean?) {
                        mView?.inviteFriendsCompelete(data)
                    }
                }))
    }


}