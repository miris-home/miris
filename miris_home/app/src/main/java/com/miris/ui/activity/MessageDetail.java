package com.miris.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.miris.R;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseException;

import butterknife.InjectView;

/**
 * Created by miris on 2016-07-22.
 */
public class MessageDetail extends BaseActivity{
    @InjectView(R.id.sendId) TextView sendIdView;       // 보낸 아이디 뷰
    @InjectView(R.id.sendName) TextView sendNameView;   // 보낸 이름 뷰
    @InjectView(R.id.sendTime) TextView sendTimeView;   // 보낸 시간 뷰
    @InjectView(R.id.msg_content) TextView contentView; // 내용 뷰
    @InjectView(R.id.idTitile) TextView idTitile;

    String objectId = null;
    String flag = null;

    String msgContent = null;
    String msgSendId = null;
    String msgSendName = null;
    String msgSendTime = null;
    String userName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = this.getIntent();
        objectId = intent.getStringExtra("objectid");
        flag = intent.getStringExtra("flag");
        new messageTask().execute();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);

        if(flag != null) {
            idTitile.setText("받는 사람");
        } else {
            idTitile.setText("보낸 사람");
        }


        Button button = (Button)findViewById(R.id.deleteButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(1);

            }
        });

        Button rButton = (Button)findViewById(R.id.returnButton);
        rButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "준비중입니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builer = new AlertDialog.Builder(this);

        builer.setTitle("삭제");
        builer.setMessage("쪽지를 삭제하시겠습니까?");
        builer.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                ParseQuery<ParseObject> query = ParseQuery.getQuery("miris_message");
                query.getInBackground(objectId, new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, com.parse.ParseException e) {
                        if(e == null){
                            parseObject.put("to_delete_yn","Y");
                            parseObject.saveInBackground();
                        }
                    }
                });

                Intent intent = new Intent(getApplication(), MessageActivity.class);
                startActivity(intent);
            }
        });

        builer.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builer.create();
    }

    class messageTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {

            /* MIRIS_MESSAGE 테이블 SELECT */
            ParseQuery<ParseObject> query = ParseQuery.getQuery("miris_message");
            query.getInBackground(objectId, new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    msgContent = parseObject.getString("content");
                    msgSendId = parseObject.getString("from_id");
                    msgSendName = "("+parseObject.getString("from_name")+")";
                    msgSendTime = parseObject.getString("sendtime");
                    Log.i("msgContent", msgContent);
                    Log.i("msgSendId", msgSendId);
                    Log.i("msgSendId", msgSendTime);
                    sendIdView.setText(msgSendId);
                    sendNameView.setText(msgSendName);
                    sendTimeView.setText(msgSendTime);
                    contentView.setText(msgContent);
                }
            });
            return null;
        }

    }
}
