/**
 * Maven goals for running Bindex actions through the Machai Ghostwriter
 * document-processing workflow.
 *
 * <p>
 * The goals in this package scan a project or Maven reactor for documentation
 * files and delegate action execution to an {@link org.machanism.machai.gw.processor.ActProcessor}.
 * The processor can use embedded {@code @guidance:} blocks and configured
 * instructions to guide transformations. The aggregator goal can coordinate
 * processing across the reactor, while the per-module goal integrates with
 * Maven's normal per-module execution.
 * </p>
 *
 * <h2>Goals</h2>
 * <ul>
 * <li>
 * {@link org.machanism.machai.bindex.maven.BindexMojo} ({@code gw:bindex})
 * runs an action from the aggregation point. It can be invoked without a
 * Maven project and resolves the scan path from {@code -Dgw.path} or the
 * current project directory.
 * </li>
 * <li>
 * {@link org.machanism.machai.bindex.maven.BindexPerModuleMojo}
 * ({@code gw:act-per-module}) runs the action in the execution-root context
 * during a standard Maven reactor build and avoids duplicate module scans.
 * </li>
 * </ul>
 *
 * <h2>Configuration</h2>
 * <p>
 * Common configuration includes {@code -Dgw.model} for selecting the provider
 * model, {@code -Dgw.path} for selecting the documentation root, and
 * {@code -Dgenai.serverId} for selecting credentials from the matching
 * {@code &lt;server&gt;} entry in {@code ~/.m2/settings.xml}. Username, password,
 * and child configuration values from that server are passed to the workflow.
 * </p>
 *
 * <h2>Usage examples</h2>
 * <pre>
 * mvn gw:bindex -Dgw.path=src/site
 * mvn gw:act-per-module -Dgw.path=src/site
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
