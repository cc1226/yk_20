package com.zplh.zplh_android_yk.task;


import com.zplh.zplh_android_yk.bean.TaskErrorBean;
import com.zplh.zplh_android_yk.bean.TaskMessageBean;
import com.zplh.zplh_android_yk.callback.TaskCallback;
import com.zplh.zplh_android_yk.constant.Priority;

/**
 * Created by yong hao zeng on 2018/4/17/017.
 */
public class NewFriendTask extends BaseTask {
    public NewFriendTask(Priority priority, int sequence, TaskMessageBean.ContentBean.DataBean taskBean) {
        super(priority, sequence, taskBean);
    }

    @Override
    public void run(TaskCallback callback)throws Exception  {

            callback.onTaskStart(this);
            int number= 0;
            while (true) {
                number++;
                Thread.sleep(1000);
                if (number > 10){
                    callback.onTaskError(this, new TaskErrorBean(TaskErrorBean.OTHER_ERROR).setErrorMsg("wwwww"));
                    break;
            }else {
                    callback.onTaskProgress(this,"做了什么");

                }
            }

            while (true){
                callback.onTaskProgress(this,"程序依旧在运行");
            }

    }

    @Override
    public void stop() {
        super.stop();
    }
}
