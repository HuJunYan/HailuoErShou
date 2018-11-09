package com.huaxi.hailuo.model.bean

/**
 * Created by wang on 2018/2/6.
 */
data class MobileListBean(var mobile_list: ArrayList<MobileListItemBean>) {
    data class MobileListItemBean(var item_name: String, var item_img_url: String, var item_sub: ArrayList<MobileListItemSubBean>)
    data class MobileListItemSubBean(var item_price: String, var item_mem: String)
}
