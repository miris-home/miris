package com.miris.ui.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.miris.R;
import com.miris.Utils;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnCheckedChanged;

/**
 * Created by Miris on 09.02.15.
 */
public class PublishActivity extends BaseActivity {
    public static final String ARG_TAKEN_PHOTO_URI = "arg_taken_photo_uri";

    @InjectView(R.id.tbFollowers)
    ToggleButton tbFollowers;
    @InjectView(R.id.tbDirect)
    ToggleButton tbDirect;
    @InjectView(R.id.ivPhoto)
    ImageView ivPhoto;
    @InjectView(R.id.etDescription)
    EditText etDescription;
    @InjectView(R.id.etSwitch)
    Switch etSwitch;

    private boolean propagatingToggleState = false;
    private Uri photoUri;
    private int photoSize;
    ProgressDialog myLoadingDialog;
    Boolean SwitchCheck = false;
    String newWritingPublic = "Y";

    public static void openWithPhotoUri(Activity openingActivity, Uri photoUri) {
        Intent intent = new Intent(openingActivity, PublishActivity.class);
        if (photoUri == null) {
            intent.putExtra(ARG_TAKEN_PHOTO_URI, "");
        } else {
            intent.putExtra(ARG_TAKEN_PHOTO_URI, photoUri);
        }
        openingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_grey600_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        etSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SwitchCheck = true;
                } else {
                    SwitchCheck = false;
                }
            }
        });
        photoSize = getResources().getDimensionPixelSize(R.dimen.publish_photo_thumbnail_size);

        if (savedInstanceState == null) {
            photoUri = getIntent().getParcelableExtra(ARG_TAKEN_PHOTO_URI);
        } else {
            photoUri = savedInstanceState.getParcelable(ARG_TAKEN_PHOTO_URI);
        }
        updateStatusBarColor();

        if (photoUri == null) {
            ivPhoto.setVisibility(View.GONE);
        } else {
            ivPhoto.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    ivPhoto.getViewTreeObserver().removeOnPreDrawListener(this);
                    loadThumbnailPhoto();
                    return true;
                }
            });
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateStatusBarColor() {
        if (Utils.isAndroid5()) {
            getWindow().setStatusBarColor(0xff888888);
        }
    }

    private void loadThumbnailPhoto() {
        ivPhoto.setScaleX(0);
        ivPhoto.setScaleY(0);
        Picasso.with(this)
                .load(photoUri)
                .centerCrop()
                .resize(photoSize, photoSize)
                .into(ivPhoto, new Callback() {
                    @Override
                    public void onSuccess() {
                        ivPhoto.animate()
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_publish, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_publish) {
            if (photoUri == null && etDescription.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(), getString(R.string.publish_text_img_check), Toast.LENGTH_SHORT).show();
            } else {
                hideSoftInputWindow(findViewById(R.id.action_publish));
                new setDataTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        if (myLoadingDialog != null && myLoadingDialog.isShowing()) {
            myLoadingDialog.dismiss();
            myLoadingDialog = null;

        }
        super.onDestroy();
    }

    class setDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            myLoadingDialog = new ProgressDialog(PublishActivity.this);
            myLoadingDialog.setMessage(getString(R.string.show_lodingbar));
            myLoadingDialog.setIndeterminate(false);
            myLoadingDialog.setCancelable(false);
            myLoadingDialog.show();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            ParseFile file = null;
            ParseObject testObject = new ParseObject("miris_notice");
            if (photoUri == null) {
            } else {
                try {
                    file = new ParseFile("user_img.png", readBytes(photoUri));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                file.saveInBackground();
                testObject.put("user_img", file);
            }
            testObject.put("user_id", memberData.get(0).getuserId());
            testObject.put("user_name", memberData.get(0).getuser_name());
            testObject.put("user_text", etDescription.getText().toString());
            testObject.put("user_like", 0);
            testObject.put("creatdate", Utils.getCalendar());
            testObject.put("user_public", newWritingPublic);

            testObject.saveInBackground();
            testObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(com.parse.ParseException e) {
                    if (e == null) {
                        ParseQuery testObject = ParseQuery.getQuery("miris_member");
                        testObject.whereEqualTo("user_id", memberData.get(0).getuserId());
                        testObject.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> updateLikeList, ParseException e) {
                                if (e == null) {
                                    for (ParseObject nameObj : updateLikeList) {
                                        int userAddApp = memberData.get(0).getuser_registernumber() +1 ;
                                        nameObj.put("user_registernumber", userAddApp);
                                        nameObj.saveInBackground();

                                        memberData.get(0).setuser_registernumber(userAddApp);
                                    }
                                }
                            }
                        });
                        if (newWritingPublic.equals("Y")) {
                            ParsePush push = new ParsePush();
                            if (SwitchCheck) {
                                push.setMessage(getString(R.string.notice_push));
                            } else {
                                push.setMessage("false");
                            }
                            push.sendInBackground();
                        }
                        bringMainActivityToTop();
                    }
                }
            });
            return null ;
        }
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

    public int exifOrientationToDegrees(int exifOrientation)
    {
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

    private void bringMainActivityToTop() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (photoUri == null) {
            intent.setAction(MainActivity.ACTION_NO_IMG_ITEM);
        } else {
            intent.setAction(MainActivity.ACTION_SHOW_LOADING_ITEM);
        }
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_TAKEN_PHOTO_URI, photoUri);
    }

    @OnCheckedChanged(R.id.tbFollowers)
    public void onFollowersCheckedChange(boolean checked) {
        if (!propagatingToggleState) {
            propagatingToggleState = true;
            tbDirect.setChecked(!checked);
            newWritingPublic = "N";
            etSwitch.setChecked(false);
            etSwitch.setVisibility(View.INVISIBLE);
            propagatingToggleState = false;
        }
    }

    @OnCheckedChanged(R.id.tbDirect)
    public void onDirectCheckedChange(boolean checked) {
        if (!propagatingToggleState) {
            propagatingToggleState = true;
            tbFollowers.setChecked(!checked);
            newWritingPublic = "Y";
            etSwitch.setVisibility(View.VISIBLE);
            propagatingToggleState = false;
        }
    }
}
