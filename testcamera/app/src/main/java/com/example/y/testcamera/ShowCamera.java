package com.example.y.testcamera;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

public class ShowCamera extends SurfaceView implements SurfaceHolder.Callback{
    public static final String TAG = "MESSAGE";
    Camera mCamera;
    //private Camera mCamera;
    SurfaceHolder mHolder;


    private int mCameraId = 1;
    private static final int CAMERA_FONT = 0;
    private static final int CAMERA_BACK = 1;
    private Context mContext;


    /*
    public ShowCamera(Context context, Camera camera){
        super(context);
        this.mCamera = camera;
        mHolder = getHolder();
        mHolder.addCallback(this);
    }*/

    public ShowCamera(Context context) {
        this(context, null);
    }

    public ShowCamera(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShowCamera(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        mContext = context;
        mHolder = getHolder();
        mHolder.setKeepScreenOn(true);
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        getDefaultCameraId();
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        /*
        Camera.Parameters params = camera.getParameters();
        //cambiar la orientacion de la camara
        // ---
        List<Camera.Size> sizes = params.getSupportedPictureSizes();
        Camera.Size mSize = null;
        for(Camera.Size size : sizes){
            mSize = size;
        }
        //---2
        if(this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE){
            params.set("orientation","portrait");
            camera.setDisplayOrientation(90);
            params.setRotation(90);

        }else{
            params.set("orientation","landscape");
            camera.setDisplayOrientation(0);
            params.setRotation(0);
        }
        //---2
        params.setPictureSize(mSize.width, mSize.height);
        //---


        camera.setParameters(params);
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        }catch (IOException e){
            //e.printStackTrace();
            Log.e("ERROR",e.getMessage());
        }*/
    }




    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        //---2
        //camera.stopPreview();
        //camera.release();
        //---
        destroyCamera();
    }


    private void destroyCamera() {
        if (mCamera == null) return;
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    public void switchCamera() {
        if (mCameraId == CAMERA_BACK) {
            mCameraId = CAMERA_FONT;
        } else {
            mCameraId = CAMERA_BACK;
        }
        destroyCamera();

        mCamera = Camera.open(mCameraId);
        try {
            mCamera.setPreviewDisplay(mHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //camera.setPreviewCallback(this);
        mCamera.startPreview();
    }


    private void getDefaultCameraId(){
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++)
        {
            Camera.getCameraInfo(i, cameraInfo);
            Log.d(TAG, "getCameraInstance: camera facing=" + cameraInfo.facing + ",camera orientation=" + cameraInfo.orientation);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK)
            {
                mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                break;
            }
            else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
            {
                mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                break;
            }
        }
    }


}
