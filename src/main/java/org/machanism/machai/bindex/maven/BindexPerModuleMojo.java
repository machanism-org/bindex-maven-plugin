package org.machanism.machai.bindex.maven;

import java.io.File;
import java.util.Map;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;
import org.machanism.machai.gw.maven.AbstractActPerModuleMojo;
import org.machanism.machai.gw.processor.GWConstants;

/**
 * Maven goal that generates Bindex metadata in the context of each module in a
 * multi-module reactor.
 *
 * <p>Unlike {@link BindexMojo}, this non-aggregating goal is invoked separately
 * for every Maven project to which it is bound. It delegates to the
 * {@code bindex} Machai Act after the inherited per-module infrastructure has
 * prepared the current project, reactor session, settings, and configured
 * workflow parameters.</p>
 *
 * <p>Use this goal when Bindex generation must be associated with individual
 * module execution, for example through {@code mvn bindex:bindex-per-module}.
 * Use {@link BindexMojo} instead when one reactor-wide invocation is desired.</p>
 *
 * @since 1.0
 */
@Mojo(name = "bindex-per-module", aggregator = false, threadSafe = true, requiresProject = true, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class BindexPerModuleMojo extends AbstractActPerModuleMojo {

	/**
	 * Creates the per-module Bindex goal.
	 *
	 * <p>Maven instantiates the mojo reflectively and injects the annotated
	 * parameters before execution.</p>
	 */
	public BindexPerModuleMojo() {
		super();
	}

	/**
	 * Executes the Bindex workflow for the current Maven module.
	 *
	 * @throws MojoExecutionException if the workflow cannot be prepared or
	 *         executed successfully
	 * @throws MojoFailureException if the workflow reports a build failure that
	 *         is not an execution error
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		performAct("bindex");
	}

	/**
	 * Supplies the effective Maven settings for this module execution.
	 *
	 * @param settings effective settings supplied by Maven; may be {@code null}
	 *        when Maven does not provide settings
	 */
	@Parameter(readonly = true, defaultValue = "${settings}")
	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	/**
	 * Supplies the active Maven session, including reactor execution context.
	 *
	 * @param session active Maven session supplied by Maven; required for goal
	 *        execution
	 */
	@Parameter(defaultValue = "${session}", readonly = true, required = true)
	public void setSession(MavenSession session) {
		this.session = session;
	}

	/**
	 * Supplies the Maven project for the module currently being processed.
	 *
	 * @param project current Maven project; required because this goal executes
	 *        per module
	 */
	@Parameter(readonly = true, defaultValue = "${project}")
	public void setProject(MavenProject project) {
		this.project = project;
	}

	/**
	 * Sets the module base directory used to resolve workflow-relative paths.
	 *
	 * @param basedir base directory of the current Maven invocation; required for
	 *        goal execution
	 */
	@Parameter(defaultValue = "${basedir}", required = true)
	public void setBasedir(File basedir) {
		this.basedir = basedir;
	}

	/**
	 * Sets an optional workflow configuration file.
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
	 * @param model model identifier, or {@code null} to use the workflow's
	 *        configured default
	 */
	@Parameter(property = GWConstants.MODEL_PROP_NAME)
	public void setModel(String model) {
		this.model = model;
	}

	/**
	 * Sets supplemental instructions passed to the Bindex workflow.
	 *
	 * @param instructions additional workflow instructions, or {@code null} when
	 *        none are configured
	 */
	@Parameter(property = GWConstants.INSTRUCTIONS_PROP_NAME, name = "instructions")
	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	/**
	 * Sets path patterns that the workflow must skip for the current module.
	 *
	 * @param excludes exclusion patterns, or {@code null} when no exclusions are
	 *        configured
	 */
	@Parameter(property = GWConstants.EXCLUDES_PROP_NAME, name = "excludes")
	public void setExcludes(String[] excludes) {
		this.excludes = excludes;
	}

	/**
	 * Selects the Maven server entry that supplies workflow credentials.
	 *
	 * @param serverId Maven {@code settings.xml} server identifier, or
	 *        {@code null} when no server entry is selected explicitly
	 */
	@Parameter(property = SERVERID_PROP_NAME, required = false)
	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	/**
	 * Sets arbitrary parameters to forward unchanged to the Machai Act.
	 *
	 * @param params parameter names and values, or {@code null} when no extra
	 *        parameters are configured
	 */
	@Parameter
	public void setParams(Map<String, String> params) {
		this.params = params;
	}

}
