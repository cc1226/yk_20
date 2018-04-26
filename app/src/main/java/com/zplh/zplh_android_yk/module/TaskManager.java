package com.zplh.zplh_android_yk.module;

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
    private static TaskListener listener;
    private TaskManager() {
        initTask();
    }

    private void initTask() {

    }
    public  static void setListener(TaskListener listener){
        TaskManager.listener = listener;

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

    public void addTask(TaskMessageBean.ContentBean.DataBean task) {
        taskMessageList.add(task);
        //addTask的同时 将任务缓存到首选项中
        String saveTaskData = GsonUtils.toJson(taskMessageList);
        if (listener!=null){
            listener.onAdd(task);
        }
        SPUtils.putString(MyApplication.getContext(), SpConstant.TASK_SP, saveTaskData);
    }

    interface TaskListener{
        void onSuccess(TaskMessageBean.ContentBean.DataBean dataBean);
        void onError(TaskMessageBean.ContentBean.DataBean dataBean);
        void onStart(TaskMessageBean.ContentBean.DataBean dataBean);
        void onAdd(TaskMessageBean.ContentBean.DataBean dataBean);
    }
}
