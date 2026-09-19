plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.astute.ai"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.astute.ai"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        vectorDrawables {
            useSupportLibrary = true
        }
        
        resourceConfigurations.addAll(listOf("en", "es", "fr", "de", "hi", "zh"))
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            
            signingConfig = signingConfigs.getByName("debug")
            
            buildConfigField("boolean", "IS_DEBUG", "false")
            buildConfigField("String", "API_VERSION", "\"v1.0\"")
        }
        
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            
            buildConfigField("boolean", "IS_DEBUG", "true")
            buildConfigField("String", "API_VERSION", "\"v1.0-debug\"")
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=kotlin.ExperimentalStdlibApi",
            "-Xjvm-default=all-compatibility"
        )
    }
    
    buildFeatures {
        compose = true
        buildConfig = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "/META-INF/LICENSE*"
            excludes += "/META-INF/NOTICE*"
            excludes += "/*.txt"
            excludes += "/*.md"
        }
    }
    
    lint {
        disable.add("MissingTranslation")
        disable.add("UnspecifiedImmutableFlag")
        abortOnError = false
        checkReleaseBuilds = true
        fatal.add("UnusedResources")
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))
    
    // Core Android
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.5")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.profileinstaller:profileinstaller:1.4.0")
    
    // Compose
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.5")
    implementation("androidx.navigation:navigation-compose:2.8.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.5")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.5")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.8.5")
    
    // Preferences DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    
    // Work Manager
    implementation("androidx.work:work-runtime-ktx:2.9.1")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.09.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
