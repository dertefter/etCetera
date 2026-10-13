import org.jetbrains.kotlin.gradle.dsl.JvmTarget

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}


plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.metro)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.dertefter.common.data"
    compileSdk { version = release(37) { minorApiLevel = 1 } }

    defaultConfig {
        minSdk = 29
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
    implementation(libs.androidx.annotation.experimental)
    implementation(libs.metro.runtime)
    implementation(libs.metrox.viewmodel)
    implementation(libs.datastore.preferences)
    implementation(libs.androidx.datastore.core)
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.retrofit)
    implementation(libs.converter.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.play.services.wearable)
    implementation(libs.kotlinx.serialization.json)
    api(platform(libs.jamal.wia.paginator.bom))
    api(libs.paginator.cursor)
}
