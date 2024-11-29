plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()

    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.10")
        classpath("com.google.gms:google-services:4.4.2") // Servicio de Google
        classpath ("com.google.gms:google-services:4.3.15") // Versión más reciente

    }

    buildscript {
        repositories {
            google()
            mavenCentral()
        }
        dependencies {
            classpath ("com.android.tools.build:gradle:7.4.1") // La versión puede variar
            classpath ("com.google.gms:google-services:4.3.15") // Esta es la clave
        }
    }




}




