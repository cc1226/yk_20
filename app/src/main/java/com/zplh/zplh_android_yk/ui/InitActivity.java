package com.zplh.zplh_android_yk.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.yanzhenjie.permission.Permission;
import com.zplh.zplh_android_yk.R;
import com.zplh.zplh_android_yk.bean.NodeXmlBean;
import com.zplh.zplh_android_yk.utils.AdbUtils;
import com.zplh.zplh_android_yk.utils.WxTaskUtils;

import java.util.List;

import butterknife.BindView;

/**
 * 初始化activity 在这里面做初始化操作
 * Created by yong hao zeng on 2018/4/20/020.
 */
public class InitActivity extends BaseUI {
    @BindView(R.id.tv_init_state)
    TextView tvInitState;
    private String xmlData;

    @Override
    protected void initData() {
        initPermission();


    }

    @Override
    protected void initView() {

    }


    void initPermission() {
        String permissions[] = {Permission.WRITE_CONTACTS, Permission.WRITE_EXTERNAL_STORAGE, Permission.GET_ACCOUNTS, Permission.READ_PHONE_STATE};

        requestPermission(permissions, 0);

    }

    @SuppressLint("CheckResult")
    void requestPermission(String[] permissions, int number) {
        new Thread(() -> {
            ActivityCompat.requestPermissions(InitActivity.this, permissions, 1);
            int flag = 0;
            while (flag < permissions.length) {
                flag++;
                try {
                    Thread.sleep(2000);
//                    AdbUtils.getAdbUtils().clickText("允许");
//                    String xmlData = AdbUtils.getAdbUtils().dumpXml2String();
//                    Log.e("WG", "requestPermission: " + xmlData);
//                    List<String> nodeList = AdbUtils.getAdbUtils().getNodeList(xmlData);
//                    for (int i = 0; i < nodeList.size(); i++) {
//                        NodeXmlBean.NodeBean node = AdbUtils.getAdbUtils().getNodeXmlBean(nodeList.get(i)).getNode();
//                        Log.e("WG", "requestPermission: 2222"+node );
//                        if (TextUtils.equals(node.getText(),"允许")) {
//                            List<Integer> xy = AdbUtils.getAdbUtils().getXY(node.getBounds());
//                            AdbUtils.getAdbUtils().click4xy(xy.get(0), xy.get(1), xy.get(2), xy.get(3));
//                            Log.e("WG", "requestPermission: " + node.getText());
//                            break;
//                        }
//                    }
//                    AdbUtils.getAdbUtils().adbDimensClick(InitActivity.this, R.dimen.x224, R.dimen.y201, R.dimen.x282, R.dimen.y235);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Intent intent = new Intent(InitActivity.this, BindingActivity.class);
            startActivity(intent);
            finish();
        }).start();
    }

    @Override
    protected int getLayoutID() {
        return R.layout.init_activity;
    }
}
