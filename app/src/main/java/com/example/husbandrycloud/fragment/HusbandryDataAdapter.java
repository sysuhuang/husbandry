package com.example.husbandrycloud.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.husbandrycloud.R;


import java.io.Serializable;
import java.util.List;

public class HusbandryDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private List<HusbandryData> dataList;


    private Context context; // 添加上下文


    public HusbandryDataAdapter(List<HusbandryData> dataList, Context context) {
        this.dataList = dataList;
        this.context = context; // 初始化上下文
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

            itemHolder.deleteButton.setOnClickListener(v -> {
                // 处理删除按钮的点击事件
                // 例如，从列表中移除该项
                dataList.remove(position - 1); // 从数据列表中移除
                notifyItemRemoved(position); // 通知适配器更新
                notifyItemRangeChanged(position, dataList.size()); // 更新后续项的索引
            });
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