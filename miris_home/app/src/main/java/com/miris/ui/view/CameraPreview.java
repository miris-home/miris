package com.miris.ui.view;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    String TAG = "CameraPreview";
    private SurfaceHolder mHolder;
    private Camera mCamera;

    public CameraPreview(Context context) {
        super(context);
        Log.i(TAG, "Preview class created");

        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated!");
        try {
			Camera.Parameters parameters = mCamera.getParameters();
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
                parameters.setFocusMode(parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            } else {
                parameters.setFocusMode(parameters.FOCUS_MODE_AUTO);
            }
			if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
				parameters.set("orientation", "portrait");
				mCamera.setDisplayOrientation(90);
                parameters.setRotation(90);
			} else {
				parameters.set("orientation", "landscape");
				mCamera.setDisplayOrientation(0);
				parameters.setRotation(0);
			}
			Log.e(null,"focus mode : "+parameters.getFocusMode());
			mCamera.setParameters(parameters);

            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "Surface Destroyed");
        if (mCamera != null) {
            Log.i(TAG,"Preview stopped");
        }
    }

    public void setCamera(Camera camera) {
        if((mCamera != null)&&(camera == null)){
            mCamera.stopPreview();
        }
        mCamera = camera;

    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        Log.i(TAG,"Surface Changed");
        if (mHolder.getSurface() == null){
            return;
        }
        try {
            mCamera.stopPreview();
        } catch (Exception e){
        }

        Camera.Parameters parameters = mCamera.getParameters();

        List<Camera.Size> cameraSize = parameters.getSupportedPreviewSizes();
        Camera.Size mPreviewSize = cameraSize.get(0);

        for (Camera.Size s : cameraSize) {
            if ((s.width * s.height) > (mPreviewSize.width * mPreviewSize.height)) {
                mPreviewSize = s;
            }
        }
        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        mCamera.setParameters(parameters);
        requestLayout();

        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
}
