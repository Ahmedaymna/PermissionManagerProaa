package com.example.permissionmanagerpro.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.example.permissionmanagerpro.model.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * أداة مساعدة (Java) لقراءة قائمة التطبيقات المثبتة وصلاحياتها
 * باستخدام PackageManager فقط - قراءة معلومات وليس تعديلها.
 */
public final class PermissionUtils {

    private PermissionUtils() {
        // Utility class - لا يجب إنشاء نسخة منه
    }

    /**
     * يرجع قائمة كاملة بالتطبيقات المثبتة مع صلاحياتها الممنوحة والمرفوضة.
     *
     * @param context سياق التطبيق
     * @param includeSystemApps هل يتم تضمين تطبيقات النظام
     */
    public static List<AppInfo> getInstalledAppsWithPermissions(Context context, boolean includeSystemApps) {
        List<AppInfo> result = new ArrayList<>();
        PackageManager pm = context.getPackageManager();

        List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        if (packages == null) {
            return result;
        }

        for (PackageInfo packageInfo : packages) {
            if (packageInfo == null || packageInfo.applicationInfo == null) {
                continue;
            }

            boolean isSystemApp = (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
            if (isSystemApp && !includeSystemApps) {
                continue;
            }

            String appName = String.valueOf(packageInfo.applicationInfo.loadLabel(pm));
            Drawable icon = safeLoadIcon(pm, packageInfo.applicationInfo);

            List<String> granted = new ArrayList<>();
            List<String> denied = new ArrayList<>();

            String[] requestedPermissions = packageInfo.requestedPermissions;
            int[] requestedFlags = packageInfo.requestedPermissionsFlags;

            if (requestedPermissions != null) {
                for (int i = 0; i < requestedPermissions.length; i++) {
                    boolean isGranted = requestedFlags != null
                            && i < requestedFlags.length
                            && (requestedFlags[i] & PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0;

                    if (isGranted) {
                        granted.add(requestedPermissions[i]);
                    } else {
                        denied.add(requestedPermissions[i]);
                    }
                }
            }

            result.add(new AppInfo(
                    packageInfo.packageName,
                    appName,
                    icon,
                    granted,
                    denied,
                    isSystemApp
            ));
        }

        return result;
    }

    private static Drawable safeLoadIcon(PackageManager pm, ApplicationInfo applicationInfo) {
        try {
            return applicationInfo.loadIcon(pm);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * يتحقق هل صلاحية معينة ممنوحة للتطبيق الحالي (تطبيقنا نفسه).
     */
    public static boolean isPermissionGrantedForSelf(Context context, String permission) {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }
}
