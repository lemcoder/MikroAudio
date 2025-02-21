import org.jetbrains.kotlin.konan.target.KonanTarget
import org.jetbrains.kotlin.konan.util.DependencyDirectories.localKonanDir
import io.github.lemcoder.KonanPluginExtension

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.konanplugin)
    id("maven-publish")
}

group = "pl.lemanski.mikroaudio"
version = "0.0.3"

android {
    namespace = "pl.lemanski.mikroaudio"
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        compileSdk = libs.versions.android.compileSdk.get().toInt()
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
                extraOpts("-libraryPath", "$rootDir\\native\\lib\\${target.konanTarget.name}")
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.coroutines.core)
        }

        commonTest.dependencies {
            implementation(libs.coroutines.test)
        }
    }
}

configure<KonanPluginExtension> {
    targets = buildList {
        add(KonanTarget.LINUX_X64)
        add(KonanTarget.MINGW_X64)
        if (System.getProperty("os.name").lowercase().contains("mac")) {
            add(KonanTarget.IOS_ARM64)
            add(KonanTarget.IOS_SIMULATOR_ARM64)
            add(KonanTarget.IOS_X64)
            add(KonanTarget.MACOS_X64)
            add(KonanTarget.MACOS_ARM64)
        }
    }
    sourceDir = "${rootDir}/native/src"
    headerDir = "${rootDir}/native/include"
    outputDir = "${rootDir}/native/lib"
    libName = "ma"
    konanPath = localKonanDir.listFiles()?.first {
        it.name.contains(libs.versions.kotlin.get())
    }?.absolutePath
}

publishing {
    repositories {
        mavenLocal()
    }
}