package com.example.husbandrycloud.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import com.example.husbandrycloud.R;
import com.example.husbandrycloud.util.SharedPreferencesManager;


import java.util.ArrayList;
import java.util.List;

public class MerchantFragment extends Fragment {


    private Uri imageUri;

    private Button uploadAnimalDataButton;
    private Button viewUploadedDataButton;

    private FrameLayout fragmentContainer;

    private List<HusbandryData> uploadedDataList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_merchant, container, false);

        uploadAnimalDataButton = view.findViewById(R.id.upload_animal_data_button);
        viewUploadedDataButton = view.findViewById(R.id.view_uploaded_data_button);
        fragmentContainer = view.findViewById(R.id.fragment_container);

        // 获取 Toolbar
        Toolbar toolbar = requireActivity().findViewById(R.id.main_toolbar);
        // 禁用默认标题
        if (toolbar != null) {
            ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // 创建并设置 TextView 或设置标题
        TextView toolbarTitle = new TextView(requireContext());
        toolbarTitle.setText("商家 "+ SharedPreferencesManager.getUsername(getActivity()));
        toolbarTitle.setTextColor(getResources().getColor(android.R.color.white)); // 设置文本颜色
        toolbarTitle.setTextSize(20); // 设置文本大小

        // 设置 TextView 居中显示
        Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(
                Toolbar.LayoutParams.WRAP_CONTENT,
                Toolbar.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER; // 居中
        toolbarTitle.setLayoutParams(layoutParams);

        // 将 TextView 添加到 Toolbar
        toolbar.addView(toolbarTitle);

        uploadAnimalDataButton.setOnClickListener(v -> {
            hideButtons();
            showUploadAnimalDataFragment();
        });

        viewUploadedDataButton.setOnClickListener(v -> {
            hideButtons();
            showUploadedDataListFragment();
        });

        return view;
    }

    private void hideButtons() {
        uploadAnimalDataButton.setVisibility(View.GONE);
        viewUploadedDataButton.setVisibility(View.GONE);
    }

    private void showButtons() {
        int backStackEntryCount = getChildFragmentManager().getBackStackEntryCount();
        Log.i("huangLog","backStackEntryCount: "+backStackEntryCount);
        if (backStackEntryCount > 0) {
            for (int i = 0; i < backStackEntryCount; i++) {
                getChildFragmentManager().popBackStack();
            }
        }


        uploadAnimalDataButton.setVisibility(View.VISIBLE);
        viewUploadedDataButton.setVisibility(View.VISIBLE);
    }



    private void showUploadAnimalDataFragment() {
        UploadAnimalDataFragment uploadFragment = new UploadAnimalDataFragment();
        uploadFragment.setOnDataUploadedListener(data -> {
            uploadedDataList.add(data);
            //Toast.makeText(getContext(), "数据上传成功", Toast.LENGTH_SHORT).show();
        });

        uploadFragment.setOnBackPressedListener(this::showButtons);
        getChildFragmentManager().beginTransaction()
                .replace(fragmentContainer.getId(), uploadFragment)
                .addToBackStack(null) // 添加到返回栈
                .commit();
    }

    private void showUploadedDataListFragment() {
        UploadedDataListFragment listFragment = new UploadedDataListFragment();
        listFragment.setOnBackPressedListener(this::showButtons);
        getChildFragmentManager().beginTransaction()
                .replace(fragmentContainer.getId(), listFragment)
                .addToBackStack(null) // 添加到返回栈
                .commit();
    }



}