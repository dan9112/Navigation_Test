import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

kotlin {
    jvmToolchain(11)
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(target = "11"))
    }
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    buildFeatures {
        compose = true
    }

    flavorDimensions += "api"

    productFlavors {
        create("modern") {
            dimension = "api"
            minSdk = 33
        }
        create("legacy") {
            dimension = "api"
            minSdk = 24
        }
    }

    sourceSets {
        named("modern") {
            kotlin.setSrcDirs(setOf("src/modern/kotlin"))
        }
        named("legacy") {
            kotlin.setSrcDirs(setOf("src/legacy/kotlin"))
        }
        named("main") {
            kotlin.setSrcDirs(setOf("src/main/kotlin"))
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation("androidx.compose.material:material-icons-core")
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

//    implementation("com.mikepenz:fastadapter-extensions:5.7.0")
    implementation("com.mikepenz:fastadapter-extensions-expandable:5.7.0")

    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    implementation(dependencyNotation = libs.composeunstyled)
    implementation(dependencyNotation = libs.composeunstyled.primitives)
    implementation(dependencyNotation = libs.composeunstyled.theming)

    implementation(dependencyNotation = kotlin(module = "reflect"))
}
