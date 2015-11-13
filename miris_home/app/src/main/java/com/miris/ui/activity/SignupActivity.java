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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.miris.R;
import com.miris.ui.utils.CircleTransformation;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import butterknife.InjectView;

public class SignupActivity extends BaseActivity {
    @InjectView(R.id.ivUserProfilePhoto)
    ImageView ivUserProfilePhoto;
    private Uri photoUri = null;

    private EditText main_edtxid, main_edtname, main_email, main_edtxps, main_edtxpsc,
            main_edtxphnum, main_edtage, main_edtxpst;
    private Button main_singupbtn, main_canclebtn;
    ProgressDialog myLoadingDialog;

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
        main_singupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signUpCheck()) {
                    showDialog();
                    new registrierenTask().execute();
                }
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
                photoUri = uri;
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
        main_edtname=(EditText)findViewById(R.id.main_edtname);
        main_email=(EditText)findViewById(R.id.main_email);
        main_edtxps=(EditText)findViewById(R.id.main_edtxps);
        main_edtxpsc=(EditText)findViewById(R.id.main_edtxpsc);
        main_edtxphnum=(EditText)findViewById(R.id.main_edtxphnum);
        main_edtage=(EditText)findViewById(R.id.main_edtage);
        main_edtxpst=(EditText)findViewById(R.id.main_edtxpst);

        main_singupbtn=(Button)findViewById(R.id.main_singupbtn);
        main_canclebtn=(Button)findViewById(R.id.main_cancleupbtn);
    }

    public boolean signUpCheck(){
        if (photoUri == null) {
            Log.e("PHJ", "photoUri");
            Toast.makeText(getApplicationContext(), "사진을 첨부해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (main_edtxid.getText().toString() == null || main_edtxid.getText().toString().equals("")) {
            Log.e("PHJ", "main_edtxid");
            Toast.makeText(getApplicationContext(), "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (main_edtname.getText().toString() == null || main_edtname.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (main_email.getText().toString() == null || main_email.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "이메일 주소를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (main_edtxps.getText().toString() == null || main_edtxps.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (main_edtxpsc.getText().toString() == null || main_edtxpsc.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "비밀번호 재 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!main_edtxps.getText().toString().equals(main_edtxpsc.getText().toString())) {
            Toast.makeText(getApplicationContext(), "비밀번호와 재입력한 비밀번호가 틀립니다.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (main_edtxphnum.getText().toString() == null || main_edtxphnum.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "연락처를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (main_edtage.getText().toString() == null || main_edtage.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "나이를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (main_edtxpst.getText().toString() == null || main_edtxpst.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "직급을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    class registrierenTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
            ParseFile file = null;
            ParseObject testObject = new ParseObject("miris_member");
            try {
                file = new ParseFile("user_img.png", readBytes(photoUri));
            } catch (IOException e) {
                e.printStackTrace();
            }

            testObject.put("user_img", file);
            testObject.put("user_id", main_edtxid.getText().toString());
            testObject.put("user_name", main_edtname.getText().toString());
            testObject.put("user_email", main_email.getText().toString());
            testObject.put("user_password", main_edtxps.getText().toString());
            testObject.put("user_phonenumber", main_edtxphnum.getText().toString());
            testObject.put("user_age", main_edtage.getText().toString());
            testObject.put("user_rank", main_edtxpst.getText().toString());
            testObject.put("user_totallike", 0);
            testObject.put("user_registernumber", 0);
            testObject.put("user_totalcommit", 0);

            testObject.saveInBackground();
            testObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(com.parse.ParseException e) {
                    if (e == null) {
                        if (myLoadingDialog != null) {
                            myLoadingDialog.dismiss();
                        }
                        Toast.makeText(getApplicationContext(), "회원가입을 축하합니다! 로그인 바랍니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });
            return null ;
        }
    }

    private void showDialog() {
        myLoadingDialog = new ProgressDialog(SignupActivity.this);
        myLoadingDialog.setMessage(getString(R.string.show_lodingbar));
        myLoadingDialog.setIndeterminate(false);
        myLoadingDialog.setCancelable(false);
        myLoadingDialog.show();
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
}