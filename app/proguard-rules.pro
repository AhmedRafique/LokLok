# ProGuard rules for Lok Lok

# LiveKit WebRTC rules
-keep class io.livekit.** { *; }
-keep class org.webrtc.** { *; }
-dontwarn org.webrtc.**

# Gson & Retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.loklok.app.data.remote.** { *; }
-keep class com.loklok.app.domain.model.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
