package com.zplh.zplh_android_yk.utils;


import android.util.Log;

import com.zplh.zplh_android_yk.base.MyApplication;

/**
 * 检查各种突如其来的view 在onprogress回掉 可以避免界面的弹窗
 * Created by yong hao zeng on 2018/4/24/024.
 */
public class ViewCheckUtils {
    public static void check() throws Exception {
        Log.e("WG", "check: 权限框进来了");
        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //检查微信的弹窗
            while (AdbUtils.getAdbUtils().dumpXml2String().contains("安全警告")) {
                Log.e("WG", "check: 警告弹框出来了");
                if (AdbUtils.getAdbUtils().dumpXml2String().contains("记住")) {
                    AdbUtils.getAdbUtils().click(76, 494);
                    AdbUtils.getAdbUtils().click(382, 567);
                }
            }
            SPUtils.putBoolean(MyApplication.getContext(),"flag",true);
        }).start();
    }

}
