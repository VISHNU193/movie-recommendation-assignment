//plugins {
//    alias(libs.plugins.android.application)
//    alias(libs.plugins.kotlin.android)
//    alias(libs.plugins.kotlin.compose)
//    id("org.jetbrains.kotlin.kapt")
//}
//
//android {
//    namespace = "com.example.movierecommendation"
//    compileSdk = 36
//
//    defaultConfig {
//        applicationId = "com.example.movierecommendation"
//        minSdk = 24
//        targetSdk = 36
//        versionCode = 1
//        versionName = "1.0"
//
//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//    }
//
//    buildTypes {
//        release {
//            isMinifyEnabled = false
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
//        }
//    }
//
//    buildTypes {
//        debug {
//            buildConfigField(
//                "String",
//                "TMDB_API_KEY",
//                "\"${project.findProperty("TMDB_API_KEY") ?: ""}\""
//            )
//        }
//        release {
//            buildConfigField(
//                "String",
//                "TMDB_API_KEY",
//                "\"${project.findProperty("TMDB_API_KEY") ?: ""}\""
//            )
//        }
//    }
//
//
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_11
//        targetCompatibility = JavaVersion.VERSION_11
//    }
//    kotlinOptions {
//        jvmTarget = "11"
//    }
//    buildFeatures {
//        compose = true
//        buildConfig = true
//    }
//}
//
//dependencies {
//
//    implementation(libs.androidx.core.ktx)
//    implementation(libs.androidx.lifecycle.runtime.ktx)
//    implementation(libs.androidx.activity.compose)
//    implementation(platform(libs.androidx.compose.bom))
//    implementation(libs.androidx.compose.ui)
//    implementation(libs.androidx.compose.ui.graphics)
//    implementation(libs.androidx.compose.ui.tooling.preview)
//    implementation(libs.androidx.compose.material3)
//    testImplementation(libs.junit)
//
//    implementation(libs.retrofit)
//    implementation(libs.retrofit.moshi)
//    implementation(libs.moshi)
//    kapt(libs.moshi.codegen)
//
//    implementation(libs.kotlinx.coroutines.android)
//
//    implementation(libs.androidx.navigation.compose)
//
//    // ---------------- Image Loading ----------------
//    implementation(libs.coil.compose)
//
//    // ---------------- Room Database ----------------
//    implementation(libs.androidx.room.runtime)
//    implementation(libs.androidx.room.ktx)
//    kapt(libs.androidx.room.compiler)
//
//    androidTestImplementation(libs.androidx.junit)
//    androidTestImplementation(libs.androidx.espresso.core)
//    androidTestImplementation(platform(libs.androidx.compose.bom))
//    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
//    debugImplementation(libs.androidx.compose.ui.tooling)
//    debugImplementation(libs.androidx.compose.ui.test.manifest)
//}


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.example.movierecommendation"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.movierecommendation"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            buildConfigField(
                "String",
                "TMDB_API_KEY",
                "\"${project.findProperty("TMDB_API_KEY") ?: ""}\""
            )
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField(
                "String",
                "TMDB_API_KEY",
                "\"${project.findProperty("TMDB_API_KEY") ?: ""}\""
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
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.material:material-icons-extended")

    testImplementation(libs.junit)

    // Networking / JSON
    implementation(libs.retrofit)
    implementation(libs.retrofit.moshi)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.okhttp.logging)
    kapt(libs.moshi.codegen)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Image loading (Coil)
    implementation(libs.coil.compose)

    // Room (local DB)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)

    // Android tests / debug
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
