package com.zplh.zplh_android_yk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zplh.zplh_android_yk.R;
import com.zplh.zplh_android_yk.base.MyApplication;
import com.zplh.zplh_android_yk.bean.NodeXmlBean;
import com.zplh.zplh_android_yk.bean.WxNumBean;
import com.zplh.zplh_android_yk.constant.URLS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
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


    /*
    *
    *微信号切换
    *
    **/

    private void switchWxAccount1() throws InterruptedException {
        int sendAccountType = SPUtils.getInt(MyApplication.getContext(), "is_accType", 0);//1为新号 2为老号 3为全部
        openWx();
        int accountNum = 0;
        String xmlData;
//        wxUtils.adbClick(411, 822, 429, 847);// 点击右下角的我
        AdbUtils.getAdbUtils().click4xy(411, 822, 429, 847);
        xmlData = AdbUtils.getAdbUtils().dumpXml2String();
        if (!xmlData.contains("相册") || !xmlData.contains("收藏")) {
            return;
        } else {
            List<String> ud = AdbUtils.getAdbUtils().getNodeList(xmlData);
            for (int a = 0; a < ud.size(); a++) {
                NodeXmlBean.NodeBean nodeBean = AdbUtils.getAdbUtils().getNodeXmlBean(ud.get(a)).getNode();
                if (nodeBean != null && nodeBean.getResourceid() != null && (nodeBean.getResourceid().equals("com.tencent.mm:id/cdh")) && nodeBean.getText() != null && nodeBean.getText().contains("微信号")) {
                    String str = nodeBean.getText();
                    String wxAccount = str.replaceAll("微信号：", "");
                    SPUtils.putString(MyApplication.getContext(), "wxAccount", wxAccount);
                    break;
                }
            }
        }
        AdbUtils.getAdbUtils().adbUpSlide(MyApplication.getContext());
        AdbUtils.getAdbUtils().wxActivityJump("com.tencent.mm/com.tencent.mm.plugin.setting.ui.setting.SettingsUI");
        AdbUtils.getAdbUtils().adbUpSlide(MyApplication.getContext());
        AdbUtils.getAdbUtils().click4xy(21, 681, 459, 714);//点击切换账号
        Thread.sleep(3000);

        xmlData = AdbUtils.getAdbUtils().dumpXml2String();
        List<String> nodeList = AdbUtils.getAdbUtils().getNodeList(xmlData);
        for (int i = 0; i < nodeList.size() - 1; i++) {
            nodeBean = AdbUtils.getAdbUtils().getNodeXmlBean(nodeList.get(i)).getNode();
            if (nodeBean != null && nodeBean.getText() != null && !nodeBean.getText().isEmpty() && nodeBean.getResourceid() != null && nodeBean.getResourceid().equals("com.tencent.mm:id/d5s")) {
                accountNum++;
            }
        }
        for (int i = 0; i < nodeList.size() - 1; i++) {
            nodeBean = AdbUtils.getAdbUtils().getNodeXmlBean(nodeList.get(i)).getNode();
            if (nodeBean != null && nodeBean.getText() != null && nodeBean.getText().equals("当前使用") && nodeBean.getResourceid() != null && nodeBean.getResourceid().equals("com.tencent.mm:id/d5v")) {
                //获取 当前使用的坐标
                listXY = AdbUtils.getAdbUtils().getXY(nodeBean.getBounds());
                break;
            }
        }
        if (accountNum == 2) {  //老号在左边  新号在右边

            //说明已经登录了两个账号
            if (listXY.get(0) == 110) {
                //正在使用的账号 在左边（老号）， 点击右边的账号切换

                if ((sendAccountType == 3) || (sendAccountType == 1)) {
                    SPUtils.putString(MyApplication.getContext(), "WxAccountLocation", "1"); //目前的账号在左边
                    AdbUtils.getAdbUtils().click4xy(0, 36, 90, 108);//点击左上角的返回
                    return;
                }
                if (sendAccountType == 2) {
                    SPUtils.putString(MyApplication.getContext(), "WxAccountLocation", "2"); //切换后的账号在右边
                    AdbUtils.getAdbUtils().click4xy(288, 457, 384, 553);
                }
            } else if (listXY.get(0) == 302) {
                //正在使用的账号 在右边(新号)， 点击左边的账号切换

                if ((sendAccountType == 3) || (sendAccountType == 2)) {
                    SPUtils.putString(MyApplication.getContext(), "WxAccountLocation", "2"); //目前的账号在右边
                    AdbUtils.getAdbUtils().click4xy(0, 36, 90, 108);//点击左上角的返回
                    return;
                }
                if (sendAccountType == 1) {
                    SPUtils.putString(MyApplication.getContext(), "WxAccountLocation", "1"); //切换后的账号在右边
                    AdbUtils.getAdbUtils().click4xy(96, 457, 192, 553);
                }
            }
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        AdbUtils.getAdbUtils().click4xy(0, 36, 90, 108);//点击左上角的返回
    }

    /*
    *
    * 统计微信和群
    *
    */

    public void statistics() {
        String xmlData;
//        xmlData = wxUtils.getXmlData();
        xmlData = AdbUtils.getAdbUtils().dumpXml2String();
        if (xmlData.contains("详细资料")) {
//            wxUtils.adb("input keyevent 4");
            AdbUtils.getAdbUtils().adb("input keyevent 4");
        }
        WxNumBean wxNumBean = new WxNumBean();
        WxNumBean.ContentBean contentBean = new WxNumBean.ContentBean();
        List<WxNumBean.ContentBean.FlockBean> flockBeanList = new ArrayList<>();
        while (true) {
//            wxUtils.adb("input swipe 200 700 200 200 50");//滑动到底部
            AdbUtils.getAdbUtils().adb("input swipe 200 700 200 200 50");
//            xmlData = wxUtils.getXmlData();
            xmlData = AdbUtils.getAdbUtils().dumpXml2String();
            if (xmlData.contains("位联系人")) {
                break;
            }
        }
//        List<String> strings = wxUtils.getNodeList(xmlData);
        List<String> strings = AdbUtils.getAdbUtils().getNodeList(xmlData);

        //设置好友数
        for (String s : strings) {
//            NodeXmlBean.NodeBean nodeBean = wxUtils.getNodeXmlBean(s).getNode();
            NodeXmlBean.NodeBean nodeBean = AdbUtils.getAdbUtils().getNodeXmlBean(s).getNode();
            if ("com.tencent.mm:id/amy".equals(nodeBean.getResourceid())) {
                contentBean.setFriends_num(nodeBean.getText().replace("位联系人", ""));
                Log.e("WG", "statistics: " + "联系人" + nodeBean.getText().replace("位联系人", ""));
//                LogUtils.d("联系人" + nodeBean.getText().replace("位联系人", ""));
                break;
            }
        }
        //设置uid
        contentBean.setUid(SPUtils.getString(MyApplication.getContext(), "uid", "0000"));

        //设置群信息
//        wxUtils.adb("input swipe 200 300 200 700 50");//滑动到顶部
        AdbUtils.getAdbUtils().adbDimensClick(MyApplication.getContext(), R.dimen.x80, R.dimen.y367, R.dimen.x160, R.dimen.y400);//点击通讯录
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        AdbUtils.getAdbUtils().adbDimensClick(MyApplication.getContext(), R.dimen.x1, R.dimen.y87, R.dimen.x320, R.dimen.y124);//群聊
/*        //进入群聊
        nodeList = wxUtils.getNodeList(xmlData);
        for (int a = 0; a < nodeList.size(); a++) {
            NodeXmlBean.NodeBean nodeBean = wxUtils.getNodeXmlBean(nodeList.get(a)).getNode();
            if ("群聊".equals(nodeBean.getText())) {//获取群聊node节点
                listXY = wxUtils.getXY(nodeBean.getBounds());//获取群聊坐标
                wxUtils.adbClick(listXY.get(0), listXY.get(1), listXY.get(2), listXY.get(3));//点击群聊
                break;
            }
        }*/

        String qunClickMark = "";//进过的群
        boolean isOneSlide = false;
        int count = 0;

        //进入了群列表
        w:
        while (true) {
//            xmlData = wxUtils.getXmlData();
            xmlData = AdbUtils.getAdbUtils().dumpXml2String();
            if (xmlData.contains("新群聊") && xmlData.contains("你可以通过群聊中的“保存到通讯录”选项，将其保存到这里")) {
//                wxUtils.adb("input keyevent 4");
                AdbUtils.getAdbUtils().adb("input keyevent 4");
//                ShowToast.show("没有群...", (Activity) context);
                break;
            }

            List<String> nodeList = new ArrayList<>();
            Pattern pattern = Pattern.compile("<node.*?text=\"(.*?)\".*?resource-id=\"(.*?)\" class=\"(.*?)\" package=\"(.*?)\".*?content-desc=\"(.*?)\".*?checked=\"(.*?)\".*?enabled=\"(.*?)\".*?selected=\"(.*?)\".*?bounds=\"\\[(\\d+),(\\d+)\\]\\[(\\d+),(\\d+)\\]\"");
            Matcher matcher = pattern.matcher(xmlData);
            while (matcher.find()) {
                nodeList.add(matcher.group() + "/>");
            }

            for (int a = 0; a < nodeList.size(); a++) {
//                NodeXmlBean.NodeBean nodeBean = wxUtils.getNodeXmlBean(nodeList.get(a)).getNode();
                NodeXmlBean.NodeBean nodeBean = AdbUtils.getAdbUtils().getNodeXmlBean(nodeList.get(a)).getNode();
                if (nodeBean.getText() != null && nodeBean.getResourceid() != null && nodeBean.getResourceid().contains("com.tencent.mm:id/a9u")) {
                    String flockName = nodeBean.getText();
                   /* if (true) {//给自己群发
                        if (nodeBean.getText().length() < 7) {
                            continue;
                        }
                        if (!(nodeBean.getText().substring(nodeBean.getText().length() - 7).startsWith("a") || nodeBean.getText().substring(nodeBean.getText().length() - 7).startsWith("A")) && !(nodeBean.getText().substring(nodeBean.getText().length() - 7).startsWith("b") || nodeBean.getText().substring(nodeBean.getText().length() - 7).startsWith("B"))) {
                            continue;
                        }
                    }*/


                    if (qunClickMark.contains(nodeBean.getText())) {
                        continue;
                    } else {
                        if (!isOneSlide) {
                            for (int b = 0; b < count; b++) {
//                                wxUtils.adbUpSlide(context);//向上滑动
                                AdbUtils.getAdbUtils().adbUpSlide(MyApplication.getContext());
                            }
                        }
                        isOneSlide = false;
//                        listXY = wxUtils.getXY(nodeBean.getBounds());//获取群坐标
                        listXY = AdbUtils.getAdbUtils().getXY(nodeBean.getBounds());//获取群坐标
//                        wxUtils.adbClick(listXY.get(0), listXY.get(1), listXY.get(2), listXY.get(3));//点击进入群
                        AdbUtils.getAdbUtils().click4xy(listXY.get(0), listXY.get(1), listXY.get(2), listXY.get(3));
                        qunClickMark = qunClickMark + nodeBean.getText() + ",";
                    }

                    //_______________________________________________________________________________________________
                    String qunName = "";
                    //获取群人数，男女群信息
//                    String qunNameData = wxUtils.getXmlData();
                    String qunNameData = AdbUtils.getAdbUtils().dumpXml2String();
                    List<String> qunNameDataList = new ArrayList<String>();
                    Matcher matcherA = pattern.matcher(qunNameData);
                    while (matcherA.find()) {
                        qunNameDataList.add(matcherA.group() + "/>");
                    }
                    if (!(qunNameData.contains("当前所在页面,与"))) {
                        Toast.makeText(MyApplication.getContext(), "任务被中断，结束拉群任务", Toast.LENGTH_LONG).show();
//                        ShowToast.show("任务被中断，结束拉群任务", (Activity) context);

                        break w;
                    }

                    for (int c = 0; c < qunNameDataList.size(); c++) {
//                        NodeXmlBean.NodeBean qunNameBean = wxUtils.getNodeXmlBean(qunNameDataList.get(c)).getNode();
                        NodeXmlBean.NodeBean qunNameBean = AdbUtils.getAdbUtils().getNodeXmlBean(qunNameDataList.get(c)).getNode();
                        if ("com.tencent.mm:id/hj".equals(qunNameBean.getResourceid())) {
                            qunName = qunNameBean.getText();
                            Log.e("WG", "statistics: " + qunName);
//                            LogUtils.d(qunName + "qunName");
                            break;
                        }
                    }

                    //操作群

                    if (qunName.length() >= 10) {
                        String regEx = "[^0-9]";
                        Pattern p = Pattern.compile(regEx);
                        Matcher m = p.matcher(qunName.substring(qunName.length() - 3));
                        int qb = Integer.parseInt(m.replaceAll("").trim());//群人数

                        flockBeanList.add(new WxNumBean.ContentBean.FlockBean(flockName, qb + ""));
                    }

//--------------------------------------------------------------------------------------------------------------------------------------

                    //返回
                    AdbUtils.getAdbUtils().adb("input keyevent 4");
                    AdbUtils.getAdbUtils().adbDimensClick(MyApplication.getContext(), R.dimen.x80, R.dimen.y367, R.dimen.x160, R.dimen.y400);
                    AdbUtils.getAdbUtils().adbDimensClick(MyApplication.getContext(), R.dimen.x1, R.dimen.y87, R.dimen.x320, R.dimen.y124);


                }
            }

            String strXmlData = xmlData;
//            wxUtils.adbUpSlide(context);//向上滑动
            AdbUtils.getAdbUtils().adbUpSlide(MyApplication.getContext());
            count++;
            isOneSlide = true;
//            xmlData = wxUtils.getXmlData();
            xmlData = AdbUtils.getAdbUtils().dumpXml2String();
            if (xmlData.equals(strXmlData)) {
//                wxUtils.adb("input keyevent 4");
                AdbUtils.getAdbUtils().adb("input keyevent 4");
//                ShowToast.show("群消息发送完成", (Activity) context);
                Toast.makeText(MyApplication.getContext(),"群消息发送完成",Toast.LENGTH_LONG).show();
                break;
            }
        }
//        ShowToast.show("群信息统计完毕", (Activity) context);
        //添加
        contentBean.setFlock(flockBeanList);
        String wxAccount = SPUtils.getString(MyApplication.getContext(), "wxAccount", ""); //目前的微信号
        String accountLocation = SPUtils.getString(MyApplication.getContext(), "WxAccountLocation", ""); //获取目前的位置
        contentBean.setAccount(wxAccount);
        contentBean.setLocation(accountLocation);
        wxNumBean.setContent(contentBean);
        String str = new Gson().toJson(wxNumBean);
        Log.e("WG", "statistics: "+"JSON" + str.toString() );
//        LogUtils.d("JSON" + str.toString());
//        ShowToast.show(str.toString(), (Activity) context);
//        setWxnum(str);

        try {
            Response data = OkHttpUtils.post().url(URLS.wxNewstatictis_crowd()).addParams("data", str.replace("\\", "")).build().execute();
            if (data.code() == 200) {
                String string = data.body().string();
                Log.d("zs1", string);
            } else {
                data = OkHttpUtils.post().url(URLS.wxNewstatictis_crowd()).addParams("data", str.replace("\\", "")).build().execute();
                if (data.code() == 200) {
                    String string = data.body().string();
                    Log.d("zs2", string);
                } else {
                    data = OkHttpUtils.post().url(URLS.wxNewstatictis_crowd()).addParams("data", str.replace("\\", "")).build().execute();
                    if (data.code() == 200) {
                        String string = data.body().string();
                        Log.d("zs3", string);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
