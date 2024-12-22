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

public class UserFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        TextView textView = view.findViewById(R.id.text_user);
        textView.setText("欢迎用户！您可以查询牲畜的成长过程和健康信息。");

        String username = SharedPreferencesManager.getUsername(getActivity());



        return view;
    }
}