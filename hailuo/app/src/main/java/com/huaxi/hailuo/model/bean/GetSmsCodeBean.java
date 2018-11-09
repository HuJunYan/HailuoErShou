package com.huaxi.hailuo.model.bean;

/**
 * Created by zhangliuguang  on 2018/4/12.
 */
public class GetSmsCodeBean {
    private String error_msg;

    public GetSmsCodeBean(String error_msg) {
        this.error_msg = error_msg;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }

}
