import org.jetbrains.kotlin.gradle.dsl.JvmTarget

private val FLAVOR_DIMENSION_NAME = "api"
private val FLAVOR_LEGACY_NAME = "legacy"
private val FLAVOR_MODERN_NAME = "modern"
private val JVM_TARGET = 17

plugins {
    with(receiver = libs) {
        alias(notation = plugins.android.application)
        alias(notation = plugins.kotlin.android)
        alias(notation = plugins.kotlin.compose)
        alias(notation = plugins.kotlin.serialization)
    }
}

kotlin {
    jvmToolchain(jdkVersion = JVM_TARGET)
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(target = "$JVM_TARGET"))
    }
}

android {
    namespace = "com.example.navigation_test"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.navigation_test"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        compose = true
    }

    flavorDimensions += FLAVOR_DIMENSION_NAME

    productFlavors {
        create(FLAVOR_LEGACY_NAME) {
            dimension = FLAVOR_DIMENSION_NAME
            minSdk = 24
        }
        create(FLAVOR_MODERN_NAME) {
            dimension = FLAVOR_DIMENSION_NAME
            minSdk = 33
        }
    }

    sourceSets {
        named(FLAVOR_LEGACY_NAME) {
            kotlin.setSrcDirs(setOf("src/$FLAVOR_LEGACY_NAME/kotlin"))
        }
        named(FLAVOR_MODERN_NAME) {
            kotlin.setSrcDirs(setOf("src/$FLAVOR_MODERN_NAME/kotlin"))
        }
    }
}

dependencies {
    with(receiver = libs) {
        implementation(dependencyNotation = androidx.core.ktx)
        implementation(dependencyNotation = androidx.lifecycle.runtime.ktx)
        implementation(dependencyNotation = androidx.activity.compose)
        implementation(dependencyNotation = platform(androidx.compose.bom))
        implementation(dependencyNotation = androidx.material.icons.core)
        implementation(dependencyNotation = androidx.ui)
        implementation(dependencyNotation = androidx.ui.graphics)
        implementation(dependencyNotation = androidx.ui.tooling.preview)
        implementation(dependencyNotation = androidx.material3)
        testImplementation(dependencyNotation = junit)
        androidTestImplementation(dependencyNotation = androidx.junit)
        androidTestImplementation(dependencyNotation = androidx.espresso.core)
        androidTestImplementation(dependencyNotation = platform(androidx.compose.bom))
        androidTestImplementation(dependencyNotation = androidx.ui.test.junit4)
        debugImplementation(dependencyNotation = androidx.ui.tooling)
        debugImplementation(dependencyNotation = androidx.ui.test.manifest)

        implementation(dependencyNotation = composeunstyled)
        implementation(dependencyNotation = composeunstyled.primitives)
        implementation(dependencyNotation = composeunstyled.theming)

        implementation(dependencyNotation = decompose)
        implementation(dependencyNotation = decompose.extensions.compose)
        implementation(dependencyNotation = essenty.lifecycle.coroutines)

        implementation(dependencyNotation = kotlinx.serialization.json)
    }
}
