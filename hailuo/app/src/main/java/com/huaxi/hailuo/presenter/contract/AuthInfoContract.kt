package com.huaxi.hailuo.presenter.contract

import com.huaxi.hailuo.base.BasePresenter
import com.huaxi.hailuo.base.BaseView
import com.huaxi.hailuo.model.bean.*

/**
 * Created by admin on 2018/1/16.
 */
object AuthInfoContract {

    interface View : BaseView {
        fun getUserInfoResult(data: UserInfoBean?)
        fun getProviceResult(data: ProvinceBean?)

        fun getCityResult(data: CityBean?)

        fun getCountyResult(data: CountyBean?)

        fun saveUserInfoComplete()
    }

    interface Presenter : BasePresenter<View> {
        fun getUserInfo()
        fun getProvinceData()

        fun getCityData(provinceId: String?)

        fun getCountyData(cityId: String?)

        fun saveUserInfo(hashMap: HashMap<Int, AddressBean>, user_address_detail: String, company_name: String,
                         company_phone: String, company_address_detail: String, qq_num: String, selected_occupation_name: String)
    }
}