package com.zplh.zplh_android_yk.ui;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zplh.zplh_android_yk.BuildConfig;
import com.zplh.zplh_android_yk.MainActivity;
import com.zplh.zplh_android_yk.R;
import com.zplh.zplh_android_yk.bean.CheckImei;
import com.zplh.zplh_android_yk.bean.ImeiData;
import com.zplh.zplh_android_yk.constant.URLS;
import com.zplh.zplh_android_yk.utils.AdbUtils;
import com.zplh.zplh_android_yk.utils.GsonUtils;
import com.zplh.zplh_android_yk.utils.NetUtils;
import com.zplh.zplh_android_yk.utils.SPUtils;
import com.zplh.zplh_android_yk.utils.ShowToast;
import com.zplh.zplh_android_yk.utils.SystemUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by lichun on 2017/6/18.
 * Description:绑定设备
 */

public class BindingActivity extends BaseUI {
    @BindView(R.id.binding_et)
    EditText bindingEt;
    @BindView(R.id.imei_tv)
    TextView imeiTv;
    @BindView(R.id.version_tv)
    TextView versionTv;
    @BindView(R.id.binding_tv)
    TextView bindingTv;
    private String imei = "";
    private ProgressDialog pd;


    @Override
    protected void initView() {

    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_binding;
    }


    @Override
    protected void initData() {

       if (SPUtils.getBoolean(this, "addshortcut", true)) {
            addShortcut(this.getString(R.string.app_name));//添加桌面图标
            SPUtils.putBoolean(this, "addshortcut", false);
        }
        versionTv.setText("version:" + BuildConfig.VERSION_NAME);
        SPUtils.putBoolean(this, "task", false);

        imei = SPUtils.getString(this, "imeiimei", "");

        if (TextUtils.isEmpty(imei)) {
            imei = SystemUtils.getIMEI(this);
            Logger.t("imei").d(imei);
        }

        isBound();
        if (imei != null && imei.length() > 0) {
            imeiTv.setText(TextUtils.concat("imei", imei));
            imeiTv.setVisibility(View.VISIBLE);
        }

    }


