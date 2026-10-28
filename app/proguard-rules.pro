# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in proguard-android-optimize.txt.

# Keep domain models and data classes to prevent issues with serialization/reflection
-keep class com.henrisusanto.asmaulhusnamemorypractice.model.** { *; }

# Keep annotations, signatures, and inner classes for reflection & coroutines
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Google Play Services & AdMob
-keep class com.google.android.gms.ads.** { *; }
-keep public class com.google.ads.** { *; }

# Google User Messaging Platform (UMP)
-keep class com.google.android.ump.** { *; }

# Kotlin Coroutines
-dontwarn kotlinx.coroutines.**
