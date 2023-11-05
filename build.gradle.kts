// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files

        classpath("com.android.tools.build:gradle:8.1.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.20")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.5")

        // https://dagger.dev/hilt/gradle-setup
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.48")

        // https://console.firebase.google.com/project/workout-pixel/overview
        // classpath("com.google.gms:google-services:4.4.0")


    }
}

plugins {
    id("com.google.devtools.ksp") version "1.9.20-1.0.14" apply false
}


allprojects {
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
        maven { setUrl("https://maven.google.com") }
        maven { setUrl ("https://androidx.dev/storage/compose-compiler/repository/") }
    }
}


/*task clean(type: Delete) {
    delete rootProject.buildDir
}*/

/*
// Kotlin stuff:
ext {
    activityVersion = "1.2.3"
    appCompatVersion = "1.3.0"
    constraintLayoutVersion = "2.0.4"
    coreTestingVersion = "2.1.0"
    coroutines = "1.5.2-native-mt"
    materialVersion = "1.3.0"
    // testing
    junitVersion = "4.13.2"
    espressoVersion = "3.1.0"
    androidxJunitVersion = "1.1.2"
    kotlinxCoroutinesAndroidVersion = "1.5.2-native-mt"
}
*/
