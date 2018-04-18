package com.zplh.zplh_android_yk.presenter;

import com.zplh.zplh_android_yk.callback.TaskPCallback;
import com.zplh.zplh_android_yk.constant.Priority;
import com.zplh.zplh_android_yk.event.TaskEvent;
import com.zplh.zplh_android_yk.task.NewFriendTask;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by yong hao zeng on 2018/4/18/018.
 */
public class TaskP extends BaseP {
    public TaskP(TaskPCallback taskPCallback) {
        super(taskPCallback);
    }
    private AtomicInteger mAtomicInteger = new AtomicInteger();

    //生成不同的任务
    @Override
    public void taskEvent(TaskEvent event) {

        switch (event.getTask().getTask_id()){
            case 1:
                NewFriendTask newFriendTask = new NewFriendTask(Priority.DEFAULT, mAtomicInteger.incrementAndGet(), event.getTask());
                taskQueue.add(newFriendTask);

                break;

            }
    }
}
