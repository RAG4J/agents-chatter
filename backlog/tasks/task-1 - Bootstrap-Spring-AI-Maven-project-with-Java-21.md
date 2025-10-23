---
id: task-1
title: Bootstrap Spring AI Maven project with Java 21
status: To Do
assignee: []
created_date: '2025-10-23 07:26'
updated_date: '2025-10-23 07:58'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Set up a new Spring Boot application that uses Maven and targets Java 21. Configure the build to use a compatible Spring Boot version, add Spring AI dependencies, and include a minimal example demonstrating how to call the AI client so future features can build on the baseline. 

Use java package org.rag4j.chatter as a base package.

Create a modular setup with a parent pom taking care of dependency management and modules for the web-app and the event-bus.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Parent Maven project defines modules for `web-app` and `event-bus`, centralizes dependency management, and uses the `org.rag4j.chatter` base package across modules.
- [ ] #2 Maven build is configured for Java 21 (compiler plugin, language level, and toolchain) and `mvn clean verify` from the root completes successfully.
- [ ] #3 `web-app` module launches with `mvn -pl web-app spring-boot:run` using the generated code without runtime errors.
- [ ] #4 Spring AI dependency is included and a sample component demonstrates invoking the AI client with a stubbed or configurable model.

- [ ] #5 README documents how to build and run the application locally, including any environment variables needed for the Spring AI example.
<!-- AC:END -->
