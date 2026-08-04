plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "dev.sandesh.kharcha.core.database"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions { jvmTarget = "17" }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {
    implementation(project(":core:model"))

    // Room
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)

    // SQLCipher
    implementation(libs.sqlcipher.android)
    implementation(libs.sqlite.ktx)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Coroutines
    implementation(libs.coroutines.android)

    // Security Crypto (Keystore key management)
    implementation(libs.security.crypto)

    // Testing
    testImplementation(libs.bundles.testing.unit)
    testImplementation(libs.room.testing)
    testImplementation(libs.robolectric)
    androidTestImplementation(libs.bundles.testing.android)
    androidTestImplementation(libs.room.testing)
}
