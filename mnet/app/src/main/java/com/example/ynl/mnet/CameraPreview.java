package com.example.ynl.mnet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;



@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {
    public static final String TAG = "CameraPreview";

    private int mDisplayOrientation;
    private int mSurfaceViewWidth, mSurfaceViewHeight;
    Camera mCamera;
    //private Camera mCamera;
    SurfaceHolder mHolder;


    private int mCameraId = 1;
    private static final int CAMERA_FONT = 0;
    private static final int CAMERA_BACK = 1;
    private Context mContext;

    //public static String LAST_IMG_TAKEN = "/storage/emulated/0/DCIM/YNL/JPEG20180919_161627665273792.jpg";


    public CameraPreview(Context context) {
        this(context, null);
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        mContext = context;
        mHolder = getHolder();
        mHolder.setKeepScreenOn(true);
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        getDefaultCameraId();
    }

    public Camera getCameraInstance(){
        final Camera[] camera = new Camera[1];
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        Log.d(TAG, "getCameraInstance: " + Thread.currentThread().getName());
        HandlerThread handlerThread = new HandlerThread("CameraThread");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: " + Thread.currentThread().getName());
                camera[0] = Camera.open(mCameraId);
                countDownLatch.countDown();
            }
        });

        try{
            countDownLatch.await();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return camera[0];
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
        //this.switchCamera();
        //this.switchCamera();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(mCamera!=null){
            mCamera.startPreview();
        }
    }



    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        destroyCamera();
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        Log.d(TAG, "surfaceChanged: ");
        mSurfaceViewWidth = w;
        mSurfaceViewHeight = h;
        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }
        try {
            if (mCamera != null)
                mCamera.stopPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
        initCamera(w, h);
    }

    private void initCamera(int w, int h){
        try{
            // mCamera = getCameraInstance();
            mCamera = Camera.open(mCameraId);
            Camera.Parameters parameters = mCamera.getParameters();//Obtener la instancia de parámetros de la cámara
            List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();//Obtener todos los tamaños de cámara admitidos
            Camera.Size bestSize = getOptimalPreviewSize(sizeList, w, h);//Obtenga el tamaño de pantalla más adecuado
            //Camera.Size bestSize = getBestCameraResolution(parameters, w, h);
            //Camera.Size bestSize = getPreferredPreviewSize(parameters, 1280, 720);
            Log.d(TAG, "initCamera: surface width==" + w + ",height==" + h);

            parameters.setPreviewSize(bestSize.width, bestSize.height);
            //Log.d(TAG, "initCamera: best size width==" + bestSize.width + ",best size height==" + bestSize.height);

            //Establecer el tamaño de la imagen de salida de la foto
            parameters.setPictureSize(bestSize.width, bestSize.height);

            //Establecer la dirección de vista previa
            mCamera.setDisplayOrientation(90);
            //Después de la mejora
            //setCameraDisplayOrientation((Activity) mContext, mCameraId, mCamera);
            int rotationDegrees = getCameraDisplayOrientation((Activity) mContext, mCameraId);
            Log.e(TAG, "initCamera: rotation degrees=" + rotationDegrees);
            mCamera.setDisplayOrientation(rotationDegrees);
            //Establecer la dirección de la foto
            parameters.setRotation(rotationDegrees);

            parameters.setPictureFormat(ImageFormat.NV21);

            //Establecer enfoque automático
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

            mCamera.setFaceDetectionListener(new MyFaceDetectionListener());

            mCamera.setParameters(parameters);

            mCamera.setPreviewDisplay(mHolder);
            mCamera.setPreviewCallback( this);
            mCamera.startPreview();
        }
        catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    private void setCameraDisplayOrientation(Activity context, int cameraType, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraType, info);
        int rotation = context.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation){
            case Surface.ROTATION_0:
                degrees = 0;
                bringToFront();
            case Surface.ROTATION_90:
                degrees = 90;
                bringToFront();
            case Surface.ROTATION_180:
                degrees = 180;
                bringToFront();
            case Surface.ROTATION_270:
                degrees = 270;
                bringToFront();
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }


    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    private void destroyCamera() {
        if (mCamera == null) return;
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }


    private Camera.Size getPreferredPreviewSize(Camera.Parameters parameters, int width, int height) {
        Log.e(TAG, "getPreferredPreviewSize: surface width=" + width + ",surface height=" + height);
        List<Camera.Size> mapSizes = parameters.getSupportedPreviewSizes();
        List<Camera.Size> collectorSizes = new ArrayList<>();
        for (Camera.Size option : mapSizes) {
            if (width > height) {
                if (option.width > width &&
                        option.height > height) {
                    collectorSizes.add(option);
                }
            } else {
                if (option.width > height &&
                        option.height > width) {
                    collectorSizes.add(option);
                }
            }
        }
        if (collectorSizes.size() > 0) {
            return Collections.min(collectorSizes, new Comparator<Camera.Size>() {
                @Override
                public int compare(Camera.Size lhs, Camera.Size rhs) {
                    return Long.signum(lhs.width * lhs.height - rhs.width * rhs.height);
                }
            });
        }
        Log.e(TAG, "getPreferredPreviewSize: best width=" +
                mapSizes.get(0).width + ",height=" + mapSizes.get(0).height);
        return mapSizes.get(0);
    }



    public int getCameraDisplayOrientation(Activity activity, int cameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                mDisplayOrientation = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                mDisplayOrientation = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                mDisplayOrientation = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                mDisplayOrientation = 270;
                break;
        }

        int result;

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }


        return result;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Log.d(TAG, "onPreviewFrame: show water mark");
        try
        {
            Camera.Size size = camera.getParameters().getPreviewSize();
            //Log.e(TAG, "onPreviewFrame: preview width=" + size.width + ",height=" + size.height);
            YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
            if (yuvImage == null) return;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            yuvImage.compressToJpeg(new Rect(0, 0, size.width, size.height), 100, stream);
            Bitmap bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());

            if (mDataClickListener!=null)
            {
                mDataClickListener.onData(bitmap);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean checkHaveCameraHardWare(int cameraId) {
        boolean result = false;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK &&
                    cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                Log.e(TAG, "checkHaveCameraHardWare: sensor orientation=" + cameraInfo.orientation);
                result = true;
                Log.d(TAG, "checkHaveCameraHardWare: have camera back");
            } else if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT &&
                    cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = true;
                Log.d(TAG, "checkHaveCameraHardWare: have camera front" + cameraId);
                Log.e(TAG, "checkHaveCameraHardWare: sensor orientation=" + cameraInfo.orientation);

            }
        }
        return result;
    }

    public void switchCamera() {
        if (mCameraId==CAMERA_FONT) {
            mCameraId = CAMERA_BACK;
        } else {
            mCameraId = CAMERA_FONT;
        }
        destroyCamera();
        mCamera = Camera.open(mCameraId);
        try {
            mCamera.setPreviewDisplay(mHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //
        int rotationDegrees = getCameraDisplayOrientation((Activity) mContext, mCameraId);
        Log.e(TAG, "initCamera: rotation degrees=" + rotationDegrees);
        mCamera.setDisplayOrientation(rotationDegrees);
        //
        mCamera.setPreviewCallback(this);
        mCamera.startPreview();
    }


    private class MyFaceDetectionListener implements Camera.FaceDetectionListener {

        @Override
        public void onFaceDetection(Camera.Face[] faces, Camera camera) {
            if (faces.length > 0) {
                Log.d("FaceDetection", "face detected: " + faces.length +
                        " Face 1 Location X: " + faces[0].rect.centerX() +
                        "Y: " + faces[0].rect.centerY());
            }
        }
    }


    private OnDataClickListener mDataClickListener;

    public void setDataClickListener(OnDataClickListener mDataClickListener) {
        this.mDataClickListener = mDataClickListener;
    }

    public interface OnDataClickListener {
        void onData(Bitmap bitmap);
    }

    public void takePicture() {
        mCamera.takePicture(null, null, null, mPictureCallback);

    }

    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            //LAST_IMG_TAKEN = pictureFile.getAbsolutePath();

            if (pictureFile == null) {
                Log.d(TAG, "Error creating media file, check storage permissions: ");
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                mCamera.startPreview();
                String base64 = Base64.encodeToString(data, Base64.NO_WRAP);
                String result = null;//WebServiceUtil.updateStudentInfo("001", "abc", base64);

                Toast.makeText(mContext, "Guardado en::" + pictureFile.getAbsolutePath(), Toast.LENGTH_LONG).show();



                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                File f = new File(pictureFile.getAbsolutePath());
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                mContext.sendBroadcast(mediaScanIntent);



                Log.e(TAG, "guardado en:: "+ pictureFile.getAbsolutePath());

                Log.e(TAG, "guardado en2:: "+ pictureFile.getAbsolutePath());

            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };


    private File getOutputMediaFile(int mediaType){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //String timeStamp = "/DCIM/mycamera";

        String fileName = null;
        File storageDir = null;

        //
        String path = Environment.getExternalStorageDirectory().toString();
        path = path +"/DCIM/YNL/";
        //Log.e(TAG, "PATH:: "+path);

        if (mediaType == MEDIA_TYPE_IMAGE) {
            fileName = "JPEG" + timeStamp;
            //Log.e(TAG, "TIMESTAMP:: "+fileName);
            storageDir = new File(path);//mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }

        //Log.e(TAG, "PATH:: "+path);
        //Log.e(TAG, "FILENAME:: "+fileName);

        //Log.e(TAG, "STORAGE:: "+storageDir.toString());
        // Create the storage directory if it does not exist
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                Log.d(TAG, "failed to create directory");
                return null;
            }
        }

        File file = null;
        try {
            file = File.createTempFile( fileName, (mediaType == MEDIA_TYPE_IMAGE) ? ".jpg" : ".mp4", storageDir);
            Log.e(TAG, "getOutputMediaFile: absolutePath==" + file.getAbsolutePath());
            //actualizar nombre de la ultima imagen

            //Intent intent = new Intent(this,MainActivity.class);
            Intent intent = new Intent();
            //String MC_LAST_IMG_TAKEN = null;
            //intent.putExtra(MC_LAST_IMG_TAKEN, file.getAbsolutePath());

            //LAST_IMG_TAKEN = file.getAbsolutePath();
            Log.e(TAG, "EN CREACION DE ARCHIVO:: "+ file.getAbsolutePath());

        } catch (IOException e){
            e.printStackTrace();
        }

        return file;
    }


}
