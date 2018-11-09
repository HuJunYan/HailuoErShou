package com.huaxi.hailuo.presenter.impl

import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.base.GlobalParams
import com.huaxi.hailuo.base.RxPresenter
import com.huaxi.hailuo.model.bean.*
import com.huaxi.hailuo.model.http.ApiManager
import com.huaxi.hailuo.model.http.CommonSubscriber
import com.huaxi.hailuo.presenter.contract.BindbankCardContract
import com.huaxi.hailuo.util.RequsetUtil
import com.huaxi.hailuo.util.RxUtil
import com.huaxi.hailuo.util.SignUtils
import com.huaxi.hailuo.util.UserUtil
import org.json.JSONObject

/**
 * Created by admin on 2018/1/16.
 */
class BindBankCardPresenter : RxPresenter<BindbankCardContract.View>(), BindbankCardContract.Presenter {
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

    override fun getUserInfo() {
        val jsonObject = JSONObject()
        jsonObject.put(GlobalParams.USER_ID, UserUtil.getUserId(App.instance))
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.getUserInfo(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<UserInfoBean>>())
                .compose(RxUtil.handleResult<UserInfoBean>())
                .subscribeWith(object : CommonSubscriber<UserInfoBean>(mView) {
                    override fun onNext(data: UserInfoBean?) {
                        mView?.getUserInfoResult(data)
                    }
                }))
    }

    override fun getBankListData() {

        val jsonObject = JSONObject()
        jsonObject.put(GlobalParams.USER_ID, UserUtil.getUserId(App.instance))
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)

        addDisposable(ApiManager.getBankList(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<BankListBean>>())
                .compose(RxUtil.handleResult<BankListBean>())
                .subscribeWith(object : CommonSubscriber<BankListBean>(mView) {
                    override fun onNext(t: BankListBean?) {
                        mView?.getBankListDataResult(t)
                    }
                }))
    }

    override fun getProvinceData() {

        val jsonObject = JSONObject()
        jsonObject.put(GlobalParams.USER_ID, UserUtil.getUserId(App.instance))
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.getProvince(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<ProvinceBean>>())
                .compose(RxUtil.handleResult<ProvinceBean>())
                .subscribeWith(object : CommonSubscriber<ProvinceBean>(mView) {
                    override fun onNext(data: ProvinceBean?) {
                        mView?.getProviceResult(data)
                    }
                }))
    }

    override fun getCityData(provinceId: String?) {
        val jsonObject = JSONObject()
        jsonObject.put(GlobalParams.USER_ID, UserUtil.getUserId(App.instance))
        jsonObject.put("provinceId", provinceId)
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.getCity(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<CityBean>>())
                .compose(RxUtil.handleResult<CityBean>())
                .subscribeWith(object : CommonSubscriber<CityBean>(mView) {
                    override fun onNext(data: CityBean?) {
                        mView?.getCityResult(data)
                    }
                }))
    }

    override fun getVefiryCode(reserved_mobile: String) {

        val jsonObject = JSONObject()

        jsonObject.put("mobile", reserved_mobile)
        jsonObject.put("type", "3")
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.sendCode(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<Any>>())
                .compose(RxUtil.handleResult<Any>())
                .subscribeWith(object : CommonSubscriber<Any>(mView) {
                    override fun onNext(data: Any?) {
                        mView?.getVefiryCodeResult(true)
                    }

                    override fun onError(e: Throwable?) {
                        super.onError(e)
                        mView?.getVefiryCodeResult(false)
                    }
                }))
    }


    override fun bindBankCard(card_user_name: String, bank_id: String, bank_name: String, province_id: String, city_id: String,
                              card_num: String, reserved_mobile: String, verify_code: String) {
        val jsonObject = JSONObject()
        jsonObject.put(GlobalParams.USER_ID, UserUtil.getUserId(App.instance))
        jsonObject.put("card_user_name", card_user_name)
        jsonObject.put("bank_id", bank_id)
        jsonObject.put("bank_name", bank_name)
        jsonObject.put("province_id", province_id)
        jsonObject.put("city_id", city_id)
        jsonObject.put("card_num", card_num)
        jsonObject.put("reserved_mobile", reserved_mobile)
        jsonObject.put("verify_code", verify_code)
        var jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        var body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.bindBankCard(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<Any>>())
                .compose(RxUtil.handleResult<Any>())
                .subscribeWith(object : CommonSubscriber<Any>(mView) {
                    override fun onNext(t: Any?) {
                    }

                    override fun onComplete() {
                        super.onComplete()
                        mView?.bindBankCardComplete()

                    }

                }))
    }

    override fun genOrder(mobile_model: String, mobile_memory: String,channel_type:String,bank_mobile:String) {
        val jsonObject = JSONObject()
        jsonObject.put(GlobalParams.USER_ID, UserUtil.getUserId(App.instance))
        jsonObject.put("mobile_model", mobile_model)
        jsonObject.put("mobile_memory", mobile_memory)
        jsonObject.put("channel_type", channel_type);
        jsonObject.put("bank_mobile", bank_mobile)
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.genOrder(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<GenOrderBean>>())
                .compose(RxUtil.handleResult<GenOrderBean>())
                .subscribeWith(object : CommonSubscriber<GenOrderBean>(mView) {
                    override fun onNext(t: GenOrderBean?) {
                        mView?.processGenOrderResult(t)
                    }

                    override fun onComplete() {
                        super.onComplete()
                        mView?.processGenOrderFinish()
                    }
                }))
    }


}