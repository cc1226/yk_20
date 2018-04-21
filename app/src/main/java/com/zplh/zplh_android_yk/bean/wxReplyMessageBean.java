package com.zplh.zplh_android_yk.bean;

import java.io.Serializable;

/**
 * Created by lichun on 2018/4/9.
 * Description:
 */

public class wxReplyMessageBean implements Serializable {

    /**
     * ret : 200
     * msg : success
     * data : 好哟
     */

    private int ret;
    private String msg;
    private String data;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
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
