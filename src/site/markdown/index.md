<!-- @guidance:
Generate or update the content as follows.  
**Important:** If any section or content already exists, update it with the latest and most accurate information instead of duplicating or skipping it.
# Page Structure: 
1. Header
   - Project Title: need to use from pom.xml
   - Maven Central Badge ([![Maven Central](https://img.shields.io/maven-central/v/[groupId]/[artifactId].svg)](https://central.sonatype.com/artifact/[groupId]/[artifactId])
   - Bindex Badge [![bindex](https://img.shields.io/badge/bindex-blue.svg)](https://raw.githubusercontent.com/machanism-org/[artifactId]/refs/heads/main/bindex.json)
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
[![bindex](https://img.shields.io/badge/bindex-blue.svg)](https://raw.githubusercontent.com/machanism-org/bindex-maven-plugin/refs/heads/main/bindex.json)

## Overview

Bindex Maven Plugin integrates Machai workflows with Maven to generate and register Bindex metadata. It supplies four thread-safe goals: the reactor-wide `bindex` and `register` goals run once and may be invoked without a Maven project, while `bindex-per-module` and `register-per-module` run in the context of each Maven module. Generation goals delegate to the `bindex` Machai Act; registration goals delegate to `bindex/register`.

All goals inherit the shared Ghostwriter workflow configuration. Maven supplies the current session, project where available, effective settings, and base directory; the workflow can additionally receive a configuration file, model selection, instructions, exclusions, a selected Maven server, and action-specific parameters. This makes the plugin suitable for both reactor-level metadata processing and module-level builds.

![Bindex Maven Plugin component diagram](./images/c4-diagram.png)

The diagram shows Maven invoking the four mojos, which delegate to Machai Ghostwriter. Ghostwriter scans project files, uses Bindex Core and its registry to generate or register metadata, and contacts a configured GenAI provider when the selected workflow requires model assistance.

### Maven goals

| Goal | Scope | Purpose |
| --- | --- | --- |
| `bindex` | Reactor | Generates Bindex metadata once for the reactor. This aggregator goal does not require a Maven project. |
| `bindex-per-module` | Module | Generates Bindex metadata for each Maven module to which the goal is bound. |
| `register` | Reactor | Registers generated Bindex metadata once for the reactor. This aggregator goal does not require a Maven project. |
| `register-per-module` | Module | Registers generated Bindex metadata for each Maven module to which the goal is bound. |

The generation goals invoke the `bindex` Machai Act; the registration goals invoke `bindex/register`. Reactor-wide goals are useful when one metadata document represents the complete build, while per-module goals are appropriate when each module must produce or register its own metadata.

## Supported AI providers

The plugin delegates model execution to Machai Ghostwriter; provider selection and provider-specific settings are interpreted by the transitive Machai GenAI client. Select a model with `gw.model` and, when credentials are needed, select a Maven `settings.xml` server with `genai.serverId`. The runtime supports the following provider modes:

| Provider | Model selection | Configuration |
| --- | --- | --- |
| **OpenAI** | Use an OpenAI model, for example `gpt-4o-mini`. | Configure the OpenAI credential and any endpoint options in the selected workflow/server configuration. OpenAI-compatible endpoints are supported by the underlying provider. |
| **Anthropic** | Use a Claude model, for example `claude-3-5-sonnet-latest`. | Configure Anthropic credentials and optional provider settings in the selected workflow/server configuration. |
| **CodeMie** | Use the CodeMie model identifier required by the configured service. | Configure CodeMie authentication in the selected server/workflow configuration. CodeMie routes supported `gpt-*`, `gemini-*`, and embedding models through its OpenAI-compatible path, and `claude-*` models through its Anthropic path. |
| **Tools** | Use the special `yaml` model. | This local, tool-only mode executes registered function tools from structured YAML prompts and does not call a remote AI provider. |
| **None** | Use the disabled provider model; `log` enables diagnostic logging. | Use when a workflow intentionally must not perform AI work; submitted provider input is discarded. |

Keep secrets out of the POM and command history. The shared Maven integration exposes the selected server's `username` and `password` to the workflow; provider endpoints, API conventions, and additional provider options belong in the Ghostwriter workflow configuration or the selected Maven server entry.

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
| `project` | `${project}` | Current Maven project; optional for aggregator goals and required for per-module goals. | Maven-supplied project when available |
| `settings` | `${settings}` | Effective Maven settings, including server entries. | Maven-supplied settings |

The parameters are available on all four goals. For example, a per-module goal can be bound in a plugin execution:

```xml
<plugin>
  <groupId>org.machanism.machai</groupId>
  <artifactId>bindex-maven-plugin</artifactId>
  <executions>
    <execution>
      <goals>
        <goal>bindex-per-module</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

Example credential configuration:

```xml
<server>
  <id>machai-genai</id>
  <username>your-api-user</username>
  <password>your-api-key</password>
</server>
```

```bash
mvn bindex:bindex -Dgw.model=gpt-4o-mini -Dgenai.serverId=machai-genai
```

## Resources

- [Machai platform](https://machai.machanism.org/)
- [Bindex Maven Plugin GitHub repository](https://github.com/machanism-org/bindex-maven-plugin)
- [Bindex Maven Plugin on Maven Central](https://central.sonatype.com/artifact/org.machanism.machai/bindex-maven-plugin)
- [Bindex metadata](https://raw.githubusercontent.com/machanism-org/bindex-maven-plugin/refs/heads/main/bindex.json)
