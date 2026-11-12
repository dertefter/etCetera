import org.jetbrains.kotlin.gradle.dsl.JvmTarget

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.metro)
}

android {
    namespace = "com.dertefter.ai"
    compileSdk { version = release(37) { minorApiLevel = 1 } }

    defaultConfig {
        minSdk = 27
    }

    buildFeatures {
        buildConfig = true
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

}

dependencies {
    api(libs.litertlm.android)
    implementation(libs.datastore.preferences)
    api(libs.metro.runtime)
    api(libs.kotlinx.coroutines.core)
    api(libs.androidx.datastore.core)
    api(libs.androidx.datastore.preferences.core)
}
