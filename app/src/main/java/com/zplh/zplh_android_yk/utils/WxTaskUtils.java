package com.zplh.zplh_android_yk.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zplh.zplh_android_yk.R;
import com.zplh.zplh_android_yk.base.BaseApplication;
import com.zplh.zplh_android_yk.base.MyApplication;
import com.zplh.zplh_android_yk.bean.NodeXmlBean;
import com.zplh.zplh_android_yk.bean.WxFriendsMessageBean;
import com.zplh.zplh_android_yk.bean.WxNumBean;
import com.zplh.zplh_android_yk.constant.URLS;
import com.zplh.zplh_android_yk.R;
import com.zplh.zplh_android_yk.base.MyApplication;
import com.zplh.zplh_android_yk.bean.NodeXmlBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Response;


/* 封装了一些普遍的微信adb操作
 * Created by yong hao zeng on 2018/4/14/014.
 */
public class WxTaskUtils {

    private List<String> commnandList;

    private static WxTaskUtils wxTaskUtils;
    private NodeXmlBean.NodeBean nodeBean;
    private List<Integer> listXY;
    private BaseApplication app = new BaseApplication();

    private WxTaskUtils() {

    }

    public static WxTaskUtils getWxTaskUtils() {
        if (wxTaskUtils == null) {
            wxTaskUtils = new WxTaskUtils();
        }
        return wxTaskUtils;
    }

    private String yunYingMark = "";//运营号

    /**
     * 修改备注.
     */
    Random random = new Random();

    private void startAlterName(String zzz) {
        String xmlData;
        if (StringUtils.isEmpty(zzz)) {
            zzz = "ZZZ0";
        } else if (zzz != null && zzz.length() > 10) {
            zzz = "ZZZ9";
        }
        boolean bottom = false;//到了底部
        int sex = 0;//0代表女。   1代表男   2代表性别未知
        DecimalFormat df = new DecimalFormat("0000");
        int zzzNum = 0;//判断是否直接到#号修改
        String endData = "";
        String meName = "";
        w:
        while (true) {


            while (true) {
//                xmlData = wxUtils.getXmlData();//修改完一个重新获取页面数据
                xmlData = AdbUtils.getAdbUtils().dumpXml2String();
                if (xmlData.contains("wx助手") || (xmlData.contains("应用") && xmlData.contains("主屏幕"))) {
//                    ShowToast.show("任务被中断，结束修改备注任务", (Activity) context);
                    Log.e("WG", "任务被中断，结束修改备注任务 ");
                    break w;
                } else if (!(xmlData.contains("通讯录") && xmlData.contains("发现"))) {
//                    wxUtils.adb("input keyevent 4");//返回
                    AdbUtils.getAdbUtils().adb("input keyevent 4");
                } else {
                    break;
                }
            }


            List<String> nodeList = AdbUtils.getAdbUtils().getNodeList(xmlData);
            a:
            for (int a = 0; a < nodeList.size(); a++) {
                nodeBean = AdbUtils.getAdbUtils().getNodeXmlBean(nodeList.get(a)).getNode();
                if (nodeBean.getResourceid() != null && (nodeBean.getResourceid().equals("com.tencent.mm:id/j_")) && nodeBean.getContentdesc() != null && nodeBean.getContentdesc() != "" && !nodeBean.getContentdesc().startsWith("微信") && !nodeBean.getContentdesc().equals("文件传输助手") && !nodeBean.getContentdesc().startsWith("ZZZ") && !nodeBean.getContentdesc().startsWith("zzz") && !meName.equals(nodeBean.getContentdesc()) && !yunYingMark.contains(nodeBean.getContentdesc())) {
                    //筛选出好友
                    listXY = AdbUtils.getAdbUtils().getXY(nodeBean.getBounds());//获取好友坐标
//                    wxUtils.adbClick(listXY.get(0), listXY.get(1), listXY.get(2), listXY.get(3));//点击好友修改备注

//                    LogUtils.d("点击进入");
                    Log.e("WG", "点击进入 ");
//                    xmlData = wxUtils.getXmlData();//重新获取页面数据
                    xmlData = AdbUtils.getAdbUtils().dumpXml2String();
                    if (!xmlData.contains("标签")) {
//                        wxUtils.adb("input keyevent 4");
                        AdbUtils.getAdbUtils().adb("input keyevent 4");
                        meName = nodeBean.getContentdesc();
                        continue;
                    }
                    StatisticsWxFriends(xmlData);//统计新增好友的信息
                    List<String> meWxIdList = AdbUtils.getAdbUtils().getNodeList(xmlData);

                    if (xmlData.contains("女")) {
                        sex = 0;
                    } else if (xmlData.contains("男")) {
                        sex = 1;
                    } else {
                        sex = 2;
                    }
//                    xmlData = wxUtils.getXmlData();//重新获取页面数据
                    xmlData = AdbUtils.getAdbUtils().dumpXml2String();
                    //                    wxUtils.adbDimensClick(context, R.dimen.x1, R.dimen.y135, R.dimen.x320, R.dimen.y166);//点击设置备注和标签

                    List<String> remarkList = AdbUtils.getAdbUtils().getNodeList(xmlData);
                    for (int r = 0; r < remarkList.size(); r++) {
                        nodeBean = AdbUtils.getAdbUtils().getNodeXmlBean(remarkList.get(r)).getNode();
                        if (nodeBean.getResourceid() != null && (nodeBean.getResourceid().equals("com.tencent.mm:id/anw"))) {
                            //筛选出好友
                            listXY = AdbUtils.getAdbUtils().getXY(nodeBean.getBounds());//获取修改备注标签
//                            wxUtils.adbClick(listXY.get(0), listXY.get(1), listXY.get(2), listXY.get(3));//点击修改备注
                            AdbUtils.getAdbUtils().click4xy(listXY.get(0), listXY.get(1), listXY.get(2), listXY.get(3));
                            break;
                        }
                    }
//                    xmlData = wxUtils.getXmlData();
                    xmlData = AdbUtils.getAdbUtils().dumpXml2String();
                    if (xmlData.contains("备注信息") && xmlData.contains("完成")) {

                    } else {
                        continue w;
                    }
                    AdbUtils.getAdbUtils().adbDimensClick(MyApplication.getContext(), R.dimen.x16, R.dimen.y89, R.dimen.x304, R.dimen.y115);//点击名字EditText
                    AdbUtils.getAdbUtils().adbDimensClick(MyApplication.getContext(), R.dimen.x252, R.dimen.y89, R.dimen.x312, R.dimen.y115);//清空名字

                    switch (sex) {//0代表女。   1代表男   2代表性别未知
                        case 0:
                            int wx_name_number_girl = (int) SPUtils.get(MyApplication.getContext(), "wx_name_number_girl", 0);
                            String wx_nume_number_new_girl = df.format(wx_name_number_girl + 1);
                            AdbUtils.getAdbUtils().adb("input text " + zzz + "B" + wx_nume_number_new_girl);
                            SPUtils.put(MyApplication.getContext(), "wx_name_number_girl", wx_name_number_girl + 1);
                            break;
                        case 1:
                            int wx_name_number_boy = (int) SPUtils.get(MyApplication.getContext(), "wx_name_number_boy", 0);
                            String wx_nume_number_new_boy = df.format(wx_name_number_boy + 1);
                            AdbUtils.getAdbUtils().adb("input text " + zzz + "A" + wx_nume_number_new_boy);
                            SPUtils.put(MyApplication.getContext(), "wx_name_number_boy", wx_name_number_boy + 1);
                            break;
                        case 2:
                            int wx_name_number_c = (int) SPUtils.get(MyApplication.getContext(), "wx_name_number_c", 0);
                            String wx_nume_number_c = df.format(wx_name_number_c + 1);
                            AdbUtils.getAdbUtils().adb("input text " + zzz + "C" + wx_nume_number_c);
                            SPUtils.put(MyApplication.getContext(), "wx_name_number_c", wx_name_number_c + 1);
                            break;
                    }
                    AdbUtils.getAdbUtils().adbDimensClick(MyApplication.getContext(), R.dimen.x252, R.dimen.y23, R.dimen.x312, R.dimen.y44);//确定修改
                    //  LogUtils.d(nodeList.get(a));
                    AdbUtils.getAdbUtils().adb("input keyevent 4");
//                    xmlData = wxUtils.getXmlData();
                    xmlData = AdbUtils.getAdbUtils().dumpXml2String();
                    if (!(xmlData.contains("微信") && xmlData.contains("通讯录") && xmlData.contains("发现") && xmlData.contains("我"))) {
                        AdbUtils.getAdbUtils().adb("input keyevent 4");

                    }

                    //设置间隔时间
                    int start;
                    if (StringUtils.isEmpty(app.getWxGeneralSettingsBean().getRemark_interval_time_s())) {
                        start = 3;
                    } else {
                        start = Integer.valueOf(app.getWxGeneralSettingsBean().getRemark_interval_time_s());
                    }
                    int end;
                    if (StringUtils.isEmpty(app.getWxGeneralSettingsBean().getRemark_interval_time_e())) {
                        end = 6;
                    } else {
                        end = Integer.valueOf(app.getWxGeneralSettingsBean().getRemark_interval_time_e());
                    }
                    int timeSleep = random.nextInt(end - start + 1) + start;
//                    LogUtils.e("end=" + end + "__start=" + start + "___间隔随机数=" + timeSleep);
                    Log.e("WG", "end=" + end + "__start=" + start + "___间隔随机数=" + timeSleep);
//                    ShowToast.show("间隔时间：" + timeSleep + "秒", (Activity) context);
                    try {
                        Thread.sleep(timeSleep * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
//            xmlData = wxUtils.getXmlData();//修改完一个重新获取页面数据
            AdbUtils.getAdbUtils().dumpXml2String();
            nodeList = AdbUtils.getAdbUtils().getNodeList(xmlData);

            if (!xmlData.contains("发现")) {
//                ShowToast.show("任务被中断，结束修改备注任务", (Activity) context);
                continue w;
            }
            zzzNum = 0;
            for (int b = 0; b < nodeList.size(); b++) {
                nodeBean = AdbUtils.getAdbUtils().getNodeXmlBean(nodeList.get(b)).getNode();
                if (nodeBean.getResourceid() != null && (nodeBean.getResourceid().equals("com.tencent.mm:id/j_")) && nodeBean.getContentdesc() != null && nodeBean.getContentdesc() != "" && !nodeBean.getContentdesc().startsWith("微信") && !nodeBean.getContentdesc().equals("文件传输助手") && !nodeBean.getContentdesc().startsWith("ZZZ") && !nodeBean.getContentdesc().startsWith("zzz") && !meName.equals(nodeBean.getContentdesc())) {
                    continue w;
                } else if (nodeBean.getResourceid() != null && (nodeBean.getResourceid().equals("com.tencent.mm:id/j_")) && nodeBean.getContentdesc() != null && nodeBean.getContentdesc() != "" && nodeBean.getContentdesc().startsWith("ZZZ")) {
                    zzzNum++;
                }
            }
            if (!bottom) {
                if (zzzNum >= 9) {
                    AdbUtils.getAdbUtils().adbDimensClick(MyApplication.getContext(), R.dimen.x296, R.dimen.y357, R.dimen.x320, R.dimen.y365);
                } else {
                    AdbUtils.getAdbUtils().adbUpSlide(MyApplication.getContext());//向上滑动
                }
            }
            endData = xmlData;
//            xmlData = wxUtils.getXmlData();//滑动后重新获取页面数据
            xmlData = AdbUtils.getAdbUtils().dumpXml2String();
            if (endData.equals(xmlData)) {
//                ShowToast.show("修改备注完成", (Activity) context);
                Log.e("WG", "修改备注完成 ");
                break w;
            }
            if (xmlData.contains("位联系人")) {//判断是否到达底部
                bottom = true;
            }
        }
    }


    public void switchWxAccount() throws Exception {
        Log.e("WG", "switchWxAccount: 切换新老账户");
        String xmlData;
        NodeXmlBean.NodeBean nodeBean;
        List<Integer> listXY = new ArrayList<>();
        int sendAccountType = SPUtils.getInt(MyApplication.getContext(), "is_accType", 0);
//        backHome();
        int accountNum = 0;
        goSwitchAccounts();

        Thread.sleep(2000);

        xmlData = AdbUtils.getAdbUtils().dumpXml2String();
        List<String> nodeList = AdbUtils.getAdbUtils().getNodeList(xmlData);
        for (int i = 0; i < nodeList.size() - 1; i++) {
            nodeBean = AdbUtils.getAdbUtils().getNodeXmlBean(nodeList.get(i)).getNode();
            if (nodeBean.getText() != null && !nodeBean.getText().isEmpty() && nodeBean.getResourceid() != null && nodeBean.getResourceid().equals("com.tencent.mm:id/d5s")) {
                Log.e("WG", "switchWxAccount: 第一次循环进来了");
                accountNum++;
            }
        }
        for (int i = 0; i < nodeList.size() - 1; i++) {
            nodeBean = AdbUtils.getAdbUtils().getNodeXmlBean(nodeList.get(i)).getNode();
            if (nodeBean.getText() != null && nodeBean.getText().equals("当前使用") && nodeBean.getResourceid() != null && nodeBean.getResourceid().equals("com.tencent.mm:id/d5v")) {
                listXY = AdbUtils.getAdbUtils().getXY(nodeBean.getBounds());//获取 当前使用的坐标
                Log.e("WG", "switchWxAccount: 第二次循环进来了");
            }
        }


////        wxUtils.adbClick(0, 36, 90, 108);//点击左上角的返回
//        AdbUtils.getAdbUtils().click4xy(0, 36, 90, 108);

        if (accountNum == 2) {  //老号在左边  新号在右边
//            Log.e("WG", "switchWxAccount: 111111");
            //说明已经登录了两个账号
            if (listXY.get(0) == 110) {
                AdbUtils.getAdbUtils().click4xy(288, 457, 384, 553);
                xmlData = AdbUtils.getAdbUtils().dumpXml2String();
                Log.e("WG", "switchWxAccount: 111" + xmlData);
                if (xmlData.contains("紧急冻结")) {
                    Log.e("WG", "switchWxAccount: 111111");
                    AdbUtils.getAdbUtils().adb("input keyevent 4");//返回
                }
                AdbUtils.getAdbUtils().click4xy(96, 457, 192, 553);
            } else if (listXY.get(0) == 302) {
                //正在使用的账号 在右边(老号)， 点击左边的账号切换
                AdbUtils.getAdbUtils().click4xy(96, 457, 192, 553);
                xmlData = AdbUtils.getAdbUtils().dumpXml2String();
                Log.e("WG", "switchWxAccount: 2222" + xmlData);
                if (xmlData.contains("紧急冻结")) {
                    Log.e("WG", "switchWxAccount:2222222 ");
                    AdbUtils.getAdbUtils().adb("input keyevent 4");//返回
                }
                AdbUtils.getAdbUtils().click4xy(288, 457, 384, 553);
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

    public boolean clickRegister() {
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


    //    // 获取目前的微信账号
    public void getUsingWxAccount() throws Exception {
        Log.e("WG", "getUsingWxAccount: 目前微信账号走了么+++");
//        backHome();
//        openWx();
//        int accountNum = 0;
        AdbUtils.getAdbUtils().click4xy(411, 822, 429, 847);// 点击右下角的我
        String xmlData = AdbUtils.getAdbUtils().dumpXml2String();
        Log.e("WG", "getUsingWxAccount: 获取目前账号" + xmlData);
        if (!xmlData.contains("相册") || !xmlData.contains("收藏")) {
            Log.e("WG", "getUsingWxAccount: 111111");
            return;
        } else {
            List<String> ud = AdbUtils.getAdbUtils().getNodeList(xmlData);
            for (int a = 0; a < ud.size(); a++) {
                NodeXmlBean.NodeBean nodeBean = AdbUtils.getAdbUtils().getNodeXmlBean(ud.get(a)).getNode();
                if (nodeBean.getResourceid() != null && (nodeBean.getResourceid().equals("com.tencent.mm:id/cdh")) && nodeBean.getText() != null && nodeBean.getText().contains("微信号")) {
                    Log.e("WG", "getUsingWxAccount: 22222");
                    String str = nodeBean.getText();
                    String wxAccount = str.replaceAll("微信号：", "");
                    SPUtils.putString(MyApplication.getContext(), "wxAccount", wxAccount);
                    break;
                }
            }
        }
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
     * 获取手机通讯录的联系人的数量
     *
     * @param context
     * @return
     */
    public int getContactCount(Context context) {
        Cursor c = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, new String[]{ContactsContract.Contacts._COUNT}, null, null, null);
        try {
            c.moveToFirst();
            return c.getInt(0);
        } catch (Exception e) {
            return 0;
        } finally {
            c.close();
        }
    }

    /**
     * 返回到微信主页面
     */
    public void backHome() throws Exception {

        wxTaskUtils.closeWx();
        wxTaskUtils.openWx();
        Thread.sleep(5000);

    }

    public void goMobileFriend() {
        ShellUtils.CommandResult commandResult = ShellUtils.execCommand("am start com.tencent.mm/com.tencent.mm.ui.bindmobile.MobileFriendUI", true);
    }

    /*
    *
    *
    *添加联系人
    *
    *
    */
    public void addContact(String name, String phoneNumber, Context context) {
        // 创建一个空的ContentValues
        ContentValues values = new ContentValues();

        // 向RawContacts.CONTENT_URI空值插入，
        // 先获取Android系统返回的rawContactId
        // 后面要基于此id插入值
        Uri rawContactUri = context.getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);
        values.clear();

        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        // 内容类型
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        // 联系人名字
        values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);
        // 向联系人URI添加联系人名字
        context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        values.clear();

        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        // 联系人的电话号码
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber);
        // 电话类型
        values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        // 向联系人电话号码URI添加电话号码
        context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        values.clear();

        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
        // 联系人的Email地址
//        values.put(ContactsContract.CommonDataKinds.Email.DATA, "zhangphil@xxx.com");
        // 电子邮件的类型
        values.put(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK);
        // 向联系人Email URI添加Email数据
        context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);

//        Toast.makeText(context, "联系人数据添加成功", Toast.LENGTH_SHORT).show();
    }


    /**
     * 清除所有联系人
     *
     * @param context
     */
    public void DeletPhone(Context context) {
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        while (cur.moveToNext()) {
            try {
                String lookupKey = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.LOOKUP_KEY));
                Uri uri = Uri.withAppendedPath(ContactsContract.
                        Contacts.CONTENT_LOOKUP_URI, lookupKey);
                System.out.println("The uri is " + uri.toString());
                cr.delete(uri, null, null);
            } catch (Exception e) {
                System.out.println(e.getStackTrace());
            }
        }
        // ShowToast.show("手机联系人清理完成", (Activity) context);
        Log.e("WG", "清理完成 ");
    }

    private void StatisticsWxFriends(String xmlData) {
        List<String> wxFriendsMessage = AdbUtils.getAdbUtils().getNodeList(xmlData);
        String wx_number = null; //微信号
        String wx_name = null;  //昵称
        String wx_location = null;//目前所在地区
        String wx_phone_number = null;//手机电话号码
        String wx_phone_name = null;//手机联系人名称
        String uid = SPUtils.getString(MyApplication.getContext(), "uid", "0000");
        for (int a = 0; a < wxFriendsMessage.size(); a++) {
            nodeBean = AdbUtils.getAdbUtils().getNodeXmlBean(wxFriendsMessage.get(a)).getNode();
            if ((xmlData.contains("com.tencent.mm:id/anq"))) {//信息里面已经有备注了的时候
                if (("com.tencent.mm:id/anq").equals(nodeBean.getResourceid())) {
                    wx_name = nodeBean.getText();
                    break;
                }
            } else {//信息里面没有备注的时候
                if (("com.tencent.mm:id/pl").equals(nodeBean.getResourceid())) {
                    wx_name = nodeBean.getText();
                    break;
                }
            }
        }
        for (int a = 0; a < wxFriendsMessage.size(); a++) {
            nodeBean = AdbUtils.getAdbUtils().getNodeXmlBean(wxFriendsMessage.get(a)).getNode();
            if ((wx_location == null) && xmlData.contains("地区") && ("android:id/summary".equals(nodeBean.getResourceid()))) {
                wx_location = nodeBean.getText();
            } else if (nodeBean != null && xmlData.contains("微信号") && ("com.tencent.mm:id/ang").equals(nodeBean.getResourceid())) {
                wx_number = nodeBean.getText();
            } else if (nodeBean != null && xmlData.contains("电话号码") && ("com.tencent.mm:id/cp2").equals(nodeBean.getResourceid())) {
                wx_phone_number = AdbUtils.getAdbUtils().getNodeXmlBean(wxFriendsMessage.get(a + 2)).getNode().getText();
            }
        }
        if (wx_number != null && wx_number.contains(":")) {
            int start = wx_number.indexOf(":");
            String wx_number2 = wx_number.substring(start + 1, wx_number.length());
            wx_number = wx_number2.trim();
        }

        if (wx_name != null && wx_name.contains(":")) {
            int start = wx_name.indexOf(":");
            String wx_name2 = wx_name.substring(start + 1, wx_name.length());
            wx_name = wx_name2.trim();
        }
        if (wx_name != null) {
            for (int i = 0; i < wx_name.length(); i++) {
                char codePoint = wx_name.charAt(i);
                if (!((codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) ||
                        (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                        ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000)
                        && (codePoint <= 0x10FFFF)))) {
                    wx_name = (wx_name.substring(0, i) + wx_name.substring(i + 1)).trim();
                }
            }
        }
        Log.d("获取到的信息", "微信号: " + wx_number + "微信名字： " + wx_name + "微信所在地区： " + wx_location + " 微信手机号：" + wx_phone_number + " 手机联系人名： " + wx_phone_name + "设备的ID ： " + uid);

        List<WxFriendsMessageBean> mWxFriendsMessageBean = new ArrayList<>();
        WxFriendsMessageBean messageBean = new WxFriendsMessageBean(wx_number, wx_name, wx_phone_number, wx_phone_name, wx_location, uid);
        //       JSON[{"wx_location":"安道尔","wx_name":"女人如烟 ","wx_phone_name":"李霞","wx_phone_number":"13801522864","wx_uid":"1122"}]
        mWxFriendsMessageBean.add(messageBean);
        String str = new Gson().toJson(mWxFriendsMessageBean);
//        LogUtils.d("JSON" + str.toString());
//        sendWxFriendsMessage(str);
//        NetUtils.get()
        return;
    }
}
