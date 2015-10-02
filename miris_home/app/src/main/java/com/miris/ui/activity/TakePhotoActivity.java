package com.miris.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.miris.R;
import com.miris.ui.view.CameraPreview;
import com.miris.ui.view.RevealBackgroundView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.InjectView;
import butterknife.OnClick;

import static com.miris.Utils.isAndroid5;

/**
 * Created by Miris on 09.02.15.
 */
public class TakePhotoActivity extends BaseActivity implements RevealBackgroundView.OnStateChangeListener, View.OnTouchListener{
    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

    private static final Interpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final Interpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final int STATE_TAKE_PHOTO = 0;
    private static final int STATE_SETUP_PHOTO = 1;

    @InjectView(R.id.vRevealBackground)
    RevealBackgroundView vRevealBackground;
    @InjectView(R.id.vPhotoRoot)
    View vTakePhotoRoot;
    @InjectView(R.id.vShutter)
    View vShutter;
    @InjectView(R.id.ivTakenPhoto)
    ImageView ivTakenPhoto;
    @InjectView(R.id.vUpperPanel)
    ViewSwitcher vUpperPanel;
    @InjectView(R.id.vLowerPanel)
    ViewSwitcher vLowerPanel;
    @InjectView(R.id.camera_preview)
    FrameLayout camera_preview;
    @InjectView(R.id.rvFilters)
    RecyclerView rvFilters;
    @InjectView(R.id.btnTakePhoto)
    Button btnTakePhoto;
    @InjectView(R.id.btnVideoCam)
    ImageButton btnVideoCam;
    @InjectView(R.id.btnTakeGallery)
    ImageButton btnTakeGallery;
    @InjectView(R.id.btn_ic_close)
    ImageButton btn_ic_close;
    @InjectView(R.id.btnBack)
    ImageButton btnBack;
    @InjectView(R.id.touchListener)
    RadioButton touchListener;

    private boolean pendingIntro;
    private int currentState;

    private File photoPath;

    static String TAG = "CAMERA";
    private Context mContext 				= this;
    FrameLayout preview;
    private Camera mCamera;
    private CameraPreview mPreview;
    private boolean isPhotoTaken 			= false;
    private boolean isFocused 				= false;
    private boolean errorFound 				= false;
    private int cameraId 					= -1;

    public static String savedPath;
    Bitmap clsBitmap;
    Boolean viewVisible = false ;

