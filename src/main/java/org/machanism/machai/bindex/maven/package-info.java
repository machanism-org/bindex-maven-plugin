/**
 * Maven plugin goals for generating and registering Bindex metadata through
 * Machai workflows.
 *
 * <p>The package exposes reactor-wide and per-module Maven goals. The
 * {@code bindex} and {@code bindex-per-module} goals delegate metadata
 * generation to the {@code bindex} Machai Act. The {@code register} and
 * {@code register-per-module} goals delegate registration of generated
 * metadata to the {@code bindex/register} Act. Aggregating goals execute once
 * for a reactor and may be invoked without a project; per-module goals execute
 * for every project to which they are bound.</p>
 *
 * <p>All goals inherit common workflow configuration, including Maven settings
 * for server credentials, an optional configuration file, model selection,
 * instructions, exclusion patterns, and arbitrary Act parameters. For
 * example, invoke reactor-wide generation with
 * {@code mvn bindex:bindex}, per-module generation with
 * {@code mvn bindex:bindex-per-module}, reactor-wide registration with
 * {@code mvn bindex:register}, or per-module registration with
 * {@code mvn bindex:register-per-module}.</p>
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
