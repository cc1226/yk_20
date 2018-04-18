package com.zplh.zplh_android_yk.taskmanager;


import android.content.Context;

import com.orhanobut.logger.Logger;
import com.zplh.zplh_android_yk.base.MyApplication;
import com.zplh.zplh_android_yk.bean.TaskErrorBean;
import com.zplh.zplh_android_yk.callback.TaskCallback;
import com.zplh.zplh_android_yk.imp.ITask;
import com.zplh.zplh_android_yk.task.BaseTask;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 任务管理
 * Created by yong hao zeng on 2018/4/12.
 */

public class TaskQueue implements TaskCallback {
    private AtomicInteger mAtomicInteger = new AtomicInteger();
    // 任务列表 需要执行的任务
    private PriorityBlockingQueue<ITask> mTaskQueue;

    // 本项目默认一个线程
    public TaskQueue() {
        mTaskQueue = new PriorityBlockingQueue<>();
    }

    // 开始执行任务。
    public void start(TaskCallback callback) {
        // 开始按照序列执行任务。
            stop();
    }


    // 停止所有任务
    public void stop() {

    }

    // 添加任务。
    public <T extends ITask> int add(T task) {
        if (!mTaskQueue.contains(task)) {
            task.setSequence(mAtomicInteger.incrementAndGet());
            mTaskQueue.add(task);
        }
        // 返回等待执行任务
        return mTaskQueue.size();
    }




    @Override
    public void onTaskStart(BaseTask iTask) {

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
        Logger.t(iTask.getTaskBean().getTask_id()+"").d(progress);
        if (Thread.currentThread().isInterrupted()) {
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
    public void onTaskError(ITask iTask, TaskErrorBean taskErrorBean)  {
        Logger.t(iTask.getTaskBean().getTask_id()+"").e(taskErrorBean.getErrorMsg());
    }

    @Override
    public Context getContext() {
        return MyApplication.getContext();
    }
}
