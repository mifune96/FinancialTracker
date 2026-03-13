# Add project specific ProGuard rules here.

# ── Room ──────────────────────────────────────────────────────
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keepclassmembers @androidx.room.Entity class * { *; }

# ── Hilt ──────────────────────────────────────────────────────
-keepnames @dagger.hilt.android.lifecycle.HiltViewModel class *
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# ── Kotlin Serialization (if added later) ─────────────────────
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# ── Keep data classes used in Room entities ───────────────────
-keepclassmembers class com.aliimran.financialtracker.data.local.entity.** {
    <init>(...);
    <fields>;
}

# ── BroadcastReceivers (notification & boot) ──────────────────
-keep class com.aliimran.financialtracker.notification.** { *; }
