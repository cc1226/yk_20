package com.zplh.zplh_android_yk.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zplh.zplh_android_yk.R;
import com.zplh.zplh_android_yk.adapter.TaskListAdapter;
import com.zplh.zplh_android_yk.bean.TaskMessageBean;
import com.zplh.zplh_android_yk.module.TaskManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 当前任务列表
 * Created by yong hao zeng on 2018/4/23/023.
 */
public class TaskListFragment extends Fragment implements TaskManager.TaskListener {
    @BindView(R.id.rv)
    RecyclerView rv;
    Unbinder unbinder;
    private View rootView;
    private LinearLayoutManager mLayoutManager;
    private List<TaskMessageBean.ContentBean.DataBean> list = new ArrayList();
    private TaskListAdapter myAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.task_list_fragment, null);
        unbinder = ButterKnife.bind(this, rootView);
        init();
        return rootView;
    }

    private void init() {
        TaskManager.getInstance().setListener(this);
        list = TaskManager.getInstance().getTaskList();
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        myAdapter = new TaskListAdapter();
        myAdapter.setDataBeans(list);
        recyclerView.setAdapter(myAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public static TaskListFragment newInstance() {
        Bundle args = new Bundle();
        TaskListFragment fragment = new TaskListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSuccess(TaskMessageBean.ContentBean.DataBean dataBean) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                myAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onError(TaskMessageBean.ContentBean.DataBean dataBean) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                myAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onStart(TaskMessageBean.ContentBean.DataBean dataBean) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                myAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onAdd(TaskMessageBean.ContentBean.DataBean dataBean) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                myAdapter.notifyDataSetChanged();
            }
        });
    }
}
