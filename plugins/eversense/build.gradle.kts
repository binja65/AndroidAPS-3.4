plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)

    id("kotlin-android")
    id("kotlinx-serialization")
    id("android-module-dependencies")
}

android {
    namespace = "com.nightscout.eversense"
}

dependencies {
    api(libs.androidx.core)
    api(libs.kotlinx.serialization.json)

    api(libs.org.slf4j.api)
    api(libs.com.github.tony19.logback.android)
}