package com.miris.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.miris.R;

public class SignupActivity extends AppCompatActivity {

    private EditText main_edtxid, main_edtxps, main_edtxpsc, main_edtxphnum, main_edtxpst;
    private Button main_singupbtn, main_canclebtn;
    /*아이디, 비밀번호, 비밀번호 확인, 이름, 이메일, 휴대폰번호, 직급, 이미지사진, 나이
      필드 추가를 해야 해서 빠진 부분 추가 부탁드립니다. 각 필드 유효성 검사 부탁드리며
      저장 DB는 https://www.parse.com/apps/--691/collections#class/miris_member 파서 디비에 있는
      각 필드 name 값 참조 부탁드립니다.
      ParseObject testObject = new ParseObject("miris_member");
        testObject.put("user_id", 저장할 아이디값);
        testObject.put("user_pass", 저장할 비밀번호값);
        testObject.put("user_name", 저장할 이름);

        등등등...
        testObject.saveInBackground();
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        init();

        main_canclebtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });

        main_singupbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                signup();
            }
        });

    }

    public void init(){
        main_edtxid=(EditText)findViewById(R.id.main_edtxid);
        main_edtxps=(EditText)findViewById(R.id.main_edtxps);
        main_edtxpsc=(EditText)findViewById(R.id.main_edtxpsc);
        main_edtxphnum=(EditText)findViewById(R.id.main_edtxphnum);
        main_edtxpst=(EditText)findViewById(R.id.main_edtxpst);
        main_singupbtn=(Button)findViewById(R.id.main_singupbtn);
        main_canclebtn=(Button)findViewById(R.id.main_cancleupbtn);
    }

    public void signup(){
        if(main_edtxid.getText().length()!=0){
            if(main_edtxps.getText().length()!=0 && main_edtxpsc.getText().length()!=0){
                passwordCheck();
            }else{
                Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
        }
        if(main_edtxphnum.getText().length()==0){
            Toast.makeText(this, "전화번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
        }
        if(main_edtxpst.getText().length()==0){
            Toast.makeText(this, "직위를 입력해주세요.", Toast.LENGTH_SHORT).show();
        }
    }
    public void passwordCheck(){
        if(main_edtxps.getText().toString().equals(main_edtxpsc.getText().toString())){
        }else{
            Toast.makeText(this,"비밀번호를 맞혀주세요",Toast.LENGTH_SHORT).show();
        }
    }
}