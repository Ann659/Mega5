plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.mega"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mega"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation ("com.squareup.okhttp3:okhttp:4.9.3")
    implementation ("org.json:json:20210307")
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.google.android.material:material:1.9.0")
    implementation ("androidx.fragment:fragment:1.6.1")
    implementation ("org.parceler:parceler-api:1.1.13")
    annotationProcessor ("org.parceler:parceler:1.1.13")
    implementation ("androidx.navigation:navigation-fragment:2.5.3")
    implementation ("androidx.navigation:navigation-ui:2.5.3")
    implementation ("androidx.fragment:fragment:1.5.5")
    implementation ("com.google.android.material:material:1.8.0")
    implementation ("io.github.jan-tennert.supabase:postgrest-kt:0.7.6")
    implementation ("io.github.jan-tennert.supabase:gotrue-kt:0.7.6")
    implementation ("io.ktor:ktor-client-okhttp:1.6.8")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}