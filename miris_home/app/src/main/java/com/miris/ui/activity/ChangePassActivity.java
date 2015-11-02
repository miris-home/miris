package com.miris.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.miris.R;
import com.miris.net.SessionPreferences;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

/**
 * Created by Miris on 09.02.15.
 */
public class ChangePassActivity extends BaseActivity {
    private EditText et1; //새비밀번호
    private EditText et2; //비밀번호확인
    private Button bt1; //확인
    private Button bt2; //취소
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepass);
        session = new SessionPreferences(getApplicationContext());
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        et1 = (EditText)findViewById(R.id.new_passwd);
        et2 = (EditText)findViewById(R.id.confirm_passwd);

        bt1 = (Button)findViewById(R.id.bt_confirm);
        bt2 = (Button)findViewById(R.id.bt_cancel);

        bt1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if ("".equals(et1.getText().toString())) {
                    Toast.makeText(getApplication(), getString(R.string.login_pass), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!(et1.getText().toString()).equals(et2.getText().toString())) {
                    Toast.makeText(getApplication(), getString(R.string.pass_confirm_fail), Toast.LENGTH_SHORT).show();
                    return;
                }

                ParseQuery<ParseObject> offerQuery = ParseQuery.getQuery("miris_member");
                offerQuery.whereEqualTo("user_id", memberData.get(0).getuserId());
                offerQuery.whereEqualTo("user_password", memberData.get(0).getuser_password());
                offerQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        if (e == null) {
                            parseObject.put("user_password", et1.getText().toString());
                            parseObject.saveInBackground();
                            parseObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Toast.makeText(getApplication(), getString(R.string.changePass_pass), Toast.LENGTH_SHORT).show();
                                        memberData.get(0).setuser_password(et1.getText().toString());
                                        session.setUser_passwd(bt1.getText().toString()); //변경된 비밀번호 저장
                                        finish();
                                    } else {
                                        Toast.makeText(getApplication(), getString(R.string.changePass_fail), Toast.LENGTH_SHORT).show();
                                        Log.d("비밀번호 수정 오류", "[" + e.toString() + "]");
                                    }
                                }
                            });
                        }
                    }
                });

            }
        });

        bt2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }


        });
    }
}
