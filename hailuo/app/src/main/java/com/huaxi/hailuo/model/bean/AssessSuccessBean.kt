package com.huaxi.hailuo.model.bean

data class AssessSuccessBean(var is_new_customer: String, var is_all_bind: String, var is_bind_bank: String, var channel_type: String, var bank_mobile: String,
                             var money: String, var pop_title: String, var pop_content: String,
                             var pop_url: String, var mobile_model: String, var mobile_desc: String, var mobile_memory: String, var pop_agreement_title: String, var recovery_process: ArrayList<RecoveryProcessBean>
) {
    data class RecoveryProcessBean(var process_icon: String, var process_title: String, var process_content: String)

}