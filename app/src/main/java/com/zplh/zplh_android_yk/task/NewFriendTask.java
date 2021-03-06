package com.zplh.zplh_android_yk.task;


import android.app.Activity;
import android.util.Log;

import com.zplh.zplh_android_yk.base.MyApplication;
import com.zplh.zplh_android_yk.bean.TaskErrorBean;
import com.zplh.zplh_android_yk.bean.TaskMessageBean;
import com.zplh.zplh_android_yk.callback.TaskCallback;
import com.zplh.zplh_android_yk.constant.Priority;
import com.zplh.zplh_android_yk.utils.GetPhoneAdd;
import com.zplh.zplh_android_yk.utils.ShowToast;
import com.zplh.zplh_android_yk.utils.WxIsInstallUtils;
import com.zplh.zplh_android_yk.utils.WxTaskUtils;

/**
 * Created by yong hao zeng on 2018/4/17/017.
 */
public class NewFriendTask extends BaseTask {
    int conunt = 0;
    public boolean flg;
    //one_add_num_s = "";//通讯录加好友 开始
    //one_add_num_e = "";//通讯录加好友 结束
    //add_interval_time_s;//单词加好友间隔时间 开始
    //add_interval_time_e;//单次加好友结束间隔时间
    //contact_verify_msg = "";//申请添加好友的发送内容
    // day_add_num = "";	/*10*///一个微信号每天最多请求加好友次数:(通讯录加好友)
    //one_add_num = "";	/*13*///一个微信号每次任务最多请求加好友次数(通讯录加好友)
    public NewFriendTask(Priority priority, int sequence, TaskMessageBean.ContentBean.DataBean taskBean) {
        super(priority, sequence, taskBean);
    }

    @Override
    public void run(TaskCallback callback) throws Exception {
        WxIsInstallUtils.GetIsInstallWx().IsInstall(getTaskBean().getTask_id());
        Log.e("WG", "run: 添加联系人开始了");
        callback.onTaskStart(this);
        WxTaskUtils.getWxTaskUtils().openWxIsHome();
        WxTaskUtils.getWxTaskUtils().switchWxAccount1();
        GetPhoneAdd getPhoneAdd = new GetPhoneAdd(flg, getTaskBean().getParam().getOne_add_num_s(),
                getTaskBean().getParam().getOne_add_num_e(), getTaskBean().getParam().getAdd_interval_time_s(),
                getTaskBean().getParam().getAdd_interval_time_e(), getTaskBean().getParam().getContact_verify_msg(),
                getTaskBean().getParam().getDay_add_num(), getTaskBean().getParam().getOne_add_num());
        if (!WxIsInstallUtils.GetIsInstallWx().getIsAccountIsOk()) {
            Log.e("WG", "有账号登录异常");
            flg = true;
        }
        while (true) {
            Log.e("WG", "run: 正在清理手机联系人请稍后...");
            getPhoneAdd.DeletPhone(MyApplication.getContext());
            if (WxTaskUtils.getWxTaskUtils().getContactCount(MyApplication.getContext()) < 1) {
                break;
            } else {
                Log.d("WG", "通讯录的好友数量是" + WxTaskUtils.getWxTaskUtils().getContactCount(MyApplication.getContext()));
                getPhoneAdd.DeletPhone(MyApplication.getContext());
            }
        }
        getPhoneAdd.getPhoneAdd();
        getPhoneAdd.addFriendsReturn();
        getPhoneAdd.toForUnRead();
        callback.onTaskSuccess(this);
    }

    @Override
    public void stop() {
        Thread.interrupted();
    }
}
