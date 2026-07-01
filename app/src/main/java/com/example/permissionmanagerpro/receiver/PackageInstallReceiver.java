package com.example.permissionmanagerpro.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.example.permissionmanagerpro.util.NotificationHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * يراقب تثبيت تطبيقات جديدة ويعرض إشعارًا إذا كان التطبيق
 * الجديد يطلب صلاحيات حساسة.
 */
public class PackageInstallReceiver extends BroadcastReceiver {

    private static final String[] DANGEROUS_PERMISSIONS = {
            "android.permission.CAMERA",
            "android.permission.RECORD_AUDIO",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.READ_CONTACTS",
            "android.permission.READ_SMS"
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getData() == null) {
            return;
        }

        if (!Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())) {
            return;
        }

        String packageName = intent.getData().getSchemeSpecificPart();
        if (packageName == null) {
            return;
        }

        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            String[] requestedPermissions = packageInfo.requestedPermissions;

            if (requestedPermissions == null) {
                return;
            }

            List<String> foundDangerous = new ArrayList<>();
            for (String permission : requestedPermissions) {
                for (String dangerous : DANGEROUS_PERMISSIONS) {
                    if (dangerous.equals(permission)) {
                        foundDangerous.add(simplifyPermissionName(permission));
                    }
                }
            }

            if (!foundDangerous.isEmpty()) {
                String appName = String.valueOf(
                        packageInfo.applicationInfo.loadLabel(pm)
                );
                NotificationHelper.showNewAppNotification(
                        context, appName, foundDangerous
                );
            }

        } catch (PackageManager.NameNotFoundException e) {
            // التطبيق أُزيل قبل معالجة الحدث - تجاهل بأمان
        }
    }

    private String simplifyPermissionName(String fullPermission) {
        int lastDot = fullPermission.lastIndexOf('.');
        return lastDot >= 0 ? fullPermission.substring(lastDot + 1) : fullPermission;
    }
}
