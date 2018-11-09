package com.huaxi.hailuo.model.bean



data class CashRedPacketBean(
    val current_money: String,
    val all_money: String,
    val history_money: String,
    val income_min: String,
    val income_max: String,
    val income_des: String,
    val is_auth_identity: String,
    val is_bind_bank: String,
    val bank_card: String,
    val button_status:String,
    val reserved_mobile:String
)