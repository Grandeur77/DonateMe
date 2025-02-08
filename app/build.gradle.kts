plugins {
    id("com.android.application");
    id("com.google.gms.google-services");
    id ("com.google.android.libraries.mapsplatform.secrets-gradle-plugin");
}

android {
    namespace = "com.example.donateme"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.donateme"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

//    implementation(libs.appcompat)
//    implementation(libs.material)
//    implementation(libs.activity)
//    implementation(libs.constraintlayout)
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.ext.junit)
//    androidTestImplementation(libs.espresso.core)
//    implementation(platform("com.google.firebase:firebase-bom:33.8.0"))
//    implementation("com.google.firebase:firebase-analytics")
//    implementation ("com.firebase:geofire-android:3.2.0")

   // implementation ("com.google.firebase:firebase-auth")

    // default ---------------------------------------------------------------------
    implementation ("androidx.appcompat:appcompat:1.6.1");
    implementation ("com.google.android.material:material:1.9.0");
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4");
    testImplementation ("junit:junit:4.13.2");
    androidTestImplementation ("androidx.test.ext:junit:1.1.5");
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1");
    // firebase -----------------------------------------------------------------------------
    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.8.0"))
    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation ("com.google.firebase:firebase-analytics");
    // auth + real-time database + core + storage---------------------------------------------------------------
    implementation ("com.google.firebase:firebase-auth:22.1.1");
    implementation ("com.google.firebase:firebase-database:20.2.1");
    implementation ("com.firebaseui:firebase-ui-database:7.1.1");
    implementation ("com.google.firebase:firebase-storage:20.2.0");
    implementation ("com.google.firebase:firebase-core:21.1.1");
    implementation ("com.squareup.retrofit2:retrofit:2.9.0");
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0");
    //Google map libraries ----------------------------------------------------------------------------------------
    implementation ("com.google.android.gms:play-services-location:21.0.1");
    implementation ("com.google.android.gms:play-services-maps:18.2.0");
    // swipe + picasso -------------------------------------------------------------------------------------
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0");
    implementation ("com.squareup.picasso:picasso:2.5.2");
    // geofire -----------------------------------------------------------------------------------------------
    implementation ("com.firebase:geofire-android-common:3.2.0");
    implementation ("com.firebase:geofire-android:3.2.0");


}