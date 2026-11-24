import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

// Secrets that must stay out of source control live in the git-ignored local.properties.
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}
fun localProperty(key: String): String =
    localProperties.getProperty(key, "").replace("\\", "\\\\").replace("\"", "\\\"")


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.policyboss.policybosspro"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.policyboss.policybosspro"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Vehicle info (vahan) lookup credentials. Read from the git-ignored local.properties
        // so they never live in source control; blank when not configured.
        buildConfigField("String", "VEHICLE_INFO_SECRET_KEY", "\"${localProperty("vehicleInfo.secretKey")}\"")
        buildConfigField("String", "VEHICLE_INFO_CLIENT_KEY", "\"${localProperty("vehicleInfo.clientKey")}\"")
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
                keepRules {
                    files.add(file("proguard-rules.pro"))
                }
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

// Configure Kotlin JVM target at the top level in AGP 9+
kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}


dependencies {
//    implementation(libs.androidx.appcompat)
//    implementation(libs.androidx.core.ktx)
//    implementation(libs.material)
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.espresso.core)
//    androidTestImplementation(libs.androidx.junit)


    // Compose BOM — controls all androidx.compose.* versions
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.ktx)
    androidTestImplementation(platform(libs.androidx.compose.bom))

    // Compose Core
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.activity.compose)

    // Compose Tooling
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.test.manifest)

    // AndroidX Core & Lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.splashscreen)

    // Navigation & Hilt
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)


    // UI & Layouts
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.lottie.compose)

    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // Network & Serialization
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.kotlinx.serialization)
    implementation(libs.retrofit.serialization)

    // Data & Google Services
    implementation(libs.datastore.preferences)
    implementation(libs.play.services.location)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.gson)

    // Media3 (ExoPlayer)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)

    // Testing
    testImplementation(libs.junit)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.11.0")
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.ui.test.junit4)


}