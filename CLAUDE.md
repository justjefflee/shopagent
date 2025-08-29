# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Structure

This is a multi-module Kotlin shop application using clean architecture principles:

- **`app`**: Main application module containing the entry point (`App.kt`). Integrates all modules and includes LangChain4j AI dependencies for chatbot functionality.
- **`domain`**: Core business logic with domain entities (`Product`) and repository interfaces. Framework-independent.
- **`data`**: Data persistence layer implementing domain repository interfaces using Kotlin Exposed and H2 database.
- **`utils`**: Utility classes and shared helper functions.
- **`buildSrc`**: Convention plugins for shared Gradle build logic across modules.

## Commands

### Build and Run
- **Build**: `./gradlew build` - Builds all modules
- **Run**: `./gradlew run` - Runs the main application
- **Clean**: `./gradlew clean` - Cleans all build outputs

### Testing
- **Run all tests**: `./gradlew check` - Runs tests and all quality checks
- **Test specific module**: `./gradlew :domain:test` or `./gradlew :data:test`

### Development
The project uses Java 21 toolchain and has build/configuration caching enabled for faster builds.

## Key Dependencies

- **LangChain4j**: AI/LLM integration with support for OpenAI, Anthropic, Gemini, Ollama, and vector databases
- **Kotlin Exposed**: Database ORM for data persistence
- **JUnit 5**: Testing framework configured across all modules

## Architecture Notes

The project follows clean architecture with clear separation:
- Domain layer contains business entities and interfaces
- Data layer implements domain interfaces using specific technologies
- Application layer orchestrates the flow between modules
- Dependencies flow inward toward the domain layer

Version catalog (`gradle/libs.versions.toml`) centralizes dependency management across modules.