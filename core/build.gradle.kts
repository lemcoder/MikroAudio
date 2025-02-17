import io.github.lemcoder.KonanPluginExtension
import io.github.lemcoder.LibraryType
import org.jetbrains.kotlin.konan.target.KonanTarget
import org.jetbrains.kotlin.konan.util.DependencyDirectories.localKonanDir

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
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    externalNativeBuild {
        cmake {
            path = file("src/androidMain/cpp/CMakeLists.txt")
        }

        ndkBuild {
            buildStagingDirectory = projectDir.resolve("src/androidMain/jniLibs")
        }
    }

    sourceSets {
        getByName("androidTest") {
            java.srcDirs("src/androidInstrumentedTest/java")
            kotlin.srcDirs("src/androidInstrumentedTest/kotlin")
        }
    }
}

kotlin {
    jvmToolchain(21)

    // TODO uncomment when API is stable
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

        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        androidMain.dependencies {
            implementation(libs.panama.port)
        }

        androidInstrumentedTest.dependencies {
            implementation(libs.junit.ktx)
            implementation(libs.androidX.testRunner)
            implementation(libs.test.rules)
        }
    }
}

val staticTargets: () -> Map<KonanTarget, LibraryType> = {
    val targets = mutableMapOf<KonanTarget, LibraryType>()

    targets[KonanTarget.LINUX_X64] = LibraryType.STATIC
    targets[KonanTarget.MINGW_X64] = LibraryType.STATIC

    if (System.getProperty("os.name").lowercase().contains("mac")) {
        targets[KonanTarget.IOS_ARM64] = LibraryType.STATIC
        targets[KonanTarget.IOS_SIMULATOR_ARM64] = LibraryType.STATIC
        targets[KonanTarget.IOS_X64] = LibraryType.STATIC
        targets[KonanTarget.MACOS_X64] = LibraryType.STATIC
        targets[KonanTarget.MACOS_ARM64] = LibraryType.STATIC
    }

    targets
}

val sharedTargets: () -> Map<KonanTarget, LibraryType> = {
    val targets = mutableMapOf<KonanTarget, LibraryType>()

    targets[KonanTarget.ANDROID_ARM32] = LibraryType.SHARED
    targets[KonanTarget.ANDROID_ARM64] = LibraryType.SHARED
    targets[KonanTarget.ANDROID_X64] = LibraryType.SHARED

    targets
}

configure<KonanPluginExtension> {
    targets = sharedTargets() + staticTargets()
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

//val nativeLibsDir = file("${rootDir}/native/lib")

//tasks.register<Copy>("copyNativeLibs") {
//    duplicatesStrategy = DuplicatesStrategy.INCLUDE
//    val abiMapping = mapOf(
//        "android_arm32" to "armeabi-v7a",
//        "android_arm64" to "arm64-v8a",
//        "android_x64" to "x86_64"
//    )
//
//    abiMapping.forEach { (srcFolder, abi) ->
//        copy {
//            from(file("$nativeLibsDir/$srcFolder/libma.so")) // Source
//            into(file("$projectDir/src/androidMain/jniLibs/$abi"))  // Destination
//        }
//    }
//}
