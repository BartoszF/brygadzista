# Brygadzista

Brygadzista is a Kotlin library for action execution with Spring integration.

## Structure

- `brygadzista-api` contains public interfaces and annotations only.
- `brygadzista-impl` contains implementations and Spring integration.
- `docs` contains user-facing documentation.
- `examples` contains focused usage examples.

## Principles

- Keep the design KISS, DRY, YAGNI, and SOLID.
- Prefer composition over inheritance.
- Prefer functional Kotlin style where it improves clarity.
- Use pragmatic tests: cover behavior and boundaries without testing implementation trivia.
- Keep documentation and examples current with the public API.

## Contribution expectations

- Keep public API changes intentional and documented.
- Preserve the module boundary: API code must not depend on implementation details.
- Run the Gradle test task before submitting changes.
- Avoid adding abstractions, dependencies, or configuration without a concrete use case.
