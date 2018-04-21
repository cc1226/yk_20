package com.zplh.zplh_android_yk.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Handler;


import com.tencent.bugly.crashreport.CrashReport;
import com.zplh.zplh_android_yk.BuildConfig;
import com.zplh.zplh_android_yk.bean.MessageListBean;
import com.zplh.zplh_android_yk.bean.WxGeneralSettingsBean;

import com.zplh.zplh_android_yk.service.NetworkChange;
import com.zplh.zplh_android_yk.utils.SPUtils;

import org.litepal.LitePalApplication;
import org.xutils.x;

import java.net.CookieStore;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;


/**
 * @创建者 Administrator
 * @创时间 2015-8-14 下午2:19:53
 * @描述 全局盒子, 里面放置一些全局的变量或者方法, Application其实是一个单例
 * @版本 $Rev: 6 $
 * @更新者 $Author: admin $
 * @更新时间 $Date: 2015-08-14 14:38:24 +0800 (Fri, 14 Aug 2015) $
 * @更新描述 TODO
 */
public class BaseApplication extends LitePalApplication {

    private static Context mContext;
    private static Handler mHandler;
    private static long mMainThreadId;
    private static Thread mMainThread;
    public CookieStore cookieStore;
    private String userId;
    private String name;
    private String customerCode;//getLastlogindate
    private String byInfo;
    private WxGeneralSettingsBean wxGeneralSettingsBean;//通用设置
    public static List<MessageListBean.ContentBean.DataBean> dataBeanList;//存任务，app关闭打开后自动执行任务

    public static List<MessageListBean.ContentBean.DataBean> getDataBeanList() {
        return dataBeanList;
    }

    public static void setDataBeanList(List<MessageListBean.ContentBean.DataBean> dataBeanList) {
        BaseApplication.dataBeanList = dataBeanList;
    }

    public WxGeneralSettingsBean getWxGeneralSettingsBean() {
        return wxGeneralSettingsBean;
    }

    public void setWxGeneralSettingsBean(WxGeneralSettingsBean wxGeneralSettingsBean) {
        this.wxGeneralSettingsBean = wxGeneralSettingsBean;
    }

    /**
     * 是否刷新和账号有关的数据
     **/

    public boolean isRefreshAccount = false;
    /**
     * 是否刷新订单
     **/
    public boolean isRefreshOrder = false;
    private int needRefresh;

    public int getNeedRefresh() {
        return needRefresh;
    }

    public void setNeedRefresh(int needRefresh) {
        this.needRefresh = needRefresh;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }


    public List<Activity> activityList = new ArrayList<Activity>();

    public static Context getContext() {
        return mContext;
    }

    public static Handler getHandler() {
        return mHandler;
    }

    public static long getMainThreadId() {
        return mMainThreadId;
    }

    public static Thread getMainThread() {
        return mMainThread;
    }

