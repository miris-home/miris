package com.miris.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.miris.R;
import com.miris.net.MemberListData;
import com.miris.net.SessionPreferences;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by Miris on 09.02.15.
 */
public class SignInActivity extends BaseActivity {

    boolean showIntro = false;
    Button btn_main;
    EditText btn_id;
    EditText btn_pass;
    List<ParseObject> ob;
    ProgressDialog myLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        mirisBadge();
        session = new SessionPreferences(getApplicationContext());

        boolean isRunIntro = getIntent().getBooleanExtra("intro", true);
        if(isRunIntro) {
            if (!session.getIntro()) {
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

                } else {
                    intent = new Intent(SignInActivity.this, SignInActivity.class);
                }
                finish();
                intent.putExtra("intro", false);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out);
            }
        }, 2000);
    }

    private void afterIntro(Bundle savedInstanceState) {
        setContentView(R.layout.activity_signin);

        btn_main = (Button) findViewById(R.id.btn_main);
        btn_id = (EditText) findViewById(R.id.btn_id);
        btn_pass = (EditText) findViewById(R.id.btn_pass);
        btn_main.setOnClickListener(listener);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.btn_main:
                    if (btn_id.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), getString(R.string.login_id), Toast.LENGTH_SHORT).show();
                        return;

                    } else if (btn_pass.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), getString(R.string.login_pass), Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        hideSoftInputWindow(v);
                        new loadLoginTask().execute();
                        break;
                    }
            }
        }
    };

    @Override
    public void onDestroy() {
        if (myLoadingDialog != null && myLoadingDialog.isShowing()) {
            myLoadingDialog.dismiss();
            myLoadingDialog = null;
        }
        super.onDestroy();
    }

    class loadLoginTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
                myLoadingDialog = new ProgressDialog(SignInActivity.this);
                myLoadingDialog.setMessage(getString(R.string.show_lodingbar));
                myLoadingDialog.setIndeterminate(false);
                myLoadingDialog.setCancelable(false);
                myLoadingDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {
            ParseQuery<ParseObject> offerQuery = ParseQuery.getQuery("miris_member");
            offerQuery.whereEqualTo("user_id", btn_id.getText().toString());
            offerQuery.whereEqualTo("user_password", btn_pass.getText().toString());

            try {
                ob = offerQuery.find();
                memberData = new ArrayList<MemberListData>();

                for (ParseObject country : ob) {
                    ParseFile userFile = (ParseFile) country.get("user_img");
                    String userImgurl = null;
                    userImgurl = userFile.getUrl();
                    Bitmap bMap = null;
                    if (userFile != null) {
                        try {
                            byte[] data = userFile.getData();
                            bMap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        } catch (ParseException e2) {
                            e2.printStackTrace();
                        }
                    }

                    memberData.add(new MemberListData(
                            country.get("user_id").toString(),
                            country.get("user_password").toString(),
                            country.get("user_name").toString(),
                            country.get("user_age").toString(),
                            bMap,
                            userImgurl,
                            country.getInt("user_totallike"),
                            country.getInt("user_totalcommit"),
                            country.getInt("user_registernumber"),
                            country.get("user_rank").toString(),
                            country.get("user_email").toString()));
                }
            } catch (ParseException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
                return false;
            }
            return true ;

        }

        @Override
        protected void onPostExecute(Boolean isDone) {
            if (myLoadingDialog != null) {
                myLoadingDialog.dismiss();
            }
            if (isDone) {
                if (ob.size() == 0) {
                    Toast.makeText(getApplicationContext(), getString(R.string.login_fail), Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                finish();
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.login_fail), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
