package com.miris.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.miris.R;
import com.miris.net.SessionPreferences;
import com.miris.ui.utils.CircleTransformation;
import com.miris.ui.view.FloatLabeledEditText;
import com.squareup.picasso.Picasso;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by Miris on 09.02.15.
 */
public class MyinfoActivity extends BaseActivity {
    @InjectView(R.id.ivUserProfilePhoto)
    ImageView ivUserProfilePhoto;
    @InjectView(R.id.user_id)
    EditText user_id;
    @InjectView(R.id.user_name)
    EditText user_name;
    @InjectView(R.id.user_email)
    EditText user_email;
    @InjectView(R.id.user_number)
    EditText user_number;
    @InjectView(R.id.user_age)
    EditText user_age;
    @InjectView(R.id.user_rank)
    EditText user_rank;
    @InjectView(R.id.btn_cancel)
    Button btn_cancel;
    @InjectView(R.id.btn_ok)
    Button btn_ok;

    @InjectView(R.id.user_id_hint)
    FloatLabeledEditText user_id_hint;
    @InjectView(R.id.user_name_hint)
    FloatLabeledEditText user_name_hint;
    @InjectView(R.id.user_email_hint)
    FloatLabeledEditText user_email_hint;
    @InjectView(R.id.user_number_hint)
    FloatLabeledEditText user_number_hint;
    @InjectView(R.id.user_age_hint)
    FloatLabeledEditText user_age_hint;
    @InjectView(R.id.user_rank_hint)
    FloatLabeledEditText user_rank_hint;

    Bitmap clsBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo);

        session = new SessionPreferences(getApplicationContext());
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        intView();

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

    private void intView(){
        Picasso.with(getApplicationContext())
                .load(memberData.get(0).getuserImgurl())
                .placeholder(R.drawable.img_circle_placeholder)
                .resize(getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size),
                        getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size))
                .centerCrop()
                .transform(new CircleTransformation())
                .into(ivUserProfilePhoto);
        user_id_hint.setHint("아이디 : " + memberData.get(0).getuserId() +" (*수정불가)");
        user_id.setFocusable(false);
        user_name_hint.setHint("이름 : " + memberData.get(0).getuser_name());
        user_email_hint.setHint("이메일 : " + memberData.get(0).getuser_email());
        user_number_hint.setHint("연락처 : " + memberData.get(0).getuser_phonenumber());
        user_age_hint.setHint("나이 : " + memberData.get(0).getuser_age());
        user_rank_hint.setHint("직급 : " + memberData.get(0).getuser_rank());
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

    @Optional
    @OnClick(R.id.btn_cancel)
    public void onbtn_cancelClick(final View v) {
        finish();
        overridePendingTransition(0, 0);
    }

    @Optional
    @OnClick(R.id.btn_ok)
    public void onibtn_okClick(final View v) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
