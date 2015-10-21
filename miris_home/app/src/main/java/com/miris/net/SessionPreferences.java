package com.miris.net;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.miris.ui.activity.SignInActivity;

public class SessionPreferences {
    SharedPreferences pref;
    Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "MIRIS";
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String IS_INTRO = "IsIntro";
    public static final String KEY_ID = "userid";
    public static final String KEY_USER_NAME = "user_name";

    @SuppressLint("CommitPrefEdits")
    public SessionPreferences(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(Context c) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putBoolean(IS_INTRO, true);
        editor.commit();
    }

    public void logoutUser(){
        editor.clear();
        editor.commit();
        Intent i = new Intent(_context, SignInActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    public boolean IsIntro(){
        return pref.getBoolean(IS_INTRO, false);
    }

    public void setIsIntro(boolean Isintro){
        editor.putBoolean(IS_LOGIN, Isintro);
        editor.commit();

    }

    public void setShortcut(int vsrsionName){
        editor.putInt("version", vsrsionName);
        editor.commit();
    }

    public int getShortcut(){
        return pref.getInt("version", 0);
    }
}