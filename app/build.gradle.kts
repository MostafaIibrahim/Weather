plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id ("kotlin-kapt")
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "com.example.weather"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.weather"
        minSdk = 26
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

    buildFeatures{
        dataBinding = true
        viewBinding = true
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
    /**
     *  Testing dependencies
     * */
    testImplementation("junit:junit:4.13.2")
    testImplementation(libs.junit.jupiter)
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    androidTestImplementation ("androidx.test.ext:junit:1.1.5") // Check for the latest version
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")
    testImplementation ("org.jetbrains.kotlin:kotlin-test:1.8.0")

    testImplementation("org.hamcrest:hamcrest:2.2")
    testImplementation("org.hamcrest:hamcrest-library:2.2")
    androidTestImplementation("org.hamcrest:hamcrest:2.2")
    androidTestImplementation("org.hamcrest:hamcrest-library:2.2")

    testImplementation ("junit:junit:4.13.2")
    testImplementation ("org.hamcrest:hamcrest-all:1.3")
    testImplementation ("androidx.arch.core:core-testing:2.1.0")

    testImplementation ("androidx.test.ext:junit-ktx:1.1.3")
    testImplementation ("androidx.test:core-ktx:1.5.0")
    testImplementation ("org.robolectric:robolectric:4.8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1")

    implementation ("com.airbnb.android:lottie:6.5.2")
    //for map openStreetMap
    implementation ("org.osmdroid:osmdroid-android:6.1.10")
    //------------------
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.0")
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
    implementation(libs.androidx.compose.material.core)
    androidTestImplementation(libs.junit.jupiter) //search
    //Room
    kapt ("androidx.room:room-compiler:2.6.1")
    implementation ("androidx.room:room-runtime:2.6.1")
    implementation ("androidx.room:room-ktx:2.6.1")
    //LiveDdata
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0")
    //Coroutine
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.0")
    //For Retrofit
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.2")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}