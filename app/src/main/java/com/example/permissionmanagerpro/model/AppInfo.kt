package com.example.permissionmanagerpro.model

import android.graphics.drawable.Drawable

/**
 * يمثل معلومات تطبيق مثبت مع صلاحياته.
 */
data class AppInfo(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val grantedPermissions: List<String>,
    val deniedPermissions: List<String>,
    val isSystemApp: Boolean
) {
    val totalPermissions: Int
        get() = grantedPermissions.size + deniedPermissions.size

    val hasDangerousGranted: Boolean
        get() = grantedPermissions.any { it in DANGEROUS_PERMISSIONS }

    companion object {
        val DANGEROUS_PERMISSIONS = setOf(
            "android.permission.CAMERA",
            "android.permission.RECORD_AUDIO",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.READ_CONTACTS",
            "android.permission.WRITE_CONTACTS",
            "android.permission.READ_SMS",
            "android.permission.SEND_SMS",
            "android.permission.READ_CALL_LOG",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_MEDIA_IMAGES",
            "android.permission.BODY_SENSORS"
        )
    }
}
