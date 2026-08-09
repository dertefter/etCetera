import org.jetbrains.kotlin.gradle.dsl.JvmTarget

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.dertefter.banner_edit"
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

    api(project(":common:data"))
    api(project(":common:navigation"))

    api(project(":app_mobile:design"))

    implementation(platform(libs.compose.bom.alpha))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.lifecycle.runtime.compose)
    runtimeOnly(libs.androidx.media3.exoplayer)
    implementation(libs.activity.compose)
    implementation(libs.coil)
    implementation(libs.hilt.android)
    implementation(libs.drawbox.enhanced)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.lifecycle.viewmodel.compose)
    debugImplementation(libs.compose.ui.tooling)
    debugRuntimeOnly(libs.compose.ui.test.manifest)

}
