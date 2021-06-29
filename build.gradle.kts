
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
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.4.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")
    //junit
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")

    //discord4j
    implementation ("com.discord4j:discord4j-core:3.2.0-M3")
    implementation ("ch.qos.logback:logback-classic:1.3.0-alpha5")
    implementation ("com.sedmelluq:lavaplayer:1.3.77")

    //Exposed
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("mysql:mysql-connector-java:5.1.48")

    //Google API
    implementation ("com.google.http-client:google-http-client:1.39.2")
    implementation ("com.google.api-client:google-api-client:1.31.4")
    implementation ("com.google.apis:google-api-services-youtube:v3-rev20210410-1.31.0")
    implementation ("com.google.http-client:google-http-client-jackson2:1.39.2")
    implementation ("com.google.api-client:google-api-client-java6:1.31.3")
    implementation ("com.google.oauth-client:google-oauth-client-jetty:1.30.4")

    //HTML Parser
    implementation ("org.jsoup:jsoup:1.13.1")

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