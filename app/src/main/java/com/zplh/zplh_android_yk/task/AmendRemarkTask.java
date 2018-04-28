package com.zplh.zplh_android_yk.task;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.zplh.zplh_android_yk.R;
import com.zplh.zplh_android_yk.base.MyApplication;
import com.zplh.zplh_android_yk.bean.NodeXmlBean;
import com.zplh.zplh_android_yk.bean.TaskErrorBean;
import com.zplh.zplh_android_yk.bean.TaskMessageBean;
import com.zplh.zplh_android_yk.callback.TaskCallback;
import com.zplh.zplh_android_yk.constant.Priority;
import com.zplh.zplh_android_yk.utils.AdbUtils;
import com.zplh.zplh_android_yk.utils.SPUtils;
import com.zplh.zplh_android_yk.utils.ShowToast;
import com.zplh.zplh_android_yk.utils.StringUtils;
import com.zplh.zplh_android_yk.utils.WxIsInstallUtils;
import com.zplh.zplh_android_yk.utils.WxTaskUtils;
import com.orhanobut.logger.Logger;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

/**
 * 修改备注
 * Created by liaoguilong on 2018/4/20/020.
 */

public class AmendRemarkTask extends BaseTask {

    public AmendRemarkTask(Priority priority, int sequence, TaskMessageBean.ContentBean.DataBean taskBean) {
        super(priority, sequence, taskBean);
    }

    @Override
    public void run(TaskCallback callback) throws Exception {
        try {
            WxIsInstallUtils.GetIsInstallWx().IsInstall(getTaskBean().getTask_id());
        } catch (Exception e) {
            callback.onTaskError(this, new TaskErrorBean(TaskErrorBean.EXCEPTION_ERROR).setException(e));
        }
        WxTaskUtils.getWxTaskUtils().switchWxAccount1();
        AdbUtils.getAdbUtils().click4xy(153, 822, 207, 847);
        callback.onTaskStart(this);
        Logger.d("修改备注开始");
        startAlterName(getTaskBean().getParam().getRemark(), MyApplication.getContext());
        callback.onTaskSuccess(this);
    }

    @Override
    public void stop() {
        Thread.interrupted();
    }

