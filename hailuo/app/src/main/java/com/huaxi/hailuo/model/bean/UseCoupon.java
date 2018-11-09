package com.huaxi.hailuo.model.bean;

import java.util.List;

/**
 * Created by admin on 2018/4/3.
 * 使用优惠券bean
 */

public class UseCoupon {


    /**
     * code : 0
     * msg : success
     * data : {"couponList":[{"coupon_id":"1","coupon_name":"退款专项红包","coupon_money_str":"5","coupon_name_description":"履约期内用户可用","coupon_description":"仅限在海螺商城平台退款时使用","validity_time_str":"2017-11-21-2017-12-30"}]}
     */


        private List<CouponListBean> couponList;

        public List<CouponListBean> getCouponList() {
            return couponList;
        }

        public void setCouponList(List<CouponListBean> couponList) {
            this.couponList = couponList;
        }

        public static class CouponListBean {
            /**
             * coupon_id : 1
             * coupon_name : 退款专项红包
             * coupon_money_str : 5
             * coupon_name_description : 履约期内用户可用
             * coupon_description : 仅限在海螺商城平台退款时使用
             * validity_time_str : 2017-11-21-2017-12-30
             * isChose:是否被选中
             */

            private String coupon_id;
            private String coupon_name;
            private String coupon_money_str;
            private String coupon_name_description;
            private String coupon_description;
            private String validity_time_str;
            private Boolean isChose = false;

            public Boolean getChose() {
                return isChose;
            }

            public void setChose(Boolean chose) {
                isChose = chose;
            }

            public String getCoupon_id() {
                return coupon_id;
            }

            public void setCoupon_id(String coupon_id) {
                this.coupon_id = coupon_id;
            }

            public String getCoupon_name() {
                return coupon_name;
            }

            public void setCoupon_name(String coupon_name) {
                this.coupon_name = coupon_name;
            }

            public String getCoupon_money_str() {
                return coupon_money_str;
            }

            public void setCoupon_money_str(String coupon_money_str) {
                this.coupon_money_str = coupon_money_str;
            }

            public String getCoupon_name_description() {
                return coupon_name_description;
            }

            public void setCoupon_name_description(String coupon_name_description) {
                this.coupon_name_description = coupon_name_description;
            }

            public String getCoupon_description() {
                return coupon_description;
            }

            public void setCoupon_description(String coupon_description) {
                this.coupon_description = coupon_description;
            }

            public String getValidity_time_str() {
                return validity_time_str;
            }

            public void setValidity_time_str(String validity_time_str) {
                this.validity_time_str = validity_time_str;
            }
        }

}
