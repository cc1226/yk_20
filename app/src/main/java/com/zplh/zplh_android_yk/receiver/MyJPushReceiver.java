package com.zplh.zplh_android_yk.receiver;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zplh.zplh_android_yk.bean.CheckImei;
import com.zplh.zplh_android_yk.bean.TaskMessageBean;
import com.zplh.zplh_android_yk.constant.TaskConstant;
import com.zplh.zplh_android_yk.constant.URLS;
import com.zplh.zplh_android_yk.event.TaskEvent;
import com.zplh.zplh_android_yk.module.TaskManager;
import com.zplh.zplh_android_yk.ui.MainActivity;
import com.zplh.zplh_android_yk.utils.AdbUtils;
import com.zplh.zplh_android_yk.utils.GsonUtils;
import com.zplh.zplh_android_yk.utils.NetUtils;
import com.zplh.zplh_android_yk.utils.SPUtils;
import com.zplh.zplh_android_yk.utils.SystemUtils;
import com.zplh.zplh_android_yk.utils.TimeUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;

import static com.zplh.zplh_android_yk.bean.NetConstant.NET_SUCCESS;
import static com.zplh.zplh_android_yk.constant.SpConstant.UID_SP;
import static com.zplh.zplh_android_yk.constant.TaskConstant.TASK_WX_GO_XIAO_CHENG_XU;

;

/**
 * Created by lichun on 2017/5/27.
 * Description:极光推送获取服务器消息
 */
public class MyJPushReceiver extends BroadcastReceiver {
    private static String TAG = "pushreceiver";
    Gson gson = new Gson();

    Context context;

