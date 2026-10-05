# Examples

Each immediate subdirectory is a standalone Gradle project demonstrating a
focused use of Brygadzista. Every example owns its `build.gradle.kts`, source
tree, and README, and must be included explicitly in the repository's
`settings.gradle.kts`.

Available examples:

- [`simple-example`](simple-example/) — a Spring Boot application with one
  action, handler, and controller endpoint.
- [`multiple-contexts`](multiple-contexts/) — two controllers dispatching
  actions with different custom contexts to the same service.
- [`security-example`](security-example/) — Spring Security Basic authentication
  with action-driven public, authenticated, and admin access.
