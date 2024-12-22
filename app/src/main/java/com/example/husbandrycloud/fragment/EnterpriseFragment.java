package com.example.husbandrycloud.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.husbandrycloud.R;
import com.example.husbandrycloud.util.OSSClientManager;
import com.example.husbandrycloud.util.SharedPreferencesManager;

public class EnterpriseFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enterprise, container, false);

        TextView textView = view.findViewById(R.id.text_enterprise);
        textView.setText("欢迎企业！您可以查看商家上传的数据和提供健康建议。");

        String username = SharedPreferencesManager.getUsername(getActivity());

        return view;
    }
}