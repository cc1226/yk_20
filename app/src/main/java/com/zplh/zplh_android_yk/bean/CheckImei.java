package com.zplh.zplh_android_yk.bean;

/**
 * Created by lichun on 2017/6/19.
 * Description:
 */

public class CheckImei {

    /**
     * ret : 400
     * msg : wrong uid or need uid
     * data :
     */

    private String ret;
    private String msg;
    private String data;

    public String getRet() {
        return ret;
    }

    public void setRet(String ret) {
        this.ret = ret;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
