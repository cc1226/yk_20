package zplh_android_yk.zplh.com.yk_20.bean;

/**
 * Created by yong hao zeng on 2018/4/14/014.
 */
public class WX_AddFans_TaskBean extends BaseTaskBean {
    int addMaxNumber;
    int addMinNumber;
    public WX_AddFans_TaskBean(String taskId) {
        super(taskId);
    }

    public int getAddMaxNumber() {
        return addMaxNumber;
    }

    public WX_AddFans_TaskBean setAddMaxNumber(int addMaxNumber) {
        this.addMaxNumber = addMaxNumber;
        return this;
    }

    public int getAddMinNumber() {
        return addMinNumber;
    }

    public WX_AddFans_TaskBean setAddMinNumber(int addMinNumber) {
        this.addMinNumber = addMinNumber;
        return this;
    }
}
