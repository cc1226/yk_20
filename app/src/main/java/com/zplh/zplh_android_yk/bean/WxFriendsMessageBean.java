package com.zplh.zplh_android_yk.bean;

import java.io.Serializable;

/**
 * Created by zhangshuai on 2017/10/23.
 * Description:
 */

public class WxFriendsMessageBean implements Serializable {




    private String wx_uid;
    private String wx_number;// 微信号
    private String wx_name;// 微信昵称
    private String wx_phone_number;// 微信手机号
    private String wx_phone_name ;//手机联系人名
    private String wx_location ;// 所在地区
    public String getUid() {
        return wx_uid;
    }

    public void setUid(String wx_uid) {
        this.wx_uid = wx_uid;
    }
    public String getWx_number() {
        return wx_number;
    }

    public void setWx_number(String wx_number) {
        this.wx_number = wx_number;
    }

    public String getWx_name() {
        return wx_name;
    }

    public void setWx_name(String wx_name) {
        this.wx_name = wx_name;
    }

    public String getWx_phone_numer() {
        return wx_phone_number;
    }

    public void setWx_phone_numer(String wx_phone_numer) {
        this.wx_phone_number = wx_phone_numer;
    }

    public String getWx_phone_name() {
        return wx_phone_name;
    }

    public void setWx_phone_name(String wx_phone_name) {
        this.wx_phone_name = wx_phone_name;
    }

    public String getWx_location() {
        return wx_location;
    }

    public void setWx_location(String wx_location) {
        this.wx_location = wx_location;
    }
                public WxFriendsMessageBean(String wx_number, String wx_name, String wx_phone_number, String wx_phone_name, String wx_location, String wx_uid){
                this.wx_number=wx_number;
                this.wx_name =wx_name;
                this.wx_phone_number=wx_phone_number;
                this.wx_phone_name=wx_phone_name;
                this.wx_location=wx_location;
                this.wx_uid=wx_uid;

            }

}
