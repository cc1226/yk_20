package com.zplh.zplh_android_yk.taskmanager;


import com.zplh.zplh_android_yk.callback.TaskCallback;
import com.zplh.zplh_android_yk.imp.ITask;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 任务管理
 * Created by yong hao zeng on 2018/4/12.
 */

public class TaskQueue {
    private AtomicInteger mAtomicInteger = new AtomicInteger();
    // 任务列表 需要执行的任务
    private PriorityBlockingQueue<ITask> mTaskQueue;

    // 指定线程数量 本项目默认一个线程
    public TaskQueue(int size) {
        mTaskQueue = new PriorityBlockingQueue<>();
    }

    // 开始执行任务。
    public void start(TaskCallback callback) {
        stop();
        // 开始执行任务。
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

}
