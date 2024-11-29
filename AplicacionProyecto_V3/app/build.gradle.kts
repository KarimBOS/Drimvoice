// build.gradle.kts (del módulo app)
plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.gms.google-services")
}

android {
    compileSdk = 34
    namespace = "com.example.aplicacionproyecto" // Asegúrate de usar tu propio valor
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }


    defaultConfig {
        applicationId = "com.example.aplicacionproyecto"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation(libs.androidx.activity)

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("com.google.code.gson:gson:2.10.1")




    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(platform("com.google.firebase:firebase-bom:33.6.0")) // Usar BOM para gestionar versiones de Firebase

    // Dependencia de Firebase Authentication
    implementation("com.google.firebase:firebase-auth")

    // Otras dependencias de Firebase (si las usas)
    implementation("com.google.firebase:firebase-firestore") // Firestore (si usas base de datos)
    implementation("com.google.firebase:firebase-storage") // Firebase Storage (si usas almacenamiento)
    implementation("com.google.firebase:firebase-messaging") // Firebase Cloud Messaging (si usas notificaciones push)

    // Dependencia para usar Gson (si lo necesitas)
    implementation("com.google.code.gson:gson:2.10.1")

    dependencies {
        implementation ("com.google.firebase:firebase-auth:21.2.0") // Firebase Auth
        implementation ("com.google.firebase:firebase-firestore:24.0.3") // Firebase Firestore
        implementation ("com.google.firebase:firebase-analytics:21.2.0") // Firebase Analytics, opcional
        // Agrega cualquier otra dependencia que necesites
    }






}
