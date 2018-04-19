package com.zplh.zplh_android_yk.presenter;

import com.zplh.zplh_android_yk.callback.TaskCallback;
import com.zplh.zplh_android_yk.callback.TaskPCallback;
import com.zplh.zplh_android_yk.event.TaskEvent;
import com.zplh.zplh_android_yk.module.TaskManager;
import com.zplh.zplh_android_yk.taskmanager.TaskQueue;
import com.zplh.zplh_android_yk.utils.EventBusCreater;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by yong hao zeng on 2018/4/18/018.
 */
public abstract class BaseP implements TaskCallback {

    private TaskManager taskManager;
    TaskQueue taskQueue;
    {

        taskManager = TaskManager.getInstance();
        taskQueue = new TaskQueue(this);
    }


    public abstract void startTask();




    private TaskPCallback taskPCallback;


    @Subscribe(threadMode =  ThreadMode.MAIN)
    public abstract   void taskEvent(TaskEvent event);

    BaseP(TaskPCallback taskPCallback) {
        this.taskPCallback = taskPCallback;
        EventBusCreater.register(this);
    }
}