    /**
     * 添加界面
     **/
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    @Override
    public void onCreate() {// 程序入口方法

        CrashReport.initCrashReport(getApplicationContext(),"082b221028",false);
        CrashReport.setAppVersion(getApplicationContext(), BuildConfig.VERSION_NAME);
        String uid = SPUtils.getString(getApplicationContext(), "uid", "0000");
        CrashReport.setUserId(uid);
        // 1.上下文
        mContext = getApplicationContext();

        // 2.创建一个handler
        mHandler = new Handler();

        // 3.得到一个主线程id
        mMainThreadId = android.os.Process.myTid();

        // 4.得到主线程
        mMainThread = Thread.currentThread();

        // 5.捕获异常
//        uncaughtException();

        // 6.配置imageLoder 使用环境
//        ImageLoaderConfigur();

        // 7.初始化xUtil
        x.Ext.init(this);

        // 8.初始化极光推送
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        //动态注册一个广播 用于网络状态的监听
        initNetWork();
        //886
        //78C4BD957AA3403283654CDF3E07E155 ID
        //0E77F0C463E55C79A8DE344558BC0E2F81DE7F08正式授权码

        //446
        //D7F395430F7B4E6199411D921AFDED16 ID

        // 9.初始化xposed wtoolsdk
       /* String wtoolsdk = new WToolSDK().init("9999", "757533D0860F8CC0590B510BE2374F48C5750673");
        WtoolsdkBean messageBean = new Gson().fromJson(wtoolsdk, WtoolsdkBean.class);
        if (messageBean.getResult() == 0) {
            SPUtils.putInt(mContext, "wtoolsdk", 0);
        } else {
            SPUtils.putInt(mContext, "wtoolsdk", -1);
        }
        LogUtils.d("初始化wtoolsdk" + messageBean.toString());
        LogUtils.d("初始化wtoolsdk" + wtoolsdk);*/

//        private	String	msg_interval_time_e;//单次发消息间隔时间 结束
//        private	String	msg_interval_time_s;//单次发消息间隔时间 开始
//        private	String	remark_interval_time_e;//修改备注结束时间
//        private	String	remark_interval_time_s;//修改备注间隔时间

//        wxGeneralSettingsBean.setAdd_interval_time_e(SPUtils.getString(mContext,"add_interval_time_e",""));
//        wxGeneralSettingsBean.setAdd_interval_time_s(SPUtils.getString(mContext,"add_interval_time_e",""));
//        wxGeneralSettingsBean.setAgree_interval_time_e(SPUtils.getString(mContext,"add_interval_time_e",""));
//        wxGeneralSettingsBean.setAgree_interval_time_s(SPUtils.getString(mContext,"add_interval_time_e",""));
        wxGeneralSettingsBean=new WxGeneralSettingsBean();
        wxGeneralSettingsBean.setMsg_interval_time_e(SPUtils.getString(mContext,"msg_interval_time_e","200"));
        wxGeneralSettingsBean.setMsg_interval_time_s(SPUtils.getString(mContext,"msg_interval_time_s","60"));
//        wxGeneralSettingsBean.setRandom_time_e(SPUtils.getString(mContext,"add_interval_time_e",""));
//        wxGeneralSettingsBean.setRandom_time_s(SPUtils.getString(mContext,"add_interval_time_e",""));
        wxGeneralSettingsBean.setRemark_interval_time_e(SPUtils.getString(mContext,"remark_interval_time_e","6"));
        wxGeneralSettingsBean.setRemark_interval_time_s(SPUtils.getString(mContext,"remark_interval_time_s","3"));
        wxGeneralSettingsBean.setTask_time_e(SPUtils.getString(mContext,"task_time_e","3600"));
        wxGeneralSettingsBean.setTask_time_s(SPUtils.getString(mContext,"task_time_s","600"));
        wxGeneralSettingsBean.setDz_interval_e(SPUtils.getString(mContext,"dz_interval_e","180"));
        wxGeneralSettingsBean.setDz_interval_s(SPUtils.getString(mContext,"dz_interval_s","10"));

        wxGeneralSettingsBean.setVideo_time_e(SPUtils.getString(mContext,"video_time_e","30"));
        wxGeneralSettingsBean.setVideo_time_s(SPUtils.getString(mContext,"video_time_s","20"));

        wxGeneralSettingsBean.setVoice_time_e(SPUtils.getString(mContext,"voice_time_e","30"));
        wxGeneralSettingsBean.setVoice_time_s(SPUtils.getString(mContext,"voice_time_s","20"));

        wxGeneralSettingsBean.setRandom_time_s(SPUtils.getString(mContext,"random_time_s","1"));
        wxGeneralSettingsBean.setRandom_time_e(SPUtils.getString(mContext,"random_time_e","200"));

        wxGeneralSettingsBean.setCrowd_ad_time_e(SPUtils.getString(mContext,"crowd_ad_time_e","10"));
        wxGeneralSettingsBean.setCrowd_ad_time_s(SPUtils.getString(mContext,"crowd_ad_time_s","5"));

        wxGeneralSettingsBean.setRecord_time_e(SPUtils.getString(mContext,"record_time_e","20"));
        wxGeneralSettingsBean.setRecord_time_s(SPUtils.getString(mContext,"record_time_s","10"));

        dataBeanList=new ArrayList<>();

        //初始化内存泄露检查
//		refWatcher = LeakCanary.install(this);

        super.onCreate();

        //百度地图初始化
     /*   SDKInitializer.initialize(getApplicationContext());
        mobile= SPUtils.getString(this,"mobile","");
        userId=SPUtils.getString(this,"userid","");
        name=SPUtils.getString(this,"name","");*/


    }

    private void initNetWork() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(NetworkChange.getInstance(), filter);
    }

    public String getByInfo() {
        return byInfo;
    }

    public void setByInfo(String byInfo) {
        this.byInfo = byInfo;
    }

//	public static RefWatcher getRefWatcher(Context context) {
//		BaseApplication application = (BaseApplication) context.getApplicationContext();
//		return application.refWatcher;
//	}

//	private RefWatcher refWatcher;

    /**
     * 配置imageLoder 使用环境
     */
//    private void ImageLoaderConfigur() {
//
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getContext())
//
//                .diskCacheSize(512 * 1024 * 1024)
//                .diskCacheExtraOptions(720, 1280, null)
//                .diskCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5 加密
//
//                .memoryCacheSizePercentage(14)
//                .memoryCacheSize(2 * 1024 * 1024)
//                .memoryCacheExtraOptions(720, 1280)
//                .memoryCache(new WeakMemoryCache())
//
//                .threadPoolSize(5)//线程池内加载的数量
//                .threadPriority(Thread.NORM_PRIORITY - 2)
//                .writeDebugLogs()
//                .build();
//        ImageLoader.getInstance().init(config);
//
//    }

    /**
     * 崩溃是重启
     */
    private void uncaughtException() {
        Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread thread, Throwable ex) {

//                LogUtils.d("崩溃了..................");

                // 复活
                PackageManager pm = getPackageManager();
                Intent launchIntentForPackage = pm.getLaunchIntentForPackage(getPackageName());
                startActivity(launchIntentForPackage);

                // 杀死
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
    }


}
