package com.huaxi.hailuo.model.bean

/**
 * 极光，审核情况
 *
 * @author liu wei
 * @date 2018/4/27
 */
data class JPushAssessBean(val msg_content: MsgContent?) : JpushBaseBean() {

    data class MsgContent(val result: String = "",
                          val title: String = "",
                          val mobile_model: String = "",
                          val mobile_memory: String = "",
                          val user_id: String = "",
                          val content:String="",
                          val btn_try:String = "")
}

