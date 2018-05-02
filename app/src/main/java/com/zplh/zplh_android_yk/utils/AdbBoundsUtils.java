package com.zplh.zplh_android_yk.utils;

import android.content.Context;
import android.os.Build;
import android.text.ClipboardManager;

import com.zplh.zplh_android_yk.R;
import com.zplh.zplh_android_yk.constant.ModelConstans;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * 常用坐标点击
 * Created by liaoguilong on 2018-04-29 15:56:45
 */
public class AdbBoundsUtils {

    /**
     * 清除备注信息
     */
    public static void clearRemark() {
        NodeUtils.clickNode("com.tencent.mm:id/aoy");//老机型点击名字
        NodeUtils.clickNode("com.tencent.mm:id/aoz");//酷派点击名字
        switch (Build.MODEL) {
            case ModelConstans.coolpad_8737:
                AdbUtils.getAdbUtils().click(640, 292); //酷派清空名子
                break;
            case ModelConstans.tvyk:
                AdbUtils.getAdbUtils().click4xy(R.dimen.x252, R.dimen.y89, R.dimen.x312, R.dimen.y115);//老机型清空名字
                break;
        }
    }

    /**
     * 顶部搜索框，长按并粘贴
     */
    public static void searchAndPaste(Context context, String text) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        cm.setText(text);
        switch (Build.MODEL) {
            case ModelConstans.tvyk:
                AdbUtils.getAdbUtils().click4xy(306, 36, 378, 108);//搜索
                break;
            case ModelConstans.coolpad_8737:
                AdbUtils.getAdbUtils().click4xy(504, 48, 584, 144);//酷派搜索
                break;
        }

        NodeUtils.clickLong(null, "com.tencent.mm:id/ht");
        switch (Build.MODEL) {
            case ModelConstans.tvyk:
                AdbUtils.getAdbUtils().click4xy(160, 200, 160, 128);//老机型点击粘贴
                break;
            case ModelConstans.coolpad_8737:
                AdbUtils.getAdbUtils().click4xy(114, 190, 214, 278);//酷派点击粘贴
                break;
        }


    }

}
