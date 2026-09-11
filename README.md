<!-- @guidance: >>> ${guidances}/readme-content.md -->

# Bindex Maven Plugin

[![Maven Central](https://img.shields.io/maven-central/v/org.machanism.machai/bindex-maven-plugin.svg)](https://central.sonatype.com/artifact/org.machanism.machai/bindex-maven-plugin) [![bindex](https://img.shields.io/badge/bindex-blue.svg)](https://raw.githubusercontent.com/machanism-org/bindex-maven-plugin/refs/heads/main/bindex.json)

Maven plugin that generates Bindex metadata for Maven projects and reactor builds.

## Cloning and Getting Started

To clone and set up this project locally, follow these steps:

1. **Clone the repository:**
   ```bash
   git clone https://github.com/machanism-org/bindex-maven-plugin.git
   cd bindex-maven-plugin
   ```
2. **Build the project using Maven:**
   ```bash
   mvn clean install
   ```

## Overview

Bindex Maven Plugin integrates Machai workflows with Maven to generate and register Bindex metadata. It provides reactor-wide and per-module goals: `bindex` and `bindex-per-module` generate metadata, while `register` and `register-per-module` register generated metadata. Reactor-wide goals execute once and can run without a Maven project; per-module goals execute for each module to which they are bound.

All goals inherit shared Ghostwriter workflow configuration. Maven provides the active session, project when available, effective settings, and base directory. The workflow can additionally receive a configuration file, model selection, instructions, exclusion patterns, a Maven server selection, and action-specific parameters.

## Project Structure

![Bindex Maven Plugin component diagram](src/site/resources/images/c4-diagram.png)

A Maven developer invokes one of four plugin goals. The generation goals and registration goals delegate their respective workflows to Machai Ghostwriter, which scans project content and uses Bindex Core and its registry to generate or register metadata. When required by the selected workflow, Ghostwriter also requests assistance from a configured GenAI provider.

## Introduction

Use the plugin when Bindex metadata must be generated or registered as part of a Maven build. Choose an aggregator goal for one reactor-wide operation, or choose a per-module goal to run in each Maven project. The plugin passes Maven build context and workflow settings to the selected Machai Act, allowing the same workflow configuration to be used consistently across local and reactor builds.

## Usage

Run a generation or registration goal from a Maven project:

```bash
mvn bindex:bindex
mvn bindex:bindex-per-module
mvn bindex:register
mvn bindex:register-per-module
```

Select a workflow model and a Maven `settings.xml` server that contains its credentials:

```bash
mvn bindex:bindex -Dgw.model=openai:gpt-4o-mini -Dgenai.serverId=machai-genai
```

Configure the referenced server in your Maven settings and keep credentials out of the project POM and command history:

```xml
<server>
  <id>machai-genai</id>
  <username>your-api-user</username>
  <password>your-api-key</password>
</server>
```

Common workflow properties are `gw.config` for an optional configuration file, `gw.model` for the provider/model identifier, `gw.instructions` for supplemental instructions, `gw.excludes` for excluded paths, and `genai.serverId` for the Maven server that supplies credentials. Additional action-specific values can be configured through the plugin's `params` configuration.

## Resources

- [Machai platform](https://machai.machanism.org/)
- [Bindex Maven Plugin on Maven Central](https://central.sonatype.com/artifact/org.machanism.machai/bindex-maven-plugin)
- [Source repository](https://github.com/machanism-org/bindex-maven-plugin)
