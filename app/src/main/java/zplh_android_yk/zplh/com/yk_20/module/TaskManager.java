package zplh_android_yk.zplh.com.yk_20.module;

import java.util.ArrayList;
import java.util.List;

import zplh_android_yk.zplh.com.yk_20.base.MyApplication;
import zplh_android_yk.zplh.com.yk_20.bean.TaskMessageBean;
import zplh_android_yk.zplh.com.yk_20.constant.SpConstant;
import zplh_android_yk.zplh.com.yk_20.utils.GsonUtils;
import zplh_android_yk.zplh.com.yk_20.utils.SPUtils;

/**
 * Created by yong hao zeng on 2018/4/16/016.
 */
public class TaskManager {

    private static TaskManager instance;
    private List<TaskMessageBean.ContentBean.DataBean> taskMessageList = new ArrayList<>();
    private TaskManager() {
            initTask();
    }

    private void initTask() {

    }

    public static TaskManager getInstance(){
        if (instance == null) {
            synchronized (TaskManager.class) {
                if (instance == null) {
                    instance = new TaskManager();
                }
            }
        }

        return instance;
    }

    public List<TaskMessageBean.ContentBean.DataBean> getTaskList(){

        return taskMessageList;
    }

    public void addTask(TaskMessageBean.ContentBean.DataBean task){
        taskMessageList.add(task);
        //addTask的同时 将任务缓存到首选项中
        String saveTaskData = GsonUtils.toJson(taskMessageList);

        SPUtils.putString(MyApplication.getContext(), SpConstant.TASK_SP, saveTaskData);
    }
}
