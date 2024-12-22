package com.example.husbandrycloud.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.husbandrycloud.R;
import com.example.husbandrycloud.util.DatabaseTask;


import java.util.ArrayList;
import java.util.List;

public class UploadedDataListFragment extends Fragment {

    private RecyclerView recyclerView;
    private HusbandryDataAdapter adapter;
    private List<HusbandryData> dataList;

    private OnBackPressedListener backPressedListener;

    public interface OnBackPressedListener {
        void onBackPressed();
    }

    public void setOnBackPressedListener(OnBackPressedListener listener) {
        this.backPressedListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_uploaded_data_list, container, false);
        Button backButton = view.findViewById(R.id.back_button);
        recyclerView = view.findViewById(R.id.data_list_view);
        dataList = new ArrayList<>();
        adapter = new HusbandryDataAdapter(dataList, getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        fetchDataFromDatabase(); // 调用数据库任务以获取数据

        backButton.setOnClickListener(v -> {
            if (backPressedListener != null) {
                backPressedListener.onBackPressed();
            }
        });

        return view;
    }

    private void fetchDataFromDatabase() {


        DatabaseTask databaseTask = new DatabaseTask(new DatabaseTask.ResultListener() {
            @Override
            public void onqQueryResult(List<HusbandryData> result) {
                if (result.isEmpty()) {
                    Toast.makeText(getContext(), "查询失败或无数据", Toast.LENGTH_SHORT).show();
                } else {
                    dataList.clear(); // 清空旧数据
                    for (HusbandryData item : result) {
                        dataList.add(item); // 添加每一项内容
                        Log.i("database", "数据: " + item.getIndex() + " " + item.getAge() + " " + item.getFeedType());
                    }
                    adapter.notifyDataSetChanged(); // 更新适配器以显示新数据
                }
            }

            @Override
            public void onqInsertResult(List<HusbandryData> result) {

            }

            @Override
            public void onqInsertFailed() {

            }
        });

        databaseTask.queryTask(); // 执行数据库任务
    }
}