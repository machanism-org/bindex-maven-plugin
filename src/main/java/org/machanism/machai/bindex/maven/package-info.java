/**
 * Maven plugin goals for generating and registering Bindex metadata through
 * Machai workflows.
 *
 * <p>{@link BindexMojo} provides the reactor-wide {@code bindex} goal. It is an
 * aggregator goal and may run without a Maven project. {@link BindexPerModuleMojo}
 * provides the {@code bindex-per-module} goal, which requires a project and
 * executes once for each module to which it is bound. {@link RegisterMojo} and
 * {@link RegisterPerModuleMojo} provide the corresponding reactor-wide and
 * per-module registration goals. Generation delegates to the {@code bindex}
 * Machai Act, while registration delegates to the {@code bindex/register} Act.</p>
 *
 * <table>
 * <caption>Available Maven goals</caption>
 * <tr><th>Goal</th><th>Execution scope</th><th>Purpose</th></tr>
 * <tr><td>{@code bindex}</td><td>Reactor</td>
 *     <td>Generates Bindex metadata once for the reactor.</td></tr>
 * <tr><td>{@code bindex-per-module}</td><td>Module</td>
 *     <td>Generates Bindex metadata for each bound Maven module.</td></tr>
 * <tr><td>{@code register}</td><td>Reactor</td>
 *     <td>Registers generated Bindex metadata once for the reactor.</td></tr>
 * <tr><td>{@code register-per-module}</td><td>Module</td>
 *     <td>Registers generated Bindex metadata for each bound Maven module.</td></tr>
 * </table>
 *
 * <p>All goals share workflow configuration for Maven settings and server
 * credentials, an optional configuration file, model selection, supplemental
 * instructions, exclusion patterns, and arbitrary Act parameters. Invoke the
 * reactor-wide generation goal with {@code mvn bindex:bindex}, the per-module
 * generation goal with {@code mvn bindex:bindex-per-module}, the reactor-wide
 * registration goal with {@code mvn bindex:register}, or the per-module
 * registration goal with {@code mvn bindex:register-per-module}.</p>
 *
 * <p>Goals may also be bound to Maven lifecycle phases. For example, bind the
 * per-module generation goal when every module must produce its own metadata:</p>
 *
 * <pre>
 * &lt;plugin&gt;
 *   &lt;groupId&gt;org.machanism.machai&lt;/groupId&gt;
 *   &lt;artifactId&gt;bindex-maven-plugin&lt;/artifactId&gt;
 *   &lt;executions&gt;
 *     &lt;execution&gt;
 *       &lt;goals&gt;&lt;goal&gt;bindex-per-module&lt;/goal&gt;&lt;/goals&gt;
 *     &lt;/execution&gt;
 *   &lt;/executions&gt;
 * &lt;/plugin&gt;
 * </pre>
 */
package org.machanism.machai.bindex.maven;

/*-
 * @guidance:
 *
 * **IMPORTANT: ADD OR UPDATE JAVADOC TO ALL CLASSES IN THE FOLDER AND THIS `package-info.java`!**	
 *
 * - Update Existing Javadoc and Add Missing Javadoc:
 *      - Review all classes in the folder.
 *      - Update any existing Javadoc to ensure it is accurate, comprehensive, and follows best practices.
 *      - Add Javadoc to any classes, methods, or fields where it is missing.
 *      - Ensure that all Javadoc is up-to-date and provides clear, meaningful documentation.
 * - Use Clear and Concise Descriptions:
 *      - Write meaningful summaries that explain the purpose, behavior, and usage of each element.
 *      - Avoid vague statements; be specific about functionality and intent.
 * - Update `package-info.java`:
 *      - Analyze the source code within this package.
 *      - Generate comprehensive package-level Javadoc that clearly describes the package’s overall purpose and usage.
 *      - Do not include a "Guidance and Best Practices" section in the `package-info.java` file.
 *      - Ensure the package-level Javadoc is placed immediately before the `package` declaration.
 * - Include Usage Examples Where Helpful:
 *      - Provide code snippets or examples in Javadoc comments for complex classes or methods.
 * - Maintain Consistency and Formatting:
 *      - Follow a consistent style and structure for all Javadoc comments.
 *      - Use proper Markdown or HTML formatting for readability.
 * - Add Javadoc:
 *      - Review the Java class source code and include comprehensive Javadoc comments for all classes,
 *           methods, and fields, adhering to established best practices.
 *      - Ensure that each Javadoc comment provides clear explanations of the purpose, parameters, return values,
 *           and any exceptions thrown.
 *      - When generating Javadoc, if you encounter code blocks inside `<pre>` tags, escape `<` and `>` as `&lt;`
 *           and `&gt;` as `&gt;` in `<pre>` content for Javadoc. Ensure that the code is properly escaped and formatted for Javadoc.
 *      - Do not use escaping in `{@code ...}` tags.    
 * - Use the Java Version Defined in `pom.xml`:
 *      - All code improvements and Javadoc updates must be compatible with the Java version `maven.compiler.release` specified in the project's `pom.xml`.
 *      - Do not use features or syntax that require a higher Java version than defined in `pom.xml`.
 */
