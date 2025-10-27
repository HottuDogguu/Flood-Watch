import groovy.util.logging.Log
import java.util.Properties


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")

}
val secretsFile = rootProject.file("secrets.properties")
val secrets = Properties()
if (secretsFile.exists()) {

    secrets.load(secretsFile.inputStream()) }


val ACCESS_TOKEN_KEY: String = secrets.getProperty("ACCESS_TOKEN_KEY") ?: ""
val WEB_CLIENT_ID: String = secrets.getProperty("WEB_CLIENT_ID") ?: ""
val API_HTTP_BASE_URL: String = secrets.getProperty("API_HTTP_BASE_URL") ?: ""
val API_WEBSOCKET_BASE_URL: String = secrets.getProperty("API_WEBSOCKET_BASE_URL") ?: ""


android {

    namespace = "com.example.myapplication"
    compileSdk = 36

    buildFeatures{
        buildConfig = true
    }

    defaultConfig {

        applicationId = "com.example.myapplication"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "ACCESS_TOKEN_KEY", "\"${ACCESS_TOKEN_KEY }\"")
        buildConfigField("String", "WEB_CLIENT_ID", "\"${WEB_CLIENT_ID }\"")
        buildConfigField("String", "API_HTTP_BASE_URL", "\"${API_HTTP_BASE_URL }\"")
        buildConfigField("String", "API_WEBSOCKET_BASE_URL", "\"${API_WEBSOCKET_BASE_URL }\"")
    }


    kotlinOptions {
        jvmTarget = "11"
    }


    buildTypes {
       debug{
           buildConfigField("boolean", "IS_DEBUG", "true")
       }
        release {

            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    dependencies {
        //For api handling
        implementation("com.squareup.retrofit2:retrofit:2.9.0")  //  3.0.0 doesn't exist
        //for converting the response as json
        implementation("com.squareup.retrofit2:converter-gson:2.9.0")
        //for websocket
        implementation("org.java-websocket:Java-WebSocket:1.5.2")
        implementation("com.squareup.okhttp3:okhttp:4.9.0")

        //For Data Store
        implementation("androidx.datastore:datastore:1.1.7")
        implementation("androidx.datastore:datastore-preferences:1.1.7")
        implementation("androidx.datastore:datastore-rxjava3:1.1.7")
        implementation("androidx.datastore:datastore-preferences-rxjava3:1.1.7")

        // RxJava
        // WorkManager for background tasks
        implementation("androidx.work:work-runtime:2.9.0")
        implementation("androidx.work:work-rxjava3:2.9.0")
        implementation("io.reactivex.rxjava3:rxjava:3.1.8")
        implementation("io.reactivex.rxjava3:rxandroid:3.0.2")
        implementation(libs.material)
        implementation(libs.activity)
        implementation(libs.constraintlayout)
        testImplementation(libs.junit)
        androidTestImplementation(libs.ext.junit)
        androidTestImplementation(libs.espresso.core)

        // Firebase - Use a stable version
        implementation(platform("com.google.firebase:firebase-bom:32.7.0"))  // stable version
        implementation("com.google.firebase:firebase-messaging")
        implementation("com.google.firebase:firebase-analytics")

        implementation("com.google.android.gms:play-services-base:18.2.0")
        implementation("androidx.credentials:credentials:1.3.0")
        implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
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
