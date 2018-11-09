package com.huaxi.hailuo.model.bean

/**
 * Created by wang on 2017/12/28.
 */
data class MineBean(var user_id: String,
                    var is_member: String,
                    var is_white: String,
                    var content: String,
                    var service_url: String,
                    //1有新消息，2没有新消息
                    var is_new_msg: String,
                    var invitation_des:String,
                    var is_invitation:String,
                    var message_pow:String

)