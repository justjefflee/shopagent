

plugins {
    // Apply the shared build logic from a convention plugin.
    // The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
    id("buildsrc.convention.kotlin-jvm")

    // Apply the Application plugin to add support for building an executable JVM application.
    application
}

val langchain4jVersion = "1.3.0"

dependencies {
    // Project "app" depends on project "utils". (Project paths are separated with ":", so ":utils" refers to the top-level "utils" project.)
    implementation(project(":utils"))
    implementation(project(":data"))
    testImplementation(project(":domain"))

    // LangChain4j 1.0.0 - Latest stable release
    implementation("dev.langchain4j:langchain4j:${langchain4jVersion}")
    implementation("dev.langchain4j:langchain4j-kotlin:${langchain4jVersion}-beta9")
    implementation("dev.langchain4j:langchain4j-open-ai:${langchain4jVersion}")
    implementation("dev.langchain4j:langchain4j-chroma:${langchain4jVersion}-beta9")
    //implementation("dev.langchain4j:langchain4j-document-loader-file-system:${langchain4jVersion}")
    //implementation("dev.langchain4j:langchain4j-embeddings:${langchain4jVersion}")
// Optional: Additional integrations
    implementation("dev.langchain4j:langchain4j-embeddings-all-minilm-l6-v2:${langchain4jVersion}-beta9")
// Local embeddings
    implementation("dev.langchain4j:langchain4j-ollama:${langchain4jVersion}")
// Local LLM support
    implementation("dev.langchain4j:langchain4j-azure-open-ai:${langchain4jVersion}")
// Azure OpenAI
    implementation("dev.langchain4j:langchain4j-anthropic:${langchain4jVersion}")
// Claude AI
    implementation("dev.langchain4j:langchain4j-google-ai-gemini:${langchain4jVersion}")
// Gemini
// Vector databases
    implementation("dev.langchain4j:langchain4j-pinecone:${langchain4jVersion}-beta9")
    implementation("dev.langchain4j:langchain4j-weaviate:${langchain4jVersion}-beta9")
    implementation("dev.langchain4j:langchain4j-elasticsearch:${langchain4jVersion}-beta9")
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // Optional: Additional document processors
    implementation("org.apache.tika:tika-core:2.9.0") // For PDF, DOCX, etc.
    implementation("com.opencsv:opencsv:5.8") // CSV processing

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")

    // Document processing
    //implementation("org.apache.tika:tika-core:2.9.1") // For PDF, DOCX, etc.
    //implementation("com.opencsv:opencsv:5.9") // CSV processing
    //implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3") // JSON
    testImplementation(kotlin("test-junit5"))

}

application {
    // Define the Fully Qualified Name for the application main class
    // (Note that Kotlin compiles `App.kt` to a class with FQN `com.example.app.AppKt`.)
    mainClass = "org.example.app.AppKt"
}
