package zplh_android_yk.zplh.com.yk_20.imp;

import zplh_android_yk.zplh.com.yk_20.bean.BaseTaskBean;
import zplh_android_yk.zplh.com.yk_20.callback.TaskCallback;
import zplh_android_yk.zplh.com.yk_20.constant.Priority;

/**
 * Created by yong hao zeng on 2018/4/17/017.
 */
public class NewFriendTask extends BaseTask {
    public NewFriendTask(Priority priority, int sequence, BaseTaskBean taskBean) {
        super(priority, sequence, taskBean);
    }

    @Override
    public void run(TaskCallback callback) throws Exception {

    }

    @Override
    public void stop() {

    }
}
