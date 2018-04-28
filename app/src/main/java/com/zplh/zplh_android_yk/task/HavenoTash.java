package com.zplh.zplh_android_yk.task;

import android.app.Activity;
import android.os.Environment;
import android.content.ClipboardManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Adapter;

import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zplh.zplh_android_yk.R;
import com.zplh.zplh_android_yk.base.MyApplication;
import com.zplh.zplh_android_yk.bean.NodeXmlBean;
import com.zplh.zplh_android_yk.bean.TaskMessageBean;
import com.zplh.zplh_android_yk.bean.WxFriendsMessageCultivate;
import com.zplh.zplh_android_yk.callback.TaskCallback;
import com.zplh.zplh_android_yk.constant.Priority;
import com.zplh.zplh_android_yk.constant.URLS;
import com.zplh.zplh_android_yk.utils.AdbUtils;
import com.zplh.zplh_android_yk.utils.FileUtils;
import com.zplh.zplh_android_yk.utils.SPUtils;
import com.zplh.zplh_android_yk.utils.ShowToast;
import com.zplh.zplh_android_yk.utils.StringUtils;
import com.zplh.zplh_android_yk.utils.WxIsInstallUtils;
import com.zplh.zplh_android_yk.utils.WxTaskUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Response;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by Administrator on 2018/4/26/026.
 */

public class HavenoTash extends BaseTask {

    private String xmlData;
    private NodeXmlBean.NodeBean nodeBean;
    private List<Integer> listXY;
    private List<String> sendFriendsMessageCultivateDatasList;
    private ClipboardManager cm;
    private Handler handler = new Handler();

    public HavenoTash(Priority priority, int sequence, TaskMessageBean.ContentBean.DataBean taskBean) {
        super(priority, sequence, taskBean);

    }

    @Override
    public void run(TaskCallback callback) throws Exception {
        String type = getTaskBean().getParam().getType();
        Log.e("WG", "养号互聊type " + type);
        WxIsInstallUtils.GetIsInstallWx().IsInstall(getTaskBean().getTask_id());
        WxTaskUtils.getWxTaskUtils().switchWxAccount1();
        WxIsInstallUtils.GetIsInstallWx().getIsAccountIsOk();
        WxTaskUtils.getWxTaskUtils().backHome();

        if (type.equals("3")) {
            sendFriendsMessageCultivate(type, new ArrayList<>());
        } else if (sendFriendsMessageCultivateDatasList != null) {
            sendFriendsMessageCultivateDatasList = null;
        }
        sendFriendsMessageCultivateDatas(type);
        Thread.sleep(1000);
        if (sendFriendsMessageCultivateDatasList != null && sendFriendsMessageCultivateDatasList.size() > 0) {
            sendFriendsMessageCultivate(type, sendFriendsMessageCultivateDatasList);
        }

    }

    @Override
    public void stop() {

    }


    /**
     * 养号互撩数据
     * 0图片   1文字
     */
    private void sendFriendsMessageCultivateDatas(final String type) {
        sendFriendsMessageCultivateDatasList = new ArrayList<>();
        OkHttpUtils.get().url(URLS.wechat_list()).addParams("type", type).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                Log.e("WG", "onResponse: " + response);
                WxFriendsMessageCultivate wxFriendsMessageCultivate1 = new Gson().fromJson(response, WxFriendsMessageCultivate.class);
                Log.e("WG", "onResponse: " + wxFriendsMessageCultivate1.getData());
                for (String s : wxFriendsMessageCultivate1.getData()) {
                    sendFriendsMessageCultivateDatasList.add(s);

                }
            }
        });

