val ktorVersion = "2.3.8"
val exposedVersion = "1.0.0-beta-5"
val h2Version = "2.2.224"
val hikariCpVersion = "5.1.0"
val flywayVersion = "10.7.1"
val logbackVersion = "1.4.11"
val assertjVersion = "3.24.2"
val restAssuredVersion = "5.3.2"
val junitVersion = "5.10.1"
val mysqlVersion = "8.0.32"
val mssqlVersion = "12.2.0.jre11"

plugins {
    //kotlin("jvm") version "2.0.21"
    //kotlin("plugin.serialization") version "2.0.21"
    //id("org.jetbrains.kotlinx.kover") version "0.7.4"
    application
}

repositories {
    mavenCentral()
//    google()
}

val langchain4jVersion = "1.3.0"

dependencies {

    testImplementation("org.assertj:assertj-core:$assertjVersion")
    testImplementation("io.rest-assured:rest-assured:$restAssuredVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

application {
    mainClass.set("MainKt")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
