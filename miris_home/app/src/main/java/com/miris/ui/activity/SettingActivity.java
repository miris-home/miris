package com.miris.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.miris.R;
import com.miris.net.SessionPreferences;

import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by Miris on 09.02.15.
 */
public class SettingActivity extends BaseActivity {
    @InjectView(R.id.logswitch)
    Switch logswitch;
    @InjectView(R.id.pushswitch)
    Switch pushswitch;
    @InjectView(R.id.myInfo)
    Button myInfo;
    @InjectView(R.id.changePass)
    Button changePass;
    @InjectView(R.id.accountOut)
    Button accountOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        session = new SessionPreferences(getApplicationContext());
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (session.getAutoLogin()) {
            logswitch.setChecked(true);
        } else {
            logswitch.setChecked(false);
        }
        if (session.getPushAlert()) {
            pushswitch.setChecked(true);
        } else {
            pushswitch.setChecked(false);
        }
    }

    @Optional
    @OnCheckedChanged(R.id.logswitch)
    public void onlogswitchCheckedChanged(boolean checked) {
        if (checked) {
            session.setUser_id(memberData.get(0).getuserId());
            session.setUser_passwd(memberData.get(0).getuser_password());
            session.setAutoLogin(true);
            Toast.makeText(getApplication(), getString(R.string.autoLogin_on), Toast.LENGTH_SHORT).show();

        } else {
            session.setAutoLogin(false);
            session.setUser_id("");
            session.setUser_passwd("");
            Toast.makeText(getApplication(), getString(R.string.autoLogin_off), Toast.LENGTH_SHORT).show();
        }
    }

    @Optional
    @OnCheckedChanged(R.id.pushswitch)
    public void onpushswitchCheckedChanged(boolean checked) {
        if (checked) {
            session.setPushAlert(true);
            Toast.makeText(getApplication(), getString(R.string.pushAlert_on), Toast.LENGTH_SHORT).show();

        } else {
            session.setPushAlert(false);
            Toast.makeText(getApplication(), getString(R.string.pushAlert_off), Toast.LENGTH_SHORT).show();
        }
    }

    @Optional
    @OnClick(R.id.myInfo)
    public void onmyInfoClick(final View v) {
        Intent intent = new Intent(getApplicationContext(), MyinfoActivity.class);
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    @Optional
    @OnClick(R.id.changePass)
    public void onchangePassClick(final View v) {
        Intent intent = new Intent(getApplicationContext(), ChangePassActivity.class);
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    @Optional
    @OnClick(R.id.accountOut)
    public void onaccountOutClick(final View v) {
        Intent intent = new Intent(getApplicationContext(), AccountOutActivity.class);
        overridePendingTransition(0, 0);
        startActivity(intent);
    }
}
