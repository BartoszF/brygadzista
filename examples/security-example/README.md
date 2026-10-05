# Security example

This Spring Boot application uses Spring Security Basic authentication while
letting action types define which authenticated users may dispatch them. HTTP
paths are all permitted; authorization happens when the action is dispatched.

Run it from the repository root with:

```text
gradlew :examples:security-example:bootRun
```

Endpoints:

```text
GET http://localhost:8080/public
GET http://localhost:8080/authorized   # Basic auth: user:user or admin:admin
GET http://localhost:8080/admin        # Basic auth: admin:admin
```

The application creates two in-memory users:

| Username | Password | Role |
| --- | --- | --- |
| `user` | `user` | `USER` |
| `admin` | `admin` | `ADMIN` |

`PublicAction` uses the base `ActionContext`. `AuthorizedAction` and
`AdminAction` use `AuthenticatedActionContext`, which contains the username
and `UserRole`. The context factory rejects missing authentication and rejects
non-admin users for admin actions.
