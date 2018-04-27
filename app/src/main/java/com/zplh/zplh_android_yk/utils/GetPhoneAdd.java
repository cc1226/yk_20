package com.zplh.zplh_android_yk.utils;

import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.content.ClipboardManager;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zplh.zplh_android_yk.R;
import com.zplh.zplh_android_yk.base.MyApplication;
import com.zplh.zplh_android_yk.bean.NodeXmlBean;
import com.zplh.zplh_android_yk.bean.WxPhone;
import com.zplh.zplh_android_yk.bean.WxPhoneNumeAskBean;
import com.zplh.zplh_android_yk.bean.wxReplyMessageBean;
import com.zplh.zplh_android_yk.constant.URLS;
import com.zplh.zplh_android_yk.ui.MainActivity;
import com.zplh.zplh_android_yk.ui.TaskFragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by Administrator on 2018/4/19/019.
 */

public class GetPhoneAdd {

    private boolean flg;
    private String one_add_num_s;//通讯录加好友 开始
    private String one_add_num_e;//通讯录加好友 结束
    private String[] sex_key;
    private String add_interval_time_s;//单次加好友间隔时间 开始

    private String add_interval_time_e;//单次加好友结束间隔时间
    private String contact_verify_msg = "";//申请添加好友的发送内容
    private String day_add_num = "";	/*10*///一个微信号每天最多请求加好友次数:(通讯录加好友)
    private String one_add_num = "";	/*13*///一个微信号每次任务最多请求加好友次数(通讯录加好友)
    private List<String> nodeList;
    private NodeXmlBean.NodeBean nodeBean;
    private String xmlData;
    private List<Integer> listXY;
    private String reply_msg;//微信通讯录加好友成功之后，发送的一条消息
    private ClipboardManager cm;
    private ClipboardManager cmm2;
    public Handler handler = new Handler(Looper.getMainLooper());


    public GetPhoneAdd(boolean flg, String one_add_num_s, String one_add_num_e, String add_interval_time_s
            , String add_interval_time_e, String contact_verify_msg, String day_add_num, String one_add_num) {
        this.flg = flg;
        this.one_add_num_s = one_add_num_s;
        this.one_add_num_e = one_add_num_e;
        this.add_interval_time_s = add_interval_time_s;
        this.add_interval_time_e = add_interval_time_e;
        this.day_add_num = day_add_num;
        this.one_add_num = one_add_num;
    }

