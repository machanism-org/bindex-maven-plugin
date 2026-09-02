<!-- @guidance:
**Objective:** Generate a well-structured and professional `README.md` file for the project. Follow the outlined structure and formatting requirements to ensure the README is clear, concise, and easy to navigate. If any section or content already exists, update it with the latest and most accurate information instead of duplicating or skipping it.
# Project Title and Overview
- **Title:** Add the project name as the main title.
- **Maven Badge:** Add the Maven Central badge as a new paragraph below the title: ([!\[Maven Central\](https://img.shields.io/maven-central/v/[groupId]/[artifactId].svg)](https://central.sonatype.com/artifact/[groupId]/[artifactId])
- **Description:** Review project classes.
- Use: src/site/resources/images/c4-diagram.png
# Installation Instructions
- **Repository Cloning:** Include instructions on how to clone the repository.
- **Build Instructions:** Provide steps to build the project using Maven or Gradle.
- **Prerequisites:** Specify any prerequisites, such as:
  - Required Java version.
  - Build tools (e.g., Maven, Gradle).
  - Any system dependencies.
# Usage
- **General Usage:** Explain how to run or use the project and its modules.
- **Examples:** Provide example commands, configurations, or code snippets to demonstrate usage.
- **Module-Specific Instructions:** If applicable, include usage instructions for individual modules.
# Contributing
- **Guidelines:** Outline the guidelines for contributing to the project, such as:
  - Code style or formatting requirements.
  - Pull request process.
  - Issue reporting process.
- **Encouragement:** Encourage contributions from the community.
# License
- **License Information:** State the project's license (e.g., MIT, Apache 2.0).
- **License Link:** Provide a link to the `LICENSE` file in the repository.
# Contact and Support
- **Contact Information:** Include contact details for support or inquiries.
- **Support Links:** Provide links to:
  - The project’s issue tracker.
  - Documentation or FAQs.
  - Any relevant community forums or chat channels.
-->

# Bindex Maven Plugin

[![Maven Central](https://img.shields.io/maven-central/v/org.machanism.machai/bindex-maven-plugin.svg)](https://central.sonatype.com/artifact/org.machanism.machai/bindex-maven-plugin)

Bindex Maven Plugin is a Java 17 Maven plugin that runs the Bindex action through the Machai Ghostwriter document-processing workflow. It scans a project (or an entire Maven reactor) for documentation and other governed files and delegates action execution to the Ghostwriter `ActProcessor`, applying guidance-driven, GenAI-assisted transformations to generate and register Bindex metadata.

The plugin is part of the [Machai](https://machai.machanism.org) toolkit and builds on the `ghostwriter` and `bindex-core` libraries. It resolves GenAI credentials from Maven `settings.xml` and can run either as an aggregator across the whole reactor or per module during a standard Maven build.

![Bindex Maven Plugin C4 diagram](src/site/resources/images/c4-diagram.png)

Key capabilities include:

- Aggregator-based scanning that can run without an active Maven project and resolves the scan path from `-Dgw.path` or the current directory.
- Per-module execution that integrates with Maven's standard reactor build and avoids duplicate module scans.
- Guidance-driven Bindex action execution powered by the Machai Ghostwriter `ActProcessor`.
- GenAI provider/model selection and credential resolution from `settings.xml` `<server>` entries.
- Parallel-build awareness that honors Maven's degree of concurrency.

## Goals

| Goal | Mojo | Description |
| --- | --- | --- |
| `bindex` | [`BindexMojo`](src/main/java/org/machanism/machai/bindex/maven/BindexMojo.java) | Aggregator goal that runs the Bindex action from the aggregation point. Can be invoked without a Maven project and resolves the scan path from `-Dgw.path` or the current project directory. |
| `bindex-per-module` | [`BindexPerModuleMojo`](src/main/java/org/machanism/machai/bindex/maven/BindexPerModuleMojo.java) | Per-module goal that runs the action in the execution-root context during a standard Maven reactor build, avoiding duplicate module scans. |

## Parameters

Common parameters supported by the goals:

| Parameter | Property | Description |
| --- | --- | --- |
| `model` | `-Dgw.model` | Provider/model identifier forwarded to the workflow (for example `openai:gpt-4o-mini`). |
| `path` | `-Dgw.path` | Optional scan root override. Defaults to the execution-root / project directory. |
| `instructions` | `-Dgw.instructions` | Additional instruction locations consumed by the workflow. |
| `serverId` | `-Dgenai.serverId` | `settings.xml` `<server>` id used to resolve GenAI credentials. |

## Installation

### Prerequisites

- Git
- Java 17 or later (the plugin is compiled with `maven.compiler.release` = 17)
- Maven 3.8.1 or later
- Network access to Maven Central and the configured GenAI provider when running the workflow

### Clone and build

```bat
git clone https://github.com/machanism-org/machai.git
cd machai\bindex-maven-plugin
mvn -U clean install
```

To build this module together with its required dependencies from the reactor root:

```bat
git clone https://github.com/machanism-org/machai.git
cd machai
mvn -pl bindex-maven-plugin -am clean install
```

## Usage

### Run the aggregator `bindex` goal

Run the Bindex action across the project, resolving the scan path from `-Dgw.path`:

```bat
mvn org.machanism.machai:bindex-maven-plugin:1.4.1-SNAPSHOT:bindex -Dgw.path=src\site
```

Select a specific GenAI model:

```bat
mvn org.machanism.machai:bindex-maven-plugin:1.4.1-SNAPSHOT:bindex -Dgw.model=openai:gpt-4o-mini
```

### Run the per-module `bindex-per-module` goal

Run the action in the execution-root context during a standard reactor build:

```bat
mvn org.machanism.machai:bindex-maven-plugin:1.4.1-SNAPSHOT:bindex-per-module -Dgw.path=src\site
```

### Resolve GenAI credentials from `settings.xml`

Reference a `<server>` id so the plugin can read the username, password, and any
custom configuration values into the workflow:

```bat
mvn org.machanism.machai:bindex-maven-plugin:1.4.1-SNAPSHOT:bindex -Dgenai.serverId=machai-genai
```

```xml
<!-- ~/.m2/settings.xml -->
<servers>
  <server>
    <id>machai-genai</id>
    <username>your-api-user</username>
    <password>your-api-key</password>
  </server>
</servers>
```

### Configure the plugin in a `pom.xml`

```xml
<plugin>
  <groupId>org.machanism.machai</groupId>
  <artifactId>bindex-maven-plugin</artifactId>
  <version>1.4.1-SNAPSHOT</version>
  <configuration>
    <model>openai:gpt-4o-mini</model>
    <path>src/site</path>
  </configuration>
</plugin>
```

## Contributing

Community contributions are welcome, including documentation improvements, bug fixes, and new workflow integrations.

- Follow the existing repository structure, naming conventions, and code style.
- Keep changes focused and add or update tests where applicable.
- Ensure changes remain compatible with the Java version defined by `maven.compiler.release` in `pom.xml`.
- Use GitHub Issues for bug reports and feature requests: https://github.com/machanism-org/machai/issues
- Submit pull requests with a clear summary, rationale, and reproduction details for fixes.

## License

Licensed under the Apache License, Version 2.0. See [LICENSE.txt](LICENSE.txt).

## Contact and Support

- Website: https://machai.machanism.org
- Source repository: https://github.com/machanism-org/machai
- Issue tracker: https://github.com/machanism-org/machai/issues
- Documentation: https://machai.machanism.org
- Community: https://github.com/machanism-org/machai/discussions
- Maintainer: Viktor Tovstyi (viktor.tovstyi@gmail.com)
