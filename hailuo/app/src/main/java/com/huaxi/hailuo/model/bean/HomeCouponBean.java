package com.huaxi.hailuo.model.bean;

import java.util.List;

/**
 * Created by zhangliuguang  on 2018/5/2.
 */
public class HomeCouponBean {

    /**
     * not_login_title : 新手福利
     * not_login_content : 68元红包
     * login_title : 完成认证获得68元红包
     * login_dialog_content : 距离活动结束仅剩6天！
     * auth_list : [{"auth_title":"完成身份认证","auth_des":"累计获得 38 元","auth_status":"1"}]
     */

    private String not_login_title;
    private String not_login_content;
    private String login_title;
    private String login_dialog_content;
    private List<AuthListBean> auth_list;

    public String getNot_login_title() {
        return not_login_title;
    }

    public void setNot_login_title(String not_login_title) {
        this.not_login_title = not_login_title;
    }

    public String getNot_login_content() {
        return not_login_content;
    }

    public void setNot_login_content(String not_login_content) {
        this.not_login_content = not_login_content;
    }

    public String getLogin_title() {
        return login_title;
    }

    public void setLogin_title(String login_title) {
        this.login_title = login_title;
    }

    public String getLogin_dialog_content() {
        return login_dialog_content;
    }

    public void setLogin_dialog_content(String login_dialog_content) {
        this.login_dialog_content = login_dialog_content;
    }

    public List<AuthListBean> getAuth_list() {
        return auth_list;
    }

    public void setAuth_list(List<AuthListBean> auth_list) {
        this.auth_list = auth_list;
    }

    public static class AuthListBean {
        /**
         * auth_title : 完成身份认证
         * auth_des : 累计获得 38 元
         * auth_status : 1
         */

        private String auth_title;
        private String auth_des;
        private String auth_status;

        public String getAuth_title() {
            return auth_title;
        }

        public void setAuth_title(String auth_title) {
            this.auth_title = auth_title;
        }

        public String getAuth_des() {
            return auth_des;
        }

        public void setAuth_des(String auth_des) {
            this.auth_des = auth_des;
        }

        public String getAuth_status() {
            return auth_status;
        }

        public void setAuth_status(String auth_status) {
            this.auth_status = auth_status;
        }
    }
}
