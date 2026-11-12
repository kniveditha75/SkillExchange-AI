plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.simats.skillexchange"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.simats.skillexchange"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "2.0"
        // Backend base URL — Updated to PC IP for mobile hotspot connection
        buildConfigField("String", "BASE_URL", "\"http://10.72.189.134:8000/\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.coil.compose)

    // Network — Retrofit + OkHttp + Gson
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)

    // Async
    implementation(libs.coroutines.android)

    // Local storage — JWT token + role
    implementation(libs.datastore.preferences)

    // ViewModel + Compose
    implementation(libs.viewmodel.compose)

    debugImplementation(libs.androidx.ui.tooling)
}
