package com.example.husbandrycloud;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.husbandrycloud.fragment.UserFragment;
import com.example.husbandrycloud.fragment.MerchantFragment;
import com.example.husbandrycloud.fragment.EnterpriseFragment;
import com.example.husbandrycloud.util.SharedPreferencesManager;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 检查用户角色
        String userRole = SharedPreferencesManager.getUserRole(this);
        if (userRole == null) {
            // 如果角色未设置，跳转到角色选择界面
            Intent intent = new Intent(this, RoleSelectionActivity.class);
            startActivity(intent);
            finish();
        } else {
            // 根据角色加载不同的Fragment
            setContentView(R.layout.activity_main);
            Fragment fragment;
            switch (userRole) {
                case "user":
                    fragment = new UserFragment();
                    break;
                case "merchant":
                    fragment = new MerchantFragment();
                    break;
                case "enterprise":
                    fragment = new EnterpriseFragment();
                    break;
                default:
                    fragment = new UserFragment(); // 默认值
            }
            loadFragment(fragment);
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, fragment)
                .commit();
    }
}