    //    private boolean isOpen = true;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //        if (isOpen) {
        //            ShellUtils.myExecCommand("am start -a android.intent.action.MAIN -n com.zplh.zplh_android_yk/com.zplh.zplh_android_yk.ui.activity.BindingActivity");
        //            Process.killProcess(Process.myPid());
        //        }
    }


    /**
     * 检查版本是否需要更新
     */
    @SuppressLint("CheckResult")
    private void updata() {
        pd = ProgressDialog.show(this, "提示", "检测版本中...", true, false);
        StringCallback callback = new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                pd.dismiss();
                isBound();
            }

            @Override
            public void onResponse(String response, int id) {
                CheckImei checkImei = GsonUtils.fromJson(response, CheckImei.class);
                if (checkImei.getRet().equals("200")) {
                    double version = Double.valueOf(BuildConfig.VERSION_NAME);
                    double ver = Double.valueOf(checkImei.getData());
                    if (ver > version) {
                        bindingTv.setVisibility(View.GONE);
                        pd.setMessage("等待更新中...");
                        //每20秒判断一次是否允许更新 因为没次只允许10台手机更新
                        final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
                        mCompositeDisposable.add(Observable.interval(0, 10000, TimeUnit.MILLISECONDS)
                                .map(new Function<Long, File>() {
                                    @Override
                                    public File apply(Long aLong) throws Exception {
                                        File file = null;
                                        if (version_update_go()) {
                                            file = downLoadApk("http://103.94.20.102:8087/download/wxzs.apk");
                                            mCompositeDisposable.clear();
                                        }
                                        return file;
                                    }
                                }).subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io())
                                .subscribeWith(new DisposableObserver<File>() {
                                    @Override
                                    public void onNext(File file) {
                                        version_update_back();
                                        try {
                                            Thread.sleep(10000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        String path = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/wxykupdata.apk";
                                        Logger.d("下载完成开始安装");
                                        AdbUtils.install(path);
                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                })
                        );


                    } else {
                        pd.dismiss();
                        isBound();
                    }
                }
            }
        };
        NetUtils.get_excute(URLS.updata(),null,callback);

    }

    /**
     * 绑定设备
     *
     * @param id
     * @param code
     */
    public void setBound(final String id, String code) {
        //http://192.168.1.126:8087/yk/index.php/home/group/binding?id=159357457&imei=87975431324687132417
        pd = ProgressDialog.show(this, "提示", "设备绑定中", true, false);

        Map params = new HashMap();
        params.put("id", id);//手机4位数
        params.put("code", code);
        params.put("imei", imei);
        NetUtils.get_excute(URLS.binding(), params, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                pd.dismiss();
                bindingTv.setClickable(true);
                ShowToast.show("绑定失败", BindingActivity.this);
            }

            @Override
            public void onResponse(String response, int tagId) {
                ImeiData imeiData = GsonUtils.fromJson(response, ImeiData.class);
                if (imeiData.getRet().equals("200")) {
                    ShowToast.show("绑定成功", BindingActivity.this);
                    SPUtils.putString(BindingActivity.this, "uid", id);
                    SPUtils.putBoolean(BindingActivity.this, "imei", true);
                    SPUtils.putString(BindingActivity.this, "imeiimei", imei);
                    Intent intent = new Intent(BindingActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        //        LogUtils.d(URLS.binding() + "?id=" + id + "&code=" + code + "&imei=" + imei);
        //        HttpManager.getInstance().sendRequest(params, new HttpObjectCallback<ImeiData>() {
        //
        //            @Override
        //            public void onSuccess(ImeiData bean) {
        //                pd.dismiss();
        //                showToast("绑定成功");
        //                SPUtils.putString(mContext, "uid", id);
        //                SPUtils.putBoolean(mContext, "imei", true);
        //                SPUtils.putString(mContext, "imeiimei", imei);
        //                Intent intent = new Intent(BindingActivity.this, MainActivity.class);
        //                startActivity(intent);
        //                isOpen = false;
        //                finish();
        //            }
        //
        //            @Override
        //            public void onFailure(int errorCode, String errorString) {
        //                pd.dismiss();
        //                binding_tv.setClickable(true);
        //                showToast("绑定失败" + errorString);
        //            }
        //        });
    }

    /**
     * 判断设备是否绑定
     */
    public void isBound() {
        pd = ProgressDialog.show(this, "提示", "数据初始化中...", true, false);
        Map params = new HashMap();
        params.put("imei", imei);//status
        params.put("status", "1");//status

        NetUtils.get_excute(URLS.isbinding(), params, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                pd.dismiss();
                SPUtils.putBoolean(BindingActivity.this, "imei", false);
                ShowToast.show("网络异常，请稍后再试...", BindingActivity.this);
                bindingTv.setVisibility(View.GONE);
                //                    exit();
            }

            @Override
            public void onResponse(String response, int id) {
                pd.dismiss();
                CheckImei checkImei = GsonUtils.fromJson(response, CheckImei.class);
                if (checkImei.getRet().equals("200")) {
                    SPUtils.putBoolean(BindingActivity.this, "imei", true);
                    SPUtils.putString(BindingActivity.this, "uid", checkImei.getData());
                    Intent intent = new Intent(BindingActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });
    }

    /*
     * 从服务器中下载APK
     */
    protected File downLoadApk(String apkUrl) throws Exception {
        File file = getFileFromServer(apkUrl, pd);

        version_update_back();//下载完成反馈
        return file;

    }


    /**
     * [下载APP]
     */
    public static File getFileFromServer(String path, ProgressDialog pd) throws Exception {
        // 如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(25000);
            // 获取到文件的大小
            pd.setMax(conn.getContentLength());
            InputStream is = conn.getInputStream();
            File file = new File(Environment.getExternalStorageDirectory(), "wxykupdata.apk");
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len;
            int total = 0;
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                total += len;
                // 获取当前下载量
                pd.setProgress(total);
            }
            fos.close();
            bis.close();
            is.close();
            return file;
        } else {
            return null;
        }
    }

    public static final String ACTION_ADD_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";

    /**
     * 创建快捷方式
     *
     * @param name
     */
    private void addShortcut(String name) {
        Intent addShortcutIntent = new Intent(ACTION_ADD_SHORTCUT);

        // 不允许重复创建
        addShortcutIntent.putExtra("duplicate", false);// 经测试不是根据快捷方式的名字判断重复的
        // 应该是根据快链的Intent来判断是否重复的,即Intent.EXTRA_SHORTCUT_INTENT字段的value
        // 但是名称不同时，虽然有的手机系统会显示Toast提示重复，仍然会建立快链
        // 屏幕上没有空间时会提示
        // 注意：重复创建的行为MIUI和三星手机上不太一样，小米上似乎不能重复创建快捷方式

        // 名字
        addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);

        // 图标
        addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(BindingActivity.this,
                        R.mipmap.ic_launcher));

        // 设置关联程序
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.setClass(BindingActivity.this, BindingActivity.class);
        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, launcherIntent);

        // 发送广播
        sendBroadcast(addShortcutIntent);
    }


    //-----------------------------下载更新开始----------------------------------

    /**
     * 判断是否可以更新
     */
    public boolean version_update_go() {

        try {
            Response response = NetUtils.get(URLS.version_update_go(), null);
            if (response.code() == 200) {
                if (response.body().string().contains("200")) {
                    return true;
                }
                return false;
            }
            return false;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 下载结束
     */
    public void version_update_back() {
        NetUtils.get_excute(URLS.version_update_back(), null, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

            }
        });
    }


    @OnClick({R.id.binding_et, R.id.imei_tv, R.id.version_tv, R.id.binding_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.binding_et:
                String strBinding = bindingEt.getText().toString().trim();
                if (TextUtils.isEmpty(strBinding) || strBinding.length() <= 5) {
                    ShowToast.show("请输入正确的激活码", this);
                } else {
                    bindingTv.setClickable(false);
                    //                    setBound(strBinding.substring(strBinding.length()-4),strBinding.substring(0,strBinding.length()-4));
                    setBound(strBinding.substring(strBinding.length() - 4), strBinding);
                }
                break;
            case R.id.imei_tv:
                break;
            case R.id.version_tv:
                break;
            case R.id.binding_tv:
                break;
        }
    }
}
