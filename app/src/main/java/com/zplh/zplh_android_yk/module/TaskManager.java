package com.zplh.zplh_android_yk.module;

import android.util.Log;

import com.zplh.zplh_android_yk.base.MyApplication;
import com.zplh.zplh_android_yk.bean.TaskMessageBean;
import com.zplh.zplh_android_yk.constant.SpConstant;
import com.zplh.zplh_android_yk.utils.GsonUtils;
import com.zplh.zplh_android_yk.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yong hao zeng on 2018/4/16/016.
 */
public class TaskManager {

    private static TaskManager instance;
    private List<TaskMessageBean.ContentBean.DataBean> taskMessageList = new ArrayList<>();
    private TaskListener listener;
    public static int TASK_SUCCESS = 1;//任务成功
    public static int TASK_STATES = 2;//任务开始
    public static int TASK_PROGRESS = 3;//任务中止
    public static int TASK_ERROR = 4;//任务异常

    private TaskManager() {
        initTask();
    }

    private void initTask() {

    }

    public void setListener(TaskListener listener) {
        this.listener = listener;

    }

    public static TaskManager getInstance() {
        if (instance == null) {
            synchronized (TaskManager.class) {
                if (instance == null) {
                    instance = new TaskManager();
                }
            }
        }

        return instance;
    }

    public List<TaskMessageBean.ContentBean.DataBean> getTaskList() {
        return taskMessageList;
    }

    public void successTask(int taskid) {
        for (TaskMessageBean.ContentBean.DataBean dataBean : taskMessageList) {
            Log.e("WG", "successTask: 1111");
            if (dataBean.getTask_id() == taskid) {
                dataBean.setStates(TASK_SUCCESS);
                Log.e("WG", "successTask: 1");
                this.listener.onSuccess(dataBean);
            }
        }
    }

    public void startTask(int taskid) {
        for (TaskMessageBean.ContentBean.DataBean dataBean : taskMessageList) {
            Log.e("WG", "successTask: 1222");
            if (dataBean.getTask_id() == taskid) {
                Log.e("WG", "startTask: 2");
                dataBean.setStates(TASK_STATES);
                listener.onStart(dataBean);
            }
        }
    }

    public void errorTask(int taskid) {
        for (TaskMessageBean.ContentBean.DataBean dataBean : taskMessageList) {
            Log.e("WG", "successTask: 13333");
            if (dataBean.getTask_id() == taskid) {
                Log.e("WG", "errorTask: 3");
                dataBean.setStates(TASK_STATES);
                listener.onError(dataBean);
            }
        }
    }

    public void addTask(TaskMessageBean.ContentBean.DataBean task) {
        taskMessageList.add(task);
        //addTask的同时 将任务缓存到首选项中
        String saveTaskData = GsonUtils.toJson(taskMessageList);
        if (listener != null) {
            listener.onAdd(task);

        }
        SPUtils.putString(MyApplication.getContext(), SpConstant.TASK_SP, saveTaskData);
    }

    public interface TaskListener {
        void onSuccess(TaskMessageBean.ContentBean.DataBean dataBean);

        void onError(TaskMessageBean.ContentBean.DataBean dataBean);

        void onStart(TaskMessageBean.ContentBean.DataBean dataBean);

        void onAdd(TaskMessageBean.ContentBean.DataBean dataBean);
    }
}
