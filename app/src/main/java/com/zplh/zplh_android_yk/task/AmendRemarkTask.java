package com.zplh.zplh_android_yk.task;

import com.zplh.zplh_android_yk.bean.TaskMessageBean;
import com.zplh.zplh_android_yk.callback.TaskCallback;
import com.zplh.zplh_android_yk.constant.Priority;
import com.zplh.zplh_android_yk.utils.AdbUtils;
import com.zplh.zplh_android_yk.utils.WxIsInstallUtils;
import com.zplh.zplh_android_yk.utils.WxTaskUtils;

/**
 * Created by Administrator on 2018/4/20/020.
 */

public class AmendRemarkTask extends BaseTask {

    public AmendRemarkTask(Priority priority, int sequence, TaskMessageBean.ContentBean.DataBean taskBean) {
        super(priority, sequence, taskBean);
    }

    @Override
    public void run(TaskCallback callback) throws Exception {
        if (WxIsInstallUtils.GetIsInstallWx().IsInstall(getTaskBean().getTask_id())){
            WxTaskUtils.getWxTaskUtils().switchWxAccount();
            AdbUtils.getAdbUtils().click4xy(153, 822, 207, 847);
            callback.onTaskStart(this);

        }
    }

    @Override
    public void stop() {

    }
}
