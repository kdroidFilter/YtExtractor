@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlinx.serialization)
}

group = "com.kdroid.ytextractor"
version = "0.0.1"

kotlin {
    jvmToolchain(17)

    androidTarget { publishLibraryVariants("release") }
    jvm()
    js { browser() }
    wasmJs { browser() }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    macosX64()
    macosArm64()
    linuxX64()
    mingwX64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.core)

        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        androidMain.dependencies {
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.ktor.client.cio)

        }

        jvmMain.dependencies {
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.ktor.client.cio)
            implementation(libs.slf4j.simple)

        }
        jsMain.dependencies {
            implementation(libs.ktor.client.js)

        }
        wasmJsMain.dependencies {
            implementation(libs.ktor.client.js)

        }
        nativeMain.dependencies {
            implementation(libs.ktor.client.cio)
        }
        linuxMain.dependencies {
            implementation(libs.ktor.client.curl)

        }
        mingwMain.dependencies {
            implementation(libs.ktor.client.winhttp)
        }
        macosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }


    }

    //https://kotlinlang.org/docs/native-objc-interop.html#export-of-kdoc-comments-to-generated-objective-c-headers
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        compilations["main"].compileTaskProvider.configure {
            compilerOptions {
                freeCompilerArgs.add("-Xexport-kdoc")
            }
        }
    }

}

android {
    namespace = "com.kdroid.ytextractor"
    compileSdk = 35

    defaultConfig {
        minSdk = 21
    }
}
