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
    namespace = "com.dertefter.settings"
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


    api(project(":common:navigation"))

    implementation(project(":app_mobile:design"))

    implementation(platform(libs.compose.bom.alpha))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.material.kolor)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.lifecycle.viewmodel.compose)
    implementation(platform(libs.jamal.wia.paginator.bom))
    debugImplementation(libs.compose.ui.tooling)
    debugRuntimeOnly(libs.compose.ui.test.manifest)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)

}
