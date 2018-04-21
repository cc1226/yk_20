package com.zplh.zplh_android_yk.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.zplh.zplh_android_yk.base.MyApplication;
import com.zplh.zplh_android_yk.bean.NodeXmlBean;
import com.zplh.zplh_android_yk.bean.WxFriendsMessageBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/4/19/019.
 * 统计重复的好友
 */

public class StatisticsWxFriendsMessage {
    private String xmlData;
    private NodeXmlBean.NodeBean nodeBean;
    private List<Integer> listXY;
    private List<String> nodeList;
    private String[] sex;
    private String wx_phone_name;
    private int status = 5;//0没群 1男群满  2女群满  3男女都满  4失败 5正常

    public StatisticsWxFriendsMessage(List<Integer> listXY, String wx_phone_name, String[] sex) {
        this.listXY = listXY;
        this.wx_phone_name = wx_phone_name;
        this.sex = sex;
    }

    public boolean sStatisticsWxFriends() {
        List<WxFriendsMessageBean> mWxFriendsMessageBean = new ArrayList<>();
//        wxUtils.adbClick(listXY.get(0) - 200, listXY.get(1), listXY.get(2) - 200, listXY.get(3));
        AdbUtils.getAdbUtils().click4xy(listXY.get(0) - 200, listXY.get(1), listXY.get(2) - 200, listXY.get(3));
        xmlData = AdbUtils.getAdbUtils().dumpXml2String();

//        xmlData = wxUtils.getXmlData();

        String wx_number = "";
        String wx_name = "";
        String wx_location = "";
        String wx_phone_number = "";
        int aaa = 0;
//        if (xmlData.contains("男")) {
//            aaa = 1;
//        } else if (xmlData.contains("女")) {
//            aaa = 2;
//        } else {
//            aaa = 3;
//        }
//        if ((sex[0].equals("男") && (aaa == 1)) || (sex[0].equals("女") && (aaa == 2)) || (sex[0].equals("全加"))) {
//            List<String> meWxFriendsMessageList = wxUtils.getNodeList(xmlData);
        List<String> meWxFriendsMessageList = AdbUtils.getAdbUtils().getNodeList(xmlData);
        for (int i = 0; i < meWxFriendsMessageList.size(); i++) {
//                NodeXmlBean.NodeBean nodeBean = wxUtils.getNodeXmlBean(meWxFriendsMessageList.get(i)).getNode();
            NodeXmlBean.NodeBean nodeBean = AdbUtils.getAdbUtils().getNodeXmlBean(meWxFriendsMessageList.get(i)).getNode();

            if (nodeBean != null && nodeBean.getResourceid() != null && "com.tencent.mm:id/pl".equals(nodeBean.getResourceid())) {
                // 为空的时候才获取，这样下次有数据只有就不会 重复获取了
                wx_name = nodeBean.getText();
            }
            if (xmlData.contains("地区") && ("android:id/summary".equals(nodeBean.getResourceid())) &&
                    AdbUtils.getAdbUtils().getNodeXmlBean(meWxFriendsMessageList.get(i - 3)).getNode().getText() != null &&
                    AdbUtils.getAdbUtils().getNodeXmlBean(meWxFriendsMessageList.get(i - 3)).getNode().getText().endsWith("地区")
//                        wxUtils.getNodeXmlBean(meWxFriendsMessageList.get(i - 3)).getNode().getText()
//                        wxUtils.getNodeXmlBean(meWxFriendsMessageList.get(i - 3)).getNode().getText().equals("地区")
                    ) {
                wx_location = nodeBean.getText();
            }
            if (xmlData.contains("社交资料") && ("com.tencent.mm:id/ga".equals(nodeBean.getResourceid()))) {
                listXY = AdbUtils.getAdbUtils().getXY(nodeBean.getBounds());//获取消息
//                    listXY = wxUtils.getXY(nodeBean.getBounds());//获取消息

//                    wxUtils.adbClick(listXY.get(0), listXY.get(1), listXY.get(2), listXY.get(3));//
                AdbUtils.getAdbUtils().click4xy(listXY.get(0), listXY.get(1), listXY.get(2), listXY.get(3));
//                    xmlData = wxUtils.getXmlData();
                xmlData = AdbUtils.getAdbUtils().dumpXml2String();
//                    List<String> phoneMessageList = wxUtils.getNodeList(xmlData);

                List<String> phoneMessageList = AdbUtils.getAdbUtils().getNodeList(xmlData);

                for (int j = 0; j < phoneMessageList.size(); j++) {
//                        NodeXmlBean.NodeBean nodeBean2 = wxUtils.getNodeXmlBean(phoneMessageList.get(j)).getNode();
                    NodeXmlBean.NodeBean nodeBean2 = AdbUtils.getAdbUtils().getNodeXmlBean(phoneMessageList.get(j)).getNode();
                    if (nodeBean2 != null && "android:id/summary".equals(nodeBean2.getResourceid()) && xmlData.contains("手机")) {
                        String wx_phone_numer2 = nodeBean2.getText();
                        String[] wx_message = wx_phone_numer2.split("\\s+");
                        wx_phone_number = wx_message[1];
//                            wxUtils.adb("input keyevent 4");
                        AdbUtils.getAdbUtils().adb("input keyevent 4");
                        break;
                    }
                }
            }
        }
        Log.d("获取到的信息", "微信名字： " + wx_name + "微信所在地区： " + wx_location + " 微信手机号：" + wx_phone_number + " 手机联系人名： " + wx_phone_name);
//            ShowToast.show("统计微信好友信息", (Activity) MyApplication.getContext());
        Log.e("WG", "统计微信好友信息");
//            wxUtils.adb("input keyevent 4");
        AdbUtils.getAdbUtils().adb("input keyevent 4");

        String uid = SPUtils.getString(MyApplication.getContext(), "uid", "0000");
        WxFriendsMessageBean messageBean = new WxFriendsMessageBean(wx_number, wx_name, wx_phone_number, wx_phone_name, wx_location, uid);
        mWxFriendsMessageBean.add(messageBean);
        String str = new Gson().toJson(mWxFriendsMessageBean);
        Log.e("WG", "JSON" + str.toString());
//            sendWxFriendsMessage(str);
//        RequestParams params = new RequestParams(URLS.statictis_wx_message_store());
//        params.addBodyParameter("json", str.replace("\\", ""));
//        HttpManagerUtils.getInstance().sendPostRequest(params, new HttpObjectCallbackUtils<Object>() {
//
//            @Override
//            public void onSuccess(Object bean) {
//                    LogUtils.d("好友个人信息上传成功");
//                Log.e("WG", "好友个人信息上传成功 ");
//            }
//
//            @Override
//            public void onFailure(int errorCode, String errorString) {
//                LogUtils.d("好友数量上传失败");
//            }
//        });
//        AdbUtils.getAdbUtils().adb("input keyevent 4");

        return true;

    }
//        wxUtils.adb("input keyevent 4");
//        return false;

//
}
