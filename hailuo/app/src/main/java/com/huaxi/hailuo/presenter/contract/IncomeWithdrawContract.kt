package com.huaxi.hailuo.presenter.contract

import com.huaxi.hailuo.base.BasePresenter
import com.huaxi.hailuo.base.BaseView
import com.huaxi.hailuo.model.bean.CashRedPacketBean
import com.huaxi.hailuo.model.bean.IncomeDetailOrderBean
import com.huaxi.hailuo.model.bean.InviteFriendBean
import com.huaxi.hailuo.model.bean.SetPasswordBean

/**
 * Created by admin on 2018/1/29.
 */
class IncomeWithdrawContract {

    interface View : BaseView {
        fun myCashRedPacketMoneyListResult(data:IncomeDetailOrderBean?,isRefresh:Boolean)


    }

    interface Presenter : BasePresenter<IncomeWithdrawContract.View> {

        fun myCashRedPacketMoneyList(type:String,current_page:String,page_size:String,isRefresh: Boolean)
    }
}