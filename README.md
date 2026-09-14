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

Bindex Maven Plugin integrates Machai workflows with Maven to generate and register Bindex metadata. It supplies four thread-safe goals: the reactor-wide `bindex` and `register` goals run once and may be invoked without a Maven project, while `bindex-per-module` and `register-per-module` run in the context of each Maven module. Generation goals delegate to the `bindex` Machai Act; registration goals delegate to `bindex/register`.

All goals inherit shared Ghostwriter workflow configuration. Maven supplies the current session, project where available, effective settings, and base directory; the workflow can additionally receive a configuration file, model selection, instructions, exclusions, a selected Maven server, and action-specific parameters. This makes the plugin suitable for both reactor-level metadata processing and module-level builds.

## Project Structure

Maven invokes four plugin goals: aggregator goals process a reactor once, and per-module goals process each module. Each goal delegates to Machai Ghostwriter, which scans project content and uses Bindex Core and its registry to generate or register metadata. When the selected workflow needs model assistance, Ghostwriter contacts the configured GenAI provider. Maven supplies build, reactor, project, and settings context to the goals; the registration goals reuse the configuration of their corresponding generation goals.

![Bindex Maven Plugin component diagram](src/site/resources/images/c4-diagram.png)

## Introduction

The plugin makes Bindex metadata workflows available from Maven builds. Use `bindex` to generate metadata for a reactor or `bindex-per-module` for each module; use `register` or `register-per-module` to run the corresponding registration workflow. Aggregator goals can run without a Maven project, while per-module goals operate with the current module's project context.

## Usage

The plugin delegates model execution to Machai Ghostwriter; provider selection and provider-specific settings are interpreted by the transitive Machai GenAI client. Select a model with `gw.model` and, when credentials are needed, select a Maven `settings.xml` server with `genai.serverId`.

### Supported AI providers

| Provider | Model selection | Configuration |
| --- | --- | --- |
| **OpenAI** | Use an OpenAI model, for example `gpt-4o-mini`. | Configure the OpenAI credential and any endpoint options in the selected workflow or server configuration. OpenAI-compatible endpoints are supported by the underlying provider. |
| **Anthropic** | Use a Claude model, for example `claude-3-5-sonnet-latest`. | Configure Anthropic credentials and optional provider settings in the selected workflow or server configuration. |
| **CodeMie** | Use the CodeMie model identifier required by the configured service. | Configure CodeMie authentication in the selected server or workflow configuration. CodeMie routes supported `gpt-*`, `gemini-*`, and embedding models through its OpenAI-compatible path, and `claude-*` models through its Anthropic path. |
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
| `project` | `${project}` | Current Maven project; optional for aggregator goals and required as the execution context for per-module goals. | Maven-supplied project when available |
| `settings` | `${settings}` | Effective Maven settings, including server entries. | Maven-supplied settings |

Configure credentials in Maven `settings.xml`:

```xml
<server>
  <id>machai-genai</id>
  <username>your-api-user</username>
  <password>your-api-key</password>
</server>
```

Generate Bindex metadata for the current reactor:

```bash
mvn bindex:bindex -Dgw.model=gpt-4o-mini -Dgenai.serverId=machai-genai
```

## Resources

- [Machai platform](https://machai.machanism.org/)
- [Bindex Maven Plugin on Maven Central](https://central.sonatype.com/artifact/org.machanism.machai/bindex-maven-plugin)
- [Source repository](https://github.com/machanism-org/bindex-maven-plugin)
- [Bindex metadata](https://raw.githubusercontent.com/machanism-org/bindex-maven-plugin/refs/heads/main/bindex.json)
