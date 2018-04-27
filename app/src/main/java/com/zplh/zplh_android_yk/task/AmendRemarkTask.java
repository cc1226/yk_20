package com.zplh.zplh_android_yk.task;

import android.util.Log;

import com.zplh.zplh_android_yk.R;
import com.zplh.zplh_android_yk.base.MyApplication;
import com.zplh.zplh_android_yk.bean.NodeXmlBean;
import com.zplh.zplh_android_yk.bean.TaskMessageBean;
import com.zplh.zplh_android_yk.callback.TaskCallback;
import com.zplh.zplh_android_yk.constant.Priority;
import com.zplh.zplh_android_yk.utils.AdbUtils;
import com.zplh.zplh_android_yk.utils.SPUtils;
import com.zplh.zplh_android_yk.utils.StringUtils;
import com.zplh.zplh_android_yk.utils.WxIsInstallUtils;
import com.zplh.zplh_android_yk.utils.WxTaskUtils;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2018/4/20/020.
 */

public class AmendRemarkTask extends BaseTask {

    public AmendRemarkTask(Priority priority, int sequence, TaskMessageBean.ContentBean.DataBean taskBean) {
        super(priority, sequence, taskBean);
    }

    @Override
    public void run(TaskCallback callback) throws Exception {
//        if (WxIsInstallUtils.GetIsInstallWx().IsInstall(getTaskBean().getTask_id())){
//            WxTaskUtils.getWxTaskUtils().switchWxAccount();
//            AdbUtils.getAdbUtils().click4xy(153, 822, 207, 847);
//            callback.onTaskStart(this);
//
//        }
        WxIsInstallUtils.GetIsInstallWx().IsInstall(getTaskBean().getTask_id());
        WxTaskUtils.getWxTaskUtils().switchWxAccount();
        AdbUtils.getAdbUtils().click4xy(153, 822, 207, 847);
        callback.onTaskStart(this);

    }

    @Override
    public void stop() {

    }


    /**
     * 修改备注.
     */
    Random random = new Random();
    private NodeXmlBean.NodeBean nodeBean;
    private List<Integer> listXY;
    private String yunYingMark = "";

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

                    Log.e("WG", "点击进入 ");
                    xmlData = AdbUtils.getAdbUtils().dumpXml2String();
                    if (!xmlData.contains("标签")) {
//                        wxUtils.adb("input keyevent 4");
                        AdbUtils.getAdbUtils().adb("input keyevent 4");
                        meName = nodeBean.getContentdesc();
                        continue;
                    }
                    WxTaskUtils.getWxTaskUtils().StatisticsWxFriends(xmlData);//统计新增好友的信息
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
}
