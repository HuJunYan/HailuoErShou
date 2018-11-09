package com.huaxi.hailuo.model.bean;

import java.util.List;

/**
 * Created by admin on 2018/2/1.
 * 订单详情bean
 */

public class OrderDetailBean {
    /**
     * detail_info : {"mobile_model":"手机的型号","mobile_icon":"手机的图片地址","assess_money":"10000","pre_money":"10000","order_term":"14","term_title":"履约期的标题","term_content":"履约期的内容"}
     * status_list : [{"status_title":"待商家收货","status_date":"2018-01-13 16:34:01"},{"status_title":"待商家收货","status_date":"2018-01-13 16:34:01"}]
     * remaining_term : 7
     * overdue_term : 5
     * order_status : 1
     * is_click : 1
     * service_url : 在线客服URL
     * order_id : 订单ID
     */

    private DetailInfoBean detail_info;
    private String remaining_term;
    private String overdue_term;
    private String order_status;
    private String is_click;
    private String service_url;
    private String order_id;
    private List<StatusListBean> status_list;
    /**
     * 1代表需要放款成功需要添加到系统日历里面,非1的时候APP不需要显
     */
    private String is_show_calendar_dialog;

    private Notice notice;

    public Notice getNotice() {
        return notice;
    }

    public void setNotice(Notice notice) {
        this.notice = notice;
    }

    public String getIs_show_calendar_dialog() {
        return is_show_calendar_dialog;
    }

    public void setIs_show_calendar_dialog(String is_show_calendar_dialog) {
        this.is_show_calendar_dialog = is_show_calendar_dialog;
    }

    public DetailInfoBean getDetail_info() {
        return detail_info;
    }

    public void setDetail_info(DetailInfoBean detail_info) {
        this.detail_info = detail_info;
    }

    public String getRemaining_term() {
        return remaining_term;
    }

    public void setRemaining_term(String remaining_term) {
        this.remaining_term = remaining_term;
    }

    public String getOverdue_term() {
        return overdue_term;
    }

    public void setOverdue_term(String overdue_term) {
        this.overdue_term = overdue_term;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getIs_click() {
        return is_click;
    }

    public void setIs_click(String is_click) {
        this.is_click = is_click;
    }

    public String getService_url() {
        return service_url;
    }

    public void setService_url(String service_url) {
        this.service_url = service_url;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public List<StatusListBean> getStatus_list() {
        return status_list;
    }

    public void setStatus_list(List<StatusListBean> status_list) {
        this.status_list = status_list;
    }

    public static class DetailInfoBean {
        /**
         * mobile_model : 手机的型号
         * mobile_icon : 手机的图片地址
         * assess_money : 10000
         * pre_money : 10000
         * order_term : 14
         * term_title : 履约期的标题
         * term_content : 履约期的内容
         */

        private String mobile_model;
        private String mobile_icon;
        private String assess_money;
        private String pre_money;
        private String order_term;
        private String term_title;
        private String term_content;

        public String getMobile_model() {
            return mobile_model;
        }

        public void setMobile_model(String mobile_model) {
            this.mobile_model = mobile_model;
        }

        public String getMobile_icon() {
            return mobile_icon;
        }

        public void setMobile_icon(String mobile_icon) {
            this.mobile_icon = mobile_icon;
        }

        public String getAssess_money() {
            return assess_money;
        }

        public void setAssess_money(String assess_money) {
            this.assess_money = assess_money;
        }

        public String getPre_money() {
            return pre_money;
        }

        public void setPre_money(String pre_money) {
            this.pre_money = pre_money;
        }

        public String getOrder_term() {
            return order_term;
        }

        public void setOrder_term(String order_term) {
            this.order_term = order_term;
        }

        public String getTerm_title() {
            return term_title;
        }

        public void setTerm_title(String term_title) {
            this.term_title = term_title;
        }

        public String getTerm_content() {
            return term_content;
        }

        public void setTerm_content(String term_content) {
            this.term_content = term_content;
        }
    }

    public static class StatusListBean {
        /**
         * status_title : 待商家收货
         * status_date : 2018-01-13 16:34:01
         */

        private String status_title;
        private String status_date;
        private String status_des;

        public String getStatus_des() {
            if (status_des == null) {
                return "";
            }
            return status_des;
        }

        public void setStatus_des(String status_des) {
            this.status_des = status_des;
        }

        public String getStatus_title() {
            return status_title;
        }

        public void setStatus_title(String status_title) {
            this.status_title = status_title;
        }

        public String getStatus_date() {
            return status_date;
        }

        public void setStatus_date(String status_date) {
            this.status_date = status_date;
        }
    }

    public static class Notice {

        /**
         * notice_timestamp : 还款日的10点整时间戳
         * notice_title : 海螺商城还款提醒（如已还款请忽略）
         */

        private String notice_timestamp;
        private String notice_title;

        public String getNotice_timestamp() {
            return notice_timestamp;
        }

        public void setNotice_timestamp(String notice_timestamp) {
            this.notice_timestamp = notice_timestamp;
        }

        public String getNotice_title() {
            return notice_title;
        }

        public void setNotice_title(String notice_title) {
            this.notice_title = notice_title;
        }
    }
}
