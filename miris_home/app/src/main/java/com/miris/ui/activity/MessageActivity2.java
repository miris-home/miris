package com.miris.ui.activity;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.miris.net.MessageListData;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import com.miris.R;
import com.miris.ui.adapter.MessageAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

/**
 * class : MessageActivity2(보낸 쪽지함)
 * Created by miris on 2016-07-25.
 */
public class MessageActivity2 extends BaseActivity implements MessageAdapter.OnMsgItemClickListener{
    @InjectView(R.id.msg_view) RecyclerView rvMessage;
    @InjectView(R.id.tileBox) TextView titleView;

    ArrayList<MessageListData> messageData;

    /* LinearLayoutManager : 리스트 항목들이 수평 또는 수직의 스크롤 가능한 리스트 레이아웃 */
    LinearLayoutManager linearLayoutManager;
    ProgressDialog myLoadingDialog;
    Context context;
    RecyclerView recyclerView;

    private MessageAdapter messageAdapter;


    String userid = memberData.get(0).getuserId();
    String titleName = null;
    String objectId = null;
    String flag = null;
    AlertDialog.Builder builder;

    List<ParseObject> ob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Intent intent = this.getIntent();
        flag = intent.getStringExtra("flag");

        if("1".equals(flag)){
            titleName = "내게쓴쪽지함";
        } else if("2".equals(flag)){
            titleName = "보낸쪽지함";
        }

        titleView.setText(titleName);
        Button button = (Button)findViewById(R.id.sendViewButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), MessageSendActivity.class);
                startActivity(intent);
            }
        });
        setupMessage();

        Button mButton = (Button)findViewById(R.id.msgBox);
        mButton.setText(titleName);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(1);

            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        final String [] items = {"받은쪽지함", "내게쓴쪽지함", "보낸쪽지함"};
        switch(id) {
            case 1:
                builder = new AlertDialog.Builder(this);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("msgMenu", items[which]);
                        Intent mIntent = null;
                        switch (which) {
                        /* 받은쪽지함*/
                            case 0:
                                mIntent = new Intent(getApplication(), MessageActivity.class);
                                mIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(mIntent);
                                break;
                        /* 내게쓴쪽지함*/
                            case 1:
                                if ("2".equals(flag)) {
                                    mIntent = new Intent(getApplication(), MessageActivity2.class);
                                    mIntent.putExtra("flag", "1");
                                    startActivity(mIntent);
                                }
                                break;
                        /* 보낸 쪽지함*/
                            case 2:
                                if ("1".equals(flag)) {
                                    mIntent = new Intent(getApplication(), MessageActivity2.class);
                                    mIntent.putExtra("flag", "2");
                                    startActivity(mIntent);
                                }
                                break;
                        }
                    }

                });
                break;
            case 2:
                builder = new AlertDialog.Builder(this);
                builder.setTitle("삭제");
                builder.setMessage("쪽지를 삭제하시겠습니까?");
                Log.i("삭제할 objectid ", objectId);
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("miris_message");
                        query.getInBackground(objectId, new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject parseObject, com.parse.ParseException e) {
                                if (e == null) {
                                    parseObject.put("to_delete_yn", "Y");
                                    parseObject.saveInBackground();
                                }
                            }
                        });
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                break;
        }
        return builder.create();
    }

    /*
    private void init() {
        recyclerView = (RecyclerView) findViewById(R.id.msg_view);
        recyclerView.setHasFixedSize(true);
        //recyclerView.setAdapter(new MessageAdapter);
    }
    */

    private void setupMessage(){
        linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
        rvMessage.setLayoutManager(linearLayoutManager);
        new loadDataTask().execute();
    }

    /* 휴지통 버튼 누를시 메시지 삭제*/
    @Override
    public void onDeleteMsg(View v, int position) {
        objectId = messageData.get(position).getObjectId();
        showDialog(2);
    }

    /* 메시지 상세 보기 */
    @Override
    public void onDetailMsg(View v, int position) {
        Intent intent = new Intent(getApplication(), MessageDetail.class);
        intent.putExtra("objectid",messageData.get(position).getObjectId());
        intent.putExtra("flag",flag);
        startActivity(intent);
    }

    private void showDialog() {
        myLoadingDialog = new ProgressDialog(MessageActivity2.this);
        myLoadingDialog.setMessage(getString(R.string.show_lodingbar));
        myLoadingDialog.setIndeterminate(false);
        myLoadingDialog.setCancelable(false);
        myLoadingDialog.show();
    }

    /* AsyncTask 클래스 사용하여 스레드 처리 : 쪽지 리스트 추출*/
    class loadDataTask extends AsyncTask<Void, Void, Void>{

        /* onPreExecute() : 백그라운드 작업실행전 수행할 동작 구현 */
        @Override
        protected void onPreExecute() {
            showDialog();
        }
        /*doInBackground() : 메인 스레드의 execute 메소드에 의해서 작업스레드가 수행*/
        @Override
        protected Void doInBackground(Void... arg0) {
            messageData = new ArrayList<MessageListData>();

            try {
                /* MIRIS_MESSAGE 테이블 SELECT */
                ParseQuery<ParseObject> query = ParseQuery.getQuery("miris_message");
                if("1".equals(flag)){
                    query.whereEqualTo("from_id", userid);
                    query.whereEqualTo("to_id", userid);
                } else if("2".equals(flag)) {
                    query.whereEqualTo("from_id", userid);
                    query.whereNotEqualTo("to_id", userid);
                }
                query.whereEqualTo("to_delete_yn", "N");
                query.orderByDescending("sendtime");

                //ob.addAll(query.find()); // 읽어온 데이터를 list(ob)에 저장
                ob = query.find(); // 읽어온 데이터를 list(ob)에 저장

                int i=0;
                for(ParseObject object : ob){
                    messageData.add(new MessageListData(
                            object.get("to_id").toString(),
                            object.get("from_id").toString(),
                            object.get("from_name").toString(),
                            object.get("content").toString(),
                            object.get("receipt_yn").toString(),
                            object.get("to_delete_yn").toString(),
                            object.get("from_delete_yn").toString(),
                            object.get("sendtime").toString(),
                            object.getObjectId()));

                    Log.i("MessageData", messageData.get(i).getSendId() + "," + messageData.get(i).getReceiptId());
                }
            } catch (ParseException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            return null ;
        }

        /* onPostExecute() : 백그라운드 실행 완료 즉 스레드 작업이 끝났을때의 동작 구현(doInBackground의 결과값 사용) */
        @Override
        protected void onPostExecute(Void result) {
            Log.i("onPostExecute","start");
            messageAdapter = new MessageAdapter(MessageActivity2.this, messageData, flag);
            rvMessage.setAdapter(messageAdapter);
            messageAdapter.setOnMsgItemClickListener(MessageActivity2.this);

            /* setOnScrollListener : 리스트뷰 스크롤시에 발생하는 이벤트를 처리하는 리스너*/
            rvMessage.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                }
            });
            messageAdapter.updateItems(true);
            Log.i("onPostExecute", "end");
            if (myLoadingDialog != null) {
                myLoadingDialog.dismiss();
            }
        }
    }
}

