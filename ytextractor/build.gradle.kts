@file:OptIn(ExperimentalWasmDsl::class)

import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.vannitktech.maven.publish)
}

group = "com.kdroid.ytextractor"
version = "0.1.0"

kotlin {
    jvmToolchain(17)

    androidTarget { publishLibraryVariants("release") }
    jvm()
    js {
        browser()
    }
    wasmJs {
        browser()
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    macosX64()
    macosArm64()
    linuxX64()
    mingwX64()

    sourceSets {
        // Dépendances communes à toutes les plateformes
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.kotlinx.serialization.json)
            api(libs.ktor.client.core)
        }

        // Dépendances pour les tests communs
        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        // Configuration pour Android et JVM (partagent une configuration de base)
        val androidJvmMain by creating {
            dependsOn(commonMain.get()) // Hérite des dépendances de commonMain
            dependencies {
                api(libs.ktor.client.cio) // Client Ktor pour JVM et Android
            }
        }

        androidMain {
            dependsOn(androidJvmMain) // Android hérite de androidJvmMain
            dependencies {
                implementation(libs.kotlinx.coroutines.android) // Coroutines spécifiques Android
            }
        }

        jvmMain {
            dependsOn(androidJvmMain) // JVM hérite aussi de androidJvmMain
            dependencies {
                implementation(libs.kotlinx.coroutines.swing) // Support pour Swing
                implementation(libs.slf4j.simple) // Logging simplifié pour JVM
            }
        }

        // Configuration partagée entre JS et WASM
        val jsWasmJsMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                api(libs.ktor.client.js) // Client Ktor pour JS et WASM
            }
        }
        jsMain {
            dependsOn(jsWasmJsMain)
        }
        wasmJsMain {
            dependsOn(jsWasmJsMain)
        }

        // Dépendances spécifiques pour Linux
        linuxX64Main.dependencies {
            api(libs.ktor.client.curl) // Curl client pour Linux
        }

        // Dépendances spécifiques pour Windows
        mingwX64Main.dependencies {
            api(libs.ktor.client.winhttp) // WinHTTP client pour Windows
        }

        // Configuration partagée entre toutes les plateformes Apple
        val appleMain by creating {
            dependsOn(commonMain.get()) // Hérite des dépendances de commonMain
            dependencies {
                api(libs.ktor.client.darwin) // Client Ktor pour Darwin (macOS/iOS)
            }
        }

        // Liste des cibles Apple pour simplifier la configuration
        val appleTargets = listOf(
            macosMain,
            iosX64Main,
            iosArm64Main,
            iosSimulatorArm64Main,
            macosX64Main,
            macosArm64Main
        )
        appleTargets.forEach { target ->
            kotlin.sourceSets.getByName(target.name).dependsOn(appleMain)
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

mavenPublishing {
    coordinates(
        groupId = "io.github.kdroidfilter",
        artifactId = "ytextractor",
        version = version.toString()
    )

    pom {
        name.set("YouTube Extractor")
        description.set("YTExtractor is a Kotlin Multiplatform library for extracting download links of YouTube videos from a URL. It supports major platforms and enables efficient and fast extraction of metadata and video links.")
        inceptionYear.set("2024")
        url.set("https://github.com/kdroidFilter/YTExtractor")

        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/licenses/MIT")
            }
        }

        developers {
            developer {
                id.set("kdroidfilter")
                name.set("Elyahou Hadass")
                email.set("elyahou.hadass@gmail.com")
            }
        }

        scm {
            url.set("https://github.com/kdroidFilter/YTExtractor")
        }
    }

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()
}