    @Override
    public void onReceive(final Context context, Intent intent) {

        this.context = context;
        Bundle bundle = intent.getExtras();


        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {

            String content = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);



            if (content != null && content.startsWith("wxversion")) {
                double version = Double.valueOf(SystemUtils.getVersionName(context));
                double ver = Double.valueOf(content.replace("wxversion", ""));
                Logger.t("onReceiver").d("版本升级  ", "当前版本为:" + version + ",目标版本为：" + ver);

                if (ver > version) {
                    Observable.create((ObservableOnSubscribe<File>) emitter -> {
                            Logger.d("开始更新任务");//wxzs1.apk 正式       wxzs.apk测试
                            File filr = NetUtils.getApk("http://103.94.20.102:8087/download/wxzs.apk");
                            if (filr!=null&&filr.exists())
                            emitter.onNext(filr);
                            else emitter.onError(new Exception("下载apk失败"));

                    }).subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.io())
                            .subscribe(new Observer<File>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onNext(File file) {
                                    String path = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/wxykupdata.apk";
                                    Logger.d("下载完成开始安装");
                                    AdbUtils.getAdbUtils().install(path);

                                }

                                @Override
                                public void onError(Throwable e) {
                                  Logger.t("升级失败").d(e.getMessage());
                                }

                                @Override
                                public void onComplete() {

                                }
                            });

                }
            }

            //将任务数据序列化为bean


                if (!TextUtils.isEmpty(extra) && extra.contains(SPUtils.getString(context, UID_SP, "0000"))) {
                    openApplicationFromBackground(context);
                    TaskMessageBean taskBean = gson.fromJson(extra, TaskMessageBean.class);

                    List<TaskMessageBean.ContentBean.DataBean> taskDataBean = taskBean.getContent().getData();
                    if (taskDataBean == null) {
                        Logger.d("结束:" + "taskBean为null");
                        return;
                    }

                    String uid_1 = SPUtils.getString(context, UID_SP, "0000");
                    String[] isAccType = new String[1];

                    //双号手机 默认左边的账号执行任务
                    isAccType[0] = "1";  //默认给左边的手机

                    //默认执行账号更改为下发的设置
                    if (extra.contains(uid_1 + "_1")) {//
                        //                        SPUtils.putInt(context, "is_accType", 1);
                        isAccType[0] = "1";
                    } else if (extra.contains(uid_1 + "_2")) {
                        //                        SPUtils.putInt(context, "is_accType", 2);
                        isAccType[0] = "2";
                    } else if (extra.contains(uid_1 + "_3")) {
                        //                        SPUtils.putInt(context, "is_accType", 3);
                        isAccType[0] = "3";
                    }


                    //可能一次性下发多个任务 进行批量处理
                    for (int a = 0; a < taskDataBean.size(); a++) {
                        taskDataBean.get(a).getParam().setIs_accType(isAccType);
                        if (taskDataBean.size() == 1) {
                            taskDataBean.get(a).setListTask(false);
                        } else {
                            taskDataBean.get(a).setListTask(true);
                        }
                        //任务下发有执行时间
                        if (!TextUtils.isEmpty(taskDataBean.get(a).getTodo_time())) {
                            if (!TextUtils.isEmpty(taskDataBean.get(a).getTodo_time())) {//如果时间过了，马上执行
                                if (TimeUtil.getCurrentTimeMillies() >= Long.valueOf(taskDataBean.get(a).getTodo_time())) {
                                    taskDataBean.get(a).setTodo_time("");
                                }
                            }
                        }

                        taskTime(taskDataBean.get(a));
                    }
                }


        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent
                .getAction())) {
            // 在这里可以做些统计，或者做些其他工作
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
                .getAction())) {
            // 在这里可以自己写代码去定义用户点击后的行为
            Intent i = new Intent(context, MainActivity.class); // 自定义打开的界面
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } else {
            Log.d(TAG, "Unhandled intent - " + intent.getAction());
        }
    }


    @SuppressLint("CheckResult")
    private void setTaskEvent(final TaskMessageBean.ContentBean.DataBean data) {

        Observable.timer(20000, TimeUnit.MILLISECONDS)
                .subscribe(aLong -> EventBus.getDefault()
                        .post(new TaskEvent(data)));


    }

    /**
     * 处理收到的任务
     *
     * @param task
     */
    @SuppressLint("CheckResult")
    private void taskTime(final TaskMessageBean.ContentBean.DataBean task) {

        String todoTimes = task.getTodo_time();//执行时间
        TaskManager.getInstance().addTask(task);
        upData_task_status(task.getLog_id());//反馈到服务器
        //网络请求 判断该lod_id任务是否取消 取消则不在往下进行
        if (task.getTask_id() == TaskConstant.Task_WX_ADD_FRIEND||task.getTask_id() == TaskConstant.TASK_WX_SHOU_FU_KUAN || task.getTask_id() == TASK_WX_GO_XIAO_CHENG_XU ||
                task.getTask_id() == TaskConstant.TASK_WX_TONG_JI_ALL || task.getTask_id() == TaskConstant.TASK_WX_COLLECT_FR ||
                task.getTask_id() == TaskConstant.TASK_WX_READ_FRIEND_CIRCLE || task.getTask_id() == TaskConstant.TASK_WX_LOOK_FR_CIRCLE ||
                task.getTask_id() == TaskConstant.TASK_WX_SETTING || task.getTask_id() == TaskConstant.TASK_WX_PHONE_SET ||
                task.getTask_id() == TaskConstant.TASK_WX_CROWD_TUWEN || task.getTask_id() == TaskConstant.TASK_WX_INIT ||
                task.getTask_id() == TaskConstant.TASK_WX_TIME_START || task.getTask_id() == TaskConstant.TASK_WX_CHECK_COLLECT_CONTENT ||
                task.getTask_id() == TaskConstant.TASK_WX_READ_TENCENT_NEWS || task.getTask_id() == TaskConstant.TASK_WX_TOP_STORIES ||
                task.getTask_id() == TaskConstant.TASK_WX_USER_SEARCH || task.getTask_id() == TaskConstant.TASK_WX_CHECK_WALLET ||
                task.getTask_id() == TaskConstant.TASK_WX_FIND_DEV) {//微信统计任务不需要加随机时间

            if (TextUtils.isEmpty(todoTimes)) {//对于没有设置
                setTaskEvent(task);
            } else {//定时执行
                long time = TimeUtil.getLongTime(Long.parseLong(todoTimes));

                Observable.timer(time * 1000, TimeUnit.MILLISECONDS).subscribe(aLong -> setTaskEvent(task));

            }
        }



    }




    /**
     * 打开应用. 应用在前台不处理,在后台就直接在前台展示当前界面, 未开启则重新启动
     */
    public static void openApplicationFromBackground(Context context) {
        Intent intent;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        if (!list.isEmpty() && list.get(0).topActivity.getPackageName().equals(context.getPackageName())) {
            //此时应用正在前台, 不作处理
            Logger.d("zplh在前台不处理");
            return;
        }
            for (ActivityManager.RunningTaskInfo info : list) {
                if (info.topActivity.getPackageName().equals(context.getPackageName())) {
                    Logger.d("zplh在运行不处理");
                    return;
                }
            }
        Logger.d("zplh在重新打开");
        intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        context.startActivity(intent);
    }

    /**
     * 收到任务后反馈
     */
    public void upData_task_status(String log_id) {
        String uid = SPUtils.getString(context, UID_SP, "0000");


        OkHttpUtils.get().url(URLS.updata_task_status())
                .addParams("log_id", log_id)
                .addParams("uid", uid)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                        Logger.t("任务反馈").d("失败");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        CheckImei checkImei = GsonUtils.fromJson(response, CheckImei.class);
                        if (TextUtils.equals(checkImei.getRet(), NET_SUCCESS)) {
                            Logger.t("任务反馈").d("成功");
                        }

                    }
                });

    }

}
