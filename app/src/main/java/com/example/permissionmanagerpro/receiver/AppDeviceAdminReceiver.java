package com.example.permissionmanagerpro.receiver;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * مستقبل صلاحيات مدير الجهاز (Device Admin).
 * يتطلب تفعيله موافقة صريحة من المستخدم عبر شاشة نظام أندرويد الرسمية
 * (لا يمكن تفعيله برمجيًا بدون تفاعل المستخدم).
 */
public class AppDeviceAdminReceiver extends DeviceAdminReceiver {

    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        Toast.makeText(context, "تم تفعيل صلاحيات مدير الجهاز", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
        Toast.makeText(context, "تم تعطيل صلاحيات مدير الجهاز", Toast.LENGTH_SHORT).show();
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return "هل أنت متأكد من رغبتك بتعطيل صلاحيات مدير الجهاز؟";
    }
}
