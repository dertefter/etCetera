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
    namespace = "com.dertefter.design"
    compileSdk { version = release(37) { minorApiLevel = 1 } }

    defaultConfig {
        minSdk = 29
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
    implementation(libs.appcompat)
    implementation(libs.androidx.palette)
    api(platform(libs.compose.bom.alpha))
    api(libs.compose.ui)
    api(libs.compose.ui.graphics)
    api(libs.compose.ui.tooling.preview)
    api(libs.compose.material3)
    debugApi(libs.compose.ui.tooling)
    debugRuntimeOnly(libs.compose.ui.test.manifest)
    api(libs.material.kolor)
    implementation(libs.kdroidfilter.composemediaplayer)
    api(libs.androidx.media3.exoplayer)
    api(libs.haze)
    api(libs.haze.blur)
    api(libs.haze.blur.materials)
    implementation(libs.zoomable)
    api(libs.coil.compose)
    runtimeOnly(libs.coil.gif)
    api(libs.coil.network.okhttp)
    api(platform(libs.jamal.wia.paginator.bom))
    implementation(libs.androidx.annotation.experimental)
    implementation(libs.androidx.animation.core)
    api(libs.androidx.animation)
    api(libs.androidx.foundation.layout)
    api(libs.androidx.foundation)
    implementation(libs.androidx.runtime.annotation)
    api(libs.androidx.runtime)
    implementation(libs.androidx.ui.geometry)
    api(libs.androidx.ui.text)
    api(libs.androidx.ui.unit)
    implementation(libs.androidx.core)
    api(libs.androidx.graphics.shapes)
    implementation(libs.androidx.media3.common)
    implementation(libs.androidx.media3.database)
    implementation(libs.androidx.media3.datasource)
    runtimeOnly(libs.androidx.transition)
    implementation(libs.coil.compose.core)
    implementation(libs.coil.core)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.annotations)
}
