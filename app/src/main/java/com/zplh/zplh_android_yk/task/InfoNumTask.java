package com.zplh.zplh_android_yk.task;

import com.zplh.zplh_android_yk.base.MyApplication;
import com.zplh.zplh_android_yk.bean.TaskErrorBean;
import com.zplh.zplh_android_yk.bean.TaskMessageBean;
import com.zplh.zplh_android_yk.callback.TaskCallback;
import com.zplh.zplh_android_yk.constant.Priority;
import com.zplh.zplh_android_yk.utils.AdbUtils;
import com.zplh.zplh_android_yk.utils.SPUtils;
import com.zplh.zplh_android_yk.utils.TimeUtil;
import com.zplh.zplh_android_yk.utils.WxIsInstallUtils;
import com.zplh.zplh_android_yk.utils.WxTaskUtils;

import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2018/4/18/018.
 */

public class InfoNumTask extends BaseTask {

    private int sendAccountType;

    public InfoNumTask(Priority priority, int sequence, TaskMessageBean.ContentBean.DataBean taskBean) {
        super(priority, sequence, taskBean);
    }

    @Override
    public void run(TaskCallback callback) throws Exception {
        if (WxIsInstallUtils.GetIsInstallWx().IsInstall(getTaskBean().getTask_id())) {
            callback.onTaskStart(this);
            WxTaskUtils.getWxTaskUtils().switchWxAccount();
            WxTaskUtils.getWxTaskUtils().getUsingWxAccount();
            WxTaskUtils.getWxTaskUtils().backHome();
            TimeUnit.SECONDS.sleep(1);
            AdbUtils.getAdbUtils().click(187, 839);
            WxTaskUtils.getWxTaskUtils().statistics();
            sendAccountType = SPUtils.getInt(MyApplication.getContext(), "is_accType", 0);
            if (sendAccountType == 3) {
                WxTaskUtils.getWxTaskUtils().switchWxAccount();
                WxTaskUtils.getWxTaskUtils().getUsingWxAccount();
                WxTaskUtils.getWxTaskUtils().backHome();
                TimeUnit.SECONDS.sleep(1);
                AdbUtils.getAdbUtils().click(187, 839);
                WxTaskUtils.getWxTaskUtils().statistics();
            }
            callback.onTaskSuccess(this);
        } else {
            callback.onTaskError(this, new TaskErrorBean(TaskErrorBean.OTHER_ERROR).setErrorMsg("统计失败"));
        }
    }

    @Override
    public void stop() {

    }
}
