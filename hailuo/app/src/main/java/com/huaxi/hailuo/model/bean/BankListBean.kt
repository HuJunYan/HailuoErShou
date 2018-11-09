package com.huaxi.hailuo.model.bean


data class BankListBean(var bank_list: ArrayList<BankBean>) {
    data class BankBean(var bank_id: String, var bank_name: String)
}