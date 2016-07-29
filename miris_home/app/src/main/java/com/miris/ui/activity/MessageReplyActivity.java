package com.miris.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.miris.R;
import com.miris.Utils;
import com.parse.ParseObject;

import butterknife.InjectView;

/**
 * Created by miris on 2016-07-29.
 */
public class MessageReplyActivity extends BaseActivity{

    @InjectView(R.id.editContent)
    EditText contentText;
    @InjectView(R.id.receiptId)
    TextView receiptId;

    String username = memberData.get(0).getuser_name();
    String targetId;

    ProgressDialog myLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_reply);

        Intent intent = this.getIntent();
        targetId = intent.getStringExtra("msgSendId");
        receiptId.setText(targetId);

        Button button = (Button)findViewById(R.id.sendButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sendCheck()) {
                    new insertDataTask().execute();
                    Intent intent = new Intent(getApplication(), MessageActivity.class);
                    startActivity(intent);
                }
            }
        });
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
            sendObject.put("to_id", targetId);
            sendObject.put("from_id", memberData.get(0).getuserId());
            sendObject.put("from_name", username);
            sendObject.put("content", contentText.getText().toString());
            sendObject.put("receipt_yn", "N");
            sendObject.put("from_delete_yn", "N");
            sendObject.put("to_delete_yn", "N");
            sendObject.put("sendtime", Utils.getCalendar());
            sendObject.saveInBackground();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (myLoadingDialog != null) {
                myLoadingDialog.dismiss();
                Toast.makeText(getApplicationContext(), "성공적으로 전송되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean sendCheck() {
        if (contentText.getText().toString() == null || contentText.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showDialog() {
        myLoadingDialog = new ProgressDialog(MessageReplyActivity.this);
        myLoadingDialog.setMessage(getString(R.string.show_lodingbar));
        myLoadingDialog.setIndeterminate(false);
        myLoadingDialog.setCancelable(false);
        myLoadingDialog.show();
    }
}
