---
id: task-1
title: Bootstrap Spring Boot Maven project with Java 21
status: In Progress
assignee:
  - assistant
created_date: '2025-10-23 07:26'
updated_date: '2025-10-23 11:57'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Set up a new Spring Boot application that uses Maven and targets Java 21. Configure the build to use a compatible Spring Boot version and include a minimal REST endpoint so future features can build on the baseline.

Use java package org.rag4j.chatter as a base package.

Create a modular setup with a parent pom taking care of dependency management and modules for the web-app and the event-bus.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Parent Maven project defines modules for `web-app` and `event-bus`, centralizes dependency management, and uses the `org.rag4j.chatter` base package across modules.
- [ ] #2 Maven build is configured for Java 21 (compiler plugin, language level, and toolchain) and `mvn clean verify` from the root completes successfully.
- [ ] #3 `web-app` module launches with `mvn -pl web-app spring-boot:run` exposing at least one REST endpoint without runtime errors.
- [ ] #4 README documents how to build and run the application locally, including any environment variables needed for the sample endpoint.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Implementation Plan:
1. Confirm platform versions and dependencies
   - Target latest stable Spring Boot release compatible with Java 21 (e.g., Spring Boot 3.5.x) and record chosen versions in task notes.
   - Identify required Maven plugins (compiler + toolchain, spring-boot-maven-plugin) and dependency BOMs to centralize in the parent.
2. Scaffold parent Maven project
   - Create root `pom.xml` with `pom` packaging, set groupId `org.rag4j.chatter`, version, Java 21 properties, and dependency management importing the Spring Boot BOM.
   - Configure Maven Toolchains (pointing to Java 21) and the compiler plugin defaults at parent level so all modules inherit settings.
   - Declare `<modules>` entries for `web-app` and `event-bus`, leaving comments/placeholders for adding `chat-domain` (task-5) without breaking the build.
3. Initialize module structure and shared configuration
   - Add module directories with skeletal `pom.xml` files inheriting from the parent; ensure consistent artifact naming (`web-app`, `event-bus`).
   - Set default package to `org.rag4j.chatter` in module source folders and include placeholder `package-info.java` or README if needed to prevent empty-source issues.
4. Implement `web-app` Spring Boot service baseline
   - Configure module POM with `spring-boot-starter-webflux` (for future reactive message streaming) and testing dependencies.
   - Generate `WebAppApplication` main class under `org.rag4j.chatter.web` and enable component scanning rooted at `org.rag4j.chatter`.
   - Provide application configuration (e.g., `application.yaml`) with basic service metadata.
5. Provide sample REST endpoint
   - Implement a controller exposing a simple reactive endpoint returning canned data to validate the stack.
   - Write a basic unit test verifying endpoint/service behavior without external integrations.
6. Prepare `event-bus` module foundation
   - Configure module POM with Spring Boot dependencies needed for future WebSocket + Reactor work (probably `spring-boot-starter-webflux` and `spring-boot-starter-actuator`), but keep implementation minimal (e.g., `EventBusApplication` class plus placeholder configuration).
   - Expose a starter configuration or interface that upcoming task-4 message bus can extend, ensuring package alignment (`org.rag4j.chatter.eventbus`).
7. Verify Maven build and runtime workflows
   - Run `mvn -pl web-app spring-boot:run` to verify the web module launches and serves the sample endpoint.
   - Execute `mvn clean verify` from the root to confirm multi-module build health and unit tests pass.
8. Documentation updates
   - Update root README to describe project structure, modules, Java 21 requirements, how to configure toolchains, and how to run the sample endpoint.
9. Cross-task coordination notes
   - Capture in README or notes how modules will integrate with upcoming tasks (web frontend, REST+WebSocket backend, message bus) so future work understands module boundaries and dependencies.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Version assumptions: Spring Boot 3.5.5, Java 21 toolchain via Temurin JDK 21.

Local verification: `mvn clean verify` attempted but fails in sandbox because Maven cannot download dependencies without network access. Command should pass once dependencies are resolved in a connected environment.
<!-- SECTION:NOTES:END -->
