package com.miris.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.miris.R;
import com.miris.ui.utils.CircleTransformation;
import com.squareup.picasso.Picasso;

import butterknife.InjectView;

public class SignupActivity extends BaseActivity {
    @InjectView(R.id.ivUserProfilePhoto)
    ImageView ivUserProfilePhoto;

    private EditText main_edtxid, main_edtxps, main_edtxpsc, main_edtxphnum, main_edtxpst;
    private Button main_singupbtn, main_canclebtn;
    /*각 필드 유효성 검사 부탁드리며
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

        main_canclebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        main_singupbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                signup();
            }
        });

        Picasso.with(getApplicationContext())
                .load(R.drawable.signinimg)
                .placeholder(R.drawable.img_circle_placeholder)
                .resize(getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size),
                        getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size))
                .centerCrop()
                .transform(new CircleTransformation())
                .into(ivUserProfilePhoto);

        ivUserProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent clsIntent = new Intent(Intent.ACTION_PICK);
                clsIntent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                clsIntent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(clsIntent, 100);
            }
        });
    }

    protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            if(requestCode == 100) {
                Uri uri = data.getData();
                Picasso.with(getApplicationContext())
                        .load(uri)
                        .placeholder(R.drawable.img_circle_placeholder)
                        .resize(getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size),
                                getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size))
                        .centerCrop()
                        .transform(new CircleTransformation())
                        .into(ivUserProfilePhoto);
            }  else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
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