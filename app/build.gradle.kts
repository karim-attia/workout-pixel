import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
    id("com.google.devtools.ksp")
    id ("androidx.navigation.safeargs")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("dagger.hilt.android.plugin")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.20"
}


android {
    val keystorePropertiesFile = rootProject.file("keystore.properties")

    val keystoreProperties = Properties()
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))


    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }

    compileSdk = 34

    defaultConfig {
        applicationId = "ch.karimattia.workoutpixel"
        minSdk = 28
        targetSdk = 34
        versionCode = 7
        versionName = "1.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables.useSupportLibrary = true
    }

    buildFeatures.compose = true

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    sourceSets["main"].java.srcDirs("src/main/java", "src/main/java/2")

    dependenciesInfo {
        includeInApk = true
        includeInBundle = true
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4-dev-k1.9.20-50f08dfa4b4"
    }

    defaultConfig {
        namespace = "ch.karimattia.workoutpixel"
    }
}

//noinspection SpellCheckingInspection
dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation ("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation ("androidx.activity:activity-ktx:1.8.0")
    testImplementation ("junit:junit:4.13.2")
    testImplementation ("org.testng:testng:7.4.0")

    // Lifecycle components
    val lifecycleVersion = "2.6.2"
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation ("androidx.lifecycle:lifecycle-common-java8:$lifecycleVersion")

    // Kotlin components
    api ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    api ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")

    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation ("androidx.test:rules:1.5.0")
    androidTestImplementation ("androidx.test:runner:1.5.2")
    androidTestImplementation ("androidx.compose.ui:ui-test-junit4:1.5.4")

    // Room components
    val room_version = "2.6.0"
    ksp ("androidx.room:room-compiler:$room_version")
    implementation ("androidx.room:room-ktx:$room_version")
    implementation("com.google.devtools.ksp:symbol-processing-api:1.9.20-1.0.14")

    // Navigation
    // implementation "androidx.navigation:navigation-ui-ktx:2.4.0-alxpha10"
    // Replaced by https://google.github.io/accompanist/navigation-animation
    // Jetpack Compose Integration
    implementation ("androidx.navigation:navigation-compose:2.7.5")
    // https://google.github.io/accompanist/navigation-animation/
    // implementation "com.google.accompanist:accompanist-navigation-animation:0.20.0"
    implementation ("androidx.hilt:hilt-navigation-compose:1.1.0")

    //Jetpack Compose UI
    implementation ("androidx.compose.ui:ui:1.5.4")
    // Tooling support (Previews, etc.)
    implementation ("androidx.compose.ui:ui-tooling:1.5.4")
    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    implementation ("androidx.compose.foundation:foundation:1.5.4")
    // Material Design
    implementation ("androidx.compose.material:material:1.5.4")
    // Material design icons
    implementation ("androidx.compose.material:material-icons-core:1.5.4")
    implementation ("androidx.compose.material:material-icons-extended:1.5.4")
    // Integration with activities
    implementation ("androidx.activity:activity-compose:1.8.0")
    // Integration with ViewModels
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    // Integration with observables
    implementation ("androidx.compose.runtime:runtime-livedata:1.5.4")
    // implementation "androidx.compose.runtime:runtime-rxjava2:1.1.0-alpha06"

    // UI Tests
    androidTestImplementation ("androidx.compose.ui:ui-test-junit4:1.5.4")

    // Pager: https://google.github.io/accompanist/pager/
    implementation ("com.google.accompanist:accompanist-pager:0.19.0")
    implementation ("com.google.accompanist:accompanist-pager-indicators:0.19.0")

    // https://stackoverflow.com/questions/68672046/how-to-use-animated-vector-drawable-in-compose
    implementation("androidx.compose.animation:animation-graphics:1.5.4")

    // Coil for instruction gifs
    implementation("io.coil-kt:coil-compose:1.3.2")
    implementation("io.coil-kt:coil-gif:1.3.2")

    // https://github.com/vanpra/compose-material-dialogs
    implementation ("io.github.vanpra.compose-material-dialogs:color:0.6.2")
    implementation ("io.github.vanpra.compose-material-dialogs:core:0.6.2")

    // https://developer.android.com/topic/libraries/architecture/datastore#kts
    implementation("androidx.datastore:datastore:1.0.0")
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // https://dagger.dev/hilt/gradle-setup
    ksp ("com.google.dagger:hilt-android:2.48")
    ksp ("com.google.dagger:hilt-compiler:2.48")

    // https://github.com/Kotlin/kotlinx.serialization
    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // https://console.firebase.google.com/project/workout-pixel/overview
    // Import the Firebase BoM
    // implementation platform("com.google.firebase:firebase-bom:29.0.0")

    // Add the dependency for the Firebase SDK for Google Analytics
    // When using the BoM, don"t specify versions in Firebase dependencies
    // implementation "com.google.firebase:firebase-analytics-ktx"

    // Add the dependencies for any other desired Firebase products
    // https://firebase.google.com/docs/android/setup#available-libraries

    // https://developers.google.com/tag-platform/tag-manager/android/v5
    // implementation "com.google.android.gms:play-services-tagmanager:18.0.4"

/*
    implementation "com.google.firebase:firebase-core:19.0.2"
    implementation "com.google.firebase:firebase-analytics:19.0.2"
*/

    implementation ("androidx.glance:glance-appwidget:1.0.0")

    implementation ("com.github.karim-attia:ChatbotComposeFramework:1.0.2")

}

repositories {
    mavenCentral()
    google()
}