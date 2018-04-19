package com.zplh.zplh_android_yk.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.zplh.zplh_android_yk.R;
import com.zplh.zplh_android_yk.base.MyApplication;
import com.zplh.zplh_android_yk.bean.NodeXmlBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/* 封装了一些普遍的微信adb操作
 * Created by yong hao zeng on 2018/4/14/014.
 */
public class WxTaskUtils {

    private List<String> commnandList;

    private static WxTaskUtils wxTaskUtils;


    private WxTaskUtils() {
    }

    public static WxTaskUtils getWxTaskUtils() {
        if (wxTaskUtils == null) {
            wxTaskUtils = new WxTaskUtils();
        }
        return wxTaskUtils;
    }


    /**
     * 切换帐号
     */
    public void switchWxAccount() throws Exception {
        String xmlData;
        NodeXmlBean.NodeBean nodeBean;
        List<Integer> listXY = new ArrayList<>();
        backHome();
        int accountNum = 0;
        goSwitchAccounts();

        Thread.sleep(3000);

        xmlData = AdbUtils.getAdbUtils().dumpXml2String();
        List<String> nodeList = AdbUtils.getAdbUtils().getNodeList(xmlData);
        for (int i = 0; i < nodeList.size() - 1; i++) {
            nodeBean = AdbUtils.getAdbUtils().getNodeXmlBean(nodeList.get(i)).getNode();
            if (nodeBean.getText() != null && !nodeBean.getText().isEmpty() && nodeBean.getResourceid() != null && nodeBean.getResourceid().equals("com.tencent.mm:id/d5s")) {
                accountNum++;
            }
        }
        for (int i = 0; i < nodeList.size() - 1; i++) {
            nodeBean = AdbUtils.getAdbUtils().getNodeXmlBean(nodeList.get(i)).getNode();
            if (nodeBean.getText() != null && nodeBean.getText().equals("当前使用") && nodeBean.getResourceid() != null && nodeBean.getResourceid().equals("com.tencent.mm:id/d5v")) {
                listXY = AdbUtils.getAdbUtils().getXY(nodeBean.getBounds());//获取 当前使用的坐标
                break;
            }
        }
        if (accountNum == 2) {  //老号在左边  新号在右边

            //说明已经登录了两个账号
            if (listXY.get(0) == 110) {
                //正在使用的账号 在左边（新号）， 点击右边的账号切换
                AdbUtils.getAdbUtils().click4xy(288, 457, 384, 553);
            } else if (listXY.get(0) == 302) {
                //正在使用的账号 在右边(老号)， 点击左边的账号切换
                AdbUtils.getAdbUtils().click4xy(96, 457, 192, 553);
            }

            Thread.sleep(15000);
            return;
        }
        AdbUtils.getAdbUtils().back();
        backHome();
    }


    //关闭微信
    public boolean closeWx() {
        ShellUtils.CommandResult commandResult = ShellUtils.execCommand("am force-stop com.tencent.mm", true);
        return commandResult.result == 0;
    }

    public  boolean clickRegister() {
        String xmlData = AdbUtils.getAdbUtils().dumpXml2String();
        if (TextUtils.isEmpty(xmlData))
            return false;
        if (!xmlData.contains("注册"))
            return false;
        if (AdbUtils.getAdbUtils().click4xy(266, 752, 450, 824)) {
            return true;
        }
        return false;
    }

    //清理缓存
    public void cleanData() {
        ShellUtils.CommandResult commandResult = ShellUtils.execCommand("pm clear com.tencent.mm", true);
    }

    //前往设置
    public void goSetting() {
        ShellUtils.CommandResult commandResult = ShellUtils.execCommand("am start com.tencent.mm/.plugin.setting.ui.setting.SettingsUI", true);
    }

    //前往切换帐号

    public void goSwitchAccounts() {
        ShellUtils.CommandResult commandResult = ShellUtils.execCommand("am start com.tencent.mm/.plugin.setting.ui.setting.SettingsSwitchAccountUI", true);
    }

    //打开微信
    public boolean openWx() {
        ShellUtils.CommandResult commandResult = ShellUtils.execCommand("am start com.tencent.mm/com.tencent.mm.ui.LauncherUI", true);

        return commandResult.result == 0;

    }


