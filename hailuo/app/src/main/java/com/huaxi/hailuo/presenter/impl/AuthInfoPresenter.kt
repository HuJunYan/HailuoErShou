package com.huaxi.hailuo.presenter.impl

import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.base.GlobalParams
import com.huaxi.hailuo.base.RxPresenter
import com.huaxi.hailuo.model.bean.*
import com.huaxi.hailuo.model.http.ApiManager
import com.huaxi.hailuo.model.http.CommonSubscriber
import com.huaxi.hailuo.presenter.contract.AuthInfoContract
import com.huaxi.hailuo.util.RequsetUtil
import com.huaxi.hailuo.util.RxUtil
import com.huaxi.hailuo.util.SignUtils
import com.huaxi.hailuo.util.UserUtil
import org.json.JSONObject

/**
 * Created by admin on 2018/1/16.
 */
class AuthInfoPresenter : RxPresenter<AuthInfoContract.View>(), AuthInfoContract.Presenter {
    override fun saveUserInfo(hashMap: HashMap<Int, AddressBean>, user_address_detail: String, company_name: String,
                              company_phone: String, company_address_detail: String, qq_num: String, selected_occupation_name: String) {
        val jsonObject = JSONObject()
        val userAdd = hashMap.get(1)
        val companyAdd = hashMap.get(2)
        var user_address_provice = ""
        var user_address_city = ""
        var user_address_county = ""
        var company_address_provice = ""
        var company_address_city = ""
        var company_address_county = ""
        if (userAdd != null) {
            user_address_provice = userAdd.province
            user_address_city = userAdd.city
            user_address_county = userAdd.county
        }
        if (companyAdd != null) {
            company_address_provice = companyAdd.province
            company_address_city = companyAdd.city
            company_address_county = companyAdd.county
        }
        jsonObject.put(GlobalParams.USER_ID, UserUtil.getUserId(App.instance))
        jsonObject.put("user_address_provice", user_address_provice)
        jsonObject.put("user_address_city", user_address_city)
        jsonObject.put("user_address_county", user_address_county)
        jsonObject.put("user_address_detail", user_address_detail)
        jsonObject.put("company_name", company_name)
        jsonObject.put("company_phone", company_phone)
        jsonObject.put("company_address_provice", company_address_provice)
        jsonObject.put("company_address_city", company_address_city)
        jsonObject.put("company_address_county", company_address_county)
        jsonObject.put("company_address_detail", company_address_detail)
        jsonObject.put("qq_num", qq_num)
        jsonObject.put("selected_occupation_name", selected_occupation_name)
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.saveUserInfo(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<Any>>())
                .compose(RxUtil.handleResult<Any>())
                .subscribeWith(object : CommonSubscriber<Any>(mView) {
                    override fun onNext(data: Any?) {
                    }

                    override fun onComplete() {
                        super.onComplete()
                        mView?.saveUserInfoComplete()
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


    override fun getCountyData(cityId: String?) {
        val jsonObject = JSONObject()
        jsonObject.put(GlobalParams.USER_ID, UserUtil.getUserId(App.instance))
        jsonObject.put("city_id", cityId)
        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)
        addDisposable(ApiManager.getCounty(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<CountyBean>>())
                .compose(RxUtil.handleResult<CountyBean>())
                .subscribeWith(object : CommonSubscriber<CountyBean>(mView) {
                    override fun onNext(data: CountyBean?) {
                        mView?.getCountyResult(data)
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


}