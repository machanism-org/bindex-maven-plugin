<!-- @guidance: >>> ${guidances}/readme-content.md -->
# Bindex Maven Plugin

[![Maven Central](https://img.shields.io/maven-central/v/org.machanism.machai/bindex-maven-plugin.svg)](https://central.sonatype.com/artifact/org.machanism.machai/bindex-maven-plugin) [![bindex](https://img.shields.io/badge/bindex-blue.svg)](https://raw.githubusercontent.com/machanism-org/bindex-maven-plugin/refs/heads/main/bindex.json)

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

Bindex Maven Plugin integrates Machai workflows into Maven builds to generate and register Bindex metadata. It provides reactor-wide and per-module goals so metadata processing can run once for an entire reactor or independently for each module.

## Project Structure

The plugin exposes four Maven mojos. The `bindex` and `register` aggregator goals execute their respective workflows once for a reactor, while `bindex-per-module` and `register-per-module` execute in each module context. Each mojo passes Maven session, project, settings, and workflow configuration to Machai Ghostwriter. Ghostwriter scans project content, works with Bindex Core and its registry to generate or register metadata, and can contact a configured GenAI provider when a workflow requires model assistance.

![Bindex Maven Plugin component diagram](src/site/resources/images/c4-diagram.png)

## Introduction

The plugin delegates metadata generation to the `bindex` workflow and metadata registration to the `bindex/register` workflow. Shared Ghostwriter configuration supports a workflow configuration file, a selected model, supplemental instructions, exclusions, a Maven server containing credentials, and action-specific parameters. Maven supplies the effective settings and build context, enabling consistent processing for both reactor-level and module-level executions.

## Usage

Configure credentials in your Maven `settings.xml` and select the model and server when invoking a goal. Keep credentials out of the POM and shell history.

```xml
<server>
  <id>machai-genai</id>
  <username>your-api-user</username>
  <password>your-api-key</password>
</server>
```

Generate Bindex metadata for the current reactor:

```bash
mvn bindex:bindex -Dgw.model=openai:gpt-4o-mini -Dgenai.serverId=machai-genai
```

Common workflow properties include `gw.config` for a configuration file, `gw.model` for the provider/model identifier, `gw.instructions` for additional instructions, `gw.excludes` for excluded paths, and `genai.serverId` for the Maven settings server that supplies credentials.

## Resources

- [Machai platform](https://machai.machanism.org/)
- [Bindex Maven Plugin on Maven Central](https://central.sonatype.com/artifact/org.machanism.machai/bindex-maven-plugin)
- [Source repository](https://github.com/machanism-org/bindex-maven-plugin)
- [Bindex metadata](https://raw.githubusercontent.com/machanism-org/bindex-maven-plugin/refs/heads/main/bindex.json)
