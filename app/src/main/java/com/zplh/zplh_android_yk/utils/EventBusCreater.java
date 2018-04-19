package com.zplh.zplh_android_yk.utils;

import com.zplh.zplh_android_yk.event.TaskEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by yong hao zeng on 2018/4/19/019.
 */
public class EventBusCreater {

    public static void post(TaskEvent taskEvent){
        EventBus.getDefault().post(taskEvent);
    }

    public static void register(Object o){
        EventBus.getDefault().register(o);
    }

    public static void unRegister(Object o){
        EventBus.getDefault().unregister(o);
    }

}
