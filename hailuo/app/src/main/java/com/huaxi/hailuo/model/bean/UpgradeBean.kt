package com.huaxi.hailuo.model.bean


data class UpgradeBean(var app_type: String = "1", var download_url: String = "",
                       var version_info: String = "", var force_upgrade: String = "0",
                       var is_review: String = "0", var is_ignore: String = "0",
                       var service_url: String = "", var is_edit_identity: String = "")
