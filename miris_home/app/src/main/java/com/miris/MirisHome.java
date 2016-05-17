package com.miris;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * Created by Miris on 09.02.15.
 */
public class MirisHome extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this); 
        Parse.initialize(this, "1tcvaqHWwpQowXoaeKMrNAFETTl2HRPCVJ5onpOZ", "SeQa0xGDWBge4AIOEIPfLPnU2zfFgdrVIL5OyUkQ");
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
