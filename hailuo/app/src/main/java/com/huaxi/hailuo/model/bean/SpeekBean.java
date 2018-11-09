package com.huaxi.hailuo.model.bean;

import java.util.List;

/**
 * Created by zhangliuguang  on 2018/4/28.
 */
public class SpeekBean {

    private List<FeedbackItemBean> feedback_item;

    public List<FeedbackItemBean> getFeedback_item() {
        return feedback_item;
    }

    public void setFeedback_item(List<FeedbackItemBean> feedback_item) {
        this.feedback_item = feedback_item;
    }

    public static class FeedbackItemBean {
        /**
         * feedback_title : 人脸识别
         * feedback_type : 1
         */

        private String feedback_title;
        private String feedback_type;

        public String getFeedback_title() {
            return feedback_title;
        }

        public void setFeedback_title(String feedback_title) {
            this.feedback_title = feedback_title;
        }

        public String getFeedback_type() {
            return feedback_type;
        }

        public void setFeedback_type(String feedback_type) {
            this.feedback_type = feedback_type;
        }
    }
}
