package com.huaxi.hailuo.presenter.contract

import com.huaxi.hailuo.base.BasePresenter
import com.huaxi.hailuo.base.BaseView
import com.huaxi.hailuo.model.bean.*

/**
 * Created by admin on 2018/1/16.
 */
object BindbankCardContract {

    interface View : BaseView {

        fun getUserInfoResult(data: UserInfoBean?)

        fun getBankListDataResult(t: BankListBean?)

        fun bindBankCardComplete()

        fun getProviceResult(data: ProvinceBean?)

        fun getCityResult(data: CityBean?)

        fun getVefiryCodeResult(isSuccess: Boolean)

        fun processGenOrderResult(t: GenOrderBean?)
        fun processGenOrderFinish()
        //现金红包提现返回的结果
        fun myCashWithdrawResult(data: Any?)

    }

    interface Presenter : BasePresenter<View> {

        fun getUserInfo()

        fun getBankListData()

        fun bindBankCard(card_user_name: String, bank_id: String, bank_name: String, province_id: String, city_id: String,
                         card_num: String, reserved_mobile: String, verify_code: String)

        fun getProvinceData()

        fun getCityData(provinceId: String?)

        fun getVefiryCode(reserved_mobile: String)
        //绑卡成功之后生成订单
        fun genOrder(mobile_model: String, mobile_memory: String,channel_type:String,bank_mobile:String)
        //现金红包提现
        fun myCashWithdraw(withdraw_money: String, code: String)
    }
}