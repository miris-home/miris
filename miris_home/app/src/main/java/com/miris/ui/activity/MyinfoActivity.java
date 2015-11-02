package com.miris.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.miris.R;
import com.miris.net.SessionPreferences;
import com.miris.ui.utils.CircleTransformation;
import com.miris.ui.view.MaterialTextField;
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
    TextView user_id;
    @InjectView(R.id.user_name)
    TextView user_name;
    @InjectView(R.id.user_email)
    TextView user_email;
    @InjectView(R.id.user_age)
    TextView user_age;
    @InjectView(R.id.user_rank)
    TextView user_rank;
    @InjectView(R.id.btn_cancel)
    Button btn_cancel;
    @InjectView(R.id.btn_ok)
    Button btn_ok;


    @InjectView(R.id.user_id_hint)
    MaterialTextField user_id_hint;
    @InjectView(R.id.user_name_hint)
    MaterialTextField user_name_hint;
    @InjectView(R.id.user_email_hint)
    MaterialTextField user_email_hint;
    @InjectView(R.id.user_age_hint)
    MaterialTextField user_age_hint;
    @InjectView(R.id.user_rank_hint)
    MaterialTextField user_rank_hint;

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


        TextView tv1 = (TextView)user_id_hint.findViewById(R.id.mtf_label);
        tv1.setText("아이디 : " + memberData.get(0).getuserId()+"  (아이디 변경불가)");

        TextView tv2 = (TextView)user_name_hint.findViewById(R.id.mtf_label);
        tv2.setText("이름 : "+memberData.get(0).getuser_name());

        TextView tv3 = (TextView)user_email_hint.findViewById(R.id.mtf_label);
        tv3.setText("이메일 : "+memberData.get(0).getuser_email());

        TextView tv4 = (TextView)user_age_hint.findViewById(R.id.mtf_label);
        tv4.setText("나이 : "+memberData.get(0).getuser_age());

        TextView tv5 = (TextView)user_rank_hint.findViewById(R.id.mtf_label);
        tv5.setText("직급 : "+memberData.get(0).getuser_rank());

        user_id.setText(memberData.get(0).getuserId());
        user_id.setFocusable(false);
        user_name.setText(memberData.get(0).getuser_name());
        user_email.setText(memberData.get(0).getuser_email());
        user_age.setText(memberData.get(0).getuser_age());
        user_rank.setText(memberData.get(0).getuser_rank());
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
