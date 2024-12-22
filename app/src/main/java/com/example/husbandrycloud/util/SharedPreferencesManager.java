package com.example.husbandrycloud.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {
    private static final String PREFS_NAME = "my_prefs";
    private static final String KEY_USER_ROLE = "user_role";
    private static final String KEY_USERNAME = "username"; // 添加用户名的键

    public static void setUserRole(Context context, String role) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_USER_ROLE, role);
        editor.apply();
    }

    public static String getUserRole(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getString(KEY_USER_ROLE, null);
    }

    // 新增存储用户名的方法
    public static void setUsername(Context context, String username) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    // 新增获取用户名的方法
    public static String getUsername(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getString(KEY_USERNAME, null);
    }
}