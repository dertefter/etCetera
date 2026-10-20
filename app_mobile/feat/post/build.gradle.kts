import org.jetbrains.kotlin.gradle.dsl.JvmTarget

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.metro)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.dertefter.post"
    compileSdk { version = release(37) { minorApiLevel = 1 } }

    defaultConfig {
        minSdk = 25
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
    }
}

dependencies {

    api(project(":common:data"))
    api(project(":common:navigation"))
    api(project(":app_mobile:design"))
    api(project(":app_mobile:feat:comments"))

    api(libs.metro.runtime)
    api(libs.metrox.viewmodel)
    implementation(libs.metrox.viewmodel.compose)

    implementation(libs.androidx.animation.core)
    implementation(libs.androidx.foundation.layout)
    implementation(libs.androidx.foundation)
    implementation(libs.compose.material3)
    api(libs.androidx.runtime)
    implementation(libs.compose.ui.graphics)
    implementation(libs.androidx.ui.text)
    implementation(libs.compose.ui)
    implementation(libs.androidx.lifecycle.common)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    api(libs.androidx.lifecycle.viewmodel)
    implementation(libs.haze)
    debugApi(libs.paginator.core.android.debug)
    releaseApi(libs.paginator.core)
    debugApi(libs.paginator.cursor.android.debug)
    releaseApi(libs.paginator.cursor)
    api(libs.kotlinx.coroutines.core)
}
