package com.example.permissionmanagerpro.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.permissionmanagerpro.R;

import java.util.List;

/**
 * مساعد لإنشاء وعرض الإشعارات الخاصة بالتطبيق.
 */
public final class NotificationHelper {

    private static final String CHANNEL_ID = "new_app_permissions_channel";
    private static int notificationIdCounter = 1000;

    private NotificationHelper() {
    }

    public static void showNewAppNotification(Context context, String appName, List<String> permissions) {
        createChannelIfNeeded(context);

        String permissionsText = android.text.TextUtils.join("، ", permissions);
        String body = context.getString(
                R.string.notification_new_app_body, appName, permissionsText
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle(context.getString(R.string.notification_new_app_title))
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        try {
            manager.notify(notificationIdCounter++, builder.build());
        } catch (SecurityException e) {
            // المستخدم لم يمنح صلاحية POST_NOTIFICATIONS - تجاهل بأمان
        }
    }

    private static void createChannelIfNeeded(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
