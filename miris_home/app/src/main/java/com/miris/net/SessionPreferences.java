package com.miris.net;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionPreferences {
    SharedPreferences pref;
    Editor editor;
    Context _context;
    private int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "MIRIS";
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String IS_PUSH = "IsPush";
    private static final String IS_INTRO = "IsIntro";
    private static final String IS_USER_ID = "IsUserId";
    private static final String IS_USER_PASSWORD = "IsUserPassWord";

    @SuppressLint("CommitPrefEdits")
    public SessionPreferences(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setIntro(boolean Isintro){
        editor.putBoolean(IS_INTRO, Isintro);
        editor.commit();
    }

    public boolean getIntro(){
        return pref.getBoolean(IS_INTRO, false);
    }

    //아이디 담기
    public void setUser_id(String user_id) {
        editor.putString("session.user_id", user_id);
        editor.commit();
    }
    //비밀번호 담기
    public void setUser_passwd(String user_passwd) {
        editor.putString("session.user_passwd", user_passwd);
        editor.commit();
    }
    //자동로그인 정보 담기
    public void setAutoLogin(boolean isAutoLogin) {
        editor.putBoolean("session.autoLogin", isAutoLogin);
        editor.commit();
    }

    //푸시 정보 담기
    public void setPushAlert(boolean isPushAlert) {
        editor.putBoolean("session.pushAlert", isPushAlert);
        editor.commit();
    }

    //아이디 가져오기
    public String getUser_id() {
        return pref.getString("session.user_id", "");
    }
    //비밀번호 가져오기
    public String getUser_passwd() {
        return pref.getString("session.user_passwd", "");
    }
    //자동로그인정보 가져오기
    public boolean getAutoLogin() {
        return pref.getBoolean("session.autoLogin", false);
    }
    //푸시알림정보 가져오기
    public boolean getPushAlert() {
        return pref.getBoolean("session.pushAlert", false);
    }
    public void setShortcut(int versionName){
        editor.putInt("version", versionName);
        editor.commit();
    }

    public int getShortcut(){
        return pref.getInt("version", 0);
    }

    public void accountUserDelete(){
        editor.clear();
        editor.commit();
    }
}