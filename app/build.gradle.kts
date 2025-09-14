plugins {
	id("com.android.application")
	id("com.google.devtools.ksp")
	id("androidx.navigation.safeargs")
	id("org.jetbrains.kotlin.android")
	id("kotlin-android")
	id("dagger.hilt.android.plugin")
	id("org.jetbrains.kotlin.plugin.serialization") version "2.2.0"
	id("org.jetbrains.kotlin.plugin.compose") version "2.2.0"
}


android {

	compileSdk = 36
	defaultConfig {
		namespace = "ch.karimattia.workoutpixel"
		applicationId = "ch.karimattia.workoutpixels"
		minSdk = 31
		targetSdk = 36
		versionCode = 10
		versionName = "1.4"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

		vectorDrawables.useSupportLibrary = true
	}

	buildTypes {
		getByName("debug") {
			//isMinifyEnabled = true
			//isShrinkResources = true
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
		}
		getByName("release") {
			isMinifyEnabled = true
			isShrinkResources = true
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
			signingConfig = signingConfigs.getByName("debug")
		}
	}

	buildFeatures.compose = true
//	composeOptions {
//		kotlinCompilerExtensionVersion = "1.5.4"
//	}

	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}

	kotlinOptions {
		jvmTarget = "17"
	}

	sourceSets["main"].java.srcDirs("src/main/java", "src/main/java/2")

	dependenciesInfo {
		includeInApk = true
		includeInBundle = true
	}

	ksp {
		arg("room.schemaLocation", "$projectDir/schemas")
	}
}

//noinspection SpellCheckingInspection
dependencies {
	implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
	// Removed deprecated lifecycle-extensions - using explicit lifecycle components instead
	implementation("androidx.activity:activity-ktx:1.11.0") // Updated
	testImplementation("junit:junit:4.13.2")
	testImplementation("org.testng:testng:7.11.0")

	// Lifecycle components
	val lifecycleVersion = "2.9.3" // Updated
	implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
	implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
	implementation("androidx.lifecycle:lifecycle-common-java8:$lifecycleVersion")

	// Kotlin components
	api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
	api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

	androidTestImplementation("androidx.test.ext:junit:1.3.0") // Updated
	androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0") // Updated
	androidTestImplementation("androidx.test:rules:1.7.0") // Updated
	androidTestImplementation("androidx.test:runner:1.7.0") // Updated
	androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.9.1") // Updated

	// Room components
	val roomVersion = "2.8.0" // Updated
	ksp("androidx.room:room-compiler:$roomVersion")
	implementation("androidx.room:room-ktx:$roomVersion")
	// implementation("com.google.devtools.ksp:symbol-processing-api:2.1.21-2.0.1")

	// Navigation
	// implementation "androidx.navigation:navigation-ui-ktx:2.4.0-alpha10"
	// Replaced by https://google.github.io/accompanist/navigation-animation
	// Jetpack Compose Integration
	implementation("androidx.navigation:navigation-compose:2.9.4") // Updated
	// https://google.github.io/accompanist/navigation-animation/
	// implementation "com.google.accompanist:accompanist-navigation-animation:0.20.0"
	implementation("androidx.hilt:hilt-navigation-compose:1.3.0") // Updated

	// Jetpack Compose UI
	implementation("androidx.compose.ui:ui:1.9.1") // Updated
	// Tooling support (Previews, etc.)
	implementation("androidx.compose.ui:ui-tooling:1.9.1") // Updated
	// Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
	implementation("androidx.compose.foundation:foundation:1.9.1") // Updated
	// Material Design
	implementation("androidx.compose.material3:material3:1.3.2")
	// implementation ("androidx.compose.material:material:1.5.4")
	// Material design icons
	implementation("androidx.compose.material:material-icons-core:1.7.8")
	implementation("androidx.compose.material:material-icons-extended:1.7.8")
	// Integration with activities
	implementation("androidx.activity:activity-compose:1.11.0") // Updated
	// Integration with ViewModels
	implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion") // Updated
	// Integration with observables
	implementation("androidx.compose.runtime:runtime-livedata:1.9.1") // Updated
	// implementation "androidx.compose.runtime:runtime-rxjava2:1.1.0-alpha06"

	// UI Tests
	androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.9.1") // Updated

	// Pager: https://google.github.io/accompanist/pager/
	// Can't be bothered to update since it looks like a pain and it's only shown to phones that can't pin widgets.
	//noinspection GradleDependency
	implementation("com.google.accompanist:accompanist-pager:0.36.0")
	//noinspection GradleDependency
	implementation("com.google.accompanist:accompanist-pager-indicators:0.36.0")

	// https://stackoverflow.com/questions/68672046/how-to-use-animated-vector-drawable-in-compose
	implementation("androidx.compose.animation:animation-graphics:1.9.1") // Updated

	// Coil for instruction gifs
	// Can't be bothered to update since it looks like a pain and it's only shown to phones that can't pin widgets.
	// noinspection NewerVersionAvailable,GradleDependency
	implementation("io.coil-kt:coil-compose:1.3.2")
	// noinspection NewerVersionAvailable,GradleDependency
	implementation("io.coil-kt:coil-gif:1.3.2")

	// https://github.com/vanpra/compose-material-dialogs
	implementation("io.github.vanpra.compose-material-dialogs:color:0.9.0")
	implementation("io.github.vanpra.compose-material-dialogs:core:0.9.0")

	// https://developer.android.com/topic/libraries/architecture/datastore#kts
	implementation("androidx.datastore:datastore:1.1.7")
	implementation("androidx.datastore:datastore-preferences:1.1.7")

	// https://dagger.dev/hilt/gradle-setup
	// Seems like a pain to upgrade to 2.50
	// 05.01.2024
	// [ksp] [Hilt] No property named assistedFactory was found in annotation HiltViewModel: java.lang.IllegalStateException: No property named assistedFactory was found in annotation HiltViewModel
	// noinspection GradleDependency
	implementation("com.google.dagger:hilt-android:2.57.1")
	// noinspection GradleDependency
	ksp("com.google.dagger:hilt-compiler:2.57.1")

	// https://github.com/Kotlin/kotlinx.serialization
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0") // Updated

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

	implementation("androidx.glance:glance-appwidget:1.1.1")

	// The chat framework for the intro.
	// https://github.com/karim-attia/compose-chatbot-framework
	implementation("com.github.karim-attia:compose-chatbot-framework:1.0.22")

}

repositories {
	mavenCentral()
	google()
}
