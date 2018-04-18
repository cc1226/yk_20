package com.zplh.zplh_android_yk.presenter;

import com.zplh.zplh_android_yk.callback.TaskPCallback;
import com.zplh.zplh_android_yk.event.TaskEvent;

/**
 * Created by yong hao zeng on 2018/4/18/018.
 */
public class TaskP extends BaseP {
    public TaskP(TaskPCallback taskPCallback) {
        super(taskPCallback);
    }


    //生成不同的任务
    @Override
    public void taskEvent(TaskEvent event) {

        switch (event.getTask().getTask_id()){


            }
    }
}
