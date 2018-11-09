package com.huaxi.hailuo.presenter.contract

import com.huaxi.hailuo.base.BasePresenter
import com.huaxi.hailuo.base.BaseView
import com.huaxi.hailuo.model.bean.MyBankListBean

/**
 * Created by admin on 2018/1/31.
 */
class MyBankListContract{

    interface View:BaseView{

        fun loadDataComplete(data:MyBankListBean)
        fun deleteCardComplete()

    }

    interface Presenter:BasePresenter<MyBankListContract.View>{

        fun loadData()
        fun deleteCard(cardId:String?)
    }
}