package com.huaxi.hailuo.model.bean;

/**
 * Created by zhangliuguang  on 2018/4/19.
 */
public class ConfirRefundBean {

    /**
     * error_msg : 验证码输入错误，请重新输入
     */

    private String error_msg;

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }

    @Override
    public String toString() {
        return "ConfirRefundBean{" +
                "error_msg='" + error_msg + '\'' +
                '}';
    }
}
