package com.zplh.zplh_android_yk.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;

import com.yanzhenjie.permission.Permission;
import com.zplh.zplh_android_yk.R;
import com.zplh.zplh_android_yk.utils.AdbUtils;

import butterknife.BindView;

/**
 * 初始化activity 在这里面做初始化操作
 * Created by yong hao zeng on 2018/4/20/020.
 */
public class InitActivity extends BaseUI {
    @BindView(R.id.tv_init_state)
    TextView tvInitState;

    @Override
    protected void initData() {
            initPermission();


    }

    @Override
    protected void initView() {

    }


    void initPermission(){
        String permissions[] = {Permission.WRITE_CONTACTS,Permission.WRITE_EXTERNAL_STORAGE,Permission.GET_ACCOUNTS,Permission.READ_PHONE_STATE};

        requestPermission(permissions,0);

    }

    @SuppressLint("CheckResult")
    void requestPermission(String[] permissions,int number){
        new Thread(() -> {
            ActivityCompat.requestPermissions(InitActivity.this, permissions, 1);
            int flag = 0;
            while (flag < permissions.length) {
            flag++;
                try {
                    Thread.sleep(2000);
                    AdbUtils.getAdbUtils().adbDimensClick(InitActivity.this, R.dimen.x224, R.dimen.y201, R.dimen.x282, R.dimen.y235);
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
