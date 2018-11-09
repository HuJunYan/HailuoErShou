package com.huaxi.hailuo.model.bean

/**
 * 极光，消息中心的消息
 *
 * @author liu wei
 * @date 2018/4/27
 */
data class JPushMessageCenterBean(val msg_content: MsgContent?) : JpushBaseBean() {

    data class MsgContent(val des: String = "",
                          val title: String = "",
                          val msg_id: String = "",
                          val push_time: Int = 0,
                          val url: String = "")

}
