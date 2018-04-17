package zplh_android_yk.zplh.com.yk_20.bean;

/**
 * Created by yong hao zeng on 2018/4/14/014.
 */
public class BaseTaskBean  {
    private String taskId;

    public BaseTaskBean(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
    }

    public BaseTaskBean setTaskId(String taskId) {
        this.taskId = taskId;
        return this;
    }
}
