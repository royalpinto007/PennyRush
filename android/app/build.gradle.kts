import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use(::load)
    }
}

fun localOrEnv(name: String): String =
    localProperties.getProperty(name) ?: providers.environmentVariable(name).orNull ?: ""

val releaseStoreFile = localOrEnv("PENNYRUSH_RELEASE_STORE_FILE")
val releaseStorePassword = localOrEnv("PENNYRUSH_RELEASE_STORE_PASSWORD")
val releaseKeyAlias = localOrEnv("PENNYRUSH_RELEASE_KEY_ALIAS")
val releaseKeyPassword = localOrEnv("PENNYRUSH_RELEASE_KEY_PASSWORD")
val hasReleaseSigning = listOf(
    releaseStoreFile,
    releaseStorePassword,
    releaseKeyAlias,
    releaseKeyPassword,
).all { it.isNotBlank() }

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "dev.pennyrush.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "dev.pennyrush.app"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"

        buildConfigField("String", "SUPABASE_URL", "\"${localOrEnv("PENNYRUSH_SUPABASE_URL")}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"${localOrEnv("PENNYRUSH_SUPABASE_ANON_KEY")}\"")
        buildConfigField("String", "SUPABASE_AUTH_SCHEME", "\"pennyrush\"")
        buildConfigField("String", "SUPABASE_AUTH_HOST", "\"auth-callback\"")
        buildConfigField("String", "WEB_BASE_URL", "\"${localOrEnv("PENNYRUSH_WEB_BASE_URL").ifBlank { "https://pennyrush.dev" }}\"")
    }

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = file(releaseStoreFile)
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":feature:home"))
    implementation(project(":feature:onboarding"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.biometric)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.auth)
    implementation(libs.supabase.postgrest)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.hilt.android)
    implementation(libs.timber)
}
