package com.zplh.zplh_android_yk.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zplh.zplh_android_yk.base.MyApplication;
import com.zplh.zplh_android_yk.bean.NodeXmlBean;
import com.zplh.zplh_android_yk.bean.WxFriendsMessageBean;
import com.zplh.zplh_android_yk.constant.URLS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

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

    public void sStatisticsWxFriends() throws IOException {
        List<WxFriendsMessageBean> mWxFriendsMessageBean = new ArrayList<>();
        AdbUtils.getAdbUtils().click4xy(listXY.get(0) - 200, listXY.get(1), listXY.get(2) - 200, listXY.get(3));
        xmlData = AdbUtils.getAdbUtils().dumpXml2String();
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
            List<String> meWxFriendsMessageList = AdbUtils.getAdbUtils().getNodeList(xmlData);
            for (int i = 0; i < meWxFriendsMessageList.size(); i++) {
                NodeXmlBean.NodeBean nodeBean = AdbUtils.getAdbUtils().getNodeXmlBean(meWxFriendsMessageList.get(i)).getNode();

                if (nodeBean != null && nodeBean.getResourceid() != null && "com.tencent.mm:id/pl".equals(nodeBean.getResourceid())) {
                    // 为空的时候才获取，这样下次有数据只有就不会 重复获取了
                    wx_name = nodeBean.getText();
                }
                if (xmlData.contains("地区") && ("android:id/summary".equals(nodeBean.getResourceid())) &&
                        AdbUtils.getAdbUtils().getNodeXmlBean(meWxFriendsMessageList.get(i - 3)).getNode().getText() != null &&
                        AdbUtils.getAdbUtils().getNodeXmlBean(meWxFriendsMessageList.get(i - 3)).getNode().getText().endsWith("地区")
                        ) {
                    wx_location = nodeBean.getText();
                }
                if (xmlData.contains("社交资料") && ("com.tencent.mm:id/ga".equals(nodeBean.getResourceid()))) {
                    listXY = AdbUtils.getAdbUtils().getXY(nodeBean.getBounds());//获取消息

                    AdbUtils.getAdbUtils().click4xy(listXY.get(0), listXY.get(1), listXY.get(2), listXY.get(3));
                    xmlData = AdbUtils.getAdbUtils().dumpXml2String();

                    List<String> phoneMessageList = AdbUtils.getAdbUtils().getNodeList(xmlData);

                    for (int j = 0; j < phoneMessageList.size(); j++) {
                        NodeXmlBean.NodeBean nodeBean2 = AdbUtils.getAdbUtils().getNodeXmlBean(phoneMessageList.get(j)).getNode();
                        if (nodeBean2 != null && "android:id/summary".equals(nodeBean2.getResourceid()) && xmlData.contains("手机")) {
                            String wx_phone_numer2 = nodeBean2.getText();
                            String[] wx_message = wx_phone_numer2.split("\\s+");
                            wx_phone_number = wx_message[1];
                            AdbUtils.getAdbUtils().back();
                            break;
                        }
                    }
                }
            }
            Log.d("获取到的信息", "微信名字： " + wx_name + "微信所在地区： " + wx_location + " 微信手机号：" + wx_phone_number + " 手机联系人名： " + wx_phone_name);
            Log.e("WG", "统计微信好友信息");
            AdbUtils.getAdbUtils().back();
            String uid = SPUtils.getString(MyApplication.getContext(), "uid", "0000");
            WxFriendsMessageBean messageBean = new WxFriendsMessageBean(wx_number, wx_name, wx_phone_number, wx_phone_name, wx_location, uid);
            mWxFriendsMessageBean.add(messageBean);
            String str = new Gson().toJson(mWxFriendsMessageBean);
            Log.e("WG", "JSON" + str.toString());
//            sendWxFriendsMessage(str);
            Response json = OkHttpUtils.post().url(URLS.statictis_wx_message_store()).addParams("json", str.replace("\\", "")).build().execute();
            if (json.code() == 200) {
                String string = json.body().string();
                Log.e("WG", "好友信息上传成功 " + string);
            }
//        AdbUtils.getAdbUtils().back();
        }
    }
//}
