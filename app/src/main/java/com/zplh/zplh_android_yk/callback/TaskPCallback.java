package com.zplh.zplh_android_yk.callback;

import com.zplh.zplh_android_yk.bean.TaskMessageBean;

/**
 * Created by yong hao zeng on 2018/4/18/018.
 */
public interface TaskPCallback {
    void onSuccessTask(TaskMessageBean.ContentBean.DataBean task);
    void onErrorTask(TaskMessageBean.ContentBean.DataBean task,Exception e);
    void onStartTask(TaskMessageBean.ContentBean.DataBean dataBean);
    void onTaskProgress(TaskMessageBean.ContentBean.DataBean dataBean,String progress);
}
