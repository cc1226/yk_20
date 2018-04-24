package com.zplh.zplh_android_yk.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zplh.zplh_android_yk.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 当前任务列表
 * Created by yong hao zeng on 2018/4/23/023.
 */
public class TaskListFragment extends BaseFragment {
    @BindView(R.id.rv)
    RecyclerView rv;
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.task_list_fragment, null);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }





    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
