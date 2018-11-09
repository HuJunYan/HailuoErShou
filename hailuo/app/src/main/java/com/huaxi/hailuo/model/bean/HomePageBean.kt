package com.huaxi.hailuo.model.bean


data class HomePageBean(var scroll_bar: ScrollBarBean, var banner_list: ArrayList<BannerBean>,
                        var mobile_info: MobileInfoBean, var recovery_flow: RecoveryFlowBean,
                        var status_title: String, var status: String,
                        var order_id: String,
                        var is_show_calendar_dialog: String,
                        var check_user_risk: String,
                        var notice: OrderDetailBean.Notice,
                        var bubble_url: String?,
                        var daoliu_title: String?,
                        var daoliu_image: String?,
        //"是否显示导流悬浮按钮"//1.显示，2不显示
                        var is_show_bubble: String?
)
