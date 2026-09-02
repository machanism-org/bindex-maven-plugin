package org.machanism.machai.bindex.maven;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.machanism.machai.gw.maven.AbstractActMojo;
import org.machanism.machai.gw.processor.GWConstants;

/**
 * Aggregated Maven goal that generates Bindex metadata for the project reactor.
 */
@Mojo(name = "bindex", aggregator = true, threadSafe = true, requiresProject = false, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class BindexMojo extends AbstractActMojo {

	/**
	 * Creates the aggregated Bindex goal.
	 */
	public BindexMojo() {
		super();
	}

	/**
	 * @param prompter the prompter to set
	 */
	@Inject
	public void setPrompter(Prompter prompter) {
		this.prompter = prompter;
	}

	/**
	 * Executes the aggregated Bindex workflow.
	 *
	 * @throws MojoExecutionException if the workflow cannot be executed
	 */
	@Override
	public void execute() throws MojoExecutionException {
		performAct("bindex");
	}

	@Parameter(readonly = true, defaultValue = "${settings}")
	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	/**
	 * @param session the session to set
	 */
	@Parameter(defaultValue = "${session}", readonly = true, required = true)
	public void setSession(MavenSession session) {
		this.session = session;
	}

	/**
	 * @param model the model to set
	 */
	@Parameter(property = GWConstants.MODEL_PROP_NAME)
	public void setModel(String model) {
		this.model = model;
	}

	@Parameter(defaultValue = "${basedir}", required = true)
	public void setBasedir(File basedir) {
		this.basedir = basedir;
	}

	@Parameter(property = GWConstants.PATH_PROP_NAME)
	public void setPath(String path) {
		this.path = path;
	}

	@Parameter(property = GWConstants.INSTRUCTIONS_PROP_NAME, name = "instructions")
	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	@Parameter(property = GWConstants.EXCLUDES_PROP_NAME, name = "excludes")
	public void setExcludes(String[] excludes) {
		this.excludes = excludes;
	}

	@Parameter(readonly = true, defaultValue = "${project}")
	public void setProject(MavenProject project) {
		this.project = project;
	}

	@Parameter(property = SERVERID_PROP_NAME, required = false)
	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	@Parameter(defaultValue = "${reactorProjects}", readonly = true)
	public void setReactorProjects(List<MavenProject> reactorProjects) {
		this.reactorProjects = reactorProjects;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	@Parameter(property = GWConstants.CONFIG_PROP_NAME, required = false)
	public void setConfigFile(File configFile) {
		this.configFile = configFile;
	}

}
