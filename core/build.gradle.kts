import org.jetbrains.kotlin.konan.target.KonanTarget
import org.jetbrains.kotlin.konan.util.DependencyDirectories.localKonanDir
import pl.lemanski.plugin.KonanPluginExtension

plugins {
    alias(libs.plugins.jetbrains.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    id("maven-publish")
    id("pl.lemanski.plugin")
}

group = "pl.lemanski.mikroaudio"
version = "0.0.3"

android {
    namespace = "pl.lemanski.mikroaudio"
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        compileSdk = libs.versions.android.compileSdk.get().toInt()
    }

    externalNativeBuild {
        cmake {
            path = file("src/androidMain/cpp/CMakeLists.txt")
        }
    }
}

kotlin {
    jvmToolchain(17)

    androidTarget().apply {
        publishAllLibraryVariants()
    }

    listOf(
        mingwX64(),
        linuxX64()
    ).forEach { target ->
        target.apply {
            val main by compilations.getting

            main.cinterops.create("libma") {
                definitionFile = File(rootDir, "native/libma.def")
                includeDirs("$rootDir\\native\\include")
                extraOpts("-libraryPath", "$rootDir\\native\\lib\\${target.name}")
            }
        }
    }

    sourceSets {
        commonMain.dependencies {

        }
    }
}

configure<KonanPluginExtension> {
    kotlinTarget = KonanTarget.LINUX_X64
    sourceDir = "${rootDir}/native/src"
    headerDir = "${rootDir}/native/src"
    libName = "ma"
    konanPath = localKonanDir.listFiles()
        ?.first { it.name.contains(libs.versions.kotlin.get()) }?.absolutePath
}

publishing {
    repositories {
        mavenLocal()
    }
}