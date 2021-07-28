
import java.net.URI

plugins {
    java
    id("org.jetbrains.kotlin.jvm") version "1.5.10"
    id ("org.jetbrains.kotlin.plugin.serialization") version "1.5.10"
}

group = "org.Giftbox.giftbot"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
    maven {
        url = URI("https://m2.dv8tion.net/releases")
        }
    maven { url = URI("https://jitpack.io")}
    mavenLocal()
}

apply(plugin = "kotlin-kapt")
apply(plugin = "maven")
val arrowVersion = "0.13.2"
val exposedVersion = "0.31.1"
val ktorVersion = "1.6.0"
dependencies {
    //Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.5.1-native-mt")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
    //junit
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.2")

    //discord4j
    implementation ("com.discord4j:discord4j-core:3.2.0-RC1")
    implementation ("ch.qos.logback:logback-classic:1.3.0-alpha5")
    implementation ("com.sedmelluq:lavaplayer:1.3.77")

    //Klaxon
    implementation ("com.beust:klaxon:5.5")

    //Google API
    implementation ("com.google.http-client:google-http-client:1.39.2-sp.1")
    implementation ("com.google.api-client:google-api-client:1.32.1")
    implementation ("com.google.apis:google-api-services-youtube:v3-rev20210706-1.32.1")
    implementation ("com.google.http-client:google-http-client-jackson2:1.39.2-sp.1")
    implementation ("com.google.api-client:google-api-client-java6:1.32.1")
    implementation ("com.google.oauth-client:google-oauth-client-jetty:1.31.5")

    //konf
    implementation ("com.google.code.gson:gson:2.8.7")

    //HTML Parser
    implementation ("org.jsoup:jsoup:1.14.1")

    //RainbowSix
    implementation ("com.github.jan-tennert:R6StatsJava:1.3.3")

    //Arrow
    implementation ("io.arrow-kt:arrow-core:$arrowVersion")
}

tasks {
    test {
        useJUnitPlatform()
    }
}