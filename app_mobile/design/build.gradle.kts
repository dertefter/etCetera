import org.jetbrains.kotlin.gradle.dsl.JvmTarget

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.dertefter.design"
    compileSdk = 37

    defaultConfig {
        minSdk = 26
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


    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.palette)
    implementation(libs.haze.blur.materials)
    api(platform(libs.compose.bom.alpha))
    api(libs.compose.ui)
    api(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    api(libs.compose.material3)
    debugImplementation(libs.compose.ui.tooling)
    debugRuntimeOnly(libs.compose.ui.test.manifest)
    api(libs.material.kolor)
    implementation(libs.coil.compose)
    implementation(libs.kdroidfilter.composemediaplayer)
    api(libs.androidx.media3.exoplayer)
    api(libs.haze)
    implementation(libs.haze.blur)
    implementation(libs.zoomable)

}
