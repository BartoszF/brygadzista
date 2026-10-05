# Dispatch lifecycle

## Startup discovery

After Spring creates its singleton beans, Brygadzista inspects their target
classes for methods annotated with @ActionHandler. A handler must:

- accept exactly one parameter;
- accept ActionContext<A> or a subclass of it;
- declare a concrete Action type for A.

The dispatcher registers that concrete action type against the bean method. A
malformed handler or two handlers for the same action type fail application
startup, so routing mistakes do not wait for the first request.

## Routing and context creation

When `dispatch(action)` is called, the dispatcher looks up the handler by
`action.javaClass`. Routing is exact: a handler for a base class or interface
does not also handle a subclass.

Before invoking the handler, ordered ActionContextFactory beans are asked to
create a context. The first factory that returns a non-null context wins. If no
factory applies, the dispatcher uses the base ActionContext(action).

The selected context must match the context type declared by the handler. A
factory that supplies an incompatible context fails the dispatch before the
handler runs.

## Interceptors and invocation

Ordered ActionInterceptor beans wrap the handler invocation. Each interceptor
receives the action context and a proceed function for the next layer. It may
observe success or failure, replace the result, or stop the chain without
calling proceed.

The effective flow is:

~~~text
dispatch(action)
  -> exact handler lookup
  -> first matching context factory, or base context
  -> ordered interceptors
  -> handler method
  -> result or propagated exception
~~~

Nested calls made through an injected ActionDispatcher go through the same
lookup, context, and interceptor pipeline.

## Failure behavior

- No registered handler: UnsupportedActionException.
- Duplicate or malformed handler: application startup failure.
- Context type mismatch: IllegalArgumentException before handler invocation.
- Handler or interceptor failure: the thrown exception is propagated.

The [multiple-contexts example](../examples/multiple-contexts/) demonstrates
two exact action routes sharing a service while using different context types.
