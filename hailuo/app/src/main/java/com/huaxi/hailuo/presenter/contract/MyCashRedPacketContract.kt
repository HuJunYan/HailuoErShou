package com.huaxi.hailuo.presenter.contract

import com.huaxi.hailuo.base.BasePresenter
import com.huaxi.hailuo.base.BaseView
import com.huaxi.hailuo.model.bean.*

/**
 * Created by admin on 2018/1/29.
 */
class MyCashRedPacketContract {

    interface View : BaseView {
        fun myCashRedPacketCompelete(data: CashRedPacketBean?)
        fun sendCodeToRedPacketResult(data: Any?)
        fun myCashWithdrawResult(data: Any?)

    }

    interface Presenter : BasePresenter<MyCashRedPacketContract.View> {

        fun myCashRedPacket()
        fun sendCodeToRedPacket(mobile: String, type: String)
        fun myCashWithdraw(withdraw_money: String, code: String)
    }
}