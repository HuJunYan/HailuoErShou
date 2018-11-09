package com.huaxi.hailuo.model.bean

/**
 * Created by wang on 2018/1/2.
 */
data class VerifyHomeDataBean(var banner_list: ArrayList<VerifyBannerBean>, var hot_list: ArrayList<VerifyItemBean>
                              , var article_list: ArrayList<VerifyItemBean>) {
    data class VerifyBannerBean(var article_id: String, var img_url: String, var article_title: String, var jump_url: String, var is_collect: String)
    data class VerifyItemBean(var article_id: String, var article_title: String, var jump_url: String, var img_url: String,var is_collect: String, var local_item_type: Int, var local_item_title: String) {
        constructor(local_type: Int, local_title: String) : this("", "", "", "","", local_type, local_title)

    }
}