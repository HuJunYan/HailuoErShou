package com.huaxi.hailuo.model.bean;

import java.util.List;

/**
 * Created by admin on 2018/2/1.
 * 获取退款初始化信息bean
 */

public class RefundInfoBean {


    /**
     * code : 0
     * msg : success
     * data : {"order_success_content":" ","total_money":"应退款总金额","pop_info":["您应退款总金额为1200元","预付款：800.00元","违约金：400元（双倍定金）"],"repayment_style":{"bank_list":{"bank_name":"中国工商银行","bank_card_num":"6212260200083563359","bank_card_logo":"www.baidu.com"},"alipay":{"title":"支付宝","aisle_amount":"2","alipay_description":"","alipay_url":"www.alipay.com","alipay_img":"www.baidu.com"},"wxpay":{"title":"微信","aisle_amount":"2","wxpay_description":"","wxpay_url":"www.alipay.com","wxpay_img":"www.baidu.com"}}}
     */


    /**
     * order_success_content :  订单成功的内容
     * total_money : 应退款总金额
     * pop_info : ["您应退款总金额为1200元","预付款：800.00元","违约金：400元（双倍定金）"]
     * repayment_style : {"bank_list":{"bank_name":"中国工商银行","bank_card_num":"6212260200083563359","bank_card_logo":"www.baidu.com"},"alipay":{"title":"支付宝","aisle_amount":"2","alipay_description":"","alipay_url":"www.alipay.com","alipay_img":"www.baidu.com"},"wxpay":{"title":"微信","aisle_amount":"2","wxpay_description":"","wxpay_url":"www.alipay.com","wxpay_img":"www.baidu.com"}}
     */

    private String order_success_content;
    private String total_money;
    private String status;
    private RepaymentStyleBean repayment_style;
    private List<String> pop_info;
    //使用优惠券的金额单位是F,客户端需要自己转成“-5元”
    private String coupon_money;
    //实际支付的金额单位是F（支付宝，微信的通道费用客户端自己加）
    private String real_money;
    //优惠券可用张数
    private String unused_count;
    private String is_all_bind;
    private String channel_type;
    private String bank_mobile;
    private String coupon_free_id;


    public String getCoupon_free_id() {
        return coupon_free_id;
    }

    public void setCoupon_free_id(String coupon_free_id) {
        this.coupon_free_id = coupon_free_id;
    }

    public String getIs_all_bind() {
        return is_all_bind;
    }

    public void setIs_all_bind(String is_all_bind) {
        this.is_all_bind = is_all_bind;
    }

    public String getChannel_type() {
        return channel_type;
    }

    public void setChannel_type(String channel_type) {
        this.channel_type = channel_type;
    }

    public String getBank_mobile() {
        return bank_mobile;
    }

    public void setBank_mobile(String bank_mobile) {
        this.bank_mobile = bank_mobile;
    }

    public String getCoupon_money() {
        return coupon_money;
    }

    public void setCoupon_money(String coupon_money) {
        this.coupon_money = coupon_money;
    }

    public String getReal_money() {
        return real_money;
    }

    public void setReal_money(String real_money) {
        this.real_money = real_money;
    }

    public String getUnused_count() {
        return unused_count;
    }

