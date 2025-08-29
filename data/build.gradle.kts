
plugins {
    id("buildsrc.convention.kotlin-jvm")
}

val exposedVersion = "1.0.0-beta-5"

dependencies {
    implementation(project(":domain"))
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("com.h2database:h2:2.1.214")
    implementation("com.arakelian:faker:3.0.0")
    testImplementation(kotlin("test-junit5"))
}
