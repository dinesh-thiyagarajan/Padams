plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.app.padams.ml"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    androidResources {
        noCompress += "tflite"
    }
}

dependencies {
    implementation(project(":database"))
    implementation(project(":domain"))

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.work)
    ksp(libs.hilt.work.compiler)

    implementation(libs.javax.inject)
    implementation(libs.androidx.core.ktx)
    implementation(libs.mlkit.face.detection)
    implementation(libs.tensorflow.lite)
    implementation(libs.tensorflow.lite.support)
    implementation(libs.work.runtime.ktx)
    implementation(libs.coroutines.android)
}
