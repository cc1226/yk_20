package com.zplh.zplh_android_yk.task;

import com.orhanobut.logger.Logger;
import com.zplh.zplh_android_yk.bean.NodeXmlBean;
import com.zplh.zplh_android_yk.bean.TaskErrorBean;
import com.zplh.zplh_android_yk.bean.TaskMessageBean;
import com.zplh.zplh_android_yk.callback.TaskCallback;
import com.zplh.zplh_android_yk.constant.Priority;
import com.zplh.zplh_android_yk.utils.AdbUtils;
import com.zplh.zplh_android_yk.utils.WxIsInstallUtils;
import com.zplh.zplh_android_yk.utils.WxTaskUtils;

/**
 * Author：liaogulong
 * Time: 2018/5/2/002   10:41
 * Description：
 */
public class SuperUpdateRemark extends BaseTask {


    SuperUpdateRemark(Priority priority, int sequence, TaskMessageBean.ContentBean.DataBean taskBean) {
        super(priority, sequence, taskBean);
    }

    @Override
    public void run(TaskCallback callback) throws Exception {
        try {
            WxIsInstallUtils.GetIsInstallWx().IsInstall(getTaskBean().getTask_id());
        } catch (Exception e) {
            callback.onTaskError(this, new TaskErrorBean(TaskErrorBean.EXCEPTION_ERROR).setException(e));
        }
        WxTaskUtils.getWxTaskUtils().switchWxAccount1();
        AdbUtils.getAdbUtils().clickNode(true, new NodeXmlBean.NodeBean().getCustomNode("通讯录", "com.tencent.mm:id/c_z")); //点击通讯录
        callback.onTaskStart(this);
        Logger.d("超级修改备注开始");


    }

    @Override
    public void stop() {

    }


}
