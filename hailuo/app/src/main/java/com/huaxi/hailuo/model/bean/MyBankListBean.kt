package com.huaxi.hailuo.model.bean

/**
 * Created by admin on 2018/1/31.
 */
data class MyBankListBean(var bank_list: ArrayList<BankBean>,var is_auth_identity:String?) {
    data class BankBean(var card_id: String,  var bank_icon: String,var bank_card_name:String,var mobile_card_num:String,var is_delete:String)
}