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
    namespace = "com.dertefter.followers"
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

    api(project(":common:data"))
    api(project(":common:navigation"))
    implementation(project(":app_wearable:design"))

    implementation(platform(libs.compose.bom.alpha))
    api(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    api(libs.compose.wear.material3)
    api(libs.compose.wear.foundation)
    implementation(libs.androidx.lifecycle.runtime.compose)
    runtimeOnly(libs.androidx.media3.exoplayer)
    api(libs.metro.runtime)
    api(libs.metrox.viewmodel)
    implementation(libs.metrox.viewmodel.compose)
    implementation(platform(libs.jamal.wia.paginator.bom))
    debugImplementation(libs.compose.ui.tooling)
    debugRuntimeOnly(libs.compose.ui.test.manifest)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    implementation(libs.androidx.animation.core)
    implementation(libs.androidx.animation)
    api(libs.androidx.foundation.layout)
    implementation(libs.androidx.foundation)
    api(libs.androidx.runtime)
    implementation(libs.androidx.ui.text)
    implementation(libs.androidx.ui.unit)
    implementation(libs.androidx.lifecycle.common)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    api(libs.androidx.lifecycle.viewmodel)
    debugApi(libs.paginator.core.android.debug)
    releaseApi(libs.paginator.core)
    debugApi(libs.paginator.cursor.android.debug)
    releaseApi(libs.paginator.cursor)
    api(libs.kotlinx.coroutines.core)
}
