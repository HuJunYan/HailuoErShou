package com.huaxi.hailuo.model.bean

/**
 * Created by wang on 2017/12/29.
 */
data class HomeDataBean(var banner_list: ArrayList<BannerBean>, var demographic: ArrayList<DemographicBean>,
                        var hot_list: ArrayList<ProductBean>, var fund_list: ArrayList<ProductBean>,
                        var is_pop: String, var popup_image_url: String) {
    data class BannerBean(var banner_url: String, var jump_url: String)
    data class DemographicBean(var content: String)
    data class ProductBean(var quota_limit: String, var day_rate: String,
                           var apply_count: String, var jump_url: String,
                           var status: String, var logo_url: String,
                           var product_name: String, var product_id: String,
                           var local_type: Int, var local_title: String) {
        constructor(local_type: Int,local_title:String) : this("", "", "", "", "", "", "", "", local_type, local_title) {}
    }


}
