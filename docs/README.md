# Brygadzista documentation

Brygadzista is a synchronous action dispatcher for Kotlin and Spring Boot.
Actions are ordinary typed values, handlers are methods on ordinary Spring
beans, and the dispatcher supplies the small amount of ceremony between them.

## Guides

- [Getting started](getting-started.md) — install the library and dispatch a
  first action.
- [Dispatch lifecycle](dispatch-lifecycle.md) — see how handlers are found,
  validated, selected, and invoked.
- [Customization](customization.md) — add request context, security checks,
  auditing, and other cross-cutting behavior.

## Capabilities

- Typed actions with typed results through `Action<R>`.
- Automatic Spring Boot configuration of `ActionDispatcher`.
- Handler discovery through `@ActionHandler` methods on Spring beans.
- Exact runtime-class routing, including nested dispatches.
- Ordered `ActionContextFactory` extensions for request-specific data.
- Ordered `ActionInterceptor` extensions for wrapping or short-circuiting
  dispatches.
- Fail-fast startup validation for malformed or duplicate handlers.

## Boundaries

The current implementation is intentionally small. It is synchronous and does
not provide built-in authorization, polymorphic action routing,
coroutine/reactive handlers, or custom configuration properties. Those concerns
can be composed from context factories, interceptors, and normal Spring
configuration. The [security example](../examples/security-example/) shows one
such composition.

For complete runnable applications, see the [examples](../examples/README.md).
