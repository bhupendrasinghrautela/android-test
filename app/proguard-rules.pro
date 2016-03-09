# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/vaibhav/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keep class com.crashlytics.** { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

-keepattributes *Annotation*
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}

-allowobfuscation
-allowoptimization
-allowshrinking
-optimizationpasses 3
-forceprocessing
-microedition
-dontobfuscate

-dontpreverify
-repackageclasses ''
-allowaccessmodification
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses

-dontwarn com.google.android.gms.**
-dontwarn com.android.support.**
-dontwarn com.jakewharton.**
-dontwarn com.github.nkzawa.**
-dontwarn com.vincentbrison.openlibraries.android
-dontwarn org.apmem.tools.**
-dontwarn com.facebook.**
-dontwarn com.tusharchoudhary.**
-dontwarn org.apache.httpcomponents.**
-dontwarn com.squareup.**
-dontwarn com.pkmmte.view.**
-dontwarn com.segment.analytics.**
-dontwarn com.google.maps.**
-dontwarn com.github.paolorotolo.**
-dontwarn com.crashlytics.**


-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference





