package com.zplh.zplh_android_yk.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zplh.zplh_android_yk.R;
import com.zplh.zplh_android_yk.base.MyApplication;
import com.zplh.zplh_android_yk.bean.NodeXmlBean;
import com.zplh.zplh_android_yk.bean.WxFriendsMessageBean;
import com.zplh.zplh_android_yk.constant.URLS;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Response;


/* 封装了一些普遍的微信adb操作
 * Created by yong hao zeng on 2018/4/14/014.
 */
public class WxTaskUtils {

    private List<String> commnandList;

    private static WxTaskUtils wxTaskUtils;
    private NodeXmlBean.NodeBean nodeBean;
    private List<Integer> listXY;
    private boolean flag;
    //    private BaseApplication app = new BaseApplication();

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
     * 微信账号切换 6.6.3 版本  只有发送 新号或者老号中的一个
     */

    public void switchWxAccount1() throws Exception {
        AdbUtils adbUtils = AdbUtils.getAdbUtils();
        String xmlData = "";
        SPUtils.putString(MyApplication.getContext(), "SwitchAccountSuccess", "0");
        int sendAccountType = SPUtils.getInt(MyApplication.getContext(), "is_accType", 0);//1为新号 2为老号 3为全部
        Log.e("WG", "什么号： " + sendAccountType);
        int accountNum = 0;
        adbUtils.click4xy(411, 822, 429, 847);// 点击右下角的我
        xmlData = AdbUtils.getAdbUtils().dumpXml2String();
        if (!xmlData.contains("相册") || !xmlData.contains("收藏")) {
            return;
        } else {
            List<String> ud = adbUtils.getNodeList(xmlData);
            for (int a = 0; a < ud.size(); a++) {
                NodeXmlBean.NodeBean nodeBean = adbUtils.getNodeXmlBean(ud.get(a)).getNode();
                if (nodeBean != null && nodeBean.getResourceid() != null && (nodeBean.getResourceid().equals("com.tencent.mm:id/cdh")) && nodeBean.getText() != null && nodeBean.getText().contains("微信号")) {
                    String str = nodeBean.getText();
                    String wxAccount = str.replaceAll("微信号：", "");
                    SPUtils.putString(MyApplication.getContext(), "wxAccount", wxAccount);
                    break;
                }
            }
        }
        adbUtils.adbUpSlide(MyApplication.getContext());
        goSwitchAccounts();
        Thread.sleep(3000);
        xmlData = AdbUtils.getAdbUtils().dumpXml2String();
        List<String> nodeList = adbUtils.getNodeList(xmlData);
        for (int i = 0; i < nodeList.size() - 1; i++) {
            nodeBean = adbUtils.getNodeXmlBean(nodeList.get(i)).getNode();
            if (nodeBean != null && nodeBean.getText() != null && !nodeBean.getText().equals("切换帐号") && nodeBean.getResourceid() != null && nodeBean.getResourceid().equals("com.tencent.mm:id/d5s")) {
                accountNum++;
            }
        }
        for (int i = 0; i < nodeList.size() - 1; i++) {
            nodeBean = adbUtils.getNodeXmlBean(nodeList.get(i)).getNode();
            if (nodeBean != null && nodeBean.getText() != null && nodeBean.getText().equals("当前使用") && nodeBean.getResourceid() != null && nodeBean.getResourceid().equals("com.tencent.mm:id/d5v")) {
                listXY = adbUtils.getXY(nodeBean.getBounds());//获取 当前使用的坐标
                break;
            }
        }
        if (accountNum == 2) {  //老号在左边  新号在右边

            //说明已经登录了两个账号
            if (listXY.get(0) == 110) {
                //正在使用的账号 在左边（老号）， 点击右边的账号切换

                if ((sendAccountType == 3) || (sendAccountType == 1)) {
                    Log.e("WG", "switchWxAccount1 110: " + sendAccountType);
                    SPUtils.putString(MyApplication.getContext(), "WxAccountLocation", "1"); //目前的账号在左边
                    adbUtils.click4xy(0, 36, 90, 108);//点击左上角的返回
//                    adbUtils.back();
                    SPUtils.putString(MyApplication.getContext(), "SwitchAccountSuccess", "1"); // 没有切换
                    adbUtils.click4xy(0, 36, 90, 108);//点击左上角的返回
//                    adbUtils.back();

                    return;
                }
                if (sendAccountType == 2) {
                    Log.e("WG", "switchWxAccount1 110: " + sendAccountType);
                    SPUtils.putString(MyApplication.getContext(), "WxAccountLocation", "2"); //切换后的账号在右边
                    adbUtils.click4xy(288, 457, 384, 553);
                    SPUtils.putString(MyApplication.getContext(), "SwitchAccountSuccess", "2"); // 切换成功
                }
            } else if (listXY.get(0) == 302) {
                //正在使用的账号 在右边(新号)， 点击左边的账号切换

                if ((sendAccountType == 3) || (sendAccountType == 2)) {
                    Log.e("WG", "switchWxAccount1 302: " + sendAccountType);
                    SPUtils.putString(MyApplication.getContext(), "WxAccountLocation", "2"); //目前的账号在右边
                    adbUtils.click4xy(0, 36, 90, 108);//点击左上角的返回
//                    adbUtils.back();
                    SPUtils.putString(MyApplication.getContext(), "SwitchAccountSuccess", "1"); // 没有切换
                    adbUtils.click4xy(0, 36, 90, 108);//点击左上角的返回
//                    adbUtils.back();


                    return;
                }
                if (sendAccountType == 1) {
                    Log.e("WG", "switchWxAccount1 302: " + sendAccountType);
                    SPUtils.putString(MyApplication.getContext(), "WxAccountLocation", "1"); //切换后的账号在右边
                    adbUtils.click4xy(96, 457, 192, 553);
                    SPUtils.putString(MyApplication.getContext(), "SwitchAccountSuccess", "2"); // 切换成功
                }
            }
            Thread.sleep(10000);

        } else {
            if (listXY.get(0) == 110) {
                if (sendAccountType == 1) {
                    SPUtils.putString(MyApplication.getContext(), "AccountIsOnlyOne", "2");
                    SPUtils.putString(MyApplication.getContext(), "WxAccountLocation", "1"); //目前的账号在左边
                } else {
                    SPUtils.putString(MyApplication.getContext(), "AccountIsOnlyOne", "1");  //失败
                    SPUtils.putString(MyApplication.getContext(), "WxAccountLocation", "1"); //目前的账号在右边
                }
            } else if (listXY.get(0) == 302) {
                if (sendAccountType == 1) {
                    SPUtils.putString(MyApplication.getContext(), "AccountIsOnlyOne", "1");//失败
                    SPUtils.putString(MyApplication.getContext(), "WxAccountLocation", "2"); //目前的账号在右边
                } else {
                    SPUtils.putString(MyApplication.getContext(), "AccountIsOnlyOne", "2");
                    SPUtils.putString(MyApplication.getContext(), "WxAccountLocation", "2"); //目前的账号在右边
                }
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
            if (nodeBean != null && nodeBean.getText() != null && !nodeBean.getText().equals("切换帐号") && nodeBean.getResourceid() != null && nodeBean.getResourceid().equals("com.tencent.mm:id/d5s")) {
                Log.e("WG", "switchWxAccount: 进来了1");
                accountNum++;
            }
        }
        for (int i = 0; i < nodeList.size() - 1; i++) {
            nodeBean = AdbUtils.getAdbUtils().getNodeXmlBean(nodeList.get(i)).getNode();
            if (nodeBean.getText() != null && nodeBean.getText().equals("当前使用") && nodeBean.getResourceid() != null && nodeBean.getResourceid().equals("com.tencent.mm:id/d5v")) {
                listXY = AdbUtils.getAdbUtils().getXY(nodeBean.getBounds());//获取 当前使用的坐标
                Log.e("WG", "switchWxAccount: 进来了2");
            }
        }

////        wxUtils.adbClick(0, 36, 90, 108);//点击左上角的返回
//        AdbUtils.getAdbUtils().click4xy(0, 36, 90, 108);

        if (accountNum == 2) {  //老号在左边  新号在右边
            Log.e("WG", "switchWxAccount: 两个号");
//            Log.e("WG", "switchWxAccount: 111111");
            //说明已经登录了两个账号
            if (listXY.get(0) == 110) {
                AdbUtils.getAdbUtils().click4xy(288, 457, 384, 553);
                SPUtils.putString(MyApplication.getContext(), "WxAccountLocation", "2");
                if (!getIsAccountIsOk()) {
//                    AdbUtils.getAdbUtils().back();
                    Log.e("WG", "switchWxAccount: 左边");
                }
            } else if (listXY.get(0) == 302) {
                //正在使用的账号 在右边(老号)， 点击左边的账号切换
                AdbUtils.getAdbUtils().click4xy(96, 457, 192, 553);
                SPUtils.putString(MyApplication.getContext(), "WxAccountLocation", "1");
                if (!getIsAccountIsOk()) {
//                    AdbUtils.getAdbUtils().back();
                    Log.e("WG", "switchWxAccount: 右边");
                }
            }
            Thread.sleep(10000);
            return;
        }
        AdbUtils.getAdbUtils().back();
//        backHome();
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


    /**
     * 判断是否被封号
     *
     * @return
     */
    public Boolean getIsAccountIsOk() throws Exception {

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String xmlData = AdbUtils.getAdbUtils().dumpXml2String();
        if (xmlData.contains("紧急冻结") && xmlData.contains("找回密码") && xmlData.contains("微信安全中心")) {
            String currentLocation = SPUtils.getString(MyApplication.getContext(), "WxAccountLocation", "0");
            AdbUtils.getAdbUtils().back();
            if (currentLocation.equals("1")) {
                Log.e("WG", "getIsAccountIsOk: 封右边");
                AdbUtils.getAdbUtils().click4xy(288, 457, 384, 553);
            } else {
                AdbUtils.getAdbUtils().click4xy(96, 457, 192, 553);
                Log.e("WG", "getIsAccountIsOk: 封左边");
            }
            Thread.sleep(10000);
            return false;
        }
        return true;
    }


    public void goMobileFriend() {
        ShellUtils.CommandResult commandResult = ShellUtils.execCommand("am start com.tencent.mm/com.tencent.mm.ui.bindmobile.MobileFriendUI", true);
    }
    public String imageData = "[微笑],[撇嘴],[色],[发呆],[得意],[流泪],[害羞],[闭嘴],[睡],[大哭],[尴尬],[调皮],[呲牙],[惊讶],[难过],[酷],[冷汗],[吐],[偷笑],[愉快],[白眼],[傲慢],[饥饿],[困],[惊恐],[流汗],[憨笑],[悠闲],[奋斗],[疑问],[嘘],[晕],[疯了],[敲打],[再见],[擦汗],[抠鼻],[鼓掌],[糗大了],[坏笑],[左哼哼],[右哼哼],[哈欠],[鄙视],[委屈],[快哭了],[阴险],[亲亲],[吓],[可怜],[西瓜],[啤酒],[篮球],[乒乓],[咖啡],[饭],[猪头],[玫瑰],[凋谢],[嘴唇],[爱心],[心碎],[蛋糕],[闪电],[足球],[瓢虫],[月亮],[太阳],[礼物],[拥抱],[强],[握手],[胜利],[抱拳],[勾引],[拳头],[差劲],[NO],[OK],[爱情],[飞吻],[跳跳],[发抖],[怄火],[转圈],[磕头],[回头],[跳绳],[投降]";

    Random random = new Random();// 定义随机类
    public String getFaceText(String data) {

//        String data="我爱哭的时候便哭，想笑的时候便笑，只要这一切出于自然。";
        String[] strings = imageData.split(",");
        int count = 0;
        String[] texts = data.split("，");

        for (int a = 0; a < texts.length; a++) {
            if (count >= 4) {
                break;
            } else {

                if (random.nextInt(texts.length) == 0) {
                    int num = random.nextInt(3);
                    switch (num) {
                        case 0:
                            texts[a] = texts[a] + strings[random.nextInt(strings.length - 1)];
                            break;
                        case 1:
                            texts[a] = texts[a] + strings[random.nextInt(strings.length - 1)] + strings[random.nextInt(strings.length - 1)];
                            break;
                        case 2:
                            texts[a] = texts[a] + strings[random.nextInt(strings.length - 1)] + strings[random.nextInt(strings.length - 1)] + strings[random.nextInt(strings.length - 1)];
                            break;
                    }
                    count++;
                }
            }
        }

        StringBuffer stringBuffer = new StringBuffer("");
        for (int b = 0; b < texts.length; b++) {
            if (count == 0) {
                int num = random.nextInt(3);
                switch (num) {
                    case 0:
                        stringBuffer.append(strings[random.nextInt(strings.length - 1)]);
                        break;
                    case 1:
                        stringBuffer.append(strings[random.nextInt(strings.length - 1)]).append(strings[random.nextInt(strings.length - 1)]);
                        break;
                    case 2:
                        stringBuffer.append(strings[random.nextInt(strings.length - 1)]).append(strings[random.nextInt(strings.length - 1)]).append(strings[random.nextInt(strings.length - 1)]);
                        break;
                }
                count = 1;
            }
            if (b == 0) {
                stringBuffer.append(texts[b].toString());
            } else {
                stringBuffer.append(",").append(texts[b].toString());
            }

        }
        return stringBuffer.toString();
    }
    public void StatisticsWxFriends(String xmlData) {
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
        try {
            Response json = OkHttpUtils.post().url(URLS.statictis_wx_message_store()).addParams("json", str).build().execute();
            if (json.code() == 200) {
                Log.e("WG", "新好友统计信息上传成功 ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    /*
    *
    * 添加到相册
    *
    * */
    public static void addimages(File result, Context context) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(result);
        intent.setData(uri);
        context.sendBroadcast(intent);
    }


    /**
     * 初始化微信 到第一个界面
     */
    public void openWxIsHome() throws Exception {
        AdbUtils adbUtils = AdbUtils.getAdbUtils();
        closeWx();
        openWx();
        Thread.sleep(2500);
        String xml = adbUtils.dumpXml2String();
        //不在初始化界面 一般情况下是未登陆
        if (!xml.contains("通讯录") || !xml.contains("发现")) {
            //在注册界面
            if (xml.contains("注册")) {
                throw new Exception("未登录");
            }
            //在切换账号界面
            if (xml.contains("清除登录痕迹")) {
                switchWxAccount();
            }
        }
    }


    public void goSearch() {
        ShellUtils.CommandResult commandResult = ShellUtils.execCommand("am start com.tencent.mm/.plugin.search.ui.FTSMainUI", true);
    }
}
