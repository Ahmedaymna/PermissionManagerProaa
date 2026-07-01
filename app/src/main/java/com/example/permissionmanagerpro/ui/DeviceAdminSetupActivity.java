package com.example.permissionmanagerpro.ui;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.permissionmanagerpro.receiver.AppDeviceAdminReceiver;

/**
 * شاشة مستقلة (Java) لعرض حالة صلاحيات مدير الجهاز بالتفصيل.
 * تُستخدم كبديل أو شاشة توسعية لزر التفعيل الموجود في MainActivity.
 */
public class DeviceAdminSetupActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ENABLE_ADMIN = 100;

    private DevicePolicyManager devicePolicyManager;
    private ComponentName adminComponent;
    private TextView statusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        statusView = new TextView(this);
        statusView.setPadding(32, 32, 32, 32);
        statusView.setTextSize(16);
        setContentView(statusView);

        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        adminComponent = new ComponentName(this, AppDeviceAdminReceiver.class);

        refreshStatus();
        requestEnableIfNeeded();
    }

    private void requestEnableIfNeeded() {
        if (devicePolicyManager != null && !devicePolicyManager.isAdminActive(adminComponent)) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
            intent.putExtra(
                    DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "منح هذه الصلاحية يتيح إدارة إعدادات الجهاز الشخصي بشكل موسّع."
            );
            startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ENABLE_ADMIN) {
            refreshStatus();
            if (resultCode == Activity.RESULT_OK) {
                finish();
            }
        }
    }

    private void refreshStatus() {
        boolean isActive = devicePolicyManager != null
                && devicePolicyManager.isAdminActive(adminComponent);
        statusView.setText(isActive
                ? "صلاحيات مدير الجهاز مفعّلة حاليًا."
                : "صلاحيات مدير الجهاز غير مفعّلة.");
    }
}
