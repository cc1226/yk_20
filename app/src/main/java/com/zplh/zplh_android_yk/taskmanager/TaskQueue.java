package com.zplh.zplh_android_yk.taskmanager;


import com.orhanobut.logger.Logger;
import com.zplh.zplh_android_yk.bean.TaskErrorBean;
import com.zplh.zplh_android_yk.callback.TaskCallback;
import com.zplh.zplh_android_yk.imp.ITask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * 任务管理
 * Created by yong hao zeng on 2018/4/12.
 */

public class TaskQueue  {
    private AtomicInteger mAtomicInteger = new AtomicInteger();
    // 任务列表 需要执行的任务
    private PriorityBlockingQueue<ITask> mTaskQueue;
    private TaskCallback callback;
    private ITask currentItTask;
    private ExecutorService executor;
    private AtomicBoolean isRunning = new AtomicBoolean();//是否正在执行任务
    private boolean isStart;//是否可以任务
    private Observable<ITask> iTaskObservable;

    // 本项目默认一个线程
    public TaskQueue(TaskCallback callback) {
        mTaskQueue = new PriorityBlockingQueue<>();
        executor = Executors.newSingleThreadExecutor();

        this.callback = callback;
    }

    // 开始执行任务。
    public void start() {
        // 开始按照序列执行任务。
        stopCurrentTask();
        isStart = true;



        iTaskObservable = Observable.create(emitter -> {

            while (!isRunning.get())
            emitter.onNext(mTaskQueue.take());
            emitter.onNext( mTaskQueue.take());
        });
        iTaskObservable
                .subscribeOn(Schedulers.from(executor))
                .subscribe(new Observer<ITask>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                            }
                            @Override
                            public void onNext(ITask iTask) {
                                currentItTask = iTask;
                                Logger.t("执行一个任务").d(iTask.getTaskBean().getTask_id());
                                try {
                                    iTask.run(callback);
                                } catch (Exception e) {
                                    try {
                                        callback.onTaskError(currentItTask, new TaskErrorBean(TaskErrorBean.EXCEPTION_ERROR).setException(e));
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }

                                }finally {
                                    onComplete();
                                }
                            }
                            @Override
                            public void onError(Throwable e) {
                                Logger.d("Rx错误" + e.getLocalizedMessage());
                            }

                            @Override
                            public void onComplete() {
                                Logger.t("Rx").d("结束");
                                mTaskQueue.remove(currentItTask);
                                isRunning.compareAndSet(true,false);
                            }
                        });
    }






    // 停止当前任务
    public void stopCurrentTask() {
        if (currentItTask!=null)
            currentItTask.stop();
        isStart = false;
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
