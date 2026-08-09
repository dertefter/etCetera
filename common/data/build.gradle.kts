import org.jetbrains.kotlin.gradle.dsl.JvmTarget

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}


plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.dertefter.common.data"
    compileSdk = 37

    defaultConfig {
        minSdk = 26
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    api(libs.hilt.android)
    implementation(libs.datastore.preferences)
    api(libs.androidx.datastore.core)
    ksp(libs.hilt.compiler)
    api(libs.room.runtime)
    ksp(libs.room.compiler)
    api(libs.retrofit)
    implementation(libs.converter.gson)
    api(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.play.services.wearable)
    api(libs.kotlinx.serialization.json)
    api(platform(libs.jamal.wia.paginator.bom))
    api(libs.paginator.cursor)
}
