package com.zplh.zplh_android_yk.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lichun on 2017/7/3.
 * Description:上传好友数量，群数量
 */

public class WxNumBean implements Serializable{

    /**
     * content : {"flock":[{"flock_name":"天气","flock_num":"3"},{"flock_name":"sf","flock_num":"3"}],"friends_num":"16"}
     */

    private ContentBean content;

    @Override
    public String toString() {
        return "WxNumBean{" +
                "content=" + content +
                '}';
    }

    public ContentBean getContent() {
        return content;
    }

    public void setContent(ContentBean content) {
        this.content = content;
    }

    public static class ContentBean implements Serializable{
        @Override
        public String toString() {
            return "ContentBean{" +
                    "friends_num='" + friends_num + '\'' +
                    ", flock=" + flock +
                    '}';
        }

        /**
         * flock : [{"flock_name":"天气","flock_num":"3"},{"flock_name":"sf","flock_num":"3"}]
         * friends_num : 16
         */
        private String account; //目前微信的账号
        private String location; //目前微信的位置

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        private String uid;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        private String friends_num;
        private List<FlockBean> flock;

        public String getFriends_num() {
            return friends_num;
        }

        public void setFriends_num(String friends_num) {
            this.friends_num = friends_num;
        }

        public List<FlockBean> getFlock() {
            return flock;
        }

        public void setFlock(List<FlockBean> flock) {
            this.flock = flock;
        }

        public static class FlockBean implements Serializable{

            public FlockBean(){

            }

            public FlockBean(String flock_name,String flock_num){
                this.flock_name=flock_name;
                this.flock_num=flock_num;
            }

            @Override
            public String toString() {
                return "FlockBean{" +
                        "flock_name='" + flock_name + '\'' +
                        ", flock_num='" + flock_num + '\'' +
                        '}';
            }

            /**
             * flock_name : 天气
             * flock_num : 3
             */

            private String flock_name;
            private String flock_num;

            public String getFlock_name() {
                return flock_name;
            }

            public void setFlock_name(String flock_name) {
                this.flock_name = flock_name;
            }

            public String getFlock_num() {
                return flock_num;
            }

            public void setFlock_num(String flock_num) {
                this.flock_num = flock_num;
            }
        }
    }
}
