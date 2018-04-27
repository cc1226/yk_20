package com.zplh.zplh_android_yk.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zplh.zplh_android_yk.R;
import com.zplh.zplh_android_yk.utils.AdbUtils;

/**
 * Created by yong hao zeng on 2018/4/18/018.
 */
public class TaskFragment extends Fragment {
    public static Context mContext = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_operation, null);

        return inflate;
    }


    public static TaskFragment newInstance() {

        Bundle args = new Bundle();
        TaskFragment fragment = new TaskFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
