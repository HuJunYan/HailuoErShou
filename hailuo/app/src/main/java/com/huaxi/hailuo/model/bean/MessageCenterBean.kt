package com.huaxi.hailuo.model.bean

import com.google.gson.annotations.SerializedName

/**
 * MessageListBean
 * @author liu wei
 * @date 2018/4/13
 */


data class MessageCenterBean(@SerializedName("msg_list")
                             val msgList: List<MessageBean>?,
                             // 是否有新消息
                             var is_new_msg: String,
                             @SerializedName("call_back_param")
                             val callbackParam: CallbackParam?,
                             @SerializedName("total_num")
                             val totalNum: String = "") {


    data class MessageBean(@SerializedName("date_str")
                           val dateStr: String = "",
                           @SerializedName("is_read")
                           var isRead: String = "",
                           @SerializedName("des")
                           val des: String = "",
                           @SerializedName("title")
                           val title: String = "",
                           // 阅读消息接口传回服务器
                           val table_identifier: String = "",
                           @SerializedName("msg_id")
                           val msgId: String = "",
                           @SerializedName("msg_url")
                           val msgUrl: String = "")


    data class CallbackParam(val table_identifier: String?,
                             val table_page_offset: String?)
}


