package zplh_android_yk.zplh.com.yk_20.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import zplh_android_yk.zplh.com.yk_20.BuildConfig;
import zplh_android_yk.zplh.com.yk_20.MainActivity;
import zplh_android_yk.zplh.com.yk_20.R;
import zplh_android_yk.zplh.com.yk_20.bean.CheckImei;
import zplh_android_yk.zplh.com.yk_20.bean.ImeiData;
import zplh_android_yk.zplh.com.yk_20.constant.URLS;
import zplh_android_yk.zplh.com.yk_20.utils.AdbUtils;
import zplh_android_yk.zplh.com.yk_20.utils.GsonUtils;
import zplh_android_yk.zplh.com.yk_20.utils.NetUtils;
import zplh_android_yk.zplh.com.yk_20.utils.SPUtils;
import zplh_android_yk.zplh.com.yk_20.utils.ShowToast;
import zplh_android_yk.zplh.com.yk_20.utils.SystemUtils;

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
    private EditText binding_et;
    private TextView binding_tv, imei_tv, version_tv;
    private String imei = "";
    private ProgressDialog pd;


    @Override
    protected void initView() {

    }

    @Override
    int getLayoutID() {
        return R.layout.activity_binding;
    }


    @Override
    protected void initData() {

            if (SPUtils.getBoolean(this, "addshortcut", true)) {
                addShortcut(this.getString(R.string.app_name));//添加桌面图标
                SPUtils.putBoolean(this, "addshortcut", false);
            }
            version_tv.setText("version:" + BuildConfig.VERSION_NAME);
            SPUtils.putBoolean(this, "task", false);

            imei = SPUtils.getString(this, "imeiimei", "");

            if (TextUtils.isEmpty(imei)) {
                imei = SystemUtils.getIMEI(this);
                Logger.t("imei").d(imei);
            }

            isBound();
            if (imei != null && imei.length() > 0) {
                imei_tv.setText(TextUtils.concat("imei",imei));
                imei_tv.setVisibility(View.VISIBLE);
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
    private void updata() {
        pd = ProgressDialog.show(this, "提示", "检测版本中...", true, false);

        NetUtils.get_excute(URLS.updata(), null, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                pd.dismiss();
                isBound();
            }

            @Override
            public void onResponse(String response, int id) {
                CheckImei checkImei = GsonUtils.fromJson(response, CheckImei.class);
                if (checkImei.getRet().equals("200")){
                double version = Double.valueOf(BuildConfig.VERSION_NAME);
                double ver = Double.valueOf(checkImei.getData());
                if (ver > version) {
                    binding_tv.setVisibility(View.GONE);
                    pd.setMessage("等待更新中...");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int a = 5000;
                            while (true) {
                                if (version_update_go) {
                                    //                                    handler.sendEmptyMessage(0x123);//发送消息
                                    version_update_go();
                                } else {
                                    pd.dismiss();
                                    version_update_go = true;
                                    handler.sendEmptyMessage(0x124);//发送消息
                                    break;
                                }
                                try {
                                    Thread.sleep(a);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                a = 20000;
                            }
                        }
                    }).start();
                }

                } else {
                    pd.dismiss();
                    isBound();
                }
            }
        });


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
                binding_tv.setClickable(true);
                ShowToast.show("绑定失败",BindingActivity.this);
            }

            @Override
            public void onResponse(String response, int tagId) {
                ImeiData imeiData = GsonUtils.fromJson(response, ImeiData.class);
                if (imeiData.getRet().equals("200")){
                    ShowToast.show("绑定成功",BindingActivity.this);
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
                    ShowToast.show("网络异常，请稍后再试...",BindingActivity.this);
                    binding_tv.setVisibility(View.GONE);
                    //                    exit();
                }

            @Override
            public void onResponse(String response, int id) {
                        pd.dismiss();
                CheckImei checkImei = GsonUtils.fromJson(response, CheckImei.class);
                if (checkImei.getMsg().equals("200")){
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
    protected void downLoadApk(final String apkUrl) {
        final ProgressDialog pd; // 进度条对话框
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("新版本下载中");
        pd.show();
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        new Thread() {
            @Override
            public void run() {
                try {
                    File file = getFileFromServer(apkUrl, pd);
                    version_update_back();//下载完成反馈
                    sleep(1000);
                    String path = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/wxykupdata.apk";
                    AdbUtils.install(path);
                    pd.dismiss(); // 结束掉进度条对话框
                } catch (Exception e) {
                    pd.dismiss();
                    e.printStackTrace();
                }
            }
        }.start();
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


    boolean version_update_go = true;
    //-----------------------------下载更新开始----------------------------------

    /**
     * 判断是否可以更新
     */
    public boolean version_update_go() {
        NetUtils.get_excute(URLS.version_update_go(), null, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                CheckImei checkImei = GsonUtils.fromJson(response, CheckImei.class);
                if (checkImei.getRet().equals("200")){
                 version_update_go = true;
                }
            }
        });

//        RequestParams params = new RequestParams(URLS.version_update_go());
//        LogUtils.d(URLS.version_update_go());
//        HttpManager.getInstance().sendRequest(params, new HttpObjectCallback<Object>() {
//
//            @Override
//            public void onSuccess(Object bean) {
//                version_update_go = false;
//            }
//
//            @Override
//            public void onFailure(int errorCode, String errorString) {
//            }
//        });
        return true;
    }

    /**
     * 下载结束
     */
    public void version_update_back() {
//        RequestParams params = new RequestParams(URLS.version_update_back());
//        //        params.addQueryStringParameter("id", id);//手机4位数
//        LogUtils.d(URLS.version_update_back());
//        HttpManager.getInstance().sendRequest(params, new HttpObjectCallback<Object>() {
//
//            @Override
//            public void onSuccess(Object bean) {
//
//            }
//
//            @Override
//            public void onFailure(int errorCode, String errorString) {
//            }
//        });
    }


    private Handler handler = new Handler() {

        // 该方法运行在主线程中
        // 接收到handler发送的消息，对UI进行操作
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub

            switch (msg.what) {
                case 0x123:
                    pd.setMessage("等待更新中");
                    break;
                case 0x124:
                    downLoadApk("http://103.94.20.102:8087/download/wxzs.apk");
                    break;
            }


        }
    };



    @OnClick({R.id.binding_et, R.id.imei_tv, R.id.version_tv, R.id.binding_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.binding_et:
                    String strBinding = binding_et.getText().toString().trim();
                    if (TextUtils.isEmpty(strBinding) || strBinding.length() <= 5) {
                       ShowToast.show("请输入正确的激活码",this);
                    } else {
                        binding_tv.setClickable(false);
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
