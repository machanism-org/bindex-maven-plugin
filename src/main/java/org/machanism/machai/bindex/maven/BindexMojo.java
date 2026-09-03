package org.machanism.machai.bindex.maven;

import java.io.File;
import java.util.Map;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;
import org.machanism.machai.gw.maven.AbstractActMojo;
import org.machanism.machai.gw.processor.GWConstants;

/**
 * Aggregated Maven goal that generates Bindex metadata for the current Maven
 * reactor.
 *
 * <p>The goal delegates its work to the {@code bindex} Machai Act. As an
 * aggregator mojo, it is invoked once for the build rather than once for every
 * module. The inherited Act infrastructure makes Maven settings, session,
 * project context, configuration, and additional parameters available to that
 * workflow.</p>
 *
 * <p>Typical command-line usage is {@code mvn bindex:bindex}. The goal may also
 * be configured in a build and supplied with workflow-specific parameters by
 * using its Maven plugin configuration.</p>
 *
 * @since 1.0
 */
@Mojo(name = "bindex", aggregator = true, threadSafe = true, requiresProject = false, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class BindexMojo extends AbstractActMojo {

	/**
	 * Creates the aggregated Bindex goal.
	 *
	 * <p>Maven instantiates mojos reflectively. Configuration values are supplied
	 * afterwards through the annotated setter methods.</p>
	 */
	public BindexMojo() {
		super();
	}

	/**
	 * Executes the aggregated Bindex workflow.
	 *
	 * <p>This implementation delegates to the shared Act executor with the
	 * {@code bindex} Act name. The inherited implementation prepares the Maven
	 * context and applies the configured workflow settings.</p>
	 *
	 * @throws MojoExecutionException if the workflow cannot be prepared or
	 *         executed successfully
	 */
	@Override
	public void execute() throws MojoExecutionException {
		performAct("bindex");
	}

	/**
	 * Supplies the effective Maven settings for the current invocation.
	 *
	 * <p>The inherited workflow infrastructure uses these settings to resolve
	 * server credentials and other user-level Maven configuration required by the
	 * Bindex workflow.</p>
	 *
	 * @param settings effective settings supplied by Maven; may be {@code null}
	 *        when Maven does not provide settings
	 */
	@Parameter(readonly = true, defaultValue = "${settings}")
	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	/**
	 * Supplies the Maven session associated with this goal execution.
	 *
	 * <p>The session exposes reactor projects and execution state needed when the
	 * aggregated workflow discovers and processes project metadata.</p>
	 *
	 * @param session active Maven session supplied by Maven; required for goal
	 *        execution
	 */
	@Parameter(defaultValue = "${session}", readonly = true, required = true)
	public void setSession(MavenSession session) {
		this.session = session;
	}

	/**
	 * Supplies the project associated with the Maven invocation.
	 *
	 * <p>Although this aggregator goal can run without a project, Maven supplies
	 * the current project when one is available so that the workflow can use its
	 * coordinates and build context.</p>
	 *
	 * @param project current Maven project, or {@code null} when no project is
	 *        associated with the invocation
	 */
	@Parameter(readonly = true, defaultValue = "${project}")
	public void setProject(MavenProject project) {
		this.project = project;
	}

	/**
	 * Sets the base directory from which the workflow resolves relative paths.
	 *
	 * @param basedir Maven invocation base directory; required for goal execution
	 */
	@Parameter(defaultValue = "${basedir}", required = true)
	public void setBasedir(File basedir) {
		this.basedir = basedir;
	}

	/**
	 * Sets an optional workflow configuration file.
	 *
	 * <p>When present, the file is forwarded to the shared Machai workflow
	 * infrastructure to provide configuration in addition to Maven parameters.</p>
	 *
	 * @param configFile optional configuration file, or {@code null} to use the
	 *        workflow's normal configuration resolution
	 */
	@Parameter(property = GWConstants.CONFIG_PROP_NAME, required = false)
	public void setConfigFile(File configFile) {
		this.configFile = configFile;
	}

	/**
	 * Selects the AI model used by the Bindex workflow.
	 *
	 * @param model model identifier, or {@code null} to allow the workflow to use
	 *        its configured default
	 */
	@Parameter(property = GWConstants.MODEL_PROP_NAME)
	public void setModel(String model) {
		this.model = model;
	}

	/**
	 * Sets additional instructions passed to the Bindex workflow.
	 *
	 * @param instructions supplemental workflow instructions, or {@code null} if
	 *        no additional instructions are configured
	 */
	@Parameter(property = GWConstants.INSTRUCTIONS_PROP_NAME, name = "instructions")
	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	/**
	 * Sets path patterns that the workflow must exclude from processing.
	 *
	 * @param excludes exclusion patterns, or {@code null} when no exclusions are
	 *        configured
	 */
	@Parameter(property = GWConstants.EXCLUDES_PROP_NAME, name = "excludes")
	public void setExcludes(String[] excludes) {
		this.excludes = excludes;
	}

	/**
	 * Selects the Maven server entry containing credentials for the workflow.
	 *
	 * @param serverId Maven {@code settings.xml} server identifier, or
	 *        {@code null} when no server entry is selected explicitly
	 */
	@Parameter(property = SERVERID_PROP_NAME, required = false)
	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	/**
	 * Sets arbitrary workflow parameters declared in the plugin configuration.
	 *
	 * <p>Entries are passed unchanged to the Machai workflow, allowing an Act to
	 * consume configuration that is not represented by a dedicated mojo
	 * parameter.</p>
	 *
	 * @param params parameter names and values, or {@code null} when no extra
	 *        parameters are configured
	 */
	@Parameter
	public void setParams(Map<String, String> params) {
		this.params = params;
	}

}
