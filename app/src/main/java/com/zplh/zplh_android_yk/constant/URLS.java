package com.zplh.zplh_android_yk.constant;

/**
 * Created by lichun on 2017/6/18.
 * Description:
 */

public class URLS {

    public static final String url = "http://103.94.20.102:8087/yk/index.php/";//正式
    public static final String pic_vo = "http://103.94.20.102:8087/yk";//正式

    //    public static final String url = "http://103.94.20.102:8087/yk_test/index.php/";//测试
    //    public static final String pic_vo = "http://103.94.20.102:8087/yk_test";//测试
    public static final String pic_vo_flock = "http://103.94.20.102:8087";

    //    public static final String url = "http://192.168.10.135/yk/index.php/";//周文鹏本地
    //    public static final String pic_vo = "http://192.168.10.135/yk";//测试

    /**
     * 获取手机号码
     */
    public static final String phone_url = "http://103.94.20.101:8087/api_wechat/index.php";

    /**
     * 绑定设备
     */
    public static final String binding() {
        return url + "home/binding/index";
    }

    /**
     * 判断设备
     */
    public static final String isbinding() {
        return url + "home/binding/check_imei";
    }

    /**
     * 拉完群上传数据
     */
    public static final String upddateGroup() {
        return url + "home/Api/upddate_group";
    }


    /**
     * 版本升级
     */
    public static final String updata() {
        return url + "home/ApiAndroid/batch_test";
    }

    /**
     * 统计微信好友 群数量
     */
    public static final String statictis_crowd() {
        return url + "home/ApiAndroid/statistic_crowd";
    }

    /**
     * 微信养号互撩
     */
    public static final String wechat_list() {
        return url + "home/ApiAndroid/wechat_list";
    }

    /**
     * 发送状态给后台（微信）执行成功
     *
     * @param
     * @param
     * @return
     */
    public static final String getResut() {
        return url + "home/ApiAndroid/hasExecutedDevices";
    }

    /**
     * 收到任务后反馈
     *
     * @return
     */
    public static final String updata_task_status() {
        return url + "home/ApiAndroid/updata_task_status";
    }


    /**
     * 判断是否可以更新（只能允许10台手机更新）
     *
     * @return
     */
    public static final String version_update_go() {
        return url + "home/ApiAndroid/version_update_go";
    }

    /**
     * 更新完成反馈（只能允许10台手机更新）
     *
     * @return
     */
    public static final String version_update_back() {
        return url + "home/ApiAndroid/version_update_back";
    }

    //http://103.94.20.102:8087/yk_test/index.php/home/ApiAndroid/is_abolish?log_id=1525

    /**
     * 把微信联系人信息上传到服务器，和从服务器下载联系人数据进行配备
     *
     * @return
     */
    public static final String statictis_wx_message_store() {
        return url + "home/ApiAndroid/profile_store";
    }

    /**
     * 把微信新增的好友拉入群中
     *
     * @return
     */
    public static final String wxNewFriendsToQun() {
        return url + "home/ApiAndroid/getQrCodeUrl";
    }

    /**
     * 把微信新增的好友拉入群中
     *
     * @return
     */
    public static final String wxNewFriendsToQunUpData() {
        return url + "home/ApiAndroid/updateQrCode";
    }

    /**
     * 微信 新好友统计
     *
     * @return
     */
    public static final String wxNewstatictis_crowd() {
        return url + "home/ApiAndroid/accountStatistic";
    }

    /**
     * 把微信新增的好友申请数
     *
     * @return
     */
    public static final String wxAccountApply() {
        return url + "home/ApiAndroid/accountApplyCountInc";
    }

    /**
     * 自动回复
     *
     * @return
     */
    public static final String ziDongReply() {
        return url + "home/ApiAndroid/getChatTexts";
    }
}
