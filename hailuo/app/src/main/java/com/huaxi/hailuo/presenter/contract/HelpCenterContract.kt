package com.huaxi.hailuo.presenter.contract

import com.huaxi.hailuo.base.BasePresenter
import com.huaxi.hailuo.base.BaseView
import com.huaxi.hailuo.model.bean.HelpCenterBean

/**
 * Created by admin on 2018/1/30.
 */
class HelpCenterContract{

    interface View:BaseView{

        /**
         * 解析数据
         */
        fun processData(data: HelpCenterBean?)
    }

    interface Presenter:BasePresenter<HelpCenterContract.View>{

        //获取预订单数据
        fun getData(type:String)
    }
}