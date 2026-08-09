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
    namespace = "com.dertefter.post"
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

    api(project(":app_mobile:feat:comments"))

    implementation(platform(libs.compose.bom.alpha))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.lifecycle.viewmodel.compose)
    debugImplementation(libs.compose.ui.tooling)
    debugRuntimeOnly(libs.compose.ui.test.manifest)
    implementation(platform(libs.jamal.wia.paginator.bom))
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)

}