//        Log.e("WG", "sendFriendsMessageCultivateDatas: " + json);

    }

    /*
    *
    * 养号互聊操作
    * */

    public void sendFriendsMessageCultivate(String type, List<String> dataBeanList) throws InterruptedException {
        handler.post(new Runnable() {
            @Override
            public void run() {
                cm = (ClipboardManager) MyApplication.getContext().getSystemService(CLIPBOARD_SERVICE);

            }
        });

        int x = MyApplication.getContext().getResources().getDimensionPixelSize(R.dimen.x136);
        int y = MyApplication.getContext().getResources().getDimensionPixelSize(R.dimen.y383);//EdiText

        boolean bottom = false;//到了底部
        String endData = "";
        String meName = "";

        String path = "";
        String strMark = "";
        String fileName = "";
        String filePath = "";
        String text = "";
        String fileUrl = "";
        List<String> imgList = new ArrayList<>();
//        if (!(type.equals("0") || type.equals("1") || type.equals("3"))) {
//            return;
//        }
        if (type.equals("0") || type.equals("2")) {
            if (dataBeanList != null && dataBeanList.size() > 0) {//判断请求地址是否为空
                fileUrl = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/ykimages";
                for (int a = 0; a < dataBeanList.size(); a++) {
                    text = dataBeanList.get(a);
                    path = URLS.pic_vo + text.replace("\\", "/");
                    strMark = text.replace("\\", "/");
                    fileName = strMark.substring(strMark.lastIndexOf("/")).replace("/", "").replace(" ", "");
                    filePath = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/ykimages/" + fileName;

                    if (new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/ykimages", fileName).exists()) {//不存在，下载
//                        LogUtils.d("存在");
                    } else {
//                        LogUtils.d("不存在");
                        File f = null;
                        try {
                            f = FileUtils.getFileDown(path, fileName);
                        } catch (Exception e) {
//                            LogUtils.e("下载失败");
                            Log.e("WG", "下载失败 ");
                            e.printStackTrace();
                        }
                        if (f == null) {
//                            return;
                        } else {
                            imgList.add(fileName);//添加到集合
                        }
                    }

                    FileUtils.copy(fileUrl + "/" + fileName, fileUrl + "/aa" + fileName, false);//改名把文件添加到第一个

                    WxTaskUtils.getWxTaskUtils().addimages(new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/ykimages", "aa" + fileName), MyApplication.getContext());

                }


            } else {
//                LogUtils.d("朋友圈图文地址为空");
                Log.e("WG", "朋友圈图文地址为空");
                return;
            }
        }
        AdbUtils.getAdbUtils().click4xy(153, 822, 207, 847);//点击微信通讯录
        Thread.sleep(1000);
        AdbUtils.getAdbUtils().adbDimensClick(MyApplication.getContext(), R.dimen.x296, R.dimen.y346, R.dimen.x320, R.dimen.y346);//点击Z aaa
        xmlData = AdbUtils.getAdbUtils().dumpXml2String();//重新获取页面数据
//        int s = random.nextInt(max) % (max - min + 1) + min;
        Random random = new Random();
        int randomFriends = random.nextInt(3) % 3 + 1;//随机3-4条
//        LogUtils.e("好友人数:" + (randomFriends + 1));
        Log.e("WG", "好友人数 " + (randomFriends + 1));
//        ShowToast.show("好友人数:" + (randomFriends + 1), (Activity) context);
        int messageCount = 0;
        w:
        while (true) {
            if (messageCount > randomFriends) {
                break w;
            }
            List<String> nodeList = AdbUtils.getAdbUtils().getNodeList(xmlData);
            a:
            for (int a = 0; a < nodeList.size(); a++) {

                if (messageCount > randomFriends) {
                    break w;
                }
                nodeBean = AdbUtils.getAdbUtils().getNodeXmlBean(nodeList.get(a)).getNode();
                if (nodeBean.getResourceid() != null && (nodeBean.getResourceid().equals("com.tencent.mm:id/j_")) && nodeBean.getContentdesc() != null && nodeBean.getContentdesc() != "" && !nodeBean.getContentdesc().startsWith("微信") && (nodeBean.getContentdesc().startsWith("ZZZ9") || nodeBean.getContentdesc().startsWith("zzz9")) && !meName.contains(nodeBean.getContentdesc())) {
                    int randomTrue1 = random.nextInt(2);//随机0-1
                    int randomTrue2 = random.nextInt(2);//随机0-1
                    if (randomTrue1 == 0 && randomTrue2 == 0) {//任务随机执行
                        continue;
                    }

                    //筛选出好友
                    listXY = AdbUtils.getAdbUtils().getXY(nodeBean.getBounds());//获取好友坐标
                    AdbUtils.getAdbUtils().click4xy(listXY.get(0), listXY.get(1), listXY.get(2), listXY.get(3));//点击好友
//                    LogUtils.d("点击进入");
                    Log.e("WG", "点击进入");
                    xmlData = AdbUtils.getAdbUtils().dumpXml2String();//重新获取页面数据
                    if (!xmlData.contains("标签")) {
//                        wxUtils.adb("input keyevent 4");
                        AdbUtils.getAdbUtils().back();
                        meName = meName + nodeBean.getContentdesc();
                        continue;
                    }
                    XiuGaiBeiZhu("ZZZ9");
//                    xmlData = wxUtils.getXmlData();//重新获取页面数据
                    List<String> messageList = AdbUtils.getAdbUtils().getNodeList(xmlData);
                    for (int b = 0; b < messageList.size(); b++) {
                        NodeXmlBean.NodeBean nodeBean = AdbUtils.getAdbUtils().getNodeXmlBean(messageList.get(b)).getNode();
                        if (nodeBean != null && "com.tencent.mm:id/ana".equals(nodeBean.getResourceid())) {
                            listXY = AdbUtils.getAdbUtils().getXY(nodeBean.getBounds());//
                            AdbUtils.getAdbUtils().click4xy(listXY.get(0), listXY.get(1), listXY.get(2), listXY.get(3));//点击发消息
                            int wCount = 0;

                            int randomNum = random.nextInt(3);
//                            LogUtils.e(randomNum + 1 + "条消息");
                            Log.e("WG", randomNum + 1 + "条消息");
//                            ShowToast.show(randomNum + 1 + "条消息", (Activity) context);

                            xmlData = AdbUtils.getAdbUtils().dumpXml2String();

                            if (!xmlData.contains("当前所在页面,与")) {
                                return;
                            }


                            List<String> copyList = AdbUtils.getAdbUtils().getNodeList(xmlData);
                            for (int c = 0; c < copyList.size(); c++) {
                                NodeXmlBean.NodeBean copyBean = AdbUtils.getAdbUtils().getNodeXmlBean(copyList.get(c)).getNode();
                                if (copyBean != null && copyBean.getResourceid() != null && "com.tencent.mm:id/aab".equals(copyBean.getResourceid())) {
                                    if (!StringUtils.isEmpty(copyBean.getText())) {
                                        int xx = MyApplication.getContext().getResources().getDimensionPixelSize(R.dimen.x296);
                                        int yy = MyApplication.getContext().getResources().getDimensionPixelSize(R.dimen.y343);//删除
                                        AdbUtils.getAdbUtils().adb("input swipe " + xx + " " + yy + " " + xx + " " + yy + " " + 7000);  //删除
                                        AdbUtils.getAdbUtils().adb("input keyevent 4");
                                        break;
                                    }
                                }
                            }

                            switch (type) {

                                case "1"://文字
                                    xmlData = AdbUtils.getAdbUtils().dumpXml2String();
                                    if (xmlData.contains("切换到键盘")) {
                                        AdbUtils.getAdbUtils().adbDimensClick(MyApplication.getContext(), R.dimen.x4, R.dimen.y367, R.dimen.x52, R.dimen.y400);//切换到键盘
                                        AdbUtils.getAdbUtils().back();
                                    }

                                    switch (randomNum) {
                                        case 0:
                                            if (dataBeanList.size() >= 1) {
                                                // 将文本内容放到系统剪贴板里。
                                                cm.setText(WxTaskUtils.getWxTaskUtils().getFaceText(dataBeanList.get(0)));
                                                AdbUtils.getAdbUtils().adb("input swipe " + x + " " + y + " " + x + " " + y + " " + 1000);  //长按EdiText
                                                AdbUtils.getAdbUtils().adbDimensClick(MyApplication.getContext(), R.dimen.x52, R.dimen.y345, R.dimen.x52, R.dimen.y345);//点击粘贴
                                                AdbUtils.getAdbUtils().adbDimensClick(MyApplication.getContext(), R.dimen.x270, R.dimen.y372, R.dimen.x314, R.dimen.y395);//点击发送
                                            }
                                            break;
                                        case 1:
                                            if (dataBeanList.size() >= 2) {
                                                // 将文本内容放到系统剪贴板里。
                                                cm.setText(WxTaskUtils.getWxTaskUtils().getFaceText(dataBeanList.get(0)));
                                                AdbUtils.getAdbUtils().adb("input swipe " + x + " " + y + " " + x + " " + y + " " + 1000);  //长按EdiText
                                                AdbUtils.getAdbUtils().adbDimensClick(MyApplication.getContext(), R.dimen.x52, R.dimen.y345, R.dimen.x52, R.dimen.y345);//点击粘贴
                                                AdbUtils.getAdbUtils().adbDimensClick(MyApplication.getContext(), R.dimen.x270, R.dimen.y372, R.dimen.x314, R.dimen.y395);//点击发送

                                                int ran = random.nextInt(5) + 3;
//                                                ShowToast.show("间隔" + ran + "秒", (Activity) context);
                                                try {
                                                    Thread.sleep(ran * 1000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }

                                                // 将文本内容放到系统剪贴板里。
                                                cm.setText(WxTaskUtils.getWxTaskUtils().getFaceText(dataBeanList.get(1)));
                                                AdbUtils.getAdbUtils().adb("input swipe " + x + " " + y + " " + x + " " + y + " " + 1000);  //长按EdiText
                                                AdbUtils.getAdbUtils().adbDimensClick(MyApplication.getContext(), R.dimen.x52, R.dimen.y345, R.dimen.x52, R.dimen.y345);//点击粘贴
                                                AdbUtils.getAdbUtils().adbDimensClick(MyApplication.getContext(), R.dimen.x270, R.dimen.y372, R.dimen.x314, R.dimen.y395);//点击发送
                                            }
                                            break;
                                        case 2:
                                            if (dataBeanList.size() >= 3) {
                                                // 将文本内容放到系统剪贴板里。
                                                cm.setText(WxTaskUtils.getWxTaskUtils().getFaceText(dataBeanList.get(0)));
                                                AdbUtils.getAdbUtils().adb("input swipe " + x + " " + y + " " + x + " " + y + " " + 1000);  //长按EdiText
                                                AdbUtils.getAdbUtils().adbDimensClick(MyApplication.getContext(), R.dimen.x52, R.dimen.y345, R.dimen.x52, R.dimen.y345);//点击粘贴
                                                AdbUtils.getAdbUtils().adbDimensClick(MyApplication.getContext(), R.dimen.x270, R.dimen.y372, R.dimen.x314, R.dimen.y395);//点击发送

                                                int ran = random.nextInt(5) + 3;
//                                                ShowToast.show("间隔" + ran + "秒", (Activity) context);
                                                try {
                                                    Thread.sleep(ran * 1000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }

                                                // 将文本内容放到系统剪贴板里。
                                                cm.setText(WxTaskUtils.getWxTaskUtils().getFaceText(dataBeanList.get(1)));
                                                AdbUtils.getAdbUtils().adb("input swipe " + x + " " + y + " " + x + " " + y + " " + 1000);  //长按EdiText
                                                AdbUtils.getAdbUtils().adbDimensClick(MyApplication.getContext(), R.dimen.x52, R.dimen.y345, R.dimen.x52, R.dimen.y345);//点击粘贴
                                                AdbUtils.getAdbUtils().adbDimensClick(MyApplication.getContext(), R.dimen.x270, R.dimen.y372, R.dimen.x314, R.dimen.y395);//点击发送

                                                int ran1 = random.nextInt(5) + 3;
//                                                ShowToast.show("间隔" + ran1 + "秒", (Activity) context);
                                                try {
                                                    Thread.sleep(ran1 * 1000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }

                                                // 将文本内容放到系统剪贴板里。
                                                cm.setText(WxTaskUtils.getWxTaskUtils().getFaceText(dataBeanList.get(2)));
                                                AdbUtils.getAdbUtils().adb("input swipe " + x + " " + y + " " + x + " " + y + " " + 1000);  //长按EdiText
                                                AdbUtils.getAdbUtils().adbDimensClick(MyApplication.getContext(), R.dimen.x52, R.dimen.y345, R.dimen.x52, R.dimen.y345);//点击粘贴
                                                AdbUtils.getAdbUtils().adbDimensClick(MyApplication.getContext(), R.dimen.x270, R.dimen.y372, R.dimen.x314, R.dimen.y395);//点击发送
                                            }
                                            break;
                                    }

                                    break;
                                case "0"://发图片
                                    AdbUtils.getAdbUtils().adbDimensClick(MyApplication.getContext(), R.dimen.x268, R.dimen.y367, R.dimen.x316, R.dimen.y400);//更多功能
                                    AdbUtils.getAdbUtils().adbDimensClick(MyApplication.getContext(), R.dimen.x16, R.dimen.y235, R.dimen.x88, R.dimen.y298);//相册

                                    a = 0;
                                    while (a < 5) {
                                        a++;
                                        xmlData = AdbUtils.getAdbUtils().dumpXml2String();
                                        if (!xmlData.contains("图片和视频")) {
                                            AdbUtils.getAdbUtils().adbDimensClick(MyApplication.getContext(), R.dimen.x16, R.dimen.y235, R.dimen.x88, R.dimen.y298);//相册
                                        } else {
                                            break;
                                        }
                                    }

                                    if (xmlData.contains("图片和视频")) {
                                        AdbUtils.getAdbUtils().click4xy(24, 801, 144, 834);
                                        boolean ccc = true;
                                        while (ccc) {
                                            String xmlData_picture = AdbUtils.getAdbUtils().dumpXml2String();
                                            List<String> pictureList = AdbUtils.getAdbUtils().getNodeList(xmlData_picture);
                                            for (int c = 0; c < pictureList.size(); c++) {
                                                NodeXmlBean.NodeBean pictureBean = AdbUtils.getAdbUtils().getNodeXmlBean(pictureList.get(c)).getNode();
                                                if (pictureBean != null && pictureBean.getResourceid() != null && "com.tencent.mm:id/d1r".equals(pictureBean.getResourceid())
                                                        && pictureBean.getText() != null && pictureBean.getText().equals("ykimages")) {
                                                    listXY = AdbUtils.getAdbUtils().getXY(pictureBean.getBounds());//获取坐标
                                                    AdbUtils.getAdbUtils().click4xy(listXY.get(0), listXY.get(1), listXY.get(2), listXY.get(3));//点击ykimages 文件夹
                                                    ccc = false;
                                                    break;
                                                }
                                            }
                                            if (ccc == true) {
                                                AdbUtils.getAdbUtils().adbUpSlide(MyApplication.getContext());//向上滑动
                                            }
                                        }
                                        switch (randomNum) {
                                            case 0:
                                                AdbUtils.getAdbUtils().click4xy(78, 119, 108, 149);//选择图片
                                                break;
                                            case 1:

                                                AdbUtils.getAdbUtils().click4xy(78, 119, 108, 149);//选择图片
                                                AdbUtils.getAdbUtils().click4xy(198, 119, 228, 149);//选择图片
                                                break;
                                            case 2:
                                                AdbUtils.getAdbUtils().click4xy(78, 119, 108, 149);//选择图片
                                                AdbUtils.getAdbUtils().click4xy(198, 119, 228, 149);//选择图片
                                                AdbUtils.getAdbUtils().click4xy(318, 119, 348, 149);//选择图片
                                                break;
                                        }

                                        AdbUtils.getAdbUtils().adbDimensClick(MyApplication.getContext(), R.dimen.x252, R.dimen.y23, R.dimen.x312, R.dimen.y44);//确定
                                        try {
                                            Thread.sleep(500);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                    }

                                    break;
                                case "3"://发送语音
                                    xmlData = AdbUtils.getAdbUtils().dumpXml2String();
                                    if (xmlData.contains("切换到按住说话")) {
                                        AdbUtils.getAdbUtils().adbDimensClick(MyApplication.getContext(), R.dimen.x4, R.dimen.y367, R.dimen.x52, R.dimen.y400);//切换到键盘
                                    }
                                    //录制时间
                                    int start;
                                    if (StringUtils.isEmpty(getTaskBean().getParam().getRandom_time_s())) {
                                        start = 2;
                                    } else {
                                        start = Integer.valueOf(getTaskBean().getParam().getRandom_time_s());
                                    }
                                    int end;
                                    if (StringUtils.isEmpty(getTaskBean().getParam().getRandom_time_e())) {
                                        end = 59;
                                    } else {
                                        end = Integer.valueOf(getTaskBean().getParam().getRandom_time_e());
//                                        end = Integer.valueOf(app.getWxGeneralSettingsBean().getRecord_time_e());
                                    }
                                    int timeSleep = random.nextInt(end - start + 1) + start;
                                    int timeSleep1 = random.nextInt(end - start + 1) + start;
                                    int timeSleep2 = random.nextInt(end - start + 1) + start;
                                    switch (randomNum) {
                                        case 0:


//                                            LogUtils.e("end=" + end + "__start=" + start + "___语音时间=" + timeSleep);
//                                            ShowToast.show("语音录音时间：" + timeSleep + "秒", (Activity) context);

                                            AdbUtils.getAdbUtils().adb("input swipe " + x + " " + y + " " + x + " " + y + " " + timeSleep * 1000);  //长按EdiText
                                            break;
                                        case 1:
//                                            LogUtils.e("end=" + end + "__start=" + start + "___语音时间=" + timeSleep);
//                                            ShowToast.show("语音录音时间：" + timeSleep + "秒", (Activity) context);

                                            AdbUtils.getAdbUtils().adb("input swipe " + x + " " + y + " " + x + " " + y + " " + timeSleep * 1000);  //长按EdiText
                                            int ran = random.nextInt(3) + 3;
//                                            ShowToast.show("间隔" + ran + "秒", (Activity) context);
                                            try {
                                                Thread.sleep(ran * 1000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }

//                                            LogUtils.e("end=" + end + "__start=" + start + "___语音时间=" + timeSleep1);
//                                            ShowToast.show("语音录音时间：" + timeSleep1 + "秒", (Activity) context);

                                            AdbUtils.getAdbUtils().adb("input swipe " + x + " " + y + " " + x + " " + y + " " + timeSleep1 * 1000);  //长按EdiText
                                            break;
                                        case 2:

//                                            LogUtils.e("end=" + end + "__start=" + start + "___语音时间=" + timeSleep);
//                                            ShowToast.show("语音录音时间：" + timeSleep + "秒", (Activity) context);

                                            AdbUtils.getAdbUtils().adb("input swipe " + x + " " + y + " " + x + " " + y + " " + timeSleep * 1000);  //长按EdiText
                                            int ran1 = random.nextInt(3) + 3;
//                                            ShowToast.show("间隔" + ran1 + "秒", (Activity) context);
                                            try {
                                                Thread.sleep(ran1 * 1000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }

//                                            LogUtils.e("end=" + end + "__start=" + start + "___语音时间=" + timeSleep1);
//                                            ShowToast.show("语音录音时间：" + timeSleep1 + "秒", (Activity) context);

                                            AdbUtils.getAdbUtils().adb("input swipe " + x + " " + y + " " + x + " " + y + " " + timeSleep1 * 1000);  //长按EdiText
                                            int ran2 = random.nextInt(3) + 3;
//                                            ShowToast.show("间隔" + ran2 + "秒", (Activity) context);
                                            try {
                                                Thread.sleep(ran2 * 1000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }

//                                            LogUtils.e("end=" + end + "__start=" + start + "___语音时间=" + timeSleep2);
//                                            ShowToast.show("语音录音时间：" + timeSleep2 + "秒", (Activity) context);

                                            AdbUtils.getAdbUtils().adb("input swipe " + x + " " + y + " " + x + " " + y + " " + timeSleep2 * 1000);  //长按EdiText
                                            break;
                                    }

                                    break;
                            }

                            break;
                        }
                    }
                    messageCount++;
                    meName = meName + nodeBean.getContentdesc();
                    AdbUtils.getAdbUtils().adb("input keyevent 4");
                    AdbUtils.getAdbUtils().adbDimensClick(MyApplication.getContext(), R.dimen.x80, R.dimen.y367, R.dimen.x160, R.dimen.y400);//点击通讯录

//                    设置间隔时间
                    int start;
                    if (StringUtils.isEmpty(getTaskBean().getParam().getMsg_interval_time_s())) {
                        start = 10;
                    } else {
                        start = Integer.valueOf(getTaskBean().getParam().getMsg_interval_time_e());
//                        app.getWxGeneralSettingsBean().getMsg_interval_time_s
                    }
                    int end;
                    if (StringUtils.isEmpty(getTaskBean().getParam().getRandom_time_e())) {
                        end = 20;
                    } else {
                        end = Integer.valueOf(getTaskBean().getParam().getMsg_interval_time_e());
                    }
                    int timeSleep = random.nextInt(5) + 1;
//                    LogUtils.e("end=" + end + "__start=" + start + "___间隔随机数=" + timeSleep);
//                    ShowToast.show("间隔时间：" + timeSleep + "秒", (Activity) context);
                    try {
                        Thread.sleep(timeSleep * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }

            xmlData = AdbUtils.getAdbUtils().dumpXml2String();//修改完一个重新获取页面数据
            if (!xmlData.contains("发现") && !xmlData.contains("com.tencent.mm:id/i")) {
//                ShowToast.show("任务被中断，发消息任务", (Activity) context);
                break w;
            }


            if (!bottom) {
//                LogUtils.d("向上滑动了");
                AdbUtils.getAdbUtils().adbUpSlide(MyApplication.getContext());//向上滑动
            }
            endData = xmlData;
            xmlData = AdbUtils.getAdbUtils().dumpXml2String();//滑动后重新获取页面数据
            if (endData.equals(xmlData)) {
//                ShowToast.show("发消息完成", (Activity) context);
                break w;
            }
            if (xmlData.contains("位联系人")) {//判断是否到达底部
                bottom = true;
            }
        }
//        ShowToast.show("发消息完成", (Activity) context);
        if (type.equals("0") || type.equals("2")) {
//            wxUtils.addimages(new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/ykimages", fileName), context);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (int a = 0; a < imgList.size(); a++) {
                FileUtils.delete(fileUrl + "/aa" + imgList.get(a));//删除复制的文件
            }

        }
    }


    /**
     * 养号专用
     * 修改备注
     */
    private void XiuGaiBeiZhu(String str) {
//        ShowToast.show("修改备注开始", (Activity) context);
        int sex = 0;//0代表女。   1代表男   2代表性别未知
        DecimalFormat df = new DecimalFormat("0000");
        xmlData = AdbUtils.getAdbUtils().dumpXml2String();//重新获取页面数据
//        if (xmlData.contains("女")) {
//            sex = 0;
//        } else if (xmlData.contains("男")) {
//            sex = 1;
//        } else {
//            sex = 2;
//        }
        List<String> remarkList = AdbUtils.getAdbUtils().getNodeList(xmlData);
        for (int r = 0; r < remarkList.size(); r++) {
            nodeBean = AdbUtils.getAdbUtils().getNodeXmlBean(remarkList.get(r)).getNode();
            if (nodeBean.getResourceid() != null && (nodeBean.getResourceid().equals("com.tencent.mm:id/anw"))) {
                //筛选出好友
                listXY = AdbUtils.getAdbUtils().getXY(nodeBean.getBounds());//获取修改备注标签
                AdbUtils.getAdbUtils().click4xy(listXY.get(0), listXY.get(1), listXY.get(2), listXY.get(3));//点击修改备注
                break;
            }
        }
        AdbUtils.getAdbUtils().adbDimensClick(MyApplication.getContext(), R.dimen.x16, R.dimen.y89, R.dimen.x304, R.dimen.y115);//点击名字EditText
        AdbUtils.getAdbUtils().adbDimensClick(MyApplication.getContext(), R.dimen.x252, R.dimen.y89, R.dimen.x312, R.dimen.y115);//清空名字

        int wx_name_number_girl = (int) SPUtils.get(MyApplication.getContext(), "wx_name_number_girl_D", 0);
        String wx_nume_number_new_girl = df.format(wx_name_number_girl + 1);
        AdbUtils.getAdbUtils().adb("input text " + str + "D" + wx_nume_number_new_girl);
        SPUtils.put(MyApplication.getContext(), "wx_name_number_girl_D", wx_name_number_girl + 1);
        AdbUtils.getAdbUtils().adbDimensClick(MyApplication.getContext(), R.dimen.x252, R.dimen.y23, R.dimen.x312, R.dimen.y44);//确定修改
        //  LogUtils.d(nodeList.get(a));
        try {
            Thread.sleep(3 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}
