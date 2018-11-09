package com.huaxi.hailuo.model.bean

/**
 * 极光，还款消息
 *
 * @author liu wei
 * @date 2018/4/27
 */
data class JPushRefundBean(val msg_content: MsgContent?) : JpushBaseBean() {

    data class MsgContent(val result: String = "",
                          val title: String = "",
                          val user_id: String = "",
                          val order_id: String = "")
}
