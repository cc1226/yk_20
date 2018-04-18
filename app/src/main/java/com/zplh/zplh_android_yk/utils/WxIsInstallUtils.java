package com.zplh.zplh_android_yk.utils;

import android.content.Context;
import android.widget.Toast;

import com.zplh.zplh_android_yk.R;
import com.zplh.zplh_android_yk.bean.NodeXmlBean;
import com.zplh.zplh_android_yk.constant.TaskConstant;

import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Created by Administrator on 2018/4/18/018.
 */

public class WxIsInstallUtils {

    private static WxIsInstallUtils IsInstallWx;
    private Context mContext;
    private int time = 0;
    private String xmlData;
    private List<Integer> listXY;
    private int status = 5;//0没群 1男群满  2女群满  3男女都满  4失败 5正常


    private WxIsInstallUtils() {

    }

    public WxIsInstallUtils GetIsInstallWx(Context context) {
        this.mContext = context;
        if (IsInstallWx == null) {
            IsInstallWx = new WxIsInstallUtils();
        }
        return IsInstallWx;
    }


    public boolean IsInstall(int task) throws Exception {

        if (WxTaskUtils.getWxTaskUtils().isInstallApp(mContext, "com.tencent.mm")) {
            WxTaskUtils.getWxTaskUtils().openWx();
            TimeUnit.SECONDS.sleep(3);
            xmlData = AdbUtils.getAdbUtils().dumpXml2String();
            if (xmlData.contains("安全警告") && xmlData.contains("wx助手 正在尝试") && xmlData.contains("记住我的选择。")) {
                AdbUtils.getAdbUtils().adbDimensClick(mContext, R.dimen.x41, R.dimen.y232, R.dimen.x41, R.dimen.y232);//记住选择
                AdbUtils.getAdbUtils().adbDimensClick(mContext, R.dimen.x260, R.dimen.y272, R.dimen.x260, R.dimen.y272);//确定
            }
            if (xmlData.contains("更新") && xmlData.contains("取消") && xmlData.contains("立刻安装")) {
                List<String> ud = AdbUtils.getAdbUtils().getNodeList(xmlData);
                for (int a = 0; a < ud.size(); a++) {
                    NodeXmlBean.NodeBean nodeBean = AdbUtils.getAdbUtils().getNodeXmlBean(ud.get(a)).getNode();
                    if (nodeBean != null && nodeBean.getText() != null && nodeBean.getText().contains("取消")) {
                        listXY = AdbUtils.getAdbUtils().getXY(nodeBean.getBounds());//取消
                        AdbUtils.getAdbUtils().clickLong(listXY.get(0), listXY.get(1), listXY.get(2), listXY.get(3));
                        AdbUtils.getAdbUtils().adbDimensClick(mContext, R.dimen.x200, R.dimen.y230, R.dimen.x264, R.dimen.y251);
                        break;
                    }
                }
                xmlData = AdbUtils.getAdbUtils().dumpXml2String();
            }
            if (xmlData.contains("忘记密码") || (xmlData.contains("登录") && xmlData.contains("注册")
                    && xmlData.contains("语言")) || (xmlData.contains("你的手机号码") && xmlData.contains("密码"))) {
                Toast.makeText(mContext, "请先登录微信", Toast.LENGTH_LONG).show();
                status = 4;
                return false;
            } else if (xmlData.contains("通讯录") && xmlData.contains("发现") && xmlData.contains("我") && !(xmlData.contains("聊天信息"))) {
                if (task == 0 || task == TaskConstant.TASK_WX_FRIENDS_DS ||
                        task == TaskConstant.TASK_WX_SUM_FRIENDS ||
                        task == TaskConstant.TASK_WX_SAVE_SIGNATURE || task == TaskConstant.TASK_WX_SEND_GZH ||
                        task == TaskConstant.TASK_WX_FRIENDS_GAME || task == TaskConstant.TASK_WX_FRIENDS_SHOP ||
                        task == TaskConstant.TASK_WX_FRIENDS_VEDIO || task == TaskConstant.TASK_WX_MASS_VEDIO ||
                        task == TaskConstant.TASK_WX_CROWD_VEDIO || task == TaskConstant.TASK_WX_ADD_XT ||
                        task == TaskConstant.TASK_WX_ADD_CROWD || task == TaskConstant.TASK_WX_COUNT ||
                        task == TaskConstant.TASK_WX_CONTACTS_ADD_BIG) {
                    AdbUtils.getAdbUtils().adbDimensClick(mContext, R.dimen.x80, R.dimen.y367, R.dimen.x160, R.dimen.y400);
                    xmlData = AdbUtils.getAdbUtils().dumpXml2String();
                    if (!xmlData.contains("新的朋友")) {
                        AdbUtils.getAdbUtils().adbDimensClick(mContext, R.dimen.x80, R.dimen.y367, R.dimen.x160, R.dimen.y400);
                        xmlData = AdbUtils.getAdbUtils().dumpXml2String();
                    }
                }
                if (task == TaskConstant.TASK_WX_ONE_MSG || task == TaskConstant.TASK_WX_CROWD_MSG) {
                    AdbUtils.getAdbUtils().adbDimensClick(mContext, R.dimen.x160, R.dimen.y368, R.dimen.x240, R.dimen.y400);
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }


}
