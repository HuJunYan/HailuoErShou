package com.huaxi.hailuo.model.bean;

/**
 * Created by hjy on 2018/1/30.
 * 评估bean
 */

public class AssessBean {

        /**
         * assess_status : 1
         * target_page : 1
         * assess_title : 提示
         * assess_message : 提示内容
         */

        private String assess_status;
        private String target_page;
        private String assess_title;
        private String assess_message;

        public String getAssess_status() {
            return assess_status;
        }

        public void setAssess_status(String assess_status) {
            this.assess_status = assess_status;
        }

        public String getTarget_page() {
            return target_page;
        }

        public void setTarget_page(String target_page) {
            this.target_page = target_page;
        }

        public String getAssess_title() {
            return assess_title;
        }

        public void setAssess_title(String assess_title) {
            this.assess_title = assess_title;
        }

        public String getAssess_message() {
            return assess_message;
        }

        public void setAssess_message(String assess_message) {
            this.assess_message = assess_message;
        }
}
