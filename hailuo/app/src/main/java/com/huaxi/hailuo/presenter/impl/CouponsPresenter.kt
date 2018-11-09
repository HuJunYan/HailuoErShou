package com.huaxi.hailuo.presenter.impl

import com.huaxi.hailuo.base.RxPresenter
import com.huaxi.hailuo.model.bean.CheckUseCouponBean
import com.huaxi.hailuo.model.bean.CouponListBean
import com.huaxi.hailuo.model.bean.MyHttpResponse
import com.huaxi.hailuo.model.http.ApiManager
import com.huaxi.hailuo.model.http.CommonSubscriber
import com.huaxi.hailuo.presenter.contract.CouponsContract
import com.huaxi.hailuo.util.RequsetUtil
import com.huaxi.hailuo.util.RxUtil
import com.huaxi.hailuo.util.SignUtils
import org.json.JSONObject

class CouponsPresenter : RxPresenter<CouponsContract.View>(), CouponsContract.Presenter {
    override fun getCouponList(type: Int, size: Int, page: Int, isRefresh: Boolean) {

        val jsonObject = JSONObject()
        jsonObject.put("page", page.toString())
        jsonObject.put("size", size.toString())
        jsonObject.put("type", type.toString())
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)

        addDisposable(ApiManager.getCouponList(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<CouponListBean>>())
                .compose(RxUtil.handleResult<CouponListBean>())
                .subscribeWith(object : CommonSubscriber<CouponListBean>(mView) {
                    override fun onNext(t: CouponListBean?) {
                        mView?.processGetCouponListData(t, isRefresh)
                    }
                }))
    }

    override fun checkUseCoupon(coupon_id: String) {
        val jsonObject = JSONObject()
        jsonObject.put("coupon_id", coupon_id)
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)

        addDisposable(ApiManager.checkUseCoupon(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<CheckUseCouponBean>>())
                .compose(RxUtil.handleResult<CheckUseCouponBean>())
                .subscribeWith(object : CommonSubscriber<CheckUseCouponBean>(mView) {
                    override fun onNext(t: CheckUseCouponBean?) {
                        mView?.processCheckUseCoupon(t,coupon_id)
                    }
                }))
    }


}