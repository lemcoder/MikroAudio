plugins {
    alias(libs.plugins.multiplatform)
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
            implementation(libs.io)
        }
    }
}

publishing {
    repositories {
        mavenLocal()
    }
}