package com.zplh.zplh_android_yk.imp;


import com.zplh.zplh_android_yk.bean.TaskMessageBean;
import com.zplh.zplh_android_yk.callback.TaskCallback;
import com.zplh.zplh_android_yk.constant.Priority;

/**
 * 此类为任务结构 每个任务要实现此接口
 * Created by yong hao zeng on 2018/4/12.
 */

public interface ITask extends Comparable<ITask> {
    /**
     * 具体执行类要复写次方法来执行具体任务
     *
     * @param callback
     * @throws Exception
     */
    void run(TaskCallback callback) throws Exception;
    TaskMessageBean.ContentBean.DataBean getTaskBean();
    void setPriority(Priority priority);

    Priority getPriority();

    void setSequence(int sequence);

    int getSequence();

    void stop();

}
