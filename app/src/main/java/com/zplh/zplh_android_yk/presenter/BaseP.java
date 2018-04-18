package com.zplh.zplh_android_yk.presenter;

import com.zplh.zplh_android_yk.callback.TaskPCallback;
import com.zplh.zplh_android_yk.event.TaskEvent;
import com.zplh.zplh_android_yk.module.TaskManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by yong hao zeng on 2018/4/18/018.
 */
public abstract class BaseP  {

    public BaseP() {
        EventBus.getDefault().register(this);

    }

    private TaskManager taskManager;

    {

        taskManager = TaskManager.getInstance();
    }
    private TaskPCallback taskPCallback;


    @Subscribe(threadMode =  ThreadMode.MAIN)
    public abstract void taskEvent(TaskEvent event);

    BaseP(TaskPCallback taskPCallback) {
        this.taskPCallback = taskPCallback;
    }
}
