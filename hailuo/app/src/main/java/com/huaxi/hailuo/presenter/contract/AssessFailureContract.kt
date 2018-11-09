package com.huaxi.hailuo.presenter.contract

import com.huaxi.hailuo.base.BasePresenter
import com.huaxi.hailuo.base.BaseView
import com.huaxi.hailuo.model.bean.BankListBean
import com.huaxi.hailuo.model.bean.CityBean
import com.huaxi.hailuo.model.bean.DownRefreshBean
import com.huaxi.hailuo.model.bean.ProvinceBean

/**
 * Created by admin on 2018/1/16.
 */
object AssessFailureContract {

    interface View : BaseView {
        fun processData(data: DownRefreshBean?)
        fun processSuccessResult(data: DownRefreshBean?)
    }

    interface Presenter : BasePresenter<View> {
        fun getData(mobile_model: String, mobile_memory: String)
        fun getRefreshData(mobile_model: String, mobile_memory: String)
        //导流埋点
        fun daoliuBuriedPoint(flag:String,result: String)
    }
}