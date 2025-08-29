
# GEMINI.md

## Project Overview

This is a Kotlin-based project that uses Gradle for dependency management. It appears to be a backend for a shop application, with a focus on clean architecture principles.

The project is structured into the following modules:

*   `app`: The main application module, responsible for starting the application and tying together the other modules.
*   `utils`: A module for utility classes.
*   `domain`: The core business logic module, containing the domain entities and repository interfaces. This module is independent of any specific framework or database implementation.
*   `data`: The data persistence module, responsible for implementing the repository interfaces defined in the `domain` module. This module uses Kotlin Exposed for database access.

## Building and Running

*   **Build:** `./gradlew build`
*   **Run:** `./gradlew run`
*   **Test:** `./gradlew check`

## Development Conventions

*   **Clean Architecture:** The project follows clean architecture principles, with a clear separation of concerns between the `domain` and `data` modules.
*   **Dependency Management:** Dependencies are managed using a version catalog (`gradle/libs.versions.toml`).
*   **Database:** The project uses Kotlin Exposed for database access, with an in-memory H2 database for development.
