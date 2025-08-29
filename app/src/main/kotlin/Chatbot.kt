// Kotlin RAG (Retrieval-Augmented Generation) Chatbot
// This example uses LangChain4j with vector embeddings to create a chatbot that answers questions based on your custom data

//import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore
// Additional imports for new features in 1.0.0
import dev.langchain4j.chain.ConversationalRetrievalChain
import dev.langchain4j.data.document.Document
import dev.langchain4j.data.document.DocumentSplitter
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader
import dev.langchain4j.data.document.splitter.DocumentSplitters
import dev.langchain4j.data.segment.TextSegment
import dev.langchain4j.model.anthropic.AnthropicChatModel
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.embedding.EmbeddingModel
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel
import dev.langchain4j.model.ollama.OllamaChatModel
import dev.langchain4j.model.ollama.OllamaStreamingChatModel
import dev.langchain4j.model.openai.OpenAiChatModel
import dev.langchain4j.model.openai.OpenAiEmbeddingModel
import dev.langchain4j.rag.content.retriever.ContentRetriever
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever
import dev.langchain4j.rag.query.Query
import dev.langchain4j.store.embedding.EmbeddingStore
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore
import kotlinx.coroutines.*
import java.io.File
import java.time.Duration
import java.util.*
import kotlin.time.measureTime

/**
 * RAG-based Chatbot that can answer questions using your custom documents
 */
class RAGChatbot(
    private val apiKey: String,
    private val embeddingModel: EmbeddingModel,
    private val chatModel: ChatModel,
    private val embeddingStore: EmbeddingStore<TextSegment>
) {
    private val contentRetriever: ContentRetriever = EmbeddingStoreContentRetriever.builder()
        .embeddingStore(embeddingStore)
        .embeddingModel(embeddingModel)
        .maxResults(5) // Retrieve top 5 relevant segments
        .minScore(0.6) // Minimum similarity score
        .build()

    private val chain = ConversationalRetrievalChain.builder()
        .chatModel(chatModel)
        .contentRetriever(contentRetriever)
        .build()

    /**
     * Process documents and add them to the knowledge base
     */
    suspend fun ingestDocuments(documentsPath: String) = withContext(Dispatchers.IO) {
        println("📚 Loading documents from: $documentsPath")

        // Load documents from directory
        val documents = FileSystemDocumentLoader.loadDocuments(documentsPath)
        println("📄 Loaded ${documents.size} documents")

        // Split documents into chunks for better retrieval
        val splitter: DocumentSplitter = DocumentSplitters.recursive(
            500,  // maxChunkSizeInTokens
            50    // maxOverlapSizeInTokens
        )

        val segments = mutableListOf<TextSegment>()
        documents.forEach { document ->
            val documentSegments = splitter.split(document)
            segments.addAll(documentSegments)
        }

        println("✂️ Split into ${segments.size} segments")

        // Generate embeddings and store them
        println("🔄 Generating embeddings...")
        segments.chunked(50).forEachIndexed { batchIndex, batch ->
            val embeddings = embeddingModel.embedAll(batch.map { it })
            batch.zip(embeddings.content()).forEach { (segment, embedding) ->
                embeddingStore.add(embedding, segment)
            }
            println("💾 Processed batch ${batchIndex + 1}/${segments.chunked(50).size}")
        }

        println("✅ Documents successfully ingested!")
    }

    /**
     * Ask a question and get an answer based on the ingested documents
     */
    suspend fun ask(question: String): String = withContext(Dispatchers.IO) {
        try {
            val response = chain.execute(question)
            return@withContext response
        } catch (e: Exception) {
            return@withContext "Sorry, I encountered an error: ${e.message}"
        }
    }

    /**
     * Search for relevant documents without generating an answer
     */
    suspend fun searchRelevantContent(query: String): List<String> = withContext(Dispatchers.IO) {
        val relevantContent = contentRetriever.retrieve(Query(query))
        return@withContext relevantContent.map { it.textSegment().text() }
    }
}

/**
 * Document processor for different file types
 */
class DocumentProcessor {
    fun processTextFile(filePath: String): Document {
        val content = File(filePath).readText()
        return Document.from(content)
    }

    fun processMarkdownFile(filePath: String): Document {
        val content = File(filePath).readText()
        // Basic markdown processing - remove headers, links, etc.
        val cleanContent = content
            .replace(Regex("#+ "), "") // Remove headers
            .replace(Regex("\\[([^\\]]+)\\]\\([^)]+\\)"), "$1") // Convert links to text
            .replace(Regex("\\*\\*([^*]+)\\*\\*"), "$1") // Remove bold formatting
        return Document.from(cleanContent)
    }

    fun processCSV(filePath: String): List<Document> {
        val lines = File(filePath).readLines()
        if (lines.isEmpty()) return emptyList()

        val headers = lines.first().split(",")
        return lines.drop(1).mapIndexed { index, line ->
            val values = line.split(",")
            val content = headers.zip(values).joinToString("\n") { (header, value) ->
                "$header: $value"
            }
            Document.from(content)
        }
    }
}

/**
 * Console interface for the RAG chatbot
 */
class RAGChatbotConsole(private val chatbot: RAGChatbot) {
    private val scanner = Scanner(System.`in`)

