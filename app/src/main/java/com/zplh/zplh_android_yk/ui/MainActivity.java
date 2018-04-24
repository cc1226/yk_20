package com.zplh.zplh_android_yk.ui;

import android.annotation.SuppressLint;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zplh.zplh_android_yk.R;
import com.zplh.zplh_android_yk.bean.TaskMessageBean;
import com.zplh.zplh_android_yk.callback.TaskPCallback;
import com.zplh.zplh_android_yk.presenter.TaskP;

import butterknife.BindView;
import cn.jpush.android.api.JPushInterface;

public class MainActivity extends BaseUI implements TaskPCallback {


    @BindView(R.id.iv_statubar)
    ImageView ivStatubar;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_actionphone)
    ImageView ivActionphone;
    @BindView(R.id.iv_action)
    ImageView ivAction;
    @BindView(R.id.tv_enter)
    TextView tvEnter;
//    @BindView(R.id.rl_title)
//    LinearLayout rlTitle;
    @BindView(R.id.fragmentLayout)
    FrameLayout fragmentLayout;
    @BindView(R.id.aTextView)
    TextView aTextView;
    @BindView(R.id.bTextView)
    TextView bTextView;
    @BindView(R.id.cTextView)
    TextView cTextView;
    @BindView(R.id.dTextView)
    TextView dTextView;
    @BindView(R.id.contentLayout)
    LinearLayout contentLayout;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    private TaskP taskP;
    private TaskFragment taskFragment;

    @SuppressLint("CheckResult")
    @Override
    protected void initData() {
        //初始化taskP
        taskP = new TaskP(this);
        taskP.startTask();
        JPushInterface.init(this);
    }

    @Override
    protected void initView() {
        taskFragment = TaskFragment.newInstance();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.contentLayout,taskFragment).commit();
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_main;
    }

    @Override
    public void onSuccessTask(TaskMessageBean.ContentBean.DataBean task) {

    }

    @Override
    public void onErrorTask(TaskMessageBean.ContentBean.DataBean task, Exception e) {

    }

    @Override
    public void onStartTask(TaskMessageBean.ContentBean.DataBean dataBean) {

    }

    @Override
    public void onTaskProgress(TaskMessageBean.ContentBean.DataBean dataBean, String progress) {

    }


}
