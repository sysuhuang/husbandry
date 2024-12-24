package com.example.husbandrycloud.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.husbandrycloud.R;
import com.example.husbandrycloud.util.OSSClientManager;
import com.example.husbandrycloud.util.SharedPreferencesManager;

public class UserFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        // 获取 Toolbar
        Toolbar toolbar = requireActivity().findViewById(R.id.main_toolbar);
        // 禁用默认标题
        if (toolbar != null) {
            ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // 创建并设置 TextView 或设置标题
        TextView toolbarTitle = new TextView(requireContext());
        toolbarTitle.setText("用户 " + SharedPreferencesManager.getUsername(getActivity()));
        toolbarTitle.setTextColor(getResources().getColor(android.R.color.white)); // 设置文本颜色
        toolbarTitle.setTextSize(20); // 设置文本大小

        // 设置 TextView 居中显示
        Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER; // 居中
        toolbarTitle.setLayoutParams(layoutParams);

        // 将 TextView 添加到 Toolbar
        toolbar.addView(toolbarTitle);


        return view;
    }
}