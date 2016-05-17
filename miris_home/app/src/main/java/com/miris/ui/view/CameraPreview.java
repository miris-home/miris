package com.miris.ui.view;

import android.content.Context;
import android.hardware.Camera;
import android.os.Build;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
			Camera.Parameters parameters = mCamera.getParameters();
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
                parameters.setFocusMode(parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            } else {
                parameters.setFocusMode(parameters.FOCUS_MODE_AUTO);
            }
            setDispaly(parameters, mCamera);
			mCamera.setParameters(parameters);

            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (IOException e) {
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
    }

    public void refreshCamera(Camera camera) {
        if (mHolder.getSurface() == null){
            return;
        }
        try {
            mCamera.stopPreview();
        } catch (Exception e){
        }
        setCamera(camera);
        Camera.Parameters parameters = mCamera.getParameters();
        if (parameters.getFocusMode().equals("auto")) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
                parameters.setFocusMode(parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
        }

        List<Camera.Size> cameraSize = parameters.getSupportedPreviewSizes();
        Camera.Size mPreviewSize = cameraSize.get(0);

        for (Camera.Size s : cameraSize) {
            if ((s.width * s.height) > (mPreviewSize.width * mPreviewSize.height)) {
                mPreviewSize = s;
            }
        }
        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);

        setDispaly(parameters, mCamera);
        mCamera.setParameters(parameters);

        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        refreshCamera(mCamera);
    }

    private void setDispaly(Camera.Parameters parameters, Camera camera) {
        if (Build.VERSION.SDK_INT >= 8) {
            setDisplayOrientation(camera, 90);
        } else {
            parameters.setRotation(90);
        }
    }

    private void setDisplayOrientation(Camera camera, int i) {
        Method downPolymorphic;
        try {
            downPolymorphic = camera.getClass().getMethod("setDisplayOrientation",
                    new Class[]{int.class});
            if (downPolymorphic != null) {
                downPolymorphic.invoke(camera, new Object[]{i});
            }
        } catch (Exception e) {
        }
    }
}