package com.zplh.zplh_android_yk.taskmanager;

import com.zplh.zplh_android_yk.bean.TaskErrorBean;
import com.zplh.zplh_android_yk.callback.TaskCallback;
import com.zplh.zplh_android_yk.imp.ITask;


/**
 * 任务执行线程
 * Created by yong hao zeng on 2018/4/12.
 */

public class TaskExecutor extends Thread  {

    TaskCallback callback;

    private final ITask iTask;


    public TaskExecutor(ITask task,TaskCallback callback) {
        iTask = task;
        this.callback = callback;
    }


    // 停止线程 任务
    public void quit() {
        interrupt();
    }

    @Override
    public void run() {
        //执行任务
        try {
            iTask.run(callback);
        } catch (Exception e) {
            //异常要接住 抛出
            callback.onTaskError( iTask,new TaskErrorBean(TaskErrorBean.EXCEPTION_ERROR));
            e.printStackTrace();
        }


    }

}
