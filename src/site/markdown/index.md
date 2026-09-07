<!-- @guidance:
Generate or update the content as follows.  
**Important:** If any section or content already exists, update it with the latest and most accurate information instead of duplicating or skipping it.
# Page Structure: 
1. Header
   - Project Title: need to use from pom.xml
   - Maven Central Badge ([![Maven Central](https://img.shields.io/maven-central/v/[groupId]/[artifactId].svg)](https://central.sonatype.com/artifact/[groupId]/[artifactId])
   - Bindex Badge [![bindex](https://img.shields.io/badge/bindex-blue.svg)](https://raw.githubusercontent.com/machanism-org/machai/refs/heads/main/genai-client/bindex.json)
# Overview
   - Full description the project based on package-info.java files in source folder..
   - Use the project structure diagram by the path: `./images/c4-diagram.png` (`src/site/puml/c4-diagram.puml`).
# Supported AI providers
   - Describe all supported AP providers with configurations.
   - Table of common configuration parameters, their descriptions, and default values.
# Resources
   - List of relevant links (platform, GitHub, Maven).
-->

# Bindex Maven Plugin

[![Maven Central](https://img.shields.io/maven-central/v/org.machanism.machai/bindex-maven-plugin.svg)](https://central.sonatype.com/artifact/org.machanism.machai/bindex-maven-plugin)
[![bindex](https://img.shields.io/badge/bindex-blue.svg)](https://raw.githubusercontent.com/machanism-org/machai/refs/heads/main/genai-client/bindex.json)

## Overview

Bindex Maven Plugin integrates Machai workflows with Maven to generate and register Bindex metadata. It supplies four thread-safe goals: the reactor-wide `bindex` and `register` goals run once and may be invoked without a Maven project, while `bindex-per-module` and `register-per-module` run in the context of each Maven module. Generation goals delegate to the `bindex` Machai Act; registration goals delegate to `bindex/register`.

All goals inherit the shared Ghostwriter workflow configuration. Maven supplies the current session, project where available, effective settings, and base directory; the workflow can additionally receive a configuration file, model selection, instructions, exclusions, a selected Maven server, and action-specific parameters. This makes the plugin suitable for both reactor-level metadata processing and module-level builds.

![Bindex Maven Plugin component diagram](./images/c4-diagram.png)

The diagram shows Maven invoking the four mojos, which delegate to Machai Ghostwriter. Ghostwriter scans project files, uses Bindex Core and its registry to generate or register metadata, and contacts a configured GenAI provider when the selected workflow requires model assistance.

## Supported AI providers

The plugin itself does not implement provider-specific clients; it passes the selected model and credentials to the Machai Ghostwriter workflow. The runtime dependencies provide support for the following providers:

- **OpenAI** — select an OpenAI model through `gw.model` (for example, `openai:gpt-4o-mini`) and select a Maven `settings.xml` server containing the required credentials with `genai.serverId`.
- **Anthropic** — select the Anthropic model identifier understood by the configured workflow through `gw.model`, and use `genai.serverId` to select the Maven server entry containing its credentials.

Provider endpoints, authentication conventions, and any additional provider-specific options are workflow configuration, rather than parameters defined by this Maven plugin. Store secrets in Maven `settings.xml`, not in the project POM or command history. The server's `username` and `password` are made available to the workflow by the shared Maven integration.

### Common configuration

| Parameter | Maven property | Description | Default value |
| --- | --- | --- | --- |
| `basedir` | `${basedir}` | Base directory used to resolve relative workflow paths. | Maven invocation base directory |
| `configFile` | `gw.config` | Optional Ghostwriter workflow configuration file. | Not set; normal workflow resolution applies |
| `model` | `gw.model` | Provider/model identifier passed to the workflow. | Not set; workflow-configured default applies |
| `instructions` | `gw.instructions` | Supplemental instructions for the workflow. | Not set |
| `excludes` | `gw.excludes` | File-path patterns excluded from processing. | Not set |
| `serverId` | `genai.serverId` | ID of the Maven `settings.xml` server entry that supplies workflow credentials. | Not set |
| `params` | — | Additional action-specific values defined in plugin configuration. | Not set |
| `session` | `${session}` | Maven session and reactor context. | Maven-supplied session |
| `project` | `${project}` | Current Maven project; optional for aggregator goals and available for per-module goals. | Maven-supplied project when available |
| `settings` | `${settings}` | Effective Maven settings, including server entries. | Maven-supplied settings |

Example credential configuration:

```xml
<server>
  <id>machai-genai</id>
  <username>your-api-user</username>
  <password>your-api-key</password>
</server>
```

```bash
mvn bindex:bindex -Dgw.model=openai:gpt-4o-mini -Dgenai.serverId=machai-genai
```

## Resources

- [Machai platform](https://machai.machanism.org/)
- [Machai GitHub repository](https://github.com/machanism-org/machai)
- [Bindex Maven Plugin on Maven Central](https://central.sonatype.com/artifact/org.machanism.machai/bindex-maven-plugin)
- [Bindex metadata](https://raw.githubusercontent.com/machanism-org/machai/refs/heads/main/genai-client/bindex.json)
