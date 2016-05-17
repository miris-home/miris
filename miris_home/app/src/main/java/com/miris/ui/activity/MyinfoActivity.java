package com.miris.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.miris.R;
import com.miris.net.MemberListData;
import com.miris.net.SessionPreferences;
import com.miris.ui.utils.CircleTransformation;
import com.miris.ui.utils.DisplayUtil;
import com.miris.ui.view.FloatLabeledEditText;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    private Uri photoUri = null;
    ProgressDialog myLoadingDialog;
    List<ParseObject> ob;

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

        try {
            intView();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

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

    private void intView() throws FileNotFoundException {
        if (memberData.get(0).getuserImgurl() == null) {
            Picasso.with(getApplicationContext())
                    .load(memberData.get(0).getinforimgfile())
                    .placeholder(R.drawable.img_circle_placeholder)
                    .resize(getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size),
                            getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size))
                    .centerCrop()
                    .transform(new CircleTransformation())
                    .into(ivUserProfilePhoto, new Callback() {
                        @Override
                        public void onSuccess() {
                            ivUserProfilePhoto.animate()
                                    .scaleX(1.f).scaleY(1.f)
                                    .setInterpolator(new OvershootInterpolator())
                                    .setDuration(400)
                                    .setStartDelay(200)
                                    .start();
                        }

                        @Override
                        public void onError() {
                        }
                    });
        } else {
            Picasso.with(getApplicationContext())
                    .load(memberData.get(0).getuserImgurl())
                    .placeholder(R.drawable.img_circle_placeholder)
                    .resize(getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size),
                            getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size))
                    .centerCrop()
                    .transform(new CircleTransformation())
                    .into(ivUserProfilePhoto, new Callback() {
                        @Override
                        public void onSuccess() {
                            ivUserProfilePhoto.animate()
                                    .scaleX(1.f).scaleY(1.f)
                                    .setInterpolator(new OvershootInterpolator())
                                    .setDuration(400)
                                    .setStartDelay(200)
                                    .start();
                        }

                        @Override
                        public void onError() {
                        }
                    });
        }

        user_id_hint.setHint("아이디 : " + memberData.get(0).getuserId() + " (*수정불가)");
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
                photoUri = uri;
                Picasso.with(getApplicationContext())
                        .load(uri)
                        .placeholder(R.drawable.img_circle_placeholder)
                        .resize(getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size),
                                getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size))
                        .centerCrop()
                        .transform(new CircleTransformation())
                        .into(ivUserProfilePhoto, new Callback() {
                            @Override
                            public void onSuccess() {
                                ivUserProfilePhoto.animate()
                                        .scaleX(1.f).scaleY(1.f)
                                        .setInterpolator(new OvershootInterpolator())
                                        .setDuration(400)
                                        .setStartDelay(200)
                                        .start();
                            }

                            @Override
                            public void onError() {
                            }
                });
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

    private void showDialog() {
        myLoadingDialog = new ProgressDialog(MyinfoActivity.this);
        myLoadingDialog.setMessage(getString(R.string.show_lodingbar));
        myLoadingDialog.setIndeterminate(false);
        myLoadingDialog.setCancelable(false);
        myLoadingDialog.show();
    }

    @Optional
    @OnClick(R.id.btn_ok)
    public void onibtn_okClick(final View v) {
        boolean checkVal = checkFeild();
        if (checkVal) {
            showDialog();
            new modifyTask().execute();
        }
    }

    class modifyTask extends AsyncTask<Void, Void, Void> {

        final String[] input_user_name = {""};
        final String[] input_user_email = {""};
        final String[] input_user_number = {""};
        final String[] input_user_age = {""};
        final String[] input_user_rank = {""};

        @Override
        protected Void doInBackground(Void... arg0) {
            ParseQuery<ParseObject> offerQuery = ParseQuery.getQuery("miris_member");
            offerQuery.whereEqualTo("user_id", memberData.get(0).getuserId());
            offerQuery.whereEqualTo("user_password", memberData.get(0).getuser_password());
            offerQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if (e == null) {
                        if (photoUri != null) {
                            ParseFile file = null;
                            try {
                                file = new ParseFile("user_img.png", readBytes(photoUri));
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            parseObject.put("user_img", file);
                        }
                        input_user_name[0] = user_name.getText().toString();
                        if (input_user_name[0] == null || "".equals(input_user_name[0])) {
                            input_user_name[0] = memberData.get(0).getuser_name();
                        }
                        input_user_email[0] = user_email.getText().toString();
                        if (input_user_email[0] == null || "".equals(input_user_email[0])) {
                            input_user_email[0] = memberData.get(0).getuser_email();
                        }
                        input_user_number[0] = user_number.getText().toString();
                        if (input_user_number[0] == null || "".equals(input_user_number[0])) {
                            input_user_number[0] = memberData.get(0).getuser_phonenumber();
                        }
                        input_user_age[0] = user_age.getText().toString();
                        if (input_user_age[0] == null || "".equals(input_user_age[0])) {
                            input_user_age[0] = memberData.get(0).getuser_age();
                        }
                        input_user_rank[0] = user_rank.getText().toString();
                        if (input_user_rank[0] == null || "".equals(input_user_rank[0])) {
                            input_user_rank[0] = memberData.get(0).getuser_rank();
                        }

                        parseObject.put("user_name", input_user_name[0]);
                        parseObject.put("user_email", input_user_email[0]);
                        parseObject.put("user_phonenumber", input_user_number[0]);
                        parseObject.put("user_age", input_user_age[0]);
                        parseObject.put("user_rank", input_user_rank[0]);
                        parseObject.saveInBackground();
                        parseObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    ParseQuery<ParseObject> loginQuery = ParseQuery.getQuery("miris_member");
                                    loginQuery.whereEqualTo("user_id", memberData.get(0).getuserId());
                                    loginQuery.whereEqualTo("user_password", memberData.get(0).getuser_password());
                                    loginQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                                        @Override
                                        public void done(final ParseObject mamberparseObject, ParseException e) {
                                            memberData.clear();
                                            memberData = null;
                                            memberData = new ArrayList<MemberListData>();

                                            final ParseFile userFile = (ParseFile) mamberparseObject.get("user_img");
                                            userFile.getDataInBackground(new GetDataCallback() {
                                                public void done(byte[] data, ParseException e) {
                                                    if (e == null) {
                                                        memberData.add(new MemberListData(
                                                                mamberparseObject.get("user_id").toString(),
                                                                mamberparseObject.get("user_password").toString(),
                                                                mamberparseObject.get("user_name").toString(),
                                                                mamberparseObject.get("user_age").toString(),
                                                                BitmapFactory.decodeByteArray(data, 0, data.length),
                                                                userFile.getUrl(),
                                                                mamberparseObject.getInt("user_totallike"),
                                                                mamberparseObject.getInt("user_totalcommit"),
                                                                mamberparseObject.getInt("user_registernumber"),
                                                                mamberparseObject.get("user_rank").toString(),
                                                                mamberparseObject.get("user_email").toString(),
                                                                mamberparseObject.get("user_phonenumber").toString()));

                                                        try {
                                                            if (userFile.getUrl() == null) {
                                                                memberData.get(0).setinforimgfile(userFile.getFile());
                                                            }
                                                        } catch (ParseException e1) {
                                                            e1.printStackTrace();
                                                        }

                                                        if (myLoadingDialog != null) {
                                                            myLoadingDialog.dismiss();
                                                        }
                                                        Toast.makeText(getApplication(), getString(R.string.changeMyinfo_pass), Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    Toast.makeText(getApplication(), getString(R.string.changeMyinfo_fail), Toast.LENGTH_SHORT).show();
                                    Log.d("내정보수정 오류", "[" + e.toString() + "]");
                                }
                            }
                        });
                    }
                }
            });
            return null ;
        }
    }

    public byte[] readBytes(Uri uri) throws IOException {
        byte[] data = null;
        Bitmap bitmap;
        try {
            bitmap = imgUriPath(uri);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            data = baos.toByteArray();
            bitmap.recycle();
            System.gc();
            Runtime.getRuntime().gc();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

    public Bitmap imgUriPath(Uri uri) throws IOException {
        Bitmap bitmap;
        AssetFileDescriptor afd = getContentResolver().openAssetFileDescriptor(uri, "r");
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(uri.getPath(), opt);

        if (opt.outHeight  > 2000 || opt.outWidth > 2000)  {
            opt.inJustDecodeBounds = false;
            opt.inSampleSize = 4;
            bitmap = BitmapFactory.decodeFileDescriptor(afd.getFileDescriptor(), null, opt);
        } else if (opt.outHeight  > 1000 || opt.outWidth > 1000) {
            opt.inJustDecodeBounds = false;
            opt.inSampleSize = 2;
            bitmap = BitmapFactory.decodeFileDescriptor(afd.getFileDescriptor(), null, opt);
        } else {
            bitmap 	= MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        }
        ExifInterface exif = new ExifInterface(uri.getPath());
        int exifOrientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int exifDegree = exifOrientationToDegrees(exifOrientation);
        bitmap = rotate(bitmap, exifDegree);

        return bitmap;
    }

    public int exifOrientationToDegrees(int exifOrientation) {
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    public Bitmap rotate(Bitmap bitmap, int degrees) {
        if(degrees != 0 && bitmap != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);
            try {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if(bitmap != converted) {
                    bitmap.recycle();
                    bitmap = converted;
                }
            } catch(OutOfMemoryError ex) {
            }
        }
        return bitmap;
    }


    public boolean checkFeild() {
        boolean returnVal = true;
        int ckcnt = 0;
        String input_user_name = user_name.getText().toString();
        String input_user_email = user_email.getText().toString();
        String input_user_number = user_number.getText().toString();
        String input_user_age = user_age.getText().toString();
        String input_user_rank = user_rank.getText().toString();

        if (input_user_name == null || "".equals(input_user_name)) {
            ckcnt ++ ;
        }
        if (input_user_email == null || "".equals(input_user_email)) {
            input_user_email = memberData.get(0).getuser_email();
            ckcnt ++ ;
        }if (input_user_number == null || "".equals(input_user_number)) {
            input_user_number = memberData.get(0).getuser_phonenumber();
            ckcnt ++ ;
        }
        if (input_user_age == null || "".equals(input_user_age)) {
            ckcnt ++ ;
        }
        if (input_user_rank == null || "".equals(input_user_rank)) {
            ckcnt ++ ;
        }

        if (ckcnt == 5) {
            returnVal = false;
            Toast.makeText(getApplication(), getString(R.string.nonChange), Toast.LENGTH_SHORT).show();
        }
        if (input_user_email.contains("@") == false) { //이메일 형식 체크
            returnVal = false;
            Toast.makeText(getApplication(), getString(R.string.checkEmail), Toast.LENGTH_SHORT).show();
        }
        if (DisplayUtil.isCellPhone(input_user_number) == false) { //폰번호 형식 체크
            returnVal = false;
            Toast.makeText(getApplication(), getString(R.string.checkPhone), Toast.LENGTH_SHORT).show();
        }
        return returnVal;
    }
}
