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

The diagram shows Maven invoking the four mojos, which delegate to Machai Ghostwriter. Ghostwriter prepares the Maven context and executes Bindex Acts, which scan project files to generate metadata or publish it to the Bindex registry. The Acts contact a configured GenAI provider when the selected workflow requires model assistance.

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
| **OpenAI** | `OpenAI:gpt-4o-mini` | Set `OPENAI_API_KEY`; optionally override `OPENAI_BASE_URL` for an OpenAI-compatible endpoint. Supports text generation, embeddings, function tools, web search, and MCP tools. |
| **Anthropic** | `Anthropic:claude-3-5-sonnet` | Set `ANTHROPIC_API_KEY` and optionally `ANTHROPIC_BASE_URL`. Credentials beginning with `sk-` are API keys; other non-null credentials are authorization tokens. Supports Claude text generation, function tools, web search, and MCP tools. |
| **CodeMie** | `CodeMie:gpt-4o-mini` or `CodeMie:claude-3-5-sonnet` | Set `GENAI_USERNAME` and `GENAI_PASSWORD`; optionally override the token endpoint with `AUTH_URL`. A username containing `@` selects the password grant; otherwise it selects the client-credentials grant. CodeMie supplies bearer tokens and endpoints to its delegate: `gpt-*`, `gemini-*`, `text-embedding-*`, `codemie-text-embedding-*`, and `amazon.titan-embed-text-*` models use OpenAI-compatible APIs, while `claude-*` models use Anthropic APIs. |
| **Tools** | `Tools:yaml` | Local, tool-only mode executes registered function tools from a YAML prompt containing `tool` and optional `params`. It requires a matching registered tool, but no remote credentials or endpoint. |
| **None** | `None:disabled` or `None:log` | No-op mode discards submitted input and returns no result. `None:log` enables INFO-level lifecycle diagnostics. No credentials or endpoint are required. |

Keep secrets out of the POM and command history. The shared Maven integration maps the selected server's `username` and `password` to `GENAI_USERNAME` and `GENAI_PASSWORD`. Direct OpenAI and Anthropic credentials use their provider-specific keys, which can be supplied in the server's `<configuration>` element. Configuration is loaded from the workflow properties file, then overlaid with the selected Maven server's credentials and configuration, then with the plugin's `params` map.

### Common configuration

| Parameter | Maven property | Description | Default value |
| --- | --- | --- | --- |
| `basedir` | `${basedir}` | Base directory used to resolve relative workflow paths. | Maven invocation base directory |
| `configFile` | `gw.config` | Optional Ghostwriter workflow configuration file. | Not set; normal workflow resolution applies |
| `model` | `gw.model` | Provider/model identifier passed to the workflow. | Not set; workflow-configured default applies |
| `instructions` | `gw.instructions` | Supplemental instructions for the workflow. | Not set |
| `excludes` | `gw.excludes` | Path patterns excluded from processing; configuration-file values are comma-separated. | Not set |
| `serverId` | `genai.serverId` | ID of the Maven `settings.xml` server entry that supplies workflow credentials. | Not set |
| `params` | — | Additional action-specific values defined in plugin configuration. | Not set |
| `session` | `${session}` | Maven session and reactor context. | Maven-supplied session |
| `project` | `${project}` | Current Maven project; optional for aggregator goals and required for per-module goals. | Maven-supplied project when available |
| `settings` | `${settings}` | Effective Maven settings, including server entries. | Maven-supplied settings |

Provider settings below are configuration keys, not dedicated Maven plugin parameters. Supply them through the workflow properties file selected by `gw.config`, the selected server's `<configuration>`, or the plugin's `params` map.

| Configuration key | Description | Default value |
| --- | --- | --- |
| `OPENAI_API_KEY` | Credential for direct OpenAI or compatible API access. | Required for direct OpenAI access |
| `OPENAI_BASE_URL` | OpenAI-compatible API endpoint override. | OpenAI SDK default |
| `ANTHROPIC_API_KEY` | Claude API key or authorization token. | Required for direct Anthropic access |
| `ANTHROPIC_BASE_URL` | Anthropic-compatible API endpoint override. | Anthropic SDK default |
| `GENAI_USERNAME` | CodeMie user name or client ID; populated from the selected Maven server's username. | Not set |
| `GENAI_PASSWORD` | CodeMie password or client secret; populated from the selected Maven server's password. | Not set |
| `AUTH_URL` | CodeMie OpenID Connect token endpoint override. | CodeMie default endpoint |
| `GENAI_TIMEOUT` | Request timeout in seconds; `0` leaves the SDK default in effect. | `0` |
| `MAX_OUTPUT_TOKENS` | Maximum generated output tokens. | `18000` |
| `MAX_TOOL_CALLS` | OpenAI Responses API tool-call limit; `0` leaves it unset. Does not limit Anthropic tool-use loops. | `0` |
| `WebSearchTool.type` | Enables supported provider-specific web search; `default` selects the provider default type. | Not set |
| `WebSearchTool.city`, `WebSearchTool.country`, `WebSearchTool.region` | Optional web-search user-location hints. | Not set |
| `MCP.name`, `MCP.url` | MCP server name and URL; a name is required to register a group. Additional groups use `MCP_1.*`, `MCP_2.*`, and so on. | Not set |
| `MCP.authorization`, `MCP.description` | Optional MCP server authorization and description, including numbered groups. | Not set |

The Maven parameters are available on all four goals. For example, a per-module goal can be bound to a lifecycle phase in a plugin execution:

```xml
<plugin>
  <groupId>org.machanism.machai</groupId>
  <artifactId>bindex-maven-plugin</artifactId>
  <executions>
    <execution>
      <phase>verify</phase>
      <goals>
        <goal>bindex-per-module</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

Example direct OpenAI credential configuration in Maven `settings.xml` (replace the illustrative key locally; do not commit credentials):

```xml
<server>
  <id>machai-genai</id>
  <configuration>
    <OPENAI_API_KEY>your-api-key</OPENAI_API_KEY>
  </configuration>
</server>
```

```bash
mvn bindex:bindex -Dgw.model=OpenAI:gpt-4o-mini -Dgenai.serverId=machai-genai
```

## Resources

- [Machai platform](https://machai.machanism.org/)
- [GenAI Client provider documentation](https://machai.machanism.org/genai-client/index.html)
- [Bindex Maven Plugin GitHub repository](https://github.com/machanism-org/bindex-maven-plugin)
- [Bindex Maven Plugin on Maven Central](https://central.sonatype.com/artifact/org.machanism.machai/bindex-maven-plugin)
- [Bindex metadata](https://raw.githubusercontent.com/machanism-org/bindex-maven-plugin/refs/heads/main/bindex.json)
