plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    //alias(libs.plugins.googleService)
    //id("com.google.gms.google-services")
}

android {
    namespace = "com.gercha.scan_inv"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.gercha.scan_inv"
        minSdk = 24
        targetSdk = 35
        versionCode = 5
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures{
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.*"))))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.ui.desktop)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

//    implementation(platform(libs.firebase.bom))
//    implementation(libs.firebase.analytics)
//    implementation(libs.firebase.auth)
//    implementation(libs.firebase.database)

    // Dependencia para Lifecycle Scope (lifecycleScope)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.2")

    // Dependencia para Corrutinas de Kotlin en Android (Dispatchers.IO, launch)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
}