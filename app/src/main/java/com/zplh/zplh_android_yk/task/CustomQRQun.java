package com.zplh.zplh_android_yk.task;

import android.text.TextUtils;

import com.zplh.zplh_android_yk.base.MyApplication;
import com.zplh.zplh_android_yk.bean.NodeXmlBean;
import com.zplh.zplh_android_yk.bean.TaskMessageBean;
import com.zplh.zplh_android_yk.callback.TaskCallback;
import com.zplh.zplh_android_yk.constant.Priority;
import com.zplh.zplh_android_yk.utils.AdbUtils;
import com.zplh.zplh_android_yk.utils.WxTaskUtils;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

/**
 * 自定义二维码拉群任务
 * Created by yong hao zeng on 2018/4/23/023.
 */
public class CustomQRQun extends BaseTask {
    CustomQRQun(Priority priority, int sequence, TaskMessageBean.ContentBean.DataBean taskBean) {
        super(priority, sequence, taskBean);
    }

    @Override
    public void run(TaskCallback callback) throws Exception {
        String fanId       =         getTaskBean().getParam().getFanId();
        String fanNum_s    =         getTaskBean().getParam().getFanNum_s();
        String  fanNum_e   =         getTaskBean().getParam().getFanNum_e();
        String fanNum_default  =    getTaskBean().getParam().getFanNum_default();   // 0  为全部
        String personNum   =         getTaskBean().getParam().getPersonNum(); // 1为小于40人，2为大于40人

        String xmlData = "";
        List<String> nodeList ;
        AdbUtils adbUtils = AdbUtils.getAdbUtils();
        WxTaskUtils wxTaskUtils = WxTaskUtils.getWxTaskUtils();
        List<Integer> listXY ;

        adbUtils.adb("settings put secure default_input_method com.android.inputmethod.latin/.LatinIME");
        wxTaskUtils.openWxIsHome();
        Thread.sleep(1000);
        wxTaskUtils.switchWxAccount1();
        Thread.sleep(1000);
        if (!wxTaskUtils.getIsAccountIsOk()) {
            throw new Exception("账号被封");
        }
        wxTaskUtils.backHome();
        String wxQunFriendsName = "";
        String meName = "";
        DecimalFormat df = new DecimalFormat("0000");
        int sex = 0; //0 代表女。   1代表男   2代表性别未知

        if(fanId.equals("y")){
            fanId = "ZZZy";
        }else if ( fanId.equals("w")){
            fanId = "YYY1";
        }else {
            fanId = "ZZZ"+fanId;
        }

//        boolean flag_gaibeizhu = true;
//        while (flag_gaibeizhu) {
//            xmlData = adbUtils.dumpXml2String();//修改完一个重新获取页面数据
//            if (xmlData.contains("wx助手") || (xmlData.contains("应用") && xmlData.contains("主屏幕"))) {
//                flag_gaibeizhu = false;
//            } else if (!(xmlData.contains("通讯录") && xmlData.contains("发现"))) {
//                adbUtils.back();//返回
//            } else if ((xmlData.contains("通讯录") && xmlData.contains("发现"))) {
//                adbUtils.click4xy(200, 800, 200, 800);//点击通讯录
//                flag_gaibeizhu = false;
//            }
//        }

        // 进入搜索界面
        wxTaskUtils.goSearch();

        callback.onTaskProgress(this,"进入搜索界面");

        Thread.sleep(1000);
        adbUtils.click(270,73);
        Thread.sleep(500);
        adbUtils.putText(fanId);
        Thread.sleep(500);
        adbUtils.click(324,154);//落下键盘
        Thread.sleep(500);
        xmlData = adbUtils.dumpXml2String();


        if (!xmlData.contains(fanId)) {
            throw new Exception("没有查询到老粉丝账号");
        }

        callback.onTaskProgress(this,"进行了搜索");

        //查询更多联系人
        if (xmlData.contains("更多联系人")) {
            nodeList = adbUtils.getNodeList(xmlData);
            for (int a = 0; a < nodeList.size(); a++) {
                NodeXmlBean.NodeBean nodeBean = adbUtils.getNodeXmlBean(nodeList.get(a)).getNode();
                if (nodeBean!=null && nodeBean.getResourceid() != null && nodeBean.getResourceid().equals("com.tencent.mm:id/in")
                        && nodeBean.getText() != null && nodeBean.getText().equals("更多联系人")
                        ) {
                    listXY = adbUtils.getXY(nodeBean.getBounds());//获取 发消息 的坐标
                    adbUtils.click4xy(listXY.get(0), listXY.get(1), listXY.get(2), listXY.get(3));//点击  更多联系人
                    break;
                }
            }
        }else {
            adbUtils.adbUpSlide(MyApplication.getContext());
            xmlData = adbUtils.dumpXml2String();
            if (xmlData.contains("更多联系人")) {
                nodeList = adbUtils.getNodeList(xmlData);
                for (int a = 0; a < nodeList.size(); a++) {
                    NodeXmlBean.NodeBean nodeBean = adbUtils.getNodeXmlBean(nodeList.get(a)).getNode();
                    if (nodeBean!=null && nodeBean.getResourceid() != null && nodeBean.getResourceid().equals("com.tencent.mm:id/in")
                            && nodeBean.getText() != null && nodeBean.getText().equals("更多联系人")
                            ) {
                        listXY = adbUtils.getXY(nodeBean.getBounds());//获取 发消息 的坐标
                        adbUtils.click4xy(listXY.get(0), listXY.get(1), listXY.get(2), listXY.get(3));//点击  更多联系人
                        callback.onTaskProgress(this,"点击了更多联系人");
                        break;
                    }
                }
            } else {
                adbUtils.adb("input swipe 200 300 200 1000 50");  //滑动到顶部
            }
        }

        Boolean Flag_GaiBeiZhu = true;
        int kkk_GaiBeiZhu = 0;
        int xiuGaiNum = 0;


        if (TextUtils.equals(fanNum_default,"0")) {
            xiuGaiNum = 1000;

        } else {
            xiuGaiNum = new Random().nextInt(Integer.valueOf(fanNum_e) - Integer.valueOf(fanNum_s) + 1) + Integer.valueOf(fanNum_s);

        }
        callback.onTaskProgress(this,"要修改的人数"+xiuGaiNum+"");

        //开始改备注
        while (Flag_GaiBeiZhu) {
            xmlData = adbUtils.dumpXml2String();
            nodeList = adbUtils.getNodeList(xmlData);
            int bb = 0;
            if (xmlData.contains(fanId)) {

                 /** 在没有更多联系人的情况下 点击最上面的联系人列表
                     */
                for (int a = 0; a < nodeList.size(); a++) {
                    NodeXmlBean.NodeBean nodeBean = adbUtils.getNodeXmlBean(nodeList.get(a)).getNode();
                    if (nodeBean != null && nodeBean.getResourceid() != null && nodeBean.getResourceid().equals("com.tencent.mm:id/kq")
                            && nodeBean.getText() != null && nodeBean.getText().startsWith( fanId ) && !meName.contains(nodeBean.getText())
                            ) {

                        listXY = adbUtils.getXY(nodeBean.getBounds());//获取  的坐标
                        adbUtils.click4xy(listXY.get(0), listXY.get(1), listXY.get(2), listXY.get(3));//点击
                        bb = 1;
                        meName = meName + nodeBean.getText();
                            Thread.sleep(2000);
                        adbUtils.click4xy(396, 36, 480, 108);//点击右上角头像
                            Thread.sleep(1000);
                        adbUtils.click4xy(21, 168, 105, 286); //点击左上角的人头像
                        xmlData = adbUtils.dumpXml2String();//重新获取页面数据
                        List<String> remarkList = adbUtils.getNodeList(xmlData);
                        if (xmlData.contains("女")) {
                            sex = 0;
                        } else if (xmlData.contains("男")) {
                            sex = 1;
                        } else {
                            sex = 2;
                        }
                        for (int r = 0; r < remarkList.size(); r++) {
                            nodeBean = adbUtils.getNodeXmlBean(remarkList.get(r)).getNode();
                            if (nodeBean!=null && nodeBean.getResourceid() != null && (nodeBean.getResourceid().equals("com.tencent.mm:id/anw"))) {
                                //筛选出好友
                                listXY = adbUtils.getXY(nodeBean.getBounds());//获取修改备注标签
                                adbUtils.click4xy(listXY.get(0), listXY.get(1), listXY.get(2), listXY.get(3));//点击修改备注
                                break;
                            }
                        }
//                        wxUtils.adbDimensClick(context, R.dimen.x16, R.dimen.y89, R.dimen.x304, R.dimen.y115);//点击名字EditText
//                        wxUtils.adbDimensClick(context, R.dimen.x252, R.dimen.y89, R.dimen.x312, R.dimen.y115);//清空名字
//                        switch (sex) {//0代表女。   1代表男   2代表性别未知
//                            case 0:
//                                int wx_name_number_girl = (int) SPUtils.get(context, "wx_name_number_girl", 0);
//                                String wx_nume_number_new_girl = df.format(wx_name_number_girl + 1);
//                                wxUtils.adb("input text " + "AA"+fanId + "B" + wx_nume_number_new_girl);
//                                SPUtils.put(context, "wx_name_number_girl", wx_name_number_girl + 1);
//                                break;
//                            case 1:
//                                int wx_name_number_boy = (int) SPUtils.get(context, "wx_name_number_boy", 0);
//                                String wx_nume_number_new_boy = df.format(wx_name_number_boy + 1);
//                                wxUtils.adb("input text " + "AA"+fanId + "A" + wx_nume_number_new_boy);
//                                SPUtils.put(context, "wx_name_number_boy", wx_name_number_boy + 1);
//                                break;
//                            case 2:
//                                int wx_name_number_c = (int) SPUtils.get(context, "wx_name_number_c", 0);
//                                String wx_nume_number_c = df.format(wx_name_number_c + 1);
//                                wxUtils.adb("input text " + "AA"+fanId + "C" + wx_nume_number_c);
//                                SPUtils.put(context, "wx_name_number_c", wx_name_number_c + 1);
//                                break;
//                        }
//                        wxUtils.adbDimensClick(context, R.dimen.x252, R.dimen.y23, R.dimen.x312, R.dimen.y44);//确定修改
//                        wxUtils.adb("input keyevent 4");//返回
//                        wxUtils.adb("input keyevent 4");//返回
//                        wxUtils.adb("input keyevent 4");//返回
//                        kkk_GaiBeiZhu++;
//                        if (kkk_GaiBeiZhu >= xiuGaiNum) {
//                            Flag_GaiBeiZhu = false;
//                            break;
//                        }
//                        int timeSleep = random.nextInt(5 - 3 + 1) + 3;
//                        LogUtils.e("end=" + 10 + "__start=" + 5 + "___间隔随机数=" + timeSleep);
//                        ShowToast.show("间隔时间：" + timeSleep + "秒", (Activity) context);
//                        try {
//                            Thread.sleep(timeSleep * 1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
                    }
                }
            }
//            if (bb == 0) {
//                String oldXml = xmlData;
//                wxUtils.adbUpSlide(context);
//                xmlData = wxUtils.getXmlData();
//                if (oldXml.equals(xmlData)) {
//                    ShowToast.show("滑到底部了", (Activity) context);
//                    Flag_GaiBeiZhu = false;
//                    continue;
//                }
//            }
        }
    }




    @Override
    public void stop() {

    }
}
