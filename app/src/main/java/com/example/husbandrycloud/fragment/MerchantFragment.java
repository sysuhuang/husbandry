package com.example.husbandrycloud.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import com.example.husbandrycloud.R;


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
        getChildFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
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