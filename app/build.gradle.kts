import java.util.Arrays

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
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
        defaultConfig {
            manifestPlaceholders.putAll(
                mapOf("BASE_URL" to "192.168.7.41")
            )
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

        dependencies {
            implementation("com.squareup.retrofit2:retrofit:3.0.0")
            implementation("com.squareup.retrofit2:converter-gson:2.9.0")

            implementation("androidx.datastore:datastore:1.1.7")
            // The Preferences DataStore library
            implementation("androidx.datastore:datastore-preferences:1.1.7")
            implementation(libs.material)
            implementation(libs.activity)
            implementation(libs.constraintlayout)
            testImplementation(libs.junit)
            androidTestImplementation(libs.ext.junit)
            androidTestImplementation(libs.espresso.core)

        }
    }
}
dependencies {
    implementation(libs.core.ktx)
}
