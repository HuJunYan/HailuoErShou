package com.huaxi.hailuo.presenter.contract

import com.huaxi.hailuo.base.BasePresenter
import com.huaxi.hailuo.base.BaseView
import com.huaxi.hailuo.model.bean.UseCoupon

/**
 * Created by admin on 2018/4/3.
 */
class UseCouponContract{

    interface View:BaseView{

        fun loadDataComplete(data:UseCoupon?)
    }

    interface Presenter:BasePresenter<View>{
        fun loadData(order_id:String?)

    }

}