package com.miris.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.miris.R;
import com.miris.ui.adapter.MessageAdapter;
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

        /* 답장 화면으로 이동 */
        Button rButton = (Button)findViewById(R.id.returnButton);
        rButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "준비중입니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplication(), MessageReplyActivity.class);
                intent.putExtra("msgSendId",msgSendId);
                startActivity(intent);
            };
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

                //new deleteTask().execute();
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

    class deleteTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {

            /* MIRIS_MESSAGE to_delete_yn를 Y로 업데이트 */
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
            return null;
        }

        /* onPostExecute() : 백그라운드 실행 완료 즉 스레드 작업이 끝났을때의 동작 구현(doInBackground의 결과값 사용) */
        @Override
        protected void onPostExecute(Void result) {
            Log.i("delete_onPostExecute","start");
            Intent intent = new Intent(getApplication(), MessageActivity.class);
            startActivity(intent);
        }

    }
}
