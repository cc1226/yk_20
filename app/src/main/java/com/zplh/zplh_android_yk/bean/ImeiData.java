package com.zplh.zplh_android_yk.bean;

/**
 * Created by lichun on 2017/6/12.
 * Description:
 */

public class ImeiData {

    /**
     * data : {"imei":"87975431324687132418","time":"2017-06-12 10:00:01","title":"binding_API","uid":"159357451"}
     * msg : 绑定成功！
     * ret : 200
     */

    private DataBean data;
    private String msg;
    private String ret;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

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

    public static class DataBean {
        /**
         * imei : 87975431324687132418
         * time : 2017-06-12 10:00:01
         * title : binding_API
         * uid : 159357451
         */

        private String imei;
        private String time;
        private String title;
        private String uid;

        public String getImei() {
            return imei;
        }

        public void setImei(String imei) {
            this.imei = imei;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }
    }
}
