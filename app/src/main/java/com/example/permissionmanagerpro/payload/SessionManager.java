package com.example.permissionmanagerpro.payload;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class SessionManager {
    private static final String PREF_NAME = "BotSession";
    private static final String KEY_DEVICES = "devices_set";
    private static final String KEY_ADMIN = "admin_chat_id";

    private SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void addDevice(String chatId) {
        Set<String> devices = prefs.getStringSet(KEY_DEVICES, new HashSet<>());
        devices.add(chatId);
        prefs.edit().putStringSet(KEY_DEVICES, devices).apply();
    }

    public Set<String> getDevices() {
        return prefs.getStringSet(KEY_DEVICES, new HashSet<>());
    }

    public void setAdminChatId(String chatId) {
        prefs.edit().putString(KEY_ADMIN, chatId).apply();
    }

    public String getAdminChatId() {
        return prefs.getString(KEY_ADMIN, "");
    }

    public boolean isDeviceRegistered(String chatId) {
        return getDevices().contains(chatId);
    }
}