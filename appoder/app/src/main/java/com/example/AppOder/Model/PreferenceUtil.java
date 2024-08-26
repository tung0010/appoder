package com.example.AppOder.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceUtil {
    private static final String LOGIN_TYPE_KEY = "login_type";
    private static final String PREF_NAME = "User";

    public static void saveLoginType(Context context, String loginType) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LOGIN_TYPE_KEY, loginType);
        editor.apply();
    }

    public static String getLoginType(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(LOGIN_TYPE_KEY, "");
    }

//    public static void saveLoginType(Context context, String loginType) {
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString(LOGIN_TYPE_KEY, loginType);
//        editor.apply();
//    }
//
//    public static String getLoginType(Context context) {
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//        return sharedPreferences.getString(LOGIN_TYPE_KEY, "");
//    }

    public static void saveUserProfile(Context context, User userProfile) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("birthday", userProfile.getBirthday());
        editor.putString("gener", userProfile.getGender());
        editor.putString("phone", userProfile.getPhoneNumber());
        // Lưu các thông tin khác của hồ sơ vào đây
        editor.apply();
    }

    public static User getUserProfile(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String birthday = sharedPreferences.getString("birthday", "");
        String gener = sharedPreferences.getString("gener", "");
        String phone = sharedPreferences.getString("phone", "");
        // Khôi phục các thông tin khác của hồ sơ từ đây
        return new User(birthday,gener,phone);
    }
}
