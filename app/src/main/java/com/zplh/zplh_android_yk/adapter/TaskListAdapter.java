package com.zplh.zplh_android_yk.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zplh.zplh_android_yk.R;
import com.zplh.zplh_android_yk.bean.TaskMessageBean;
import com.zplh.zplh_android_yk.constant.TaskConstant;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yong hao zeng on 2018/4/23/023.
 */
public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder> {
    List<TaskMessageBean.ContentBean.DataBean> dataBeans;


    public List<TaskMessageBean.ContentBean.DataBean> getDataBeans() {
        return dataBeans;
    }

    public TaskListAdapter setDataBeans(List<TaskMessageBean.ContentBean.DataBean> dataBeans) {
        this.dataBeans = dataBeans;
        return this;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_task_list, null);
        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TaskMessageBean.ContentBean.DataBean taskBean = dataBeans.get(position);
        holder.renwuName.setText(TaskConstant.getTaskNameForID(taskBean.getTask_id()));
        holder.renwuLogId.setText(taskBean.getLog_id());
        holder.renwuTime.setText(taskBean.getTodo_time());
    }

    @Override
    public int getItemCount() {
        return dataBeans == null ? 0 : dataBeans.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.renwu_name)
        TextView renwuName;
        @BindView(R.id.renwu_log_id)
        TextView renwuLogId;
        @BindView(R.id.renwu_time)
        TextView renwuTime;
        @BindView(R.id.image_state)
        ImageView imageState;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
