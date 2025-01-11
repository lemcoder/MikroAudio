plugins {
    alias(libs.plugins.jetbrains.kotlin.multiplatform)
    id("maven-publish")
}


kotlin {
    jvmToolchain(17)

    mingwX64().apply {
        binaries {
            executable {
                entryPoint = "main"
            }
        }
    }

    sourceSets {
        nativeMain.dependencies {
            implementation(projects.core)
        }
    }
}

publishing {
    repositories {
        mavenLocal()
    }
}