package com.zplh.zplh_android_yk.receiver;

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
import com.zplh.zplh_android_yk.MainActivity;
import com.zplh.zplh_android_yk.bean.CheckImei;
import com.zplh.zplh_android_yk.bean.TaskMessageBean;
import com.zplh.zplh_android_yk.constant.TaskConstant;
import com.zplh.zplh_android_yk.constant.URLS;
import com.zplh.zplh_android_yk.event.TaskEvent;
import com.zplh.zplh_android_yk.module.TaskManager;
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
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;

import static com.zplh.zplh_android_yk.bean.NetConstant.NET_SUCCESS;
import static com.zplh.zplh_android_yk.constant.SpConstant.IMEI_SP;
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
        openApplicationFromBackground(context);

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {

            String content = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
            System.out.println("收到了自定义消息@@消息内容是:" + content);
            System.out.println("收到了自定义消息@@消息extra是:" + extra);


            if (content != null && content.startsWith("wxversion")) {
                double version = Double.valueOf(SystemUtils.getVersionName(context));
                double ver = Double.valueOf(content.replace("wxversion", ""));
                Logger.t("onReceiver").d("版本升级  ", "当前版本为:" + version + ",目标版本为：" + ver);

                if (ver > version) {
                    Observable.create(new ObservableOnSubscribe<File>() {
                        @Override
                        public void subscribe(ObservableEmitter<File> emitter) throws Exception {
                                Logger.d("开始更新任务");//wxzs1.apk 正式       wxzs.apk测试
//                                String uid = SPUtils.getString(context, UID_SP, "0001");
//                                int sleepTime = Integer.valueOf(uid);
//                                if (sleepTime > 0) {
//                                    Logger.d("等待" + sleepTime + "秒下载");
//                                    Thread.sleep(sleepTime * 1000);
                                File filr = NetUtils.getApk("http://103.94.20.102:8087/download/wxzs.apk");
                                if (filr!=null&&filr.exists())
                                emitter.onNext(filr);
                                else emitter.onError(new Exception("下载apk失败"));

                        }
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
                                    AdbUtils.install(path);

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

            if (SPUtils.getBoolean(context, IMEI_SP, false)) {//绑定过设备才执行任务
                //判断uid是否一样 是一样的才执行任务






                if (!TextUtils.isEmpty(extra) && extra.contains(SPUtils.getString(context, UID_SP, "0000"))) {
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

//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 1:
//                    TaskMessageBean.ContentBean.DataBean dataBean = (TaskMessageBean.ContentBean.DataBean) msg.obj;
//                    setTaskEvent(dataBean);
//                    break;
//            }
//        }
//    };

    private void setTaskEvent(final TaskMessageBean.ContentBean.DataBean data) {

        Disposable subscribe = Observable.timer(20000, TimeUnit.MILLISECONDS).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                EventBus.getDefault().post(new TaskEvent(data));
            }
        });


//        Timer timer = new Timer();
//        TimerTask timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                Intent intent2 = new Intent();
//                intent2.setAction(MyConstains.Broadcast_Task);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("messageBean", data);
//                intent2.putExtras(bundle);
//                context.sendBroadcast(intent2);
//            }
//        };
//        timer.schedule(timerTask, 20000);

    }

    /**
     * 处理收到的任务
     *
     * @param task
     */
    private void taskTime(final TaskMessageBean.ContentBean.DataBean task) {

        String todoTimes = task.getTodo_time();//执行时间
        //        String interval_time = task.getParam().getInterval_time();//任务执行的间隔时间

        //        List<String> rangeList = task.getRange();//判断是否是指定设备执行任务

        //        if (rangeList != null && (rangeList.size() == 0 || rangeList.toString().contains(SPUtils.getString(context, UID_SP, "0000")))) {
        //            if (task.getTask_id() == TASK_WX_GO_XIAO_CHENG_XU || task.getTask_id() == TASK_WX_QUN_TU_WEN) {
        //                downImg(task);
        //            }




        //            stateRenwuBean = new StateRenwuBean(task.getTask_id(), Integer.parseInt(task.getLog_id()), "任务待执行", timeUtil.getDtae());
        //            dao.addPerson(stateRenwuBean);

        //每个任务进行缓存和持久化
        TaskManager.getInstance().addTask(task);



        upData_task_status(task.getLog_id());//反馈到服务器
        //网络请求 判断该lod_id任务是否取消 取消则不在往下进行
        if (task.getTask_id() == TaskConstant.TASK_WX_SHOU_FU_KUAN || task.getTask_id() == TASK_WX_GO_XIAO_CHENG_XU ||
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

                Disposable subscribe = Observable.timer(time * 1000, TimeUnit.MILLISECONDS).subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                            setTaskEvent(task);
                    }
                });

            }
        }


        //            //支付宝加粉任务的判断
        //            if (task.getTask_id() == 50) {
        //                String start_time_e = task.getParam().getStart_time_e();//随机结束
        //                String start_time_s = task.getParam().getStart_time_s();//随机开始
        //                int max = 0;
        //                int min = 0;
        //                SPUtils.putString(context, "newsLog_id", "");
        //                if (TextUtils.isEmpty(start_time_e)) {
        //                    max = 200;
        //                } else {
        //                    max = Integer.parseInt(start_time_e);
        //                }
        //                if (TextUtils.isEmpty(start_time_s)) {
        //                    min = 0;
        //                } else {
        //                    min = Integer.parseInt(start_time_s);
        //                }
        //                Random random = new Random();
        //                final int s = random.nextInt(max) % (max - min + 1) + min;
        //                SPUtils.putString(context, "data_taskid", String.valueOf(task));
        //                if (TextUtils.isEmpty(todoTimes)) {//如果执行时间为空
        //                    if (TextUtils.isEmpty(interval_time)) {//如果没有设置 则给定默认的执行时间
        //                        long time = (24 * 60 * 60 + 30 * 60) * 1000;
        //                        timerTasks = new TimerTask() {
        //                            @Override
        //                            public void run() {
        //                                Message message = new Message();
        //                                message.what = 1;
        //                                message.obj = task;
        //                                handler.sendMessage(message);
        //                            }
        //                        };
        //                        long time_wait = s + time / 1000 + TimeUtil.getCurrentTimeMillies();
        //                        SPUtils.putString(context, "time_wait", time_wait * 1000 + "");
        //                        Logger.d("执行的是时间为空、设置间隔时间为空的情况下,执行等待时间是" + s + "秒:执行周期的时间是" + time / 1000 + "秒:" + "下一个周期预计执行的时间是" + TimeUtil.getTimesCuo(time_wait * 1000));
        //                        mytime.schedule(timerTasks, s * 1000);
        //                    } else {
        //                        long time = (Long.parseLong(interval_time) * 60 * 60 + 30 * 60) * 1000;
        //                        timerTasks = new TimerTask() {
        //                            @Override
        //                            public void run() {
        //                                Message message = new Message();
        //                                message.what = 1;
        //                                message.obj = task;
        //                                handler.sendMessage(message);
        //                            }
        //                        };
        //                        long time_wait = s + time / 1000 + TimeUtil.getCurrentTimeMillies();
        //                        SPUtils.putString(context, "time_wait", time_wait * 1000 + "");
        //                        Logger.d("执行的是时间为空、间隔周期时间是" + time / 1000 + "秒:" + "任务随机的等待时间是" + s + "秒:" + "下一个周期预计执行的时间是" + TimeUtil.getTimesCuo(time_wait * 1000));
        //                        mytime.schedule(timerTasks, s * 1000);
        //                    }
        //                } else {//执行时间不为空的情况下
        //                    long times = TimeUtil.getLongTime(Long.parseLong(todoTimes));
        //                    if (TextUtils.isEmpty(interval_time)) {//间隔时间也为空
        //                        long one_day = (24 * 60 * 60 + 30 * 60) * 1000;
        //                        timerTasks = new TimerTask() {
        //                            @Override
        //                            public void run() {
        //                                Message message = new Message();
        //                                message.what = 1;
        //                                message.obj = task;
        //                                handler.sendMessage(message);
        //                            }
        //                        };
        //                        long time_wait = s + one_day / 1000 + TimeUtil.getCurrentTimeMillies() + times / 1000;
        //                        SPUtils.putString(context, "time_wait", time_wait * 1000 + "");
        //                        Logger.d("执行的是时间为" + todoTimes + "、设置间隔时间为空的情况下执行等待时间是" + s + "秒:执行周期的时间是" + times / 1000 + "秒:" + "下一个周期执行的预计时间是" + TimeUtil.getTimesCuo(time_wait * 1000));
        //                        mytime.schedule(timerTasks, times * 1000 + s * 1000);
        //
        //                    } else {
        //                        long r = (Long.parseLong(interval_time) * 60 * 60 + 30 * 60) * 1000;
        //                        Logger.d("Long.parseLong(interval_time)" + Long.parseLong(interval_time) + "秒" + "Long.parseLong(interval_time)*60*60" + Long.parseLong(interval_time) * 60 * 60);
        //                        timerTasks = new TimerTask() {
        //                            @Override
        //                            public void run() {
        //                                Message message = new Message();
        //                                message.what = 1;
        //                                message.obj = task;
        //                                handler.sendMessage(message);
        //                            }
        //                        };
        //                        long time_wait = s + r / 1000 + TimeUtil.getCurrentTimeMillies() + times;
        //                        SPUtils.putString(context, "time_wait", time_wait * 1000 + "");
        //                        Logger.d("执行的是时间为" + todoTimes + "、间隔时间为" + r / 1000 + "秒." + "情况下执行等待时间是" + s + "秒:" + "下一个周期预计执行的时间是" + TimeUtil.getTimesCuo(time_wait * 1000));
        //                        mytime.schedule(timerTasks, times * 1000 + s * 1000);
        //
        //                    }
        //                }
        //                return;
        //            }
        //
        //
        //
        //
        //            /**
        //             * 其他任务
        //             *
        //             */
        //            int max = 0;
        //            int min = 0;
        //            if (TextUtils.isEmpty(todoTimes)) {//没有设置时间，马上执行
        //                long time = 0;
        //                Logger.d("执行时间" + time);
        //                Timer timer = new Timer();
        //                TimerTask timerTask = new TimerTask() {
        //                    @Override
        //                    public void run() {
        //                        setTaskEvent(task);
        //
        //                    }
        //                };
        //                if (TextUtils.isEmpty(SPUtils.getString(context, "random_time_s", "")) && TextUtils.isEmpty(SPUtils.getString(context, "random_time_e", ""))) {
        //                    max = 200;
        //                    min = 0;
        //                } else {
        //                    max = Integer.parseInt(SPUtils.getString(context, "random_time_e", "").trim());
        //                    min = Integer.parseInt(SPUtils.getString(context, "random_time_s", "").trim());
        //                    Logger.d(max + "____" + min);
        //                }
        //                Random random = new Random();
        //                int s = random.nextInt(max) % (max - min + 1) + min;
        //                Logger.d("启动的时间是" + (time + s) * 1000 + "毫秒");
        //                timer.schedule(timerTask, (time + s) * 1000);
        //            } else {//定时执行
        //                long time = TimeUtil.getLongTime(Long.parseLong(todoTimes));
        //                Logger.d("执行时间" + time);
        //                Timer timer = new Timer();
        //                TimerTask timerTask = new TimerTask() {
        //                    @Override
        //                    public void run() {
        //                        setTaskEvent(task);
        //                    }
        //                };
        //                if (TextUtils.isEmpty(SPUtils.getString(context, "random_time_s", "")) && TextUtils.isEmpty(SPUtils.getString(context, "random_time_e", ""))) {
        //                    max = 200;
        //                    min = 0;
        //                } else {
        //                    max = Integer.parseInt(SPUtils.getString(context, "random_time_e", "").trim());
        //                    min = Integer.parseInt(SPUtils.getString(context, "random_time_s", "").trim());
        //                }
        //                Random random = new Random();
        //                int s = random.nextInt(max) % (max - min + 1) + min;
        //                Logger.d("启动的时间是" + (time + s) * 1000 + "毫秒");
        //                timer.schedule(timerTask, (time + s) * 1000);
        //            }
    }


    //
    //    /**
    //     * 发单图片下载
    //     *
    //     * @param dataBean
    //     */
    //    private void downImg(TaskMessageBean.ContentBean.DataBean dataBean) {
    //
    //        if (TextUtils.isEmpty(dataBean.getParam().getMateria_ss())) {
    //            return;
    //        }
    //        String messageData = dataBean.getParam().getMateria_ss();
    //
    //        WxFlockMessageBean[] wxFlockMessageBeans = new Gson().fromJson(messageData.replace("&quot", "\"").replace(";", ""), WxFlockMessageBean[].class);
    //        if (!StringUtils.isEmpty(messageData) && wxFlockMessageBeans != null && wxFlockMessageBeans.length > 0) {
    //            LogUtils.d(wxFlockMessageBeans.length + "条信息");
    //            for (int b = 0; b < wxFlockMessageBeans.length; b++) {//图片下载
    //                if (wxFlockMessageBeans[b].getType().equals("img")) {
    //                    String imgUrl = wxFlockMessageBeans[b].getData();
    //                    if (!StringUtils.isEmpty(imgUrl)) {
    //                        downloadFile(imgUrl);
    //                    }
    //                }
    //            }
    //
    //        }
    //
    //    }

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
        }
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(context.getPackageName())) {
                Logger.d("zplh在运行不处理");
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


    //    /**
    //     * 图片下载
    //     *
    //     * @param messageData
    //     */
    //    private void downloadFile(String messageData) {
    //
    //        String path = "";
    //        String strMark = "";
    //        String fileName = "";
    //        String filePath = "";
    //        String text = "";
    //
    //        if (!StringUtils.isEmpty(messageData)) {//判断请求地址是否为空
    //            text = messageData;
    //        } else {
    //            LogUtils.d("x图文发布地址为空");
    //            return;
    //        }
    //        path = URLS.pic_vo_flock + text.replace("\\", "/");
    //        LogUtils.d("x文件url__" + path);
    //        strMark = text.replace("\\", "/");
    //        fileName = strMark.substring(strMark.lastIndexOf("/")).replace("/", "").replace(" ", "");
    //        LogUtils.d("xa" + fileName);
    //        filePath = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/ykimages/" + fileName;
    //        LogUtils.d("xb" + filePath);
    //        LogUtils.d("xc" + FileUtils.createDirs(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/ykimages"));
    //
    //        String pathUrl = Environment.getExternalStorageDirectory() + "/ykimages/" + fileName;
    //
    //        if (new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/ykimages", fileName).exists()) {//不存在，下载
    //            LogUtils.d("x存在");
    //            return;
    //        } else {
    //            LogUtils.d("x不存在");
    //        }
    //
    //
    //        RequestParams requestParams = new RequestParams(path);
    //        requestParams.setSaveFilePath(pathUrl);
    //        x.http().get(requestParams, new Callback.ProgressCallback<File>() {
    //            @Override
    //            public void onWaiting() {
    //            }
    //
    //            @Override
    //            public void onStarted() {
    //            }
    //
    //            @Override
    //            public void onLoading(long total, long current, boolean isDownloading) {
    //
    //                LogUtils.d((int) total + "");
    //                LogUtils.d((int) current + "");
    //            }
    //
    //            @Override
    //            public void onSuccess(File result) {
    //                LogUtils.d("xutils文件下载成功");
    //            }
    //
    //            @Override
    //            public void onError(Throwable ex, boolean isOnCallback) {
    //                ex.printStackTrace();
    //                LogUtils.d("x下载失败");
    //            }
    //
    //            @Override
    //            public void onCancelled(CancelledException cex) {
    //            }
    //
    //            @Override
    //            public void onFinished() {
    //            }
    //        });
    //    }

}
