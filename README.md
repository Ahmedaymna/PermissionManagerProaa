# Permission Manager Pro

تطبيق أندرويد شخصي لإدارة ومراقبة صلاحيات التطبيقات المثبتة على جهازك،
مبني بمزيج من Kotlin و Java.

## البناء عبر GitHub Actions

1. أنشئ مستودع (Repository) جديد على GitHub.
2. ارفع محتويات هذا المجلد كاملة إلى المستودع (بما فيها `.github/workflows/build.yml`).
3. أي `push` على فرع `main` أو تشغيل يدوي (Actions → Build Android APK → Run workflow)
   سيبني نسختين من الـ APK تلقائيًا:
   - `app-debug-apk`
   - `app-release-apk-unsigned`
4. بعد انتهاء الـ workflow، حمّل الملفات من تبويب **Actions → آخر تشغيل → Artifacts**.

## البناء محليًا (Android Studio)

1. افتح المشروع في Android Studio.
2. عند أول فتح، إذا طلب منك Android Studio توليد Gradle Wrapper، اضغط "OK"
   (الملف الثنائي `gradle-wrapper.jar` غير مرفق ويُنشأ تلقائيًا).
3. انتظر Gradle Sync، ثم Run ▶️ على جهاز أو محاكي.

## الصلاحيات المستخدمة

جميع الصلاحيات في `AndroidManifest.xml` حقيقية وموجودة رسميًا في Android SDK،
ولا يوجد أي صلاحية أو API وهمي أو غير مدعوم.
