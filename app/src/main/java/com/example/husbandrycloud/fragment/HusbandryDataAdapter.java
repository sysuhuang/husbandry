package com.example.husbandrycloud.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.husbandrycloud.Config;
import com.example.husbandrycloud.R;
import com.example.husbandrycloud.util.DatabaseTask;
import com.example.husbandrycloud.util.OSSClientManager;


import java.io.Serializable;
import java.util.List;

public class HusbandryDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private List<HusbandryData> dataList;


    private Context context; // 添加上下文

    private String userRole;


    public HusbandryDataAdapter(List<HusbandryData> dataList, Context context, String userRole) {
        this.dataList = dataList;
        this.context = context; // 初始化上下文
        this.userRole = userRole;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_husbandry_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_husbandry_data, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            HusbandryData data = dataList.get(position - 1); // Adjust for header
            ItemViewHolder itemHolder = (ItemViewHolder) holder;

            itemHolder.indexText.setText(data.getIndex());

            // 设置按钮点击事件
            itemHolder.detailButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("husbandry_data", data); // 传递数据
                context.startActivity(intent);
            });

            if ("enterprise".equals(userRole)) {
                itemHolder.deleteButton.setVisibility(View.GONE);
            } else {
                itemHolder.deleteButton.setOnClickListener(v -> {
                    // 创建确认删除对话框
                    new AlertDialog.Builder(context).setTitle("确认删除").setMessage("您确定要删除这条数据吗？").setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 处理删除操作
                                    DatabaseTask databaseTask = new DatabaseTask(new DatabaseTask.ResultListener() {
                                        @Override
                                        public void onqQueryResult(List<HusbandryData> result) {
                                            // 处理查询结果
                                        }

                                        @Override
                                        public void onqInsertResult(List<HusbandryData> result) {
                                            // 处理插入结果
                                        }

                                        @Override
                                        public void onqInsertFailed() {
                                            // 处理插入失败
                                        }

                                        @Override
                                        public void onqDeleteResult(Boolean success) {
                                            if (success) {
                                                OSSClientManager.deleteFileFromOSS(context, Config.OSS_BUCKET_NAME, data.getUri());
                                                dataList.remove(position - 1); // 从数据列表中移除
                                                notifyItemRemoved(position); // 通知适配器更新
                                                notifyItemRangeChanged(position, dataList.size()); // 更新后续项的索引
                                            } else {
                                                // 处理删除失败的情况
                                            }
                                        }
                                    });
                                    databaseTask.deleteData(data.getIndex());
                                }
                            }).setNegativeButton("取消", null) // 关闭对话框
                            .show(); // 显示对话框
                });

            }
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size() + 1; // Include header
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            // Optionally, you can initialize header views here
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView indexText;
        TextView detailButton, deleteButton; // 添加按钮引用

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            indexText = itemView.findViewById(R.id.index_text);
            detailButton = itemView.findViewById(R.id.detail_button); // 初始化按钮
            deleteButton = itemView.findViewById(R.id.delete_button); // 初始化按钮
        }
    }
}