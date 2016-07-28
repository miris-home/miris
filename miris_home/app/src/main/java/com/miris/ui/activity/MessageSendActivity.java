package com.miris.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.miris.net.MessageListData;
import com.miris.ui.adapter.MessageAdapter;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;


import com.miris.R;
import com.miris.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

/**
 * Created by miris on 2016-07-22.
 */
public class MessageSendActivity extends BaseActivity{

    @InjectView(R.id.editContent) EditText contentText;
    @InjectView(R.id.receiptId) TextView receiptId;
    @InjectView(R.id.receiptName) TextView receiptName;

    List<ParseObject> ob;
    List<ParseObject> targetId;

    ProgressDialog myLoadingDialog;

    String username = memberData.get(0).getuser_name();

    String[] memberId;
    String[] memberName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_send);

        Button button = (Button)findViewById(R.id.sendButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sendCheck()) {
                    new insertDataTask().execute();
                    Intent intent = new Intent(getApplication(), MessageActivity.class);
                    startActivity(intent);
                }
            }
        });

        new loadDataTask().execute();

        Button sbutton = (Button)findViewById(R.id.searchId);
        sbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(1);
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        final String[] items = new String[memberId.length];
        for(int i=0; i<memberId.length; i++){
            items[i] = memberId[i];
            Log.i("items",items[i]);
        }
        //ArrayList<memberInfo> items = member;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("memberId", items[which]);
                receiptId.setText(items[which]);
                Log.i("memberName", memberName[which]);
                receiptName.setText("("+memberName[which]+")");

            }
        });
        return builder.create();
    }
    @Override
    protected void onPrepareDialog(int id, Dialog dialog){
        super.onPrepareDialog(id, dialog);
    }

    private void showDialog() {
        myLoadingDialog = new ProgressDialog(MessageSendActivity.this);
        myLoadingDialog.setMessage(getString(R.string.show_lodingbar));
        myLoadingDialog.setIndeterminate(false);
        myLoadingDialog.setCancelable(false);
        myLoadingDialog.show();
    }

    /* 쪽지 보내기 */
    class insertDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            showDialog();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            ParseObject sendObject = new ParseObject("miris_message");
            sendObject.put("to_id", receiptId.getText().toString());
            sendObject.put("from_id", memberData.get(0).getuserId());
            sendObject.put("from_name", username);
            sendObject.put("content", contentText.getText().toString());
            sendObject.put("receipt_yn", "N");
            sendObject.put("from_delete_yn", "N");
            sendObject.put("to_delete_yn", "N");
            sendObject.put("sendtime", Utils.getCalendar());
            sendObject.saveInBackground();
            return null ;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (myLoadingDialog != null) {
                myLoadingDialog.dismiss();
                Toast.makeText(getApplicationContext(), "성공적으로 전송되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*받는 사람 정보 추출*/
    class loadDataTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            //member = new ArrayList<memberInfo>();
            try {
                /* MIRIS_MESSAGE 테이블 SELECT */
                ParseQuery<ParseObject> query = ParseQuery.getQuery("miris_member");
                targetId = query.find();
                memberId = new String[targetId.size()];
                memberName = new String[targetId.size()];
                int i=0;
                for(ParseObject object : targetId){
                    memberId[i] = object.get("user_id").toString();
                    memberName[i] = object.get("user_name").toString();
                    Log.i("memberId ",memberId[i]);
                    Log.i("memberName ",memberName[i]);
                    i++;
                }
            } catch (ParseException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null ;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i("onPostExecute", "start");
        }
    }

    public boolean sendCheck() {
        if (receiptId.getText().toString() == null || receiptId.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "받는사람을 선택하세요.", Toast.LENGTH_SHORT).show();
            return false;
        }else if (contentText.getText().toString() == null || contentText.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
