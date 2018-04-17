package com.zplh.zplh_android_yk.taskmanager;

import android.content.Context;

import com.orhanobut.logger.Logger;
import com.zplh.zplh_android_yk.bean.TaskErrorBean;
import com.zplh.zplh_android_yk.callback.TaskCallback;
import com.zplh.zplh_android_yk.imp.BaseTask;
import com.zplh.zplh_android_yk.imp.ITask;


/**
 * 任务执行线程
 * Created by yong hao zeng on 2018/4/12.
 */

public class TaskExecutor extends Thread implements TaskCallback {

    private final ITask iTask;
    // 是否在执行任务
    private boolean isRunning = true;


    public TaskExecutor(ITask task) {
        iTask = task;
    }


    // 停止线程 任务
    public void quit() {
        isRunning = false;
        interrupt();
    }

    @Override
    public void run() {
        //执行任务
        try {
            iTask.run(this);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /**
     * 任务成功的时候
     *
     * @param iTask
     */

    @Override
    public void onTaskSuccess(BaseTask iTask) {

    }

    /**
     * 应当及时回掉progress 在每一步操作完成完之后  令其可以判断是否被终中止
     *
     * @param iTask
     * @param progress
     */

    @Override
    public void onTaskProgress(BaseTask iTask, String progress) throws Exception {
        //当每个进度完成的时候 判断一下interrupt状态 在合适的时候停止该任务
        Logger.t(iTask.getTaskBean().getTaskId()).d(progress);
        if (this.isInterrupted()) {
            throw new Exception("主动停止了任务");
        }
    }


    /**
     * 执行错误 应当结束任务流程 做失败处理
     *
     * @param iTask
     * @param taskErrorBean
     */
    @Override
    public void onTaskError(BaseTask iTask, TaskErrorBean taskErrorBean) throws Exception {

    }


    @Override
    public void onTaskStart(BaseTask iTask) {

    }

    @Override
    public Context getContext() {
        return null;
    }
}
