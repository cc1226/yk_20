package com.zplh.zplh_android_yk.event;

import com.zplh.zplh_android_yk.bean.TaskMessageBean;

/**
 * Created by yong hao zeng on 2018/4/18/018.
 */
public class TaskEvent {
    TaskMessageBean.ContentBean.DataBean task;

    public TaskMessageBean.ContentBean.DataBean getTask() {
        return task;
    }

    public TaskEvent setTask(TaskMessageBean.ContentBean.DataBean task) {
        this.task = task;
        return this;
    }

    public TaskEvent(TaskMessageBean.ContentBean.DataBean task) {
        this.task = task;
    }
}
