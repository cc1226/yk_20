package com.zplh.zplh_android_yk.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zplh.zplh_android_yk.R;
import com.zplh.zplh_android_yk.adapter.TaskListAdapter;
import com.zplh.zplh_android_yk.bean.TaskMessageBean;
import com.zplh.zplh_android_yk.module.TaskManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/4/25/025.
 */

public class TaskListFragment2 extends Fragment {

    private List<TaskMessageBean.ContentBean.DataBean> list = new ArrayList();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.task_list_fragment, null);
        list= TaskManager.getInstance().getTaskList();
        RecyclerView recyclerView = (RecyclerView) inflate.findViewById(R.layout.item_task_list);
        TaskListAdapter myAdapter=new TaskListAdapter();
        myAdapter.setDataBeans(list);
        recyclerView.setAdapter(myAdapter);
        return inflate;
    }



    public static TaskListFragment2 newInstance() {
        Bundle args = new Bundle();
        TaskListFragment2 fragment = new TaskListFragment2();
        fragment.setArguments(args);
        return fragment;
    }
}
