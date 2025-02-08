// Top-level build file where you can add configuration options common to all sub-projects/modules.
//plugins {
//    alias(libs.plugins.android.application) apply false
//    id("com.google.gms.google-services") version "4.4.2" apply false
//}

buildscript {
    dependencies {
        // Add the dependency for the Google services Gradle plugin
        classpath ("com.google.gms:google-services:4.3.15");
        classpath ("com.android.tools.build:gradle:8.2.0");
    }
}
plugins {
    id ("com.android.application") version "7.4.2" apply false
    id ("com.android.library") version "7.4.2" apply false
    id ("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
    // Add the dependency for the Google services Gradle plugin
    id ("com.google.gms.google-services") version "4.4.2" apply false

}