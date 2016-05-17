package com.miris.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.miris.R;
import com.miris.net.SessionPreferences;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by Miris on 09.02.15.
 */
public class AccountOutActivity extends BaseActivity {
    private EditText et1; //현재비밀번호
    private EditText et2; //비밀번호확인
    private Button bt1; //탈퇴
    private Button bt2; //취소
    ProgressDialog myLoadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountout);
        session = new SessionPreferences(getApplicationContext());
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        et1 = (EditText)findViewById(R.id.present_passwd);
        et2 = (EditText)findViewById(R.id.confirm_passwd);

        bt1 = (Button)findViewById(R.id.bt_out);
        bt2 = (Button)findViewById(R.id.bt_cancel);

        bt1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View v) {
                if ("".equals(et1.getText().toString())) {
                    Toast.makeText(getApplication(), getString(R.string.login_pass), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!(et1.getText().toString()).equals(et2.getText().toString())) {
                    Toast.makeText(getApplication(), getString(R.string.pass_confirm_fail), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!(et1.getText().toString()).equals(memberData.get(0).getuser_password())) {
                    Toast.makeText(getApplication(), getString(R.string.pass_server_fail), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (et1.getText().toString().equals(memberData.get(0).getuser_password())) {
                    AlertDialog dlg = new AlertDialog.Builder(AccountOutActivity.this)
                            .setTitle("계정 삭제")
                            .setMessage("삭제한 계정은 복구 되지 않습니다.\n정말로 삭제 하겠습니까?")
                            .setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    if (whichButton == DialogInterface.BUTTON_NEGATIVE) {
                                        dialog.cancel();
                                    }
                                }
                            })
                            .setPositiveButton(getString(R.string.btn_confirm), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    hideSoftInputWindow(v);
                                    showDialog();
                                    new deleteAcountTask().execute();
                                }
                            })
                            .show();
                }
            }
        });

        bt2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    class deleteAcountTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... Void) {
            ParseQuery<ParseObject> commitListQuery = new ParseQuery<ParseObject>("miris_commit");
            commitListQuery.whereEqualTo("user_id", noticeData.get(0).getuserid());
            commitListQuery.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> module, ParseException e) {
                    if (e == null) {
                        for (ParseObject delete : module) {
                            delete.deleteInBackground();
                        }
                    }
                }
            });

            ParseQuery<ParseObject> mainListQuery = new ParseQuery<ParseObject>("miris_notice");
            mainListQuery.whereEqualTo("user_id", noticeData.get(0).getuserid());
            mainListQuery.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> module, ParseException e) {
                    if (e == null) {
                        for (final ParseObject noticeDelete : module) {

                            ParseQuery<ParseObject> commitListQuery = new ParseQuery<ParseObject>("miris_commit");
                            commitListQuery.findInBackground(new FindCallback<ParseObject>() {
                                public void done(List<ParseObject> module, ParseException e) {
                                    if (e == null) {
                                        for (ParseObject commitDelete : module) {
                                            if (noticeDelete.getObjectId().equals(commitDelete.getString("user_defulf_id"))) {
                                                commitDelete.deleteInBackground();
                                            }
                                        }
                                    }
                                }
                            });
                            noticeDelete.deleteInBackground();
                        }
                    }
                }
            });

            ParseQuery<ParseObject> offerQuery = ParseQuery.getQuery("miris_member");
            offerQuery.whereEqualTo("user_id",          memberData.get(0).getuserId());
            offerQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if (e == null) {
                        try {
                            parseObject.deleteInBackground();
                        } catch (Exception e1) {
                            Toast.makeText(getApplication(), getString(R.string.accountOut_fail), Toast.LENGTH_SHORT).show();
                            Log.d("회원탈퇴처리 오류", "[" + e1.toString() + "]");
                        }
                    }
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (myLoadingDialog != null) {
                myLoadingDialog.dismiss();
            }
            Toast.makeText(getApplication(), getString(R.string.accountOut_pass), Toast.LENGTH_SHORT).show();
            session.accountUserDelete();
            Intent intent = new Intent(getApplicationContext(), IntroActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);//IntroActivity.화면 이동
            finish();
        }
    }

    private void showDialog() {
        myLoadingDialog = new ProgressDialog(AccountOutActivity.this);
        myLoadingDialog.setMessage(getString(R.string.show_lodingbar));
        myLoadingDialog.setIndeterminate(false);
        myLoadingDialog.setCancelable(false);
        myLoadingDialog.show();
    }
}