    public void getPhoneAdd() throws Exception {
        String xmlData;
        List<Integer> listXY;
        //"http://103.94.20.101:8087/api_wechat/index.php";
        // 模拟http请求，提交数据到服务器

        String uid = SPUtils.getString(MyApplication.getContext(), "uid", "0000");
        int add_nums = 0;
        int max = 0;
        int min = 0;
        Log.e("WG", "添加联系人进来");
        if (StringUtils.isEmpty(one_add_num_s) || StringUtils.isEmpty(one_add_num_e)) {
            add_nums = 5;
        } else {
            max = Integer.parseInt(one_add_num_e);
            min = Integer.parseInt(one_add_num_s);
            Random random = new Random();
            add_nums = random.nextInt(max) % (max - min + 1) + min;
            Log.e("WG", "通讯录添加好友的次数为" + add_nums);
//        }
            String account = SPUtils.getString(MyApplication.getContext(), "wxAccount", ""); //目前的微信号
            Log.e("WG", "微信搜索添加好友的接口是:" + URLS.phone_url + "?zh=" + uid + "&limit=" + add_nums + "&account=" + account);
            try {
                //通讯录加好友请求网络访问部分
                Response data = OkHttpUtils.get().url(URLS.phone_url).addParams("zh", uid).addParams("limit", add_nums + "").addParams("account", account).build().execute();
                Log.e("WG", " 请求网络" + data);
                if (data.code() == 200) {
                    String string = data.body().string();
                    Log.e("zs1", string);
                    WxPhone phoneBean = GsonUtils.fromJson(string, WxPhone.class);
                    Log.e("WG", "正在添加手机联系人请稍后... ");
                    for (int i = 0; i < phoneBean.getData().size(); i++) {
                        addContact(phoneBean.getData().get(i).getName(), phoneBean.getData().get(i).getPhone(), MyApplication.getContext());
                        Log.e("WG", "电话号码为" + phoneBean.getData().get(i).getPhone());
                    }
                    addCommunication();
                } else {
                    Log.e("WG", " 获取号码失败");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MyApplication.getContext(), "连接服务器超时,获取号码失败", Toast.LENGTH_LONG).show();
                        }

                    });
                }
            } catch (IOException e) {
                Log.e("WG", "getPhoneAdd: 异常：" + e.toString());
                e.printStackTrace();
            }
        }
    }

    public void addCommunication() throws Exception {
        /**
         * 添加手机联系人通讯录
         */
        String xmlData;
        List<Integer> listXY;
        Log.e("WG", "addCommunication: " + flg);
        if (!flg) {
            WxTaskUtils.getWxTaskUtils().switchWxAccount();
            WxTaskUtils.getWxTaskUtils().switchWxAccount();
        }
        AdbUtils.getAdbUtils().wxActivityJump("com.tencent.mm/com.tencent.mm.ui.bindmobile.MobileFriendUI");
        Log.e("WG", "等待中 ");
        Thread.sleep(15000);
        xmlData = AdbUtils.getAdbUtils().dumpXml2String();

        if (!xmlData.contains("查看手机通讯录") || !xmlData.contains("添加") || !xmlData.contains("微信")) {
            Log.e("WG", "不在添加联系人界面，结束加好友任务");
            WxTaskUtils.getWxTaskUtils().backHome();
            return;
        }

        boolean tag = true;
        xmlData = AdbUtils.getAdbUtils().dumpXml2String();
        String name = "";
        int zuiduo_num = 0;
        int meici_num = 0;
        xmlData = AdbUtils.getAdbUtils().dumpXml2String();
        int max = 0;
        int min = 0;
        String str_name = "";
        String meName = "";
        int x1 = 0;
        int y1 = 0;
        int x2 = 0;
        int y2 = 0;
        SPUtils.putString(MyApplication.getContext(), "MeiCiNum", "");
        while (tag) {
            xmlData = AdbUtils.getAdbUtils().dumpXml2String();
            List<String> meWxFriend = AdbUtils.getAdbUtils().getNodeList(xmlData);
//            List<String> meWxFriend = wxUtils.getNodeList(xmlData);
            if (xmlData.contains("添加")) {
                for (int i = 5; i < meWxFriend.size(); i++) {
                    NodeXmlBean.NodeBean nodeBean = AdbUtils.getAdbUtils().getNodeXmlBean(meWxFriend.get(i)).getNode();
                    if (nodeBean != null && nodeBean.getText() != null && "添加".equals(nodeBean.getText())) {
                        str_name = AdbUtils.getAdbUtils().getNodeXmlBean(meWxFriend.get(i - 3)).getNode().getText();
                        Log.e("WG", "addCommunication: " + "通讯录好友名称是" + str_name);
//                        LogUtils.d("通讯录好友名称是" + str_name);
                        if (nodeBean.getResourceid() != null && ("com.tencent.mm:id/b_k".equals(nodeBean.getResourceid()))) {
                            listXY = AdbUtils.getAdbUtils().getXY(nodeBean.getBounds());//获取添加坐标
                            x1 = listXY.get(0);
                            y1 = listXY.get(1);
                            x2 = listXY.get(2);
                            y2 = listXY.get(3);
                            //在添加之前，我们先点击通讯录的个人信息，统计微信好友信息
                            if (str_name != null && !meName.contains(str_name)) {//统计一次之后记录之前的名字，下次就不要统计了
                                meName = meName + str_name;
                                new StatisticsWxFriendsMessage(listXY, str_name, sex_key).sStatisticsWxFriends();
                                Thread.sleep(3000);
                            } else {
                                continue;
                            }
                            if (StringUtils.isEmpty(add_interval_time_s) && StringUtils.isEmpty(add_interval_time_e)) {
                            } else {
                                SPUtils.putString(MyApplication.getContext(), "add_interval_time_s", add_interval_time_s);
                                SPUtils.putString(MyApplication.getContext(), "add_interval_time_e", add_interval_time_e);
                            }
                            if (StringUtils.isEmpty(SPUtils.getString(MyApplication.getContext(), "add_" +
                                    "+interval_time_s", "")) && StringUtils.isEmpty(SPUtils.getString(MyApplication.getContext(), "add_interval_time_e", ""))) {
                                min = 20;
                                max = 30;
                            } else {
                                min = Integer.parseInt(SPUtils.getString(MyApplication.getContext(), "add_interval_time_s", "").trim());
                                max = Integer.parseInt(SPUtils.getString(MyApplication.getContext(), "add_interval_time_e", "").trim());
                            }

                            if (!name.contains(AdbUtils.getAdbUtils().getNodeXmlBean(meWxFriend.get(i - 2)).getNode().getText())) {
                                Random random = new Random();
                                int s = random.nextInt(max) % (max - min + 1) + min;
                                Log.e("WG", "通讯录添加好友的休眠时间为" + s + "秒");
                                Thread.sleep(s * 1000);
                                Log.e("WG", "点击添加好友的坐标位置 " + "x1:" + x1 + "y1: " + y1 + "x2:" + x2 + "y2:" + y2);
                                AdbUtils.getAdbUtils().click4xy(x1, y1, x2, y2);//点击添加
                                meici_num = meici_num + 1;
                                SPUtils.putString(MyApplication.getContext(), "MeiCiNum", "" + meici_num);
                                xmlData = AdbUtils.getAdbUtils().dumpXml2String();
                                if (!xmlData.contains("验证申请")) {
                                    continue;//证明无需验证，自动通过了
                                }
                                List<String> sendneirong = AdbUtils.getAdbUtils().getNodeList(xmlData);
                                Log.e("WG", "addCommunication: " + sendneirong.size());
                                for (int a = 0; a < sendneirong.size(); a++) {
                                    NodeXmlBean.NodeBean nodeBeans = AdbUtils.getAdbUtils().getNodeXmlBean(sendneirong.get(a)).getNode();
                                    if (nodeBeans != null && "com.tencent.mm:id/d0c".equals(nodeBeans.getResourceid())) {
                                        String neirong = "";
                                        if (StringUtils.isEmpty(contact_verify_msg)) {
                                            neirong = "你好";
                                        } else {
                                            neirong = contact_verify_msg.replaceAll("《name》", str_name);
                                        }
                                        AdbUtils.getAdbUtils().adbDimensClick(MyApplication.getContext(), R.dimen.x252, R.dimen.y80, R.dimen.x312, R.dimen.y107);
                                        int x = MyApplication.getContext().getResources().getDimensionPixelSize(R.dimen.x160);
                                        int y = MyApplication.getContext().getResources().getDimensionPixelSize(R.dimen.y93);//EdiText
                                        AdbUtils.getAdbUtils().adb("input swipe " + x + " " + y + " " + x + " " + y + " " + 2000);//长按EdiText
                                        String finalNeirong = neirong;
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                AdbUtils.getAdbUtils().adb("settings put secure default_input_method com.android.inputmethod.latin/.LatinIME");
                                                cm = (ClipboardManager) MyApplication.getContext().getSystemService(CLIPBOARD_SERVICE);
                                                cm.setText(finalNeirong);
                                            }
                                        });
                                        Log.e("WG", "addCommunication: 复制了");
                                        AdbUtils.getAdbUtils().adbDimensClick(MyApplication.getContext(), R.dimen.x24, R.dimen.y51, R.dimen.x96, R.dimen.y80);//点击复制 黏贴
                                        Log.e("WG", "addCommunication: 复制点击了");
                                        Thread.sleep(3000);
                                    }
                                }
                                AdbUtils.getAdbUtils().click(431, 72);
                                Thread.sleep(3000);
                                Boolean Flag = true;
                                while (Flag) {
                                    String xmlData2 = AdbUtils.getAdbUtils().dumpXml2String();
                                    if (xmlData2.contains("你需要发送验证申请")) {
                                        AdbUtils.getAdbUtils().adb("input keyevent 4");
                                        Thread.sleep(1000);
                                    } else {
                                        Flag = false;
                                    }
                                }
                                name = name + AdbUtils.getAdbUtils().getNodeXmlBean(meWxFriend.get(i - 2)).getNode().getText() + ",";

                                if (StringUtils.isEmpty(day_add_num)) {

                                } else {
                                    if (zuiduo_num == Integer.parseInt(day_add_num)) {
//                                        WxTaskUtils.getWxTaskUtils().backHome();
                                        AdbUtils.getAdbUtils().back();
                                        Log.e("WG", "第一个位置");
                                        break;
                                    }
                                }
                                if (StringUtils.isEmpty(one_add_num)) {

                                } else {
                                    if (meici_num == Integer.parseInt(one_add_num)) {
                                        SPUtils.put(MyApplication.getContext(), "meici_num", meici_num);
                                        Thread.sleep(3000);
                                        Log.e("WG", "任务完成");
//                                        WxTaskUtils.getWxTaskUtils().backHome();
                                        AdbUtils.getAdbUtils().back();
                                        Log.e("WG", "第二个位置");
                                        //CheckMessage();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                String TAG = xmlData;
                AdbUtils.getAdbUtils().adbUpSlide(MyApplication.getContext());
                xmlData = AdbUtils.getAdbUtils().dumpXml2String();
                if (TAG.equals(xmlData)) {
//                    WxTaskUtils.getWxTaskUtils().backHome();
                    AdbUtils.getAdbUtils().back();
                    Log.e("WG", " 第三个位置");
//                    LogUtils.d("第三个位置");
                    // CheckMessage();
                    break;
                }
            } else {
                tag = false;

            }
        }
        SPUtils.putString(MyApplication.getContext(), "sendAccountApplySuccess", "0");
//        ShowToast.show("准备上传数据 请稍后20s...", (Activity) MyApplication.getContext());
//        Toast.makeText(MyApplication.getContext(), "准备上传数据 请稍后20s...", Toast.LENGTH_LONG).show();
        Log.e("WG", "准备上传 ");
        String uid = SPUtils.getString(MyApplication.getContext(), "uid", "0000");
        String wxAccount = SPUtils.getString(MyApplication.getContext(), "wxAccount", ""); //目前的微信号
        String accountLocation = SPUtils.getString(MyApplication.getContext(), "WxAccountLocation", ""); //获取目前的位置
        List<WxPhoneNumeAskBean> mWxPhoneNumeAskBeanList = new ArrayList<>();
        String meiCiNum = SPUtils.getString(MyApplication.getContext(), "MeiCiNum", "");
        WxPhoneNumeAskBean wxPhoneNumeAskBean = new WxPhoneNumeAskBean(uid, wxAccount, accountLocation, meiCiNum + "");
        mWxPhoneNumeAskBeanList.add(wxPhoneNumeAskBean);
        String str = new Gson().toJson(mWxPhoneNumeAskBeanList);
//        LogUtils.d("JSON" + str.toString());
        Log.e("WG", "JSON " + str.toString());
        try {
            Response data = OkHttpUtils.post().url(URLS.wxAccountApply()).addParams("data", str.replace("\\", "")).build().execute();
            if (data.code() == 200) {
                String string = data.body().string();
                Log.e("WG", "上传成功 ");
                Log.d("zs1", string);
            } else {
                Log.e("WG", "上传失败 ");
                data = OkHttpUtils.post().url(URLS.wxAccountApply()).addParams("data", str.replace("\\", "")).build().execute();
                if (data.code() == 200) {
                    String string = data.body().string();
                    Log.d("zs2", string);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void toForUnRead() throws Exception {
        String xmlData;
        List<Integer> listXY;
        WxTaskUtils.getWxTaskUtils().backHome();
        AdbUtils.getAdbUtils().click4xy(42, 822, 78, 847);//点击微信通讯录
        String meName = "";
        int kkk = 0;
        SPUtils.putInt(MyApplication.getContext(), "HuaDongCiShu", 0);
        Boolean Flag = true;
        while (Flag) {
            kkk = 0;
            xmlData = AdbUtils.getAdbUtils().dumpXml2String();
            if (xmlData.contains("com.tencent.mm:id/j4")) {
                nodeList = AdbUtils.getAdbUtils().getNodeList(xmlData);
                kkk = 1;
                for (int b = 8; b < nodeList.size(); b++) {
                    nodeBean = AdbUtils.getAdbUtils().getNodeXmlBean(nodeList.get(b)).getNode();
                    NodeXmlBean.NodeBean nodeBean2 = AdbUtils.getAdbUtils().getNodeXmlBean(nodeList.get(b - 4)).getNode();
                    NodeXmlBean.NodeBean nodeBean3 = AdbUtils.getAdbUtils().getNodeXmlBean(nodeList.get(b - 8)).getNode();
                    if (nodeBean.getResourceid() != null && nodeBean.getResourceid().equals("com.tencent.mm:id/apt")
                            && nodeBean.getText() != null && nodeBean2.getText() != null && nodeBean3.getText() != null) {
//                        listXY = AdbUtils.getAdbUtils().getXY(nodeBean.getBounds());//
                        listXY = AdbUtils.getAdbUtils().getXY(nodeBean.getBounds());
                        AdbUtils.getAdbUtils().click4xy(listXY.get(0), listXY.get(1), listXY.get(2), listXY.get(3));
                        wxReply(nodeBean.getText(), nodeBean2.getText());
                    }
                }
            }
            if (kkk == 0) {
//                wxUtils.adbUpSlide(context);
                AdbUtils.getAdbUtils().adbUpSlide(MyApplication.getContext());
                int huaDongCishu = SPUtils.getInt(MyApplication.getContext(), "HuaDongCiShu", 0);
                if (huaDongCishu == 1) { //滑动过一次就结束
                    Flag = false;
                    continue;
                }
                huaDongCishu++;
                SPUtils.putInt(MyApplication.getContext(), "HuaDongCiShu", huaDongCishu);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                xmlData = AdbUtils.getAdbUtils().dumpXml2String();
                if (xmlData.contains("com.tencent.mm:id/j4")) {
                    nodeList = AdbUtils.getAdbUtils().getNodeList(xmlData);
                    kkk = 1;
                    for (int b = 8; b < nodeList.size(); b++) {
                        nodeBean = AdbUtils.getAdbUtils().getNodeXmlBean(nodeList.get(b)).getNode();
                        NodeXmlBean.NodeBean nodeBean2 = AdbUtils.getAdbUtils().getNodeXmlBean(nodeList.get(b - 4)).getNode();
                        NodeXmlBean.NodeBean nodeBean3 = AdbUtils.getAdbUtils().getNodeXmlBean(nodeList.get(b - 8)).getNode();
                        if (nodeBean.getResourceid() != null && nodeBean.getResourceid().equals("com.tencent.mm:id/apt")
                                && nodeBean.getText() != null && nodeBean2.getText() != null && nodeBean3.getText() != null) {
                            if (nodeBean.getText().contains("你是") || nodeBean.getText().contains("哪位") || nodeBean.getText().contains("你怎么知道") || nodeBean.getText().contains("哪个") || nodeBean.getText().contains("你叫什么")
                                    || nodeBean.getText().contains("你的名字") || nodeBean.getText().contains("谁") || nodeBean.getText().contains("电话") || nodeBean.getText().contains("手机号")
                                    || nodeBean.getText().contains("那个") || nodeBean.getText().contains("名字") || nodeBean.getText().contains("你好") || nodeBean.getText().contains("哪里人")
                                    || nodeBean.getText().contains("中午好") || nodeBean.getText().contains("晚上好") || nodeBean.getText().contains("认识")) {
                                listXY = AdbUtils.getAdbUtils().getXY(nodeBean.getBounds());//
                                AdbUtils.getAdbUtils().click4xy(listXY.get(0), listXY.get(1), listXY.get(2), listXY.get(3));
                                wxReply(nodeBean.getText(), nodeBean2.getText());
                                Thread.sleep(1000);
                                break;
                            } else {
                                listXY = AdbUtils.getAdbUtils().getXY(nodeBean.getBounds());
                                AdbUtils.getAdbUtils().click4xy(listXY.get(0), listXY.get(1), listXY.get(2), listXY.get(3));
                                Thread.sleep(2000);
//                                WxTaskUtils.getWxTaskUtils().backHome();
                                AdbUtils.getAdbUtils().back();
                                break;
                            }
                        }

                    }
                }
                if (kkk == 0) {
                    Flag = false;
                }
            }

        }
    }


    private void wxReply(String str, String str2) throws Exception {
        String xmlData;
        AdbUtils.getAdbUtils().adb("settings put secure default_input_method com.android.inputmethod.latin/.LatinIME");
        xmlData = AdbUtils.getAdbUtils().dumpXml2String();
        if (!xmlData.contains("com.tencent.mm:id/he")) {
            WxTaskUtils.getWxTaskUtils().backHome();
            return;
        }

        xmlData = AdbUtils.getAdbUtils().dumpXml2String();
        List<String> qunNameDataList = AdbUtils.getAdbUtils().getNodeList(xmlData);

        for (int c = 0; c < qunNameDataList.size(); c++) {
            NodeXmlBean.NodeBean qunNameBean = AdbUtils.getAdbUtils().getNodeXmlBean(qunNameDataList.get(c)).getNode();
            if (qunNameBean != null && qunNameBean.getResourceid() != null && "com.tencent.mm:id/hj".equals(qunNameBean.getResourceid()) && qunNameBean.getText() != null) {
                Log.e("WG", "程序名称 ：" + qunNameBean.getText());
                if (qunNameBean.getText().contains("群截图") || qunNameBean.getText().contains("微商引流会") || qunNameBean.getText().contains("京东内部福利群")) {
                    WxTaskUtils.getWxTaskUtils().backHome();
                    break;
                } else if (qunNameBean.getText().contains("腾讯新闻")) {
                    AdbUtils.getAdbUtils().click4xy(45, 401, 435, 482);//点击腾讯新闻 阅读
                    Log.e("WG", "阅读中 ");
                    Thread.sleep(40000);
                    WxTaskUtils.getWxTaskUtils().backHome();
                    return;
                } else {
                    AdbUtils.getAdbUtils().adb("settings put secure default_input_method com.sohu.inputmethod.sogou/.SogouIME");
                    String newStr = str;
                    if (str.contains("你好")) {
                        newStr = "你好";
                    } else if (str.contains("中午好")) {
                        newStr = "中午好";
                    } else if (str.contains("早上好")) {
                        newStr = "早上好";
                    } else if (str.contains("下午好")) {
                        newStr = "下午好";
                    } else if (str.contains("晚上好")) {
                        newStr = "晚上好";
                    } else if (str.contains("你是")) {
                        newStr = "你是";
                    } else if (str.contains("哪位")) {
                        newStr = "哪位";
                    } else if (str.contains("哪个")) {
                        newStr = "哪个";
                    } else if (str.contains("那个")) {
                        newStr = "那个";
                    } else if (str.contains("哪位")) {
                        newStr = "哪位";
                    } else if (str.contains("约吗")) {
                        newStr = "约吗";
                    } else if (str.contains("电话")) {
                        newStr = "手机号";
                    } else if (str.contains("手机号")) {
                        newStr = "手机号";
                    } else if (str.contains("认识")) {
                        newStr = "认识";
                    } else if (str.contains("哪里人")) {
                        newStr = "哪里人";
                    } else if (str.contains("好吗")) {
                        newStr = "好吗";
                    } else if (str.contains("名字")) {
                        newStr = "名字";
                    } else if (str.contains("谁")) {
                        newStr = "谁";
                    } else if (str.contains("你叫什么")) {
                        newStr = "名字";
                    } else {
                        newStr = "";
                    }
                    SPUtils.putString(MyApplication.getContext(), "WxReplyMessage", "");
                    final String newStr2 = newStr;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            sendReply(newStr2);
                        }
                    }).start();
                    Thread.sleep(8000);
                    String[] imageData1 = {"[微笑]", "[得意]", "[呲牙]", "[嘿哈]", "[捂脸]", "[机智]", "[奸笑]", "[耶]", "[爱情]"};
                    Random rand = new Random();
                    int x = MyApplication.getContext().getResources().getDimensionPixelSize(R.dimen.x136);
                    int y = MyApplication.getContext().getResources().getDimensionPixelSize(R.dimen.y383);//EdiText
                    int randNum1 = rand.nextInt(9);
                    xmlData = AdbUtils.getAdbUtils().dumpXml2String();
                    if (!xmlData.contains("切换到按住说话")) {
                        AdbUtils.getAdbUtils().adbDimensClick(MyApplication.getContext(), R.dimen.x4, R.dimen.y367, R.dimen.x52, R.dimen.y400);//切换到键盘
                    } else {
                        AdbUtils.getAdbUtils().click4xy(100, 820, 100, 820);//点击输入框
                    }
                    AdbUtils.getAdbUtils().adb("input swipe " + 100 + " " + 420 + " " + 100 + " " + 420 + " " + 3000);//长按5秒

                    handler.post(() -> {
                        ClipboardManager cm = (ClipboardManager) MyApplication.getContext().getSystemService(CLIPBOARD_SERVICE);
                        String replyMessageData = SPUtils.getString(MyApplication.getContext(), "WxReplyMessage", "");
                        cm.setText(imageData1[randNum1] + replyMessageData);
//
                    });
                    AdbUtils.getAdbUtils().click4xy(110, 385, 110, 385);//点击粘贴
                    AdbUtils.getAdbUtils().click4xy(405, 411, 471, 459);//点击发送
//                    WxTaskUtils.getWxTaskUtils().backHome();
                    AdbUtils.getAdbUtils().back();
                    AdbUtils.getAdbUtils().back();
                    break;
                }
            }
        }
    }


    public void sendReply(String newStr) {
        String Path = URLS.ziDongReply() + "?str=" + newStr;
        URL url = null;
        try {
            url = new URL(Path);
            // 2.建立一个http连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 3.设置一些请求方式
            conn.setRequestMethod("GET");// 注意GET单词字幕一定要大写
            conn.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36");
            int code = conn.getResponseCode(); // 服务器的响应码 200 OK //404 页面找不到
            // // 503服务器内部错误
            if (code == 200) {
                InputStream is = conn.getInputStream();
                // 把is的内容转换为字符串
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = -1;
                while ((len = is.read(buffer)) != -1) {
                    bos.write(buffer, 0, len);
                }
                String result = new String(bos.toByteArray());
                Log.d("WG", "返回的结果是" + result);
//                wxReplyMessageBean mWxReplyMessageBean = GsonUtil.parseJsonWithGson(result, wxReplyMessageBean.class);
                wxReplyMessageBean mWxReplyMessageBean = GsonUtils.fromJson(result, wxReplyMessageBean.class);
                SPUtils.putString(MyApplication.getContext(), "WxReplyMessage", mWxReplyMessageBean.getData());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void addFriendsReturn() throws Exception {
//        WxTaskUtils.getWxTaskUtils().openWx();
        WxTaskUtils.getWxTaskUtils().backHome();
        AdbUtils.getAdbUtils().adbDimensClick(MyApplication.getContext(), R.dimen.x80, R.dimen.y367, R.dimen.x160, R.dimen.y400);//点击通讯录
        AdbUtils.getAdbUtils().adbDimensClick(MyApplication.getContext(), R.dimen.x80, R.dimen.y367, R.dimen.x160, R.dimen.y400);//点击通讯录
        AdbUtils.getAdbUtils().adbDimensClick(MyApplication.getContext(), R.dimen.x14, R.dimen.y51, R.dimen.x296, R.dimen.y87);//新的朋友
        int count = 0;
        while (count < 3) {
            count++;
            xmlData = AdbUtils.getAdbUtils().dumpXml2String();
            if (!(xmlData.contains("添加朋友") && xmlData.contains("新的朋友"))) {
                Log.e("WG", "addFriendsReturn: 任务完成了");
                return;
            }
            nodeList = AdbUtils.getAdbUtils().getNodeList(xmlData);
            Log.e("WG", "addFriendsReturn: " + nodeList);
            for (int a = 0; a < nodeList.size(); a++) {
                if (a < 4) {
                    continue;
                }
                NodeXmlBean.NodeBean nodeBean = AdbUtils.getAdbUtils().getNodeXmlBean(nodeList.get(a)).getNode();
                NodeXmlBean.NodeBean nodeBean1 = AdbUtils.getAdbUtils().getNodeXmlBean(nodeList.get(a - 2)).getNode();
                if (nodeBean.getResourceid() != null && "com.tencent.mm:id/b8j".equals(nodeBean.getResourceid()) && nodeBean.getText() != null && nodeBean.getText().equals("添加")) {
                    if (!StringUtils.isEmpty(nodeBean1.getText()) && !nodeBean1.getText().contains("您是京东挑选的优质用户") && !nodeBean1.getText().contains("手机联系人")) {
                        listXY = AdbUtils.getAdbUtils().getXY(nodeBean1.getBounds());//
                        AdbUtils.getAdbUtils().click4xy(listXY.get(0), listXY.get(1), listXY.get(2), listXY.get(3));
                        //进入详细资料界面
                        xmlData = AdbUtils.getAdbUtils().dumpXml2String();
                        if (reply_msg == null) {
                            reply_msg = "你好";
                        }
                        if (!xmlData.contains("详细资料") && !xmlData.contains("设置备注和标签") || xmlData.contains(reply_msg) || xmlData.contains("滚") || xmlData.contains("病")
                                || xmlData.contains("傻逼") || xmlData.contains("妈") || xmlData.contains("死") || xmlData.contains("智障") || xmlData.contains("白痴")
                                ) {//判断下是否已经回复了，回复了就返回
                            AdbUtils.getAdbUtils().back();//返回
                            continue;
                        }
                        List<String> returnList = AdbUtils.getAdbUtils().getNodeList(xmlData);
                        String newFriends = "";
                        for (int ccc = 0; ccc < returnList.size(); ccc++) {
                            NodeXmlBean.NodeBean nodeBean_ccc = AdbUtils.getAdbUtils().getNodeXmlBean(returnList.get(ccc)).getNode();
                            if (nodeBean_ccc != null && nodeBean_ccc.getText() != null && nodeBean_ccc.getResourceid() != null && nodeBean_ccc.getResourceid().equals("com.tencent.mm:id/pl")) {
                                newFriends = nodeBean_ccc.getText();
                                Log.e("WG", "addFriendsReturn: " + newFriends);
                                break;
                            }
                        }
                        int kkk = 0;
                        for (int ddd = 0; ddd < returnList.size(); ddd++) {
                            NodeXmlBean.NodeBean nodeBean_ddd = AdbUtils.getAdbUtils().getNodeXmlBean(returnList.get(ddd)).getNode();
                            if (nodeBean_ddd != null && nodeBean_ddd.getText() != null && nodeBean_ddd.getText().contains("我:")) {
                                kkk++;
                                break;
                            }
                        }
                        if (kkk == 2 || kkk == 3) {
                            AdbUtils.getAdbUtils().back();
                            continue;
                        }
                        String newFriends2 = "";
                        for (int eee = returnList.size() - 5; eee > 0; eee--) {
                            NodeXmlBean.NodeBean nodeBean_eee = AdbUtils.getAdbUtils().getNodeXmlBean(returnList.get(eee)).getNode();
                            if (nodeBean_eee != null && nodeBean_eee.getText() != null && nodeBean_eee.getResourceid() != null
                                    && nodeBean_eee.getResourceid().equals("com.tencent.mm:id/b8z")) {
                                newFriends2 = nodeBean_eee.getText();
                                break;
                            }
                        }
                        if (newFriends2.contains("我")) {
                            AdbUtils.getAdbUtils().back();
                            continue;
                        }
                        for (int b = 0; b < returnList.size(); b++) {
                            NodeXmlBean.NodeBean nodeBean2 = AdbUtils.getAdbUtils().getNodeXmlBean(returnList.get(b)).getNode();
                            if (nodeBean2.getResourceid() != null && "com.tencent.mm:id/b90".equals(nodeBean2.getResourceid()) && nodeBean2.getText() != null && nodeBean2.getText().equals("回复")) {
                                listXY = AdbUtils.getAdbUtils().getXY(nodeBean2.getBounds());//
//                                wxUtils.adbClick(listXY.get(0), listXY.get(1), listXY.get(2), listXY.get(3));//
                                AdbUtils.getAdbUtils().click4xy(listXY.get(0), listXY.get(1), listXY.get(2), listXY.get(3));
                                //点击了回复
                                if (!TextUtils.isEmpty(reply_msg)) {//不为null，服务器给了 数据。需要设置，然后回复给刚刚加的好友

                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            AdbUtils.getAdbUtils().adb("settings put secure default_input_method com.android.inputmethod.latin/.LatinIME");
                                            ClipboardManager cmm2 = (ClipboardManager) MyApplication.getContext().getSystemService(CLIPBOARD_SERVICE);
                                            cmm2.setText(reply_msg);
                                        }
                                    });
                                    AdbUtils.getAdbUtils().adb("input swipe " + 150 + " " + 260 + " " + 200 + " " + 265 + " " + 2000);//长按
                                    Thread.sleep(1000);
                                    AdbUtils.getAdbUtils().click4xy(100, 215, 110, 220);
                                    //重新加载页面，计算出 确定 的位置
                                    xmlData = AdbUtils.getAdbUtils().dumpXml2String();
                                    List<String> nodeList3 = AdbUtils.getAdbUtils().getNodeList(xmlData);
                                    for (int c = 0; c < nodeList3.size(); c++) {
                                        NodeXmlBean.NodeBean nodeBean3 = AdbUtils.getAdbUtils().getNodeXmlBean(nodeList3.get(c)).getNode();
                                        if (nodeBean3.getResourceid() != null && "com.tencent.mm:id/all".equals(nodeBean3.getResourceid()) && nodeBean3.getText() != null && nodeBean3.getText().equals("确定")) {
                                            listXY = AdbUtils.getAdbUtils().getXY(nodeBean3.getBounds());
                                            AdbUtils.getAdbUtils().click4xy(listXY.get(0), listXY.get(1), listXY.get(2), listXY.get(3));
                                            break;
                                        }
                                    }
                                }
                                Thread.sleep(2000);
                                AdbUtils.getAdbUtils().back();
                                break;
                            }
                        }

                    }
                }
            }
            AdbUtils.getAdbUtils().adbUpSlide(MyApplication.getContext());//向上滑动
        }
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
        try {
            ViewCheckUtils.check();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    }


    /**
     * 清除所有联系人
     *
     * @param context
     */
    public void DeletPhone(Context context) {
        try {
            ViewCheckUtils.check();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        boolean flag = SPUtils.getBoolean(MyApplication.getContext(), "flag", true);
        Log.e("WG", "DeletPhone: " + cur);
        if (cur != null) {
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
            Log.e("WG", "清理完成 ");
        }
    }


}
