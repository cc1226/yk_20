package com.zplh.zplh_android_yk.utils;


import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.zplh.zplh_android_yk.R;
import com.zplh.zplh_android_yk.XmlToJson.XmlToJson;
import com.zplh.zplh_android_yk.bean.NodeXmlBean;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * adb 执行工具
 * Created by yong hao zeng on 2018/4/10 0010.
 */

public class AdbUtils {
    private static List<String> commnandList;

    private static AdbUtils adbUtils;

    private AdbUtils() {
    }

    public static AdbUtils getAdbUtils() {
        if (adbUtils == null) {
            adbUtils = new AdbUtils();
        }
        return adbUtils;
    }

    /**
     * adb shell input tap 279 1897
     * input tap 279 1897
     * 执行adb命令
     *
     * @param str
     */
    public void adb(String str) {
        if (commnandList != null) {
            commnandList.clear();
        } else {
            commnandList = new ArrayList<>();
        }
        commnandList.add(str);
        ShellUtils.CommandResult result = ShellUtils.execCommand(commnandList, true);
        Log.e("WG ", "adb: " + result.result + "adb" + result.successMsg);
    }


    //单击某按钮 根据坐标
    public boolean click4xy(int a, int b, int c, int d) {
        if (commnandList != null) {
            commnandList.clear();
        } else {
            commnandList = new ArrayList<>();
        }
        commnandList.add("input tap " + (a + c) / 2 + " " + (b + d) / 2);
        ShellUtils.CommandResult result = ShellUtils.execCommand(commnandList, true);
        Logger.t("adb click").d("x" + (a + c) / 2 + "y" + (b + d) / 2);
        return result.result == 0;
    }

    public String dumpXml2String() {

        ShellUtils.CommandResult commandResult = ShellUtils.execCommand("uiautomator dump /sdcard/wx_ui.xml", true);
        if (commandResult.result != 0) return "";
        return FileUtils.readTxtFile();
    }


    /**
     * 从字符串中提取数字
     *
     * @param s
     * @return
     */
    public List<Integer> getXY(String s) {

        ArrayList<Integer> listXY = new ArrayList<>();

        for (String sss : s.replaceAll("[^0-9]", ",").split(",")) {
            if (sss.length() > 0)
                try {
                    Integer a = Integer.parseInt(sss);
                    listXY.add(a);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

        }
        return listXY;
    }

    /**
     * xml转json
     *
     * @param xml
     * @return
     */
    public String xml2JSON(String xml) {
        try {
            XmlToJson xmlToJson = new XmlToJson.Builder(xml).build();

            String newJson = xmlToJson.toJson().toString().replaceAll("\"node\":\\[", "\"node_list\":[");
            return newJson;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public NodeXmlBean getNodeXmlBean(String str) {
        Gson gson = new Gson();
        return gson.fromJson(xml2JSON(str), NodeXmlBean.class);
    }

    //返回node节点数据
    public List<String> getNodeList(String node) {
        List<String> ls = new ArrayList<String>();
        ls.clear();
        Pattern pattern = Pattern.compile("<node.*?text=\"(.*?)\".*?resource-id=\"(.*?)\" class=\"(.*?)\" package=\"(.*?)\".*?content-desc=\"(.*?)\".*?checked=\"(.*?)\".*?enabled=\"(.*?)\".*?selected=\"(.*?)\".*?bounds=\"\\[(\\d+),(\\d+)\\]\\[(\\d+),(\\d+)\\]\"");
        Matcher matcher = pattern.matcher(node);
        while (matcher.find()) {
            ls.add(matcher.group() + "/>");
        }
        return ls;
    }

    public void putText(String str) {
        ShellUtils.CommandResult commandResult = ShellUtils.execCommand("input text " + str, true);


    }

    //返回键
    public void back() {
        ShellUtils.execCommand("input keyevent 4", true);
    }

    public static boolean boardisShow() {
        ShellUtils.CommandResult commandResult = ShellUtils.execCommand("\"dumpsys input_method |grep mInputShown=true\"", true);
        return commandResult.result == 0;
    }

    public void clickLong(int a, int b, int c, int d) {
        if (commnandList != null) {
            commnandList.clear();
        } else {
            commnandList = new ArrayList<>();
        }

        commnandList.add("input swipe " + (a + c) / 2 + " " + (b + d) / 2 + " " + (a + c) / 2 + " " + (b + d) / 2 + " " + 4000);
        ShellUtils.CommandResult result = ShellUtils.execCommand(commnandList, true);
        Logger.t("adb click").d("长按");

    }
    /*
    * 坐标点
    * */

    public void click(int i, int i1) {
        if (commnandList != null) {
            commnandList.clear();
        } else {
            commnandList = new ArrayList<>();
        }
        commnandList.add("input tap " + i + " " + i1);
        ShellUtils.CommandResult result = ShellUtils.execCommand(commnandList, true);
    }

    //安装apk
    public void install(String path) {

        ShellUtils.execCommand("pm install -r " + path, true);
    }

    //适配的坐标点
    public void adbDimensClick(Context context, int aa, int bb, int cc, int dd) {
        int a = context.getResources().getDimensionPixelSize(aa);
        int b = context.getResources().getDimensionPixelSize(bb);
        int c = context.getResources().getDimensionPixelSize(cc);
        int d = context.getResources().getDimensionPixelSize(dd);

        if (commnandList != null) {
            commnandList.clear();
        } else {
            commnandList = new ArrayList<>();
        }

        commnandList.add("input tap " + (a + c) / 2 + " " + (b + d) / 2);
        ShellUtils.CommandResult result = ShellUtils.execCommand(commnandList, true);
    }


    /**
     * 向上滑动（屏幕适配）
     *
     * @param context
     */
    public void adbUpSlide(Context context) {

        int a = context.getResources().getDimensionPixelSize(R.dimen.x134);

        int b = context.getResources().getDimensionPixelSize(R.dimen.y295);
        int c = context.getResources().getDimensionPixelSize(R.dimen.x134);
        int d = context.getResources().getDimensionPixelSize(R.dimen.y94);

        if (commnandList != null) {
            commnandList.clear();
        } else {
            commnandList = new ArrayList<>();
        }

        commnandList.add("input swipe " + a + " " + b + " " + c + " " + d);
        ShellUtils.CommandResult result = ShellUtils.execCommand(commnandList, true);
        Log.e("WG", "adbUpSlide: " + result.result + "adb" + result.successMsg);
    }

    /**
     * Wx 通用页面跳转
     */
    Random random = new Random();

    public void wxActivityJump(String str) {
        if (commnandList != null) {
            commnandList.clear();
        } else {
            commnandList = new ArrayList<>();
        }
        int count = random.nextInt(10);
        FileUtils.createDirs(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/BBB");
        String command = "  am start  " + str;
        commnandList.add(command);
        ShellUtils.CommandResult result = ShellUtils.execCommand(commnandList, true);
        Log.e("WG", "wxActivityJump: " + result.result + "adb" + result.successMsg);

    }

}
