package com.zplh.zplh_android_yk.presenter;

import android.content.Context;
import android.util.Log;

import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zplh.zplh_android_yk.base.MyApplication;
import com.zplh.zplh_android_yk.bean.TaskErrorBean;
import com.zplh.zplh_android_yk.bean.TaskMessageBean;
import com.zplh.zplh_android_yk.callback.TaskPCallback;
import com.zplh.zplh_android_yk.constant.Priority;
import com.zplh.zplh_android_yk.constant.URLS;
import com.zplh.zplh_android_yk.event.TaskEvent;
import com.zplh.zplh_android_yk.imp.ITask;
import com.zplh.zplh_android_yk.module.TaskManager;
import com.zplh.zplh_android_yk.task.BaseTask;
import com.zplh.zplh_android_yk.task.HavenoTash;
import com.zplh.zplh_android_yk.task.InfoNumTask;
import com.zplh.zplh_android_yk.task.NewFriendTask;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Response;

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
        if (taskQueue != null) {
            taskQueue.start();
        }
    }

    //生成不同的任务
    @Override
    public void taskEvent(TaskEvent event) {
        Logger.t("event").d("收到eventbus：" + event.getTask().getTask_id());
        Log.e("WG", "taskEvent: " + event.getTask().getParam().getRecord_time_s());
        ITask task = null;
        switch (event.getTask().getTask_id()) {
            case 5:
                task = new NewFriendTask(Priority.DEFAULT, mAtomicInteger.incrementAndGet(), event.getTask());
                break;
            case 25:
                task = new InfoNumTask(Priority.DEFAULT, mAtomicInteger.incrementAndGet(), event.getTask());
                break;
            case 31:
//                task = new HavenoTash(Priority.DEFAULT, mAtomicInteger.incrementAndGet(), event.getTask());

                break;

        }
        if (task != null)
            taskQueue.add(task);
    }


    @Override
    public void onTaskStart(BaseTask iTask) {
        String start_logid = "";
        int start_taskid = 0;
        Logger.t(iTask.getTaskBean().getTask_id() + "").d("任务开始");
        //todo 网络  标记taskmanager的状态
        for (TaskMessageBean.ContentBean.DataBean dataBean : TaskManager.getInstance().getTaskList()) {
            if (dataBean.getLog_id().equals(iTask.getTaskBean().getLog_id())) {
                start_logid = dataBean.getLog_id();
                start_taskid = dataBean.getTask_id();
            }
        }
        try {
            Response json = OkHttpUtils.post().url(URLS.updata_task_status()).addParams("log_id", start_logid).addParams("uid", String.valueOf(start_taskid)).build().execute();
            if (json.code() == 200) {
                Log.e("WG", "任务开始 上传成功");
                TaskManager.getInstance().startTask(start_taskid);
            }
        } catch (IOException e) {
            e.printStackTrace();
            TaskManager.getInstance().errorTask(start_taskid);
            Log.e("WG", "上传任务开始 异常 " + e.toString());
        }
    }

    /**
     * 任务成功的时候
     *
     * @param iTask
     */

    @Override
    public void onTaskSuccess(BaseTask iTask) {
        // TODO: 2018/4/25/025 网络
        String log_id = "";
        int task_id = 0;
        for (TaskMessageBean.ContentBean.DataBean dataBean : TaskManager.getInstance().getTaskList()) {
            log_id = dataBean.getLog_id();
            task_id = dataBean.getTask_id();
        }
        try {
            Response json = OkHttpUtils.post().url(URLS.getResut()).addParams("log_id", log_id).addParams("uid", String.valueOf(task_id)).build().execute();
            if (json.code() == 200) {
                Log.e("WG", "上传任务成功 完成 ");
                TaskManager.getInstance().successTask(task_id);
            }
        } catch (IOException e) {
            e.printStackTrace();
            TaskManager.getInstance().errorTask(task_id);
            Log.e("WG", "上传任务成功 异常 " + e.toString());
        }
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
        Logger.t(iTask.getTaskBean().getTask_id() + "").d(progress);
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
        int taskid = 0;
        Logger.t(iTask.getTaskBean().getTask_id() + "").e(taskErrorBean.getErrorMsg());
        for (TaskMessageBean.ContentBean.DataBean dataBean : TaskManager.getInstance().getTaskList()) {
            taskid = dataBean.getTask_id();
        }
        TaskManager.getInstance().errorTask(taskid);
        throw new Exception(taskErrorBean.getException());
    }

    @Override
    public Context getContext() {
        return MyApplication.getContext();
    }
}
