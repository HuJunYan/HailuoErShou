package com.huaxi.hailuo.presenter.impl

import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.base.GlobalParams
import com.huaxi.hailuo.base.RxPresenter
import com.huaxi.hailuo.model.bean.ArticalDetailBean
import com.huaxi.hailuo.model.bean.GetSmsCodeBean
import com.huaxi.hailuo.model.bean.MyHttpResponse
import com.huaxi.hailuo.model.bean.SetPasswordBean
import com.huaxi.hailuo.model.http.ApiManager
import com.huaxi.hailuo.model.http.CommonSubscriber
import com.huaxi.hailuo.presenter.contract.*
import com.huaxi.hailuo.util.*
import org.json.JSONObject

/**
 * Created by admin on 2018/1/29.
 * 消息详情
 */
class ArticalDetailPresenter : RxPresenter<ArticleDetailContract.View>(), ArticleDetailContract.Presenter {
    override fun getArticalData(msg_id: String) {
        val jsonObject = JSONObject()
        jsonObject.put(GlobalParams.USER_ID, UserUtil.getUserId(App.instance))
        jsonObject.put("msg_id", msg_id)
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)

        addDisposable(ApiManager.getMsgInfo(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<ArticalDetailBean>>())
                .compose(RxUtil.handleResult<ArticalDetailBean>())
                .subscribeWith(object : CommonSubscriber<ArticalDetailBean>(mView) {
                    override fun onNext(t: ArticalDetailBean?) {
                        mView?.getArticalResult(t)
                    }
                }))
    }


}