package com.zplh.zplh_android_yk.presenter;

import android.content.Context;

import com.orhanobut.logger.Logger;
import com.zplh.zplh_android_yk.base.MyApplication;
import com.zplh.zplh_android_yk.bean.TaskErrorBean;
import com.zplh.zplh_android_yk.callback.TaskPCallback;
import com.zplh.zplh_android_yk.constant.Priority;
import com.zplh.zplh_android_yk.event.TaskEvent;
import com.zplh.zplh_android_yk.imp.ITask;
import com.zplh.zplh_android_yk.task.BaseTask;
import com.zplh.zplh_android_yk.task.InfoNumTask;
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


    @Override
    public void startTask() {
        if (taskQueue!=null){
            taskQueue.start();
        }
    }

    //生成不同的任务
    @Override
    public void taskEvent(TaskEvent event) {
        Logger.t("event").d("收到eventbus："+event.getTask().getTask_id());
        ITask task = null;
        switch (event.getTask().getTask_id()){
            case 1:

                task = new NewFriendTask(Priority.DEFAULT, mAtomicInteger.incrementAndGet(), event.getTask());
                break;
            case 25:
                task = new InfoNumTask(Priority.DEFAULT, mAtomicInteger.incrementAndGet(), event.getTask());
                break;

            }
            if (task!=null)
        taskQueue.add(task);
    }

    @Override
    public void onTaskStart(BaseTask iTask) {
        Logger.t(iTask.getTaskBean().getTask_id()+"").d("任务开始");
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
    public void onTaskError(ITask iTask, TaskErrorBean taskErrorBean) throws Exception {
        Logger.t(iTask.getTaskBean().getTask_id()+"").e(taskErrorBean.getErrorMsg());
        throw  new Exception(taskErrorBean.getException());
    }

    @Override
    public Context getContext() {
        return MyApplication.getContext();
    }
}
