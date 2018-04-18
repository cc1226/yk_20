package com.zplh.zplh_android_yk.task;

import android.support.annotation.NonNull;

import com.zplh.zplh_android_yk.bean.TaskMessageBean;
import com.zplh.zplh_android_yk.constant.Priority;
import com.zplh.zplh_android_yk.imp.ITask;


/**
 * 具体任务继承此类
 * Created by yong hao zeng on 2018/4/12.
 */

public abstract class BaseTask<T extends TaskMessageBean.ContentBean.DataBean> implements ITask {

    private Priority priority = Priority.DEFAULT;//优先级
    private int sequence;//加入的顺序
    private T taskBean;

    @Override
    public T getTaskBean() {
        return taskBean;
    }

    BaseTask(Priority priority, int sequence, T taskBean) {
        this.priority = priority;
        this.sequence = sequence;
        this.taskBean = taskBean;
    }

    public BaseTask(T taskBean) {

        this.taskBean = taskBean;
    }

    public BaseTask<T> setTaskBean(T taskBean) {
        this.taskBean = taskBean;
        return this;
    }


    @Override
    public Priority getPriority() {
        return priority;
    }

    @Override
    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @Override
    public int getSequence() {
        return sequence;
    }

    @Override
    public void setSequence(int sequence) {
        this.sequence = sequence;
    }


    /**
     * 根据任务优先级进行排序
     *
     * @param iTask
     * @return
     */
    @Override
    public int compareTo(@NonNull ITask iTask) {
        final Priority me = this.getPriority();
        final Priority it = iTask.getPriority();
        return me == it ? this.getSequence() - iTask.getSequence() :
                it.ordinal() - me.ordinal();
    }


}
