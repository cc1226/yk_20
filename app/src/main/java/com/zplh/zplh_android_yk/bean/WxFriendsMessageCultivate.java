package com.zplh.zplh_android_yk.bean;

import java.util.List;

/**
 * Created by lichun on 2017/7/18.
 * Description:微信养号互撩
 */

public class WxFriendsMessageCultivate {


    /**
     * data : ["都放假放假有看头进而股份及法定","合计罚款碌一条属于人体的","都放假放假有看头进而股份及法定"]
     * msg : success
     * ret : 200
     */

    private String msg;
    private String ret;
    private List<String> data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getRet() {
        return ret;
    }

    public void setRet(String ret) {
        this.ret = ret;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
