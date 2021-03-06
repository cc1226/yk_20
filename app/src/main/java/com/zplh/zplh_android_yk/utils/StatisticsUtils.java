package com.zplh.zplh_android_yk.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zplh.zplh_android_yk.R;
import com.zplh.zplh_android_yk.base.MyApplication;
import com.zplh.zplh_android_yk.bean.NodeXmlBean;
import com.zplh.zplh_android_yk.bean.WxNumBean;
import com.zplh.zplh_android_yk.constant.URLS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Response;

/**
 * Created by Administrator on 2018/4/19/019.
 */

public class StatisticsUtils {
    public Context mContext;
    private List<Integer> listXY;

    public StatisticsUtils(Context context) {
        this.mContext = context;
    }


    public void statistics() throws IOException, InterruptedException {
        String xmlData;
//        xmlData = wxUtils.getXmlData();
        xmlData = AdbUtils.getAdbUtils().dumpXml2String();
        Log.e("WG", "statistics: 开始统计:" + xmlData);
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
            NodeXmlBean.NodeBean nodeBean = AdbUtils.getAdbUtils().getNodeXmlBean(s).getNode();
            if ("com.tencent.mm:id/amy".equals(nodeBean.getResourceid())) {
                contentBean.setFriends_num(nodeBean.getText().replace("位联系人", ""));
                Log.e("WG", "statistics: " + "联系人" + nodeBean.getText().replace("位联系人", ""));
                break;
            }
        }
        //设置uid
        contentBean.setUid(SPUtils.getString(mContext, "uid", "0000"));

        //设置群信息
        AdbUtils.getAdbUtils().adbDimensClick(mContext, R.dimen.x80, R.dimen.y367, R.dimen.x160, R.dimen.y400);//点击通讯录

        Thread.sleep(1000);
        AdbUtils.getAdbUtils().adbDimensClick(mContext, R.dimen.x1, R.dimen.y87, R.dimen.x320, R.dimen.y124);//群聊

        String qunClickMark = "";//进过的群
        boolean isOneSlide = false;
        int count = 0;

        //进入了群列表
        w:
        while (true) {
            xmlData = AdbUtils.getAdbUtils().dumpXml2String();
            if (xmlData.contains("新群聊") && xmlData.contains("你可以通过群聊中的“保存到通讯录”选项，将其保存到这里")) {
                AdbUtils.getAdbUtils().back();
                break;
            }

            List<String> nodeList = new ArrayList<>();
            Pattern pattern = Pattern.compile("<node.*?text=\"(.*?)\".*?resource-id=\"(.*?)\" class=\"(.*?)\" package=\"(.*?)\".*?content-desc=\"(.*?)\".*?checked=\"(.*?)\".*?enabled=\"(.*?)\".*?selected=\"(.*?)\".*?bounds=\"\\[(\\d+),(\\d+)\\]\\[(\\d+),(\\d+)\\]\"");
            Matcher matcher = pattern.matcher(xmlData);
            while (matcher.find()) {
                nodeList.add(matcher.group() + "/>");
            }

            for (int a = 0; a < nodeList.size(); a++) {
                NodeXmlBean.NodeBean nodeBean = AdbUtils.getAdbUtils().getNodeXmlBean(nodeList.get(a)).getNode();
                if (nodeBean.getText() != null && nodeBean.getResourceid() != null && nodeBean.getResourceid().contains("com.tencent.mm:id/a9u")) {
                    String flockName = nodeBean.getText();

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
                        listXY = AdbUtils.getAdbUtils().getXY(nodeBean.getBounds());//获取群坐标
                        AdbUtils.getAdbUtils().click4xy(listXY.get(0), listXY.get(1), listXY.get(2), listXY.get(3));
                        qunClickMark = qunClickMark + nodeBean.getText() + ",";
                    }

                    //_______________________________________________________________________________________________
                    String qunName = "";
                    //获取群人数，男女群信息
                    String qunNameData = AdbUtils.getAdbUtils().dumpXml2String();
                    List<String> qunNameDataList = new ArrayList<String>();
                    Matcher matcherA = pattern.matcher(qunNameData);
                    while (matcherA.find()) {
                        qunNameDataList.add(matcherA.group() + "/>");
                    }
                    if (!(qunNameData.contains("当前所在页面,与"))) {
//                        Toast.makeText(MyApplication.getContext(), "任务被中断，结束拉群任务", Toast.LENGTH_LONG).show();
                        break w;
                    }

                    for (int c = 0; c < qunNameDataList.size(); c++) {
                        NodeXmlBean.NodeBean qunNameBean = AdbUtils.getAdbUtils().getNodeXmlBean(qunNameDataList.get(c)).getNode();
                        if ("com.tencent.mm:id/hj".equals(qunNameBean.getResourceid())) {
                            qunName = qunNameBean.getText();
                            Log.e("WG", "statistics: " + qunName);
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
                    AdbUtils.getAdbUtils().back();
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
                AdbUtils.getAdbUtils().back();
                Log.e("WG", "statistics: 消息发送完成");
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
        Log.e("WG", "statistics: " + "JSON" + str.toString());
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
