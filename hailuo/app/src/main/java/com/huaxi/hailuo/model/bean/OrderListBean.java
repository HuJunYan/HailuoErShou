package com.huaxi.hailuo.model.bean;

import java.util.List;

/**
 * Created by admin on 2018/1/30.
 * 订单列表bean
 */

public class OrderListBean {


    private String page_size;
    private String total_num;
    private String now_page;
    private List<Order_list> order_list;

    public void setPage_size(String page_size) {
        this.page_size = page_size;
    }

    public String getPage_size() {
        return page_size;
    }

    public void setTotal_num(String total_num) {
        this.total_num = total_num;
    }

    public String getTotal_num() {
        return total_num;
    }

    public void setNow_page(String now_page) {
        this.now_page = now_page;
    }

    public String getNow_page() {
        return now_page;
    }

    public void setOrder_list(List<Order_list> order_list) {
        this.order_list = order_list;
    }

    public List<Order_list> getOrder_list() {
        return order_list;
    }

    public class Order_list {

        private String order_id;
        private String mobile_model;
        private String mobile_icon;
        private String assess_money;
        private String pre_money;
        private String order_term;
        public void setOrder_id(String order_id) {
            this.order_id = order_id;
        }
        public String getOrder_id() {
            return order_id;
        }

        public void setMobile_model(String mobile_model) {
            this.mobile_model = mobile_model;
        }
        public String getMobile_model() {
            return mobile_model;
        }

        public void setMobile_icon(String mobile_icon) {
            this.mobile_icon = mobile_icon;
        }
        public String getMobile_icon() {
            return mobile_icon;
        }

        public void setAssess_money(String assess_money) {
            this.assess_money = assess_money;
        }
        public String getAssess_money() {
            return assess_money;
        }

        public void setPre_money(String pre_money) {
            this.pre_money = pre_money;
        }
        public String getPre_money() {
            return pre_money;
        }

        public void setOrder_term(String order_term) {
            this.order_term = order_term;
        }
        public String getOrder_term() {
            return order_term;
        }

    }

}
