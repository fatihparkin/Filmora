plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false

}
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath (libs.hilt.android.gradle.plugin) // Hilt plugin eklenmeli
        classpath("com.google.gms:google-services:4.4.1")
    }
}

allprojects {
    configurations.all {
        resolutionStrategy {
            force("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.9.22")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.22")
        }
    }
}