    /**
     * 修改备注.
     */
    private void startAlterName(String zzz, Context context) {
        String xmlData;
        String yunYingMark = "";//运营号
        NodeXmlBean.NodeBean nodeBean;
        List<Integer> listXY;
        Random random = new Random();// 定义随机类
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
                xmlData = AdbUtils.getAdbUtils().dumpXml2String();//修改完一个重新获取页面数据
                if (xmlData.contains("wx助手") || (xmlData.contains("应用") && xmlData.contains("主屏幕"))) {
                    Logger.d("任务被中断，结束修改备注任务");
                    break w;
                } else if (!(xmlData.contains("通讯录") && xmlData.contains("发现"))) {
                    AdbUtils.getAdbUtils().adb("input keyevent 4");//返回
                } else {
                    break;
                }
            }
            List<String> nodeList = AdbUtils.getAdbUtils().getNodeList(xmlData);
            a:
            for (int a = 0; a < nodeList.size(); a++) {
                nodeBean = AdbUtils.getAdbUtils().getNodeXmlBean(nodeList.get(a)).getNode();
                if (nodeBean.getResourceid() != null && (nodeBean.getResourceid().equals("com.tencent.mm:id/j_")) && nodeBean.getContentdesc() != null && nodeBean.getContentdesc() != "" && !nodeBean.getContentdesc().startsWith("微信") && !nodeBean.getContentdesc().equals("文件传输助手")
                        && !nodeBean.getContentdesc().startsWith("YYY") && !nodeBean.getContentdesc().startsWith("ZZZ") && !nodeBean.getContentdesc().startsWith("zzz") && !meName.equals(nodeBean.getContentdesc()) && !yunYingMark.contains(nodeBean.getContentdesc())) {
                    //筛选出好友
                    listXY = AdbUtils.getAdbUtils().getXY(nodeBean.getBounds());//获取好友坐标
                    AdbUtils.getAdbUtils().click4xy(listXY.get(0), listXY.get(1), listXY.get(2), listXY.get(3));//点击好友修改备注

                    Logger.d("点击进入");
                    xmlData = AdbUtils.getAdbUtils().dumpXml2String();//重新获取页面数据
                    if (!xmlData.contains("标签")) {
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
                    xmlData = AdbUtils.getAdbUtils().dumpXml2String();//重新获取页面数据
                    //                    wxUtils.adbDimensClick(context, R.dimen.x1, R.dimen.y135, R.dimen.x320, R.dimen.y166);//点击设置备注和标签
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
                    xmlData = AdbUtils.getAdbUtils().dumpXml2String();

                    if (xmlData.contains("备注信息") && xmlData.contains("完成")) {

                    } else {
                        continue w;
                    }
                    AdbUtils.getAdbUtils().adbDimensClick(context, R.dimen.x16, R.dimen.y89, R.dimen.x304, R.dimen.y115);//点击名字EditText
                    AdbUtils.getAdbUtils().adbDimensClick(context, R.dimen.x252, R.dimen.y89, R.dimen.x312, R.dimen.y115);//清空名字

                    switch (sex) {//0代表女。   1代表男   2代表性别未知
                        case 0:
                            int wx_name_number_girl = (int) SPUtils.get(context, "wx_name_number_girl", 0);
                            String wx_nume_number_new_girl = df.format(wx_name_number_girl + 1);
                            AdbUtils.getAdbUtils().adb("input text " + zzz + "B" + wx_nume_number_new_girl);
                            SPUtils.put(context, "wx_name_number_girl", wx_name_number_girl + 1);
                            break;
                        case 1:
                            int wx_name_number_boy = (int) SPUtils.get(context, "wx_name_number_boy", 0);
                            String wx_nume_number_new_boy = df.format(wx_name_number_boy + 1);
                            AdbUtils.getAdbUtils().adb("input text " + zzz + "A" + wx_nume_number_new_boy);
                            SPUtils.put(context, "wx_name_number_boy", wx_name_number_boy + 1);
                            break;
                        case 2:
                            int wx_name_number_c = (int) SPUtils.get(context, "wx_name_number_c", 0);
                            String wx_nume_number_c = df.format(wx_name_number_c + 1);
                            AdbUtils.getAdbUtils().adb("input text " + zzz + "C" + wx_nume_number_c);
                            SPUtils.put(context, "wx_name_number_c", wx_name_number_c + 1);
                            break;
                    }
                    AdbUtils.getAdbUtils().adbDimensClick(context, R.dimen.x252, R.dimen.y23, R.dimen.x312, R.dimen.y44);//确定修改
                    //  LogUtils.d(nodeList.get(a));
                    AdbUtils.getAdbUtils().adb("input keyevent 4");
                    xmlData = AdbUtils.getAdbUtils().dumpXml2String();
                    if (!(xmlData.contains("微信") && xmlData.contains("通讯录") && xmlData.contains("发现") && xmlData.contains("我"))) {
                        AdbUtils.getAdbUtils().adb("input keyevent 4");
                    }

                    //设置间隔时间
                    int start;
                    if (StringUtils.isEmpty(getTaskBean().getParam().getRemark_interval_time_s())) {
                        start = 3;
                    } else {
                        start = Integer.valueOf(getTaskBean().getParam().getRemark_interval_time_s());
                    }
                    int end;
                    if (StringUtils.isEmpty(getTaskBean().getParam().getRemark_interval_time_e())) {
                        end = 6;
                    } else {
                        end = Integer.valueOf(getTaskBean().getParam().getRemark_interval_time_e());
                    }
                    int timeSleep = random.nextInt(5 - 3 + 1) + 3;
                    Logger.d("end=" + end + "__start=" + start + "___间隔随机数=" + timeSleep);
                    Logger.d("间隔时间：" + timeSleep + "秒");
                    try {
                        Thread.sleep(timeSleep * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
            xmlData = AdbUtils.getAdbUtils().dumpXml2String();//修改完一个重新获取页面数据
            nodeList = AdbUtils.getAdbUtils().getNodeList(xmlData);
            if (!xmlData.contains("发现")) {
                Logger.d("任务被中断，结束修改备注任务");
                continue w;
            }
            zzzNum = 0;
            for (int b = 0; b < nodeList.size(); b++) {
                nodeBean = AdbUtils.getAdbUtils().getNodeXmlBean(nodeList.get(b)).getNode();
                if (nodeBean.getResourceid() != null && (nodeBean.getResourceid().equals("com.tencent.mm:id/j_")) && nodeBean.getContentdesc() != null && nodeBean.getContentdesc() != "" && !nodeBean.getContentdesc().startsWith("微信") && !nodeBean.getContentdesc().equals("文件传输助手") && !nodeBean.getContentdesc().startsWith("YYY") && !nodeBean.getContentdesc().startsWith("ZZZ") && !nodeBean.getContentdesc().startsWith("zzz") && !meName.equals(nodeBean.getContentdesc())) {
                    continue w;
                } else if (nodeBean.getResourceid() != null && (nodeBean.getResourceid().equals("com.tencent.mm:id/j_")) && nodeBean.getContentdesc() != null && nodeBean.getContentdesc() != "" && nodeBean.getContentdesc().startsWith("ZZZ")) {
                    zzzNum++;
                }
            }
            if (!bottom) {
                if (zzzNum >= 9) {
                    AdbUtils.getAdbUtils().adbDimensClick(context, R.dimen.x296, R.dimen.y357, R.dimen.x320, R.dimen.y365);
                } else {
                    AdbUtils.getAdbUtils().adbUpSlide(context);//向上滑动
                }
            }
            endData = xmlData;
            xmlData = AdbUtils.getAdbUtils().dumpXml2String();//滑动后重新获取页面数据
            if (endData.equals(xmlData)) {
                Logger.d("修改备注完成");
                break w;
            }
            if (xmlData.contains("位联系人")) {//判断是否到达底部
                bottom = true;
            }
        }
    }
}
