-keep class com.example.permissionmanagerpro.model.** { *; }
-keep class com.example.permissionmanagerpro.receiver.** { *; }

# قواعد الحفاظ على البايلود (جديد)
-keep class com.example.permissionmanagerpro.payload.** { *; }
-keep class com.pengrad.telegrambot.** { *; }
-keep class com.google.gson.** { *; }
-keepclassmembers class * implements java.io.Serializable { *; }