    // 获取目前的微信账号
    public void getUsingWxAccount() throws Exception {
        backHome();
        openWx();
        int accountNum = 0;
        AdbUtils.getAdbUtils().click4xy(411, 822, 429, 847);// 点击右下角的我
        String xmlData = AdbUtils.getAdbUtils().dumpXml2String();
        if (!xmlData.contains("相册") || !xmlData.contains("收藏")) {
            return;
        } else {
            List<String> ud = AdbUtils.getAdbUtils().getNodeList(xmlData);
            for (int a = 0; a < ud.size(); a++) {
                NodeXmlBean.NodeBean nodeBean = AdbUtils.getAdbUtils().getNodeXmlBean(ud.get(a)).getNode();
                if (nodeBean.getResourceid() != null && (nodeBean.getResourceid().equals("com.tencent.mm:id/cdh")) && nodeBean.getText() != null && nodeBean.getText().contains("微信号")) {
                    String str = nodeBean.getText();
                    String wxAccount = str.replaceAll("微信号：", "");
                    SPUtils.putString(MyApplication.getContext(), "wxAccount", wxAccount);
                    break;
                }
            }
        }
    }


    /*
    *
    * 获取Xml数据
    *
    * */

    public void adbDump() {
        if (commnandList != null) {
            commnandList.clear();
        } else {
            commnandList = new ArrayList<>();
        }
        commnandList.add("uiautomator dump /sdcard/uidump.xml");
        ShellUtils.CommandResult result = ShellUtils.execCommand(commnandList, true);
        Log.e("WG", "adbDump: " + result.result + "adb" + result.successMsg);
    }


    /**
     * 读取sd卡 xml数据
     *
     * @param
     * @return
     */
    public String readTxtFile() {
        String path = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/uidump.xml";
        ;
        StringBuilder builder = new StringBuilder();
        //打开文件
        File file = new File(path);
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory()) {
            Log.e("TestFile", "The File doesn't not exist.");
        } else {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null) {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    while ((line = buffreader.readLine()) != null) {
                        builder.append(line + "\n");
                    }
                    instream.close();
                }
            } catch (java.io.FileNotFoundException e) {
                Log.e("WG", "The File doesn't not exist.");
            } catch (IOException e) {
                Log.d("WG", e.getMessage());
            }
        }
        return builder.toString();
    }


    /**
     * 判断app是否安装
     *
     * @param context
     * @param packageName
     * @return
     */
    public boolean isInstallApp(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    /**
     * 返回到微信主页面
     */
    public void backHome() throws Exception {
        List<Integer> listXY = new ArrayList<>();
        String xmlData = AdbUtils.getAdbUtils().dumpXml2String();
        if (xmlData.contains("wx助手") || (xmlData.contains("主屏幕") && xmlData.contains("应用"))) {
            openWx();

            Thread.sleep(7000);

            backHome();
        } else if (xmlData.contains("更新") && xmlData.contains("取消") && xmlData.contains("立刻安装")) {
            List<String> ud = AdbUtils.getAdbUtils().getNodeList(xmlData);
            for (int a = 0; a < ud.size(); a++) {
                NodeXmlBean.NodeBean nodeBean = AdbUtils.getAdbUtils().getNodeXmlBean(ud.get(a)).getNode();
                if (nodeBean.getText() != null && nodeBean.getText().contains("取消")) {
                    listXY = AdbUtils.getAdbUtils().getXY(nodeBean.getBounds());//取消
                    AdbUtils.getAdbUtils().click4xy(listXY.get(0), listXY.get(1), listXY.get(2), listXY.get(3));//取消
                    AdbUtils.getAdbUtils().adbDimensClick(MyApplication.getContext(), R.dimen.x200, R.dimen.y230, R.dimen.x264, R.dimen.y251);//确定
                    break;
                }
            }

        } else if (xmlData.contains("你要关闭购物页面?")) {
            AdbUtils.getAdbUtils().adbDimensClick(MyApplication.getContext(), R.dimen.x200, R.dimen.y230, R.dimen.x264, R.dimen.y250);
        } else if (xmlData.contains("忘记密码") || (xmlData.contains("登录") && xmlData.contains("注册") && xmlData.contains("语言")) || (xmlData.contains("你的手机号码") && xmlData.contains("密码"))) {//判断是否登录

            throw new Exception("未登录");
        } else if (!(xmlData.contains("通讯录") && xmlData.contains("发现") && xmlData.contains("我") && !(xmlData.contains("聊天信息")))) {//判断是否在微信主界面

            AdbUtils.getAdbUtils().back();
            backHome();
        } else {
            throw new Exception("返回失败");
        }
    }

    public static void goMobileFriend() {
        ShellUtils.CommandResult commandResult = ShellUtils.execCommand("am start com.tencent.mm/com.tencent.mm.ui.bindmobile.MobileFriendUI", true);
    }

}
