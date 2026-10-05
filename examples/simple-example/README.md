# Simple example

This directory contains a small Spring Boot application demonstrating one
action, one annotated handler, and one controller endpoint.

Run it from the repository root with:

```text
gradlew :examples:simple-example:bootRun
```

Then request `http://localhost:8080/greetings/Ada` to receive:

```json
{"message":"Hello, Ada!"}
```

The same example is explained in [the v0 documentation](../../docs/README.md).