    public void setUnused_count(String unused_count) {
        this.unused_count = unused_count;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrder_success_content() {
        return order_success_content;
    }

    public void setOrder_success_content(String order_success_content) {
        this.order_success_content = order_success_content;
    }

    public String getTotal_money() {
        return total_money;
    }

    public void setTotal_money(String total_money) {
        this.total_money = total_money;
    }

    public RepaymentStyleBean getRepayment_style() {
        return repayment_style;
    }

    public void setRepayment_style(RepaymentStyleBean repayment_style) {
        this.repayment_style = repayment_style;
    }

    public List<String> getPop_info() {
        return pop_info;
    }

    public void setPop_info(List<String> pop_info) {
        this.pop_info = pop_info;
    }

    public static class RepaymentStyleBean {
        /**
         * bank_list : {"bank_name":"中国工商银行","bank_card_num":"6212260200083563359","bank_card_logo":"www.baidu.com"}
         * alipay : {"title":"支付宝","aisle_amount":"2","alipay_description":"","alipay_url":"www.alipay.com","alipay_img":"www.baidu.com"}
         * wxpay : {"title":"微信","aisle_amount":"2","wxpay_description":"","wxpay_url":"www.alipay.com","wxpay_img":"www.baidu.com"}
         */

        private BankListBean bank_list;
        private AlipayBean alipay;
        private WxpayBean wxpay;

        public BankListBean getBank_list() {
            return bank_list;
        }

        public void setBank_list(BankListBean bank_list) {
            this.bank_list = bank_list;
        }

        public AlipayBean getAlipay() {
            return alipay;
        }

        public void setAlipay(AlipayBean alipay) {
            this.alipay = alipay;
        }

        public WxpayBean getWxpay() {
            return wxpay;
        }

        public void setWxpay(WxpayBean wxpay) {
            this.wxpay = wxpay;
        }

        public static class BankListBean {
            /**
             * bank_name : 中国工商银行
             * bank_card_num : 6212260200083563359
             * bank_card_logo : www.baidu.com
             */

            private String bank_name;
            private String bank_card_num;
            private String bank_card_logo;

            public String getBank_name() {
                return bank_name;
            }

            public void setBank_name(String bank_name) {
                this.bank_name = bank_name;
            }

            public String getBank_card_num() {
                return bank_card_num;
            }

            public void setBank_card_num(String bank_card_num) {
                this.bank_card_num = bank_card_num;
            }

            public String getBank_card_logo() {
                return bank_card_logo;
            }

            public void setBank_card_logo(String bank_card_logo) {
                this.bank_card_logo = bank_card_logo;
            }
        }

        public static class AlipayBean {
            /**
             * title : 支付宝
             * aisle_amount : 2
             * alipay_description :
             * alipay_url : www.alipay.com
             * alipay_img : www.baidu.com
             */

            private String title;
            private String aisle_amount;
            private String alipay_description;
            private String alipay_url;
            private String alipay_img;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getAisle_amount() {
                return aisle_amount;
            }

            public void setAisle_amount(String aisle_amount) {
                this.aisle_amount = aisle_amount;
            }

            public String getAlipay_description() {
                return alipay_description;
            }

            public void setAlipay_description(String alipay_description) {
                this.alipay_description = alipay_description;
            }

            public String getAlipay_url() {
                return alipay_url;
            }

            public void setAlipay_url(String alipay_url) {
                this.alipay_url = alipay_url;
            }

            public String getAlipay_img() {
                return alipay_img;
            }

            public void setAlipay_img(String alipay_img) {
                this.alipay_img = alipay_img;
            }
        }

        public static class WxpayBean {
            /**
             * title : 微信
             * aisle_amount : 2
             * wxpay_description :
             * wxpay_url : www.alipay.com
             * wxpay_img : www.baidu.com
             */

            private String title;
            private String aisle_amount;
            private String wxpay_description;
            private String wxpay_url;
            private String wxpay_img;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getAisle_amount() {
                return aisle_amount;
            }

            public void setAisle_amount(String aisle_amount) {
                this.aisle_amount = aisle_amount;
            }

            public String getWxpay_description() {
                return wxpay_description;
            }

            public void setWxpay_description(String wxpay_description) {
                this.wxpay_description = wxpay_description;
            }

            public String getWxpay_url() {
                return wxpay_url;
            }

            public void setWxpay_url(String wxpay_url) {
                this.wxpay_url = wxpay_url;
            }

            public String getWxpay_img() {
                return wxpay_img;
            }

            public void setWxpay_img(String wxpay_img) {
                this.wxpay_img = wxpay_img;
            }
        }
    }

}
