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
        minSdk = 25
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
    implementation(libs.core.ktx)
    api(libs.metro.runtime)
    implementation(libs.datastore.preferences)
    api(libs.androidx.datastore.core)
    api(libs.room.runtime)
    ksp(libs.room.compiler)
    api(libs.retrofit)
    implementation(libs.converter.kotlinx.serialization)
    api(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.play.services.wearable)
    api(libs.kotlinx.serialization.json)
    api(platform(libs.jamal.wia.paginator.bom))
    api(libs.paginator.cursor)
    api(libs.androidx.datastore.preferences.core)
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.sqlite)
    implementation(libs.okio)
    debugImplementation(libs.paginator.core.android.debug)
    releaseImplementation(libs.paginator.core)
    debugApi(libs.paginator.cursor.android.debug)
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.serialization.core)
}
