// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        maven(url = "https://maven.aliyun.com/repository/google")
        maven(url = "https://maven.aliyun.com/repository/gradle-plugin")
        maven(url = "https://maven.aliyun.com/repository/public")
        maven(url = "https://maven.aliyun.com/repository/jcenter")
        maven(url="https://repo.eclipse.org/content/repositories/paho-snapshots/")
        google()
        mavenCentral()
        maven(url="https://maven.pkg.jetbrains.space/public/p/ktor/eap")
        maven(url="https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
        jcenter()
    }
    dependencies {
//        classpath("com.android.tools.build:gradle:8.2.0")
//        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
        // 添加序列化插件
//        classpath("io.ktor:ktor-serialization-jackson:3.2.0")
    }
}


plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}