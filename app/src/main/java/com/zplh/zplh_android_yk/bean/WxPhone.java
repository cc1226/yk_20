package com.zplh.zplh_android_yk.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/8/1.
 */

/**
 * 微信获取手机通讯录bean
 */

public class WxPhone {

    private String ret;

    private String success;

    private List<Data> data;

    public void setRet(String ret) {
        this.ret = ret;
    }

    public String getRet() {
        return this.ret;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getSuccess() {
        return this.success;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public List<Data> getData() {
        return this.data;
    }

    public class Data {
        private String name;

        private String phone;

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getPhone() {
            return this.phone;
        }
    }
}
