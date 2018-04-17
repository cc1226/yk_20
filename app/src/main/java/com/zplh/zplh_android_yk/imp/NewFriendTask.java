package com.zplh.zplh_android_yk.imp;


import com.zplh.zplh_android_yk.bean.BaseTaskBean;
import com.zplh.zplh_android_yk.callback.TaskCallback;
import com.zplh.zplh_android_yk.constant.Priority;

/**
 * Created by yong hao zeng on 2018/4/17/017.
 */
public class NewFriendTask extends BaseTask {
    public NewFriendTask(Priority priority, int sequence, BaseTaskBean taskBean) {
        super(priority, sequence, taskBean);
    }

    @Override
    public void run(TaskCallback callback) throws Exception {

    }

    @Override
    public void stop() {

    }
}
