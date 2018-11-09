package com.huaxi.hailuo.presenter.contract

import com.huaxi.hailuo.base.BasePresenter
import com.huaxi.hailuo.base.BaseView
import com.huaxi.hailuo.model.bean.OrderDetailBean

/**
 * Created by admin on 2018/2/1.
 * 订单详情界面
 */
class OrderDetailContract{

    interface View:BaseView{

        fun getOrderDetailResult(data: OrderDetailBean?)

    }

    interface Presenter:BasePresenter<OrderDetailContract.View>{

        fun getOrderDetail(order_id:String?)
    }
}