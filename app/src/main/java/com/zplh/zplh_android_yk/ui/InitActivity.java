package com.zplh.zplh_android_yk.ui;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.yanzhenjie.permission.Permission;
import com.zplh.zplh_android_yk.R;
import com.zplh.zplh_android_yk.bean.NodeXmlBean;
import com.zplh.zplh_android_yk.utils.AdbUtils;
import com.zplh.zplh_android_yk.utils.FileUtils;
import com.zplh.zplh_android_yk.utils.NodeUtils;
import com.zplh.zplh_android_yk.utils.WxTaskUtils;

import java.io.File;
import java.util.List;

import butterknife.BindView;

/**
 * 初始化activity 在这里面做初始化操作
 * Created by yong hao zeng on 2018/4/20/020.
 */
public class InitActivity extends BaseUI {
    @BindView(R.id.tv_init_state)
    TextView tvInitState;
    private String xmlData;

    @Override
    protected void initData() {
        initPermission();


    }

    @Override
    protected void initView() {

    }


    void initPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)  //打开相机权限
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)   //可读
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)  //可写
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_PHONE_STATE)  //可写
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_CONTACTS)  //可写
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_CONTACTS)  //可写
                        != PackageManager.PERMISSION_GRANTED) {
            String permissions[] = {Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                    , Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_CONTACTS,
                    Manifest.permission.READ_CONTACTS};
            requestPermission(permissions, 0);
        }
    }

    @SuppressLint("CheckResult")
    void requestPermission(String[] permissions, int number) {
        new Thread(() -> {
            AdbUtils.getAdbUtils().adb("input keyevent 82");//点亮屏幕
            ActivityCompat.requestPermissions(InitActivity.this, permissions, 1);
            int flag = 0;
            while (flag < permissions.length) {
                flag++;
                try {
                    Thread.sleep(1000);
                    NodeUtils.clickNode("允许", "com.android.packageinstaller:id/permission_allow_button");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //region 判断是否打开了通知监听权限
            if (!isEnabled()) {
                startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                NodeUtils.clickNode4Text("NotificationMonitor");
                if (AdbUtils.getAdbUtils().dumpXml2String().contains("要允许NotificationMonitor获取通知访问权限吗？")) {
                    NodeUtils.clickNode("允许", "android:id/button1");
                    AdbUtils.getAdbUtils().adb("input keyevent 4");
                }
            }
            //endregion
            //region 申请辅助点击权限
            if (!isAccessibilitySettingsOn(InitActivity.this)) {
                try {
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    List<String> li = AdbUtils.getAdbUtils().getNodeList(AdbUtils.getAdbUtils().dumpXml2String());
                    for (String l : li) {
                        if (l.contains(getAppName(InitActivity.this))) {
                            NodeXmlBean.NodeBean nodeBean = AdbUtils.getAdbUtils().getNodeXmlBean(l).getNode();
                            List<Integer> xy = AdbUtils.getAdbUtils().getXY(nodeBean.getBounds());
                            AdbUtils.getAdbUtils().click4xy(xy.get(0), xy.get(1), xy.get(2), xy.get(3));
                            List<String> li1 = AdbUtils.getAdbUtils().getNodeList(AdbUtils.getAdbUtils().dumpXml2String());
                            int i = 0;
                            for (String str : li1) {
                                if (str.contains("关闭")) {
                                    i = -1;
                                    NodeXmlBean.NodeBean nodeBean1 = AdbUtils.getAdbUtils().getNodeXmlBean(str).getNode();
                                    List<Integer> xy1 = AdbUtils.getAdbUtils().getXY(nodeBean1.getBounds());
                                    AdbUtils.getAdbUtils().click4xy(xy1.get(0), xy1.get(1), xy1.get(2), xy1.get(3));
                                    List<String> li2 = AdbUtils.getAdbUtils().getNodeList(AdbUtils.getAdbUtils().dumpXml2String());
                                    for (String s1 : li2) {
                                        if (s1.contains("确定")) {
                                            NodeXmlBean.NodeBean nodeBean2 = AdbUtils.getAdbUtils().getNodeXmlBean(s1).getNode();
                                            List<Integer> xy2 = AdbUtils.getAdbUtils().getXY(nodeBean2.getBounds());
                                            AdbUtils.getAdbUtils().click4xy(xy2.get(0), xy2.get(1), xy2.get(2), xy2.get(3));
                                            AdbUtils.getAdbUtils().adb("input keyevent 4");
                                            AdbUtils.getAdbUtils().adb("input keyevent 4");
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                            if (i == 0) {
                                AdbUtils.getAdbUtils().adb("input keyevent 4");
                                AdbUtils.getAdbUtils().adb("input keyevent 4");
                            }
                            break;
                        }
                    }
                }
            }
            //endregion
            AdbUtils.getAdbUtils().adb("settings put secure default_input_method com.sohu.inputmethod.sogou/.SogouIME");
            deleteAA();
            Intent intent = new Intent(InitActivity.this, BindingActivity.class);
            startActivity(intent);
            finish();
        }).start();
    }

    /**
     * 删除aa开头的文件
     */
    private void deleteAA() {
        if (FileUtils.createDirs(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/ykimages")) {//删除aa开头文件文件
            String fileUrl = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/ykimages";
            File folder = new File(fileUrl);
            File[] files = folder.listFiles();
            for (File file : files) {
                Logger.d("删除了:" + file.getName());
                file.delete();
            }
        }
    }

    public String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = getPackageName() + "/" + AccessibilityService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            // com.z.buildingaccessibilityservices/com.z.buildingaccessibilityservices.TestService
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        } else {
        }
        return false;
    }


    // 判断是否打开了通知监听权限
    private boolean isEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    @Override
    protected int getLayoutID() {
        return R.layout.init_activity;
    }
}
