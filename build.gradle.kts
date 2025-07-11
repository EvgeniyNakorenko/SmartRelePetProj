// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    kotlin("plugin.serialization") version "1.9.0" apply false

}
buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.7.2")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.48")
    }
}
