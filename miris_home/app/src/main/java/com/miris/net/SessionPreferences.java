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

    public void setShortcut(int versionName){
        editor.putInt("version", versionName);
        editor.commit();
    }

    public int getShortcut(){
        return pref.getInt("version", 0);
    }
}