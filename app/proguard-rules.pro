# Keep kotlinx.serialization models
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.spikestats.app.**$$serializer { *; }
-keepclassmembers class com.spikestats.app.** {
    *** Companion;
}
-keepclasseswithmembers class com.spikestats.app.** {
    kotlinx.serialization.KSerializer serializer(...);
}