    suspend fun start() {
        println("🤖 RAG Chatbot Started!")
        println("Commands: 'ingest <path>' to add documents, 'search <query>' to find relevant content")
        println("Type your questions normally, or 'quit' to exit")
        println("=")

        while (true) {
            print("\n💬 You: ")
            val input = scanner.nextLine().trim()

            when {
                input.lowercase() in listOf("quit", "exit") -> {
                    println("👋 Goodbye!")
                    break
                }
                input.startsWith("ingest ") -> {
                    val path = input.substring(7).trim()
                    try {
                        chatbot.ingestDocuments(path)
                    } catch (e: Exception) {
                        println("❌ Error ingesting documents: ${e.message}")
                    }
                }
                input.startsWith("search ") -> {
                    val query = input.substring(7).trim()
                    val results = chatbot.searchRelevantContent(query)
                    println("🔍 Relevant content:")
                    results.take(3).forEachIndexed { index, content ->
                        println("${index + 1}. ${content.take(150)}...")
                    }
                }
                input.isNotBlank() -> {
                    print("🤖 Bot: ")
                    val response = chatbot.ask(input)
                    println(response)
                }
            }
        }
    }
}

/**
 * Factory class to create different types of chatbots
 */
object ChatbotFactory {
    fun createOpenAIRagChatbot(apiKey: String): RAGChatbot {
        val embeddingModel = OpenAiEmbeddingModel.builder()
            .apiKey(apiKey)
            .modelName("text-embedding-3-small") // Updated to latest model
            .build()

        val chatModel = OpenAiChatModel.builder()
            .apiKey(apiKey)
            .modelName("gpt-4o-mini") // Updated to latest model
            .temperature(0.7)
            .build()

        val embeddingStore = InMemoryEmbeddingStore<TextSegment>()

        return RAGChatbot(apiKey, embeddingModel, chatModel, embeddingStore)
    }

    fun createLocalRagChatbot(): RAGChatbot {
        // Use local embedding model - no API key needed!
        val embeddingModel = AllMiniLmL6V2EmbeddingModel()

        // Use Ollama for local LLM - make sure Ollama is running locally
        val model = "llama3.1:8b"
        val chatModel = OllamaChatModel.builder()
            .baseUrl("http://localhost:11434")
//            .modelName("llama3.2") // or any model you have installed
            .modelName(model) // or any model you have installed
            .temperature(0.7)
            .timeout(Duration.ofMinutes(5))
            .build()

        val embeddingStore = InMemoryEmbeddingStore<TextSegment>()

        return RAGChatbot("local", embeddingModel, chatModel, embeddingStore)
    }

    fun createAnthropicRagChatbot(apiKey: String): RAGChatbot {
        val embeddingModel = OpenAiEmbeddingModel.builder()
            .apiKey(apiKey)
            .build()

        val chatModel = AnthropicChatModel.builder()
            .apiKey(apiKey)
            .modelName("claude-3-sonnet-20240229")
            .build()

        val embeddingStore = InMemoryEmbeddingStore<TextSegment>()

        return RAGChatbot(apiKey, embeddingModel, chatModel, embeddingStore)
    }

    fun createChromatRagChatbot(apiKey: String, chromaUrl: String = "http://localhost:8000"): RAGChatbot {
        val embeddingModel = OpenAiEmbeddingModel.builder()
            .apiKey(apiKey)
            .build()

        val chatModel = OpenAiChatModel.builder()
            .apiKey(apiKey)
            .build()

        val embeddingStore = ChromaEmbeddingStore.builder()
            .baseUrl(chromaUrl)
            .collectionName("chatbot-knowledge")
            .build()

        return RAGChatbot(apiKey, embeddingModel, chatModel, embeddingStore)
    }
}

// Usage example
suspend fun main() {
    try {
        println("🚀 Kotlin RAG Chatbot")
        println("Choose your setup:")
        println("1. OpenAI-powered RAG (requires API key)")
        println("2. Local RAG (basic similarity matching)")

        val scanner = Scanner(System.`in`)
        print("Enter choice (1 or 2): ")
        val choice = scanner.nextLine().trim()

        when (choice) {
            "1" -> {
                print("Enter your OpenAI API key: ")
                val apiKey = scanner.nextLine().trim()

                if (apiKey.isBlank()) {
                    println("❌ API key is required!")
                    return
                }

                val chatbot = ChatbotFactory.createOpenAIRagChatbot(apiKey)
                val console = RAGChatbotConsole(chatbot)
                console.start()
            }

            "2" -> {
                val localChatbot = ChatbotFactory.createLocalRagChatbot()

                println("📝 Add some sample documents:")
                println("Adding sample knowledge base...")

                // Add sample documents
                localChatbot.ingestDocuments("./docs")

                println("🤖 Local RAG Chatbot Ready!")
                println("Ask questions about the knowledge base, or 'quit' to exit")

                while (true) {
                    print("\n💬 You: ")
                    val question = scanner.nextLine().trim()

                    if (question.lowercase() in listOf("quit", "exit")) {
                        println("👋 Goodbye!")
                        break
                    }

                    if (question.isNotBlank()) {
                        val elapsedTime = measureTime {
                            val answer = localChatbot.ask(question)
                            println("🤖 Bot: $answer")
                        }
                        println("Request took: $elapsedTime")
                    }
                }
            }

            else -> {
                println("❌ Invalid choice!")
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
