# Brygadzista documentation

Brygadzista dispatches typed actions in Kotlin Spring Boot and Ktor
applications. Spring handlers are synchronous methods on ordinary beans; Ktor
handlers are registered suspending functions.

## Guides

- [Getting started](getting-started.md) — install the library and dispatch a
  first action.
- [Dispatch lifecycle](dispatch-lifecycle.md) — see how handlers are found,
  validated, selected, and invoked.
- [Customization](customization.md) — add request context, security checks,
  auditing, and other cross-cutting behavior.
- [Ktor](ktor.md) — install the Ktor plugin and register suspending handlers.

## Capabilities

- Typed actions with typed results through `Action<R>`.
- Automatic Spring Boot configuration of `ActionDispatcher`.
- Ktor application plugin with `SuspendingActionDispatcher` access per call.
- Handler discovery through `@ActionHandler` methods on Spring beans.
- Exact runtime-class routing, including nested dispatches.
- Ordered `ActionContextFactory` extensions for request-specific data.
- Ordered `ActionInterceptor` extensions for wrapping or short-circuiting
  dispatches.
- Fail-fast startup validation for malformed or duplicate handlers.

## Boundaries

The current implementation is intentionally small. Spring dispatch is
synchronous; Ktor dispatch is coroutine-native. Neither integration provides
built-in authorization, polymorphic action routing, DI adapters, reactive/Flow
handlers, or custom configuration properties. Those concerns can be composed
from context factories, interceptors, and normal application configuration.
The [security example](../examples/security-example/) shows one Spring
composition.

For complete runnable applications, see the [examples](../examples/README.md).
