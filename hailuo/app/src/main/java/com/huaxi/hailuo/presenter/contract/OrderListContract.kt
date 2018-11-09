package com.huaxi.hailuo.presenter.contract

import com.huaxi.hailuo.base.BasePresenter
import com.huaxi.hailuo.base.BaseView
import com.huaxi.hailuo.model.bean.OrderListBean

/**
 * Created by admin on 2018/1/30.
 */
class OrderListContract{

    interface View:BaseView{

        fun loadDataComplete(data:OrderListBean?)
    }

    interface Presenter:BasePresenter<OrderListContract.View>{
        fun loadData(now_page:String,page_size:String)
    }

}