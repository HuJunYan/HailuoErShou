package com.huaxi.hailuo.presenter.contract

import com.huaxi.hailuo.base.BasePresenter
import com.huaxi.hailuo.base.BaseView
import com.huaxi.hailuo.model.bean.MessageCenterBean

/**
 * MessageCenterContract
 * @author liu wei
 * @date 2018/4/12
 */

interface MessageCenterContract {
    interface View : BaseView {
        fun showMessageList(list: List<MessageCenterBean.MessageBean>?)
        fun refreshMessageReadState(position: Int)
        fun refreshAllMessageState()
        fun setLoadMoreEnable(isEnable: Boolean)
        fun setReadAllEnable(isEnable: Boolean)
    }

    interface Presenter : BasePresenter<View> {
        fun refreshMessageList()
        fun getMessageList()
        fun readAllMessage()
        fun readMessage(position: Int, msg: MessageCenterBean.MessageBean?)
    }
}