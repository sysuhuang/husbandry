package com.example.husbandrycloud;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.husbandrycloud.util.SharedPreferencesManager;



public class RoleSelectionActivity extends AppCompatActivity {

    private boolean hasStoragePermissions(Context context) {
        //版本判断，如果比android 13 就走正常的权限获取
        if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU){
            int readPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
            int writePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return readPermission == PackageManager.PERMISSION_GRANTED && writePermission == PackageManager.PERMISSION_GRANTED;
        }else{
            int audioPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_AUDIO);
            int imagePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES);
            int videoPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_VIDEO);
            return audioPermission == PackageManager.PERMISSION_GRANTED && imagePermission == PackageManager.PERMISSION_GRANTED && videoPermission == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestStoragePermissions(Context context) {
        String [] permissions;
        if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU){
            permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        }else{
            permissions = new String[]{Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_MEDIA_IMAGES,Manifest.permission.READ_MEDIA_VIDEO};
        }
        ActivityCompat.requestPermissions((Activity) context,
                permissions,
                1234);
    }


    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        if(!hasStoragePermissions(this)){
            requestStoragePermissions(this);
        }

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);



        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (validateCredentials(username, password)) {
                    navigateToRoleScreen(username);
                } else {
                    Toast.makeText(RoleSelectionActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateCredentials(String username, String password) {
        return password.equals("123456") && (
                (username.equals("farmer1") || username.equals("farmer2")) ||
                        (username.equals("merchant1") || username.equals("merchant2")) ||
                        (username.equals("enterprise1") || username.equals("enterprise2")));
    }

    private void navigateToRoleScreen(String username) {
        String userRole = getUserRoleFromUsername(username);

        // 将用户角色和用户名存储到 SharedPreferences
        SharedPreferencesManager.setUserRole(this, userRole);
        SharedPreferencesManager.setUsername(this, username); // 存储用户名

        // 启动 MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // 可选，结束当前活动
    }

    // 根据用户名返回对应的角色
    private String getUserRoleFromUsername(String username) {
        if (username.equals("farmer1") || username.equals("farmer2")) {
            return "user";
        } else if (username.equals("merchant1") || username.equals("merchant2")) {
            return "merchant";
        } else if (username.equals("enterprise1") || username.equals("enterprise2")) {
            return "enterprise";
        }
        return null; // 未知角色
    }
}