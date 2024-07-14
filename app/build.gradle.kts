plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.alpharishi.cityweatherpro"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.alpharishi.cityweatherpro"
        minSdk = 24
        targetSdk = 34
        versionCode = 3
        versionName = "1.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.app.update.ktx)
    implementation(libs.play.services.ads) //ads
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
//    Lottie
    implementation (libs.lottie)
//    retrofit
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    // Optional (for proguard)
    implementation (libs.gson)

    implementation(libs.androidx.lifecycle.process)
}