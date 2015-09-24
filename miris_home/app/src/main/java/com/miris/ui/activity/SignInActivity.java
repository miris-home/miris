package com.miris.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.miris.R;
import com.miris.net.SessionPreferences;

/**
 * Created by Miris on 09.02.15.
 */
public class SignInActivity extends BaseActivity {

    boolean showIntro = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        session = new SessionPreferences(getApplicationContext());

        boolean isRunIntro = getIntent().getBooleanExtra("intro", true);
        if(isRunIntro) {
            if (!session.isLoggedIn()) {
                showIntro = true;
            }
            beforeIntro(showIntro);
        } else {
            afterIntro(savedInstanceState);
        }
    }

    private void beforeIntro(final boolean showIntro) {
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (showIntro) {
                    intent = new Intent(SignInActivity.this, IntroActivity.class);
                    finish();
                } else {
                    intent = new Intent(SignInActivity.this, SignInActivity.class);
                }
                intent.putExtra("intro", false);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out);
            }
        }, 2000);
    }

    private void afterIntro(Bundle savedInstanceState) {
        setContentView(R.layout.activity_signin);
    }
}