    public static void startCameraFromLocation(int[] startingLocation, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, TakePhotoActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
        startingActivity.startActivity(intent);
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            isPhotoTaken = true;
            touchListener.setChecked(false);
            mCamera.startPreview();
            new ImageSaveTask().execute(data);
        }
    };

    private Camera.AutoFocusCallback mFocus = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success) {
                touchListener.setChecked(true);
                isFocused = true;
            } else
                touchListener.setChecked(false);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        updateStatusBarColor();
        updateState(STATE_TAKE_PHOTO);
        setupRevealBackground(savedInstanceState);
        //setupPhotoFilters(); plus334.park

        vUpperPanel.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                vUpperPanel.getViewTreeObserver().removeOnPreDrawListener(this);
                pendingIntro = true;
                vUpperPanel.setTranslationY(-vUpperPanel.getHeight());
                vLowerPanel.setTranslationY(vLowerPanel.getHeight());
                return true;
            }
        });
        mContext = this;
        touchListener.setChecked(false);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateStatusBarColor() {
        if (isAndroid5()) {
            getWindow().setStatusBarColor(0xff111111);
        }
    }

    private void setupRevealBackground(Bundle savedInstanceState) {
        vRevealBackground.setFillPaintColor(0xFF16181a);
        vRevealBackground.setOnStateChangeListener(this);
        if (savedInstanceState == null) {
            final int[] startingLocation = getIntent().getIntArrayExtra(ARG_REVEAL_START_LOCATION);
            vRevealBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    vRevealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                    vRevealBackground.startFromLocation(startingLocation);
                    return true;
                }
            });
        } else {
            vRevealBackground.setToFinishedFrame();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!viewVisible) {
            initLayout();
        }
    }

    private void initLayout() {
        if (checkCameraHardware(mContext)) {
            mCamera = getCameraInstance(0);

            preview = (FrameLayout) findViewById(R.id.camera_preview);
            mPreview = new CameraPreview(this);
            mPreview.setCamera(mCamera);

            preview.addView(mPreview);

            touchListener.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();

                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            mCamera.autoFocus(mFocus);
                            break;
                        case MotionEvent.ACTION_UP:
                                mCamera.takePicture(null, null, mPicture);
                            break;
                        case MotionEvent.ACTION_MOVE:
                            isFocused = false;
                            touchListener.setChecked(false);
                    }
                    return false;
                }
            });

        } else if(Camera.CameraInfo.CAMERA_FACING_FRONT > -1){
            try {
                cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                mCamera = Camera.open(cameraId);
            } catch (Exception e) {
                errorFound = true;
            }
            if (errorFound = true) {
                try {
                    mCamera = Camera.open(0);
                    cameraId = 0;
                } catch (Exception e) {
                    cameraId = -1;
                }
            }
        } else {
            Toast.makeText(mContext, "no camera on this device!",Toast.LENGTH_SHORT).show();
            finish();
        }
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        mPreview = new CameraPreview(this);
        mPreview.setCamera(mCamera);

        preview.addView(mPreview);

        touchListener.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mCamera.autoFocus(mFocus);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (isFocused)
                            mCamera.takePicture(null, null, mPicture);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        isFocused = false;
                        touchListener.setChecked(false);
                }
                return false;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!viewVisible) {
            releaseCameraAndPreview();
            preview.removeAllViews();
        }
    }

    @OnClick(R.id.btnTakePhoto)
    public void onTakePhotoClick() {
        mCamera.autoFocus(mFocus);
        if (isFocused) {
            btnTakePhoto.setEnabled(false);
            mCamera.takePicture(null, null, mPicture);
            animateShutter();
        }
    }

    @OnClick(R.id.btnTakeGallery)
    public void onbtnTakeGalleryClick() {
        viewVisible = true;
        Intent clsIntent = new Intent(Intent.ACTION_PICK);
        clsIntent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        clsIntent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(clsIntent, 100);
    }

    @OnClick(R.id.btnVideoCam)
    public void onbtnVideoCamClick() {
        // TODO: 2015-09-21
    }

    protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            if(requestCode == 100) {
                try {
                    Uri uri = data.getData();
                    String urlPath = getImageNameToUri(uri);
                    AssetFileDescriptor afd = getContentResolver().openAssetFileDescriptor(uri, "r");
                    BitmapFactory.Options opt = new BitmapFactory.Options();

                    opt.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(urlPath, opt);

                    if (opt.outHeight  > 2000 || opt.outWidth > 2000)  {
                        opt.inJustDecodeBounds = false;
                        opt.inSampleSize = 4;
                        clsBitmap = BitmapFactory.decodeFileDescriptor(afd.getFileDescriptor(), null, opt);
                    } else {
                        clsBitmap 	= MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    }
                    File file = new File(urlPath);

                    ExifInterface exif = new ExifInterface(urlPath);
                    int exifOrientation = exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int exifDegree = exifOrientationToDegrees(exifOrientation);
                    clsBitmap = rotate(clsBitmap, exifDegree);

                    photoPath = file;
                    showTakenPicture(clsBitmap);
                    viewVisible = true;

                } catch( Exception e ) {
                    Log.e("Picture", e.toString());
                }
            } else if (requestCode == 200) {
                // TODO: 2015-09-21
            }  else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
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

    public String getImageNameToUri(Uri data) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(data, proj, null, null, null);
        String imgPath = null;

        if (cursor != null && cursor.getCount() != 0){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            cursor.moveToFirst();

            imgPath = cursor.getString(column_index);
        }
        cursor.close();
        return imgPath;
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

    @OnClick(R.id.btn_ic_close)
    public void onbtn_ic_closeClick() {
        finish();
    }
    @OnClick(R.id.btnBack)
    public void onbtnBackClick() {
        viewVisible = false;
        if (currentState == STATE_SETUP_PHOTO) {
            btnTakePhoto.setEnabled(true);
            vUpperPanel.showNext();
            vLowerPanel.showNext();
            updateState(STATE_TAKE_PHOTO);
        } else {
            super.onBackPressed();
        }
    }

    @OnClick(R.id.btnAccept)
    public void onAcceptClick() {
        PublishActivity.openWithPhotoUri(this, Uri.fromFile(photoPath));
    }

    private void animateShutter() {
        vShutter.setVisibility(View.VISIBLE);
        vShutter.setAlpha(0.f);

        ObjectAnimator alphaInAnim = ObjectAnimator.ofFloat(vShutter, "alpha", 0f, 0.8f);
        alphaInAnim.setDuration(100);
        alphaInAnim.setStartDelay(100);
        alphaInAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

        ObjectAnimator alphaOutAnim = ObjectAnimator.ofFloat(vShutter, "alpha", 0.8f, 0f);
        alphaOutAnim.setDuration(200);
        alphaOutAnim.setInterpolator(DECELERATE_INTERPOLATOR);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(alphaInAnim, alphaOutAnim);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                vShutter.setVisibility(View.GONE);
            }
        });
        animatorSet.start();
    }

    @Override
    public void onStateChange(int state) {
        if (RevealBackgroundView.STATE_FINISHED == state) {
            vTakePhotoRoot.setVisibility(View.VISIBLE);
            if (pendingIntro) {
                startIntroAnimation();
            }
        } else {
            vTakePhotoRoot.setVisibility(View.INVISIBLE);
        }
    }

    private void startIntroAnimation() {
        vUpperPanel.animate().translationY(0).setDuration(400).setInterpolator(DECELERATE_INTERPOLATOR);
        vLowerPanel.animate().translationY(0).setDuration(400).setInterpolator(DECELERATE_INTERPOLATOR).start();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    private void showTakenPicture(Bitmap bitmap) {
        vUpperPanel.showNext();
        vLowerPanel.showNext();
        ivTakenPhoto.setImageBitmap(bitmap);
        updateState(STATE_SETUP_PHOTO);
    }

    @Override
    public void onBackPressed() {
        viewVisible = false;
        if (currentState == STATE_SETUP_PHOTO) {
            btnTakePhoto.setEnabled(true);
            vUpperPanel.showNext();
            vLowerPanel.showNext();
            updateState(STATE_TAKE_PHOTO);
        } else {
            super.onBackPressed();
        }
    }

    private void updateState(int state) {
        currentState = state;
        if (currentState == STATE_TAKE_PHOTO) {
            vUpperPanel.setInAnimation(this, R.anim.slide_in_from_right);
            vLowerPanel.setInAnimation(this, R.anim.slide_in_from_right);
            vUpperPanel.setOutAnimation(this, R.anim.slide_out_to_left);
            vLowerPanel.setOutAnimation(this, R.anim.slide_out_to_left);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ivTakenPhoto.setVisibility(View.GONE);
                }
            }, 400);
        } else if (currentState == STATE_SETUP_PHOTO) {
            vUpperPanel.setInAnimation(this, R.anim.slide_in_from_left);
            vLowerPanel.setInAnimation(this, R.anim.slide_in_from_left);
            vUpperPanel.setOutAnimation(this, R.anim.slide_out_to_right);
            vLowerPanel.setOutAnimation(this, R.anim.slide_out_to_right);
            ivTakenPhoto.setVisibility(View.VISIBLE);
        }
    }
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            Toast.makeText(mContext, "No camera found!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    public Camera getCameraInstance(int id) {
        Camera c = null;
        try {
            releaseCameraAndPreview();
            c = Camera.open(id);
            Log.i(TAG,"><>< Camera resource opened successfully ><><");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }
    private void releaseCameraAndPreview() {
        if(mPreview != null){
            Log.i(TAG,"preview camera released");
            mPreview = null;
        }
        if (mCamera != null) {
            Log.i(TAG,"Safely releasing camera!!");
            mCamera.release();
            mCamera = null;
        }
    }
    class ImageSaveTask extends AsyncTask<byte[], Void, Boolean> {
        
        @Override
        protected Boolean doInBackground(byte[]... data) {

            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                return false;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data[0]);
                fos.close();
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

                Uri contentUri = Uri.fromFile(pictureFile);
                mediaScanIntent.setData(contentUri);
                mContext.sendBroadcast(mediaScanIntent);

                photoPath = pictureFile;

                BitmapFactory.Options options = new BitmapFactory.Options();

                Bitmap bitmap = BitmapFactory.decodeByteArray(data[0], 0, data[0].length, options);
                clsBitmap = bitmap;

                ExifInterface exif = new ExifInterface(savedPath);
                int exifOrientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int exifDegree = exifOrientationToDegrees(exifOrientation);
                clsBitmap = rotate(clsBitmap, exifDegree);

                Log.d(TAG, "picture loading path : " + pictureFile.getPath()+"/Miris/");

            } catch (IOException e) {
                return false;
            }
            return true;
        }
        
        @Override
        protected void onPostExecute(Boolean isDone) {
            if (isDone) {
                showTakenPicture(clsBitmap);
                Toast.makeText(mContext, getString(R.string.save_toast), Toast.LENGTH_SHORT)
                        .show();
            }
        }

        private File getOutputMediaFile() {
            File mediaStorageDir = new File(
                    Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "Miris");
            
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d(TAG, "failed to create directory");
                    return null;
                }
            }

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                    .format(new Date());
            File mediaFile;
            
            savedPath = mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg";
            mediaFile = new File(savedPath);
            Log.i(TAG,"Saved at"+ Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
            return mediaFile;
        }
    }
}
