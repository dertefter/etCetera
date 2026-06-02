import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.oss.licenses)
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
    jvmToolchain(17)
}

android {
    namespace = "com.dertefter.etcetera"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.dertefter.etcetera"
        minSdk = 29
        targetSdk = 37
        versionCode = 2
        versionName = "0.0.2-alpha"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("debug")
        }
    }
}

dependencies {

    implementation(project(":core:data"))
    implementation(project(":core:design"))
    implementation(project(":core:navigation"))

    implementation(project(":feat:auth"))
    implementation(project(":feat:feed"))
    implementation(project(":feat:comments"))
    implementation(project(":feat:user"))
    implementation(project(":feat:new_post"))
    implementation(project(":feat:followers"))
    implementation(project(":feat:notifications"))
    implementation(project(":feat:banner_edit"))
    implementation(project(":feat:crash_reports"))
    implementation(project(":feat:post"))

    implementation(libs.core.ktx)
    implementation(libs.core.splashscreen)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.appcompat)
    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.oss.licenses.droibit)
    implementation(libs.hilt.android)
    implementation(libs.play.services.wearable)
    ksp(libs.hilt.compiler)

    implementation(libs.hilt.navigation.compose)
    implementation(libs.navigation.compose)
    implementation(libs.androidx.compose.adaptive)
    implementation(libs.androidx.compose.adaptive.layout)
    implementation(libs.androidx.compose.adaptive.navigation)

    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.appwidget.preview)
    implementation(libs.androidx.glance.preview)
    implementation(libs.androidx.glance.material3)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.material.kolor)
    implementation(libs.haze)
    implementation(libs.haze.materials)
    implementation(libs.coil)
    implementation(libs.composefadingedges)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}
