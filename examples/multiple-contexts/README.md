# Multiple contexts example

This Spring Boot application uses two action types and two custom context
types. Their handlers call the same service method with different origins.

Run it from the repository root with:

```text
gradlew :examples:multiple-contexts:bootRun
```

Then request:

```text
GET http://localhost:8080/human/greetings/Ada
{"message":"Hello, Ada from human!"}

GET http://localhost:8080/robot/greetings/R2-D2
{"message":"Hello, R2-D2 from robot!"}
```
