plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    kotlin("plugin.serialization")
}

android {
    namespace = "com.example.spaceapp_quizapi"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.spaceapp_quizapi"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    packagingOptions {
        exclude("META-INF/INDEX.LIST")
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
    // Ktor Client and JSON Parsing
    implementation("io.ktor:ktor-client-android:2.3.12") {
        exclude(group = "ch.qos.logback", module = "logback-classic") // Exclude Logback from Ktor
    }
    implementation("io.ktor:ktor-client-core:2.3.12")
    implementation("io.ktor:ktor-client-cio:2.3.12") // For making HTTP requests
    implementation("io.ktor:ktor-client-serialization:2.3.12") // For JSON parsing
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.12") // For kotlinx.serialization
    implementation("io.ktor:ktor-client-content-negotiation:2.3.12")

    // SLF4J Android logging
    implementation("org.slf4j:slf4j-android:1.7.36")

    // Android libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Test libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Navigation
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.0")

    //Glide
    implementation("com.github.bumptech.glide:glide:4.15.1")
  //  annotationProcessor("com.github.bumptech.glide:compiler:4.15.1") // For custom models

}
