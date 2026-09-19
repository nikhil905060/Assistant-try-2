# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep service classes
-keep class com.astute.ai.service.** { *; }

# Keep Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Keep coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Keep Voice Interaction classes
-keep class android.service.voice.** { *; }
-dontwarn android.service.voice.**

# Keep Accessibility classes
-keep class android.accessibilityservice.** { *; }
-dontwarn android.accessibilityservice.**

# Optimization
-dontoptimize
-dontobfuscate
