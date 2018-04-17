package com.zplh.zplh_android_yk.bean;

/**
 * 任务错误bean
 * Created by yong hao zeng on 2018/4/14/014.
 */
public class TaskErrorBean {
    public static final int NET_ERROR = 0;//网络错误;
    public static final int EXCEPTION_ERROR = 1;//程序异常错误
    public static final int OTHER_ERROR = 2;//其他错误;

    public int errorType;
    public Exception exception;
    public String errorMsg;

    public TaskErrorBean(int errorType) {
        this.errorType = errorType;
    }

    public int getErrorType() {
        return errorType;
    }

    public TaskErrorBean setErrorType(int errorType) {
        this.errorType = errorType;
        return this;
    }

    public Exception getException() {
        return exception;
    }

    public TaskErrorBean setException(Exception exception) {
        this.exception = exception;
        return this;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public TaskErrorBean setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        return this;
    }
}
