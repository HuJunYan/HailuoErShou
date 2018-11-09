package com.huaxi.hailuo.presenter.impl

import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.base.RxPresenter
import com.huaxi.hailuo.model.bean.MyBankListBean
import com.huaxi.hailuo.model.bean.MyHttpResponse
import com.huaxi.hailuo.model.http.ApiManager
import com.huaxi.hailuo.model.http.CommonSubscriber
import com.huaxi.hailuo.presenter.contract.MyBankListContract
import com.huaxi.hailuo.util.RequsetUtil
import com.huaxi.hailuo.util.RxUtil
import com.huaxi.hailuo.util.SignUtils
import com.huaxi.hailuo.util.UserUtil
import org.json.JSONObject

/**
 * Created by admin on 2018/1/31.
 */
class MyBankListPresenter : RxPresenter<MyBankListContract.View>(), MyBankListContract.Presenter {

    override fun loadData() {
        val jsonObject = JSONObject()
        jsonObject.put("user_id", UserUtil.getUserId(App.instance))
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)

        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.getUserBankList(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<MyBankListBean>>())
                .compose(RxUtil.handleResult<MyBankListBean>())
                .subscribeWith(object : CommonSubscriber<MyBankListBean>(mView) {
                    override fun onNext(data: MyBankListBean?) {
                        if (data != null) {
                            mView?.loadDataComplete(data)
                        }
                    }

                    override fun onComplete() {
                        super.onComplete()
                    }
                }))
    }

    override fun deleteCard(cardId: String?) {
        val jsonObject = JSONObject()
        jsonObject.put("user_id", UserUtil.getUserId(App.instance))
        jsonObject.put("bank_id", cardId)
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)

        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.deleteUserBankList(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<Any>>())
                .compose(RxUtil.handleResult<Any>())
                .subscribeWith(object : CommonSubscriber<Any>(mView) {
                    override fun onNext(data: Any?) {
                        mView?.deleteCardComplete()
                    }

                    override fun onComplete() {
                        super.onComplete()
                    }
                }))

    }

}