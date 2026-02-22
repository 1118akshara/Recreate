plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}
android {
    namespace = "com.btech.recreate"

    compileSdk = 36

    defaultConfig {
        applicationId = "com.btech.recreate"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
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

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    /* -----------------------------
       AR / SCANNING (KEEP AS IS)
     ----------------------------- */
    implementation("com.google.ar:core:1.43.0")
    implementation("com.google.ar.sceneform:core:1.17.1")
    implementation("com.google.ar.sceneform.ux:sceneform-ux:1.17.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.airbnb.android:lottie:6.0.0")
    /* -----------------------------
       ANDROID BASICS
     ----------------------------- */
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    /* -----------------------------
       NETWORKING (AI BACKEND – LATER)
     ----------------------------- */
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    /* -----------------------------
       COROUTINES (ASYNC TASKS)
     ----------------------------- */
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    /* -----------------------------
       IMAGE LOADING (DALL·E – LATER)
     ----------------------------- */
    implementation("com.github.bumptech.glide:glide:4.16.0")

    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    /* -----------------------------
       LIFECYCLE (SAFE UI UPDATES)
     ----------------------------- */
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")

    /* -----------------------------
   FIREBASE
----------------------------- */
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")

}
