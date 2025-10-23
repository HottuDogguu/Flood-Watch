import java.util.Arrays

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services") version "4.4.3" apply false
}

android {



    namespace = "com.example.myapplication"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    android {

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

        dependencies {
            //For api handling
            implementation("com.squareup.retrofit2:retrofit:3.0.0")
            //for converting the response as json
            implementation("com.squareup.retrofit2:converter-gson:2.9.0")
            //for websocket
            implementation("org.java-websocket:Java-WebSocket:1.5.2")
            implementation("com.squareup.okhttp3:okhttp:4.9.0")
            implementation("androidx.datastore:datastore:1.1.7")
            // The Preferences DataStore library
            implementation("androidx.datastore:datastore-preferences:1.1.7")
            // For encryption
            implementation("androidx.security:security-crypto:1.1.0-alpha06")
            implementation(libs.material)
            implementation(libs.activity)
            implementation(libs.constraintlayout)
            testImplementation(libs.junit)
            androidTestImplementation(libs.ext.junit)
            androidTestImplementation(libs.espresso.core)
            implementation(platform("com.google.firebase:firebase-bom:34.3.0"))
            implementation("androidx.credentials:credentials:1.3.0")
            implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
        }
    }

}


dependencies {
    implementation(libs.core.ktx)
    implementation(libs.googleid)
    implementation(libs.play.services.auth)
    implementation(libs.fragment)
    implementation(libs.leanback)
    implementation(libs.glide)
}
