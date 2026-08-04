package org.machanism.machai.bindex.maven;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.machanism.macha.core.commons.configurator.PropertiesConfigurator;
import org.machanism.machai.ai.manager.UsageStatistics;
import org.machanism.machai.ai.provider.AbstractAIProvider;
import org.machanism.machai.gw.processor.ActProcessor;
import org.machanism.machai.gw.processor.GWConstants;
import org.machanism.machai.gw.processor.GuidanceProcessor;
import org.machanism.machai.gw.tools.ProcessTerminationException;
import org.machanism.machai.project.layout.MavenProjectLayout;
import org.machanism.machai.project.layout.ProjectLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Maven goal {@code gw:act} that runs an interactive, predefined "action" over
 * a documentation tree.
 *
 * <p>
 * An action is a prompt (typically sourced from a resource bundle or prompt
 * file) that is applied to scanned documents. If {@code -Dgw.act} is not
 * provided, the goal prompts the user interactively via Maven's
 * {@link Prompter} component.
 * </p>
 *
 * <h2>Parameters</h2>
 * <dl>
 * <dt><b>{@code -Dgw.act}</b> / {@code &lt;act&gt;}</dt>
 * <dd>Action text/prompt to apply. If omitted, the goal reads it
 * interactively.</dd>
 *
 * <dt><b>{@code -Dgw.acts}</b> / {@code &lt;acts&gt;}</dt>
 * <dd>Optional directory containing predefined action definitions.</dd>
 * </dl>
 *
 * <h3>Inherited parameters (from {@link AbstractGWMojo})</h3>
 * <p>
 * This goal also supports all common parameters defined by
 * {@link AbstractGWMojo} (for example {@code -Dgw.model}, {@code -Dgw.path},
 * {@code -Dgw.excludes}, {@code -Dgenai.serverId}, and {@code -DlogInputs}).
 * </p>
 *
 * <h2>Usage examples</h2>
 *
 * <pre>
 * mvn gw:act
 * </pre>
 *
 * <pre>
 * mvn gw:act -Dgw.act="Rewrite headings for clarity" -Dgw.path=src\\site
 * </pre>
 *
 * <pre>
 * mvn gw:act -Dgw.acts=src\\site\\acts -DlogInputs=true
 * </pre>
 */
@Mojo(name = "bindex", aggregator = true, threadSafe = true, requiresProject = false, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class BindexMojo extends AbstractMojo {

	static final Logger logger = LoggerFactory.getLogger(BindexMojo.class);

	/**
	 * Provider/model identifier to pass to the workflow.
	 */
	@Parameter(property = GWConstants.MODEL_PROP_NAME)
	protected String model;

	/**
	 * The Maven module base directory.
	 */
	@Parameter(defaultValue = "${basedir}", required = true)
	protected File basedir;

	/**
	 * Optional scan root override.
	 */
	@Parameter(property = GWConstants.PATH_PROP_NAME)
	String path;

	/**
	 * Instruction locations consumed by the workflow.
	 */
	@Parameter(property = GWConstants.INSTRUCTIONS_PROP_NAME, name = "instructions")
	protected String instructions;

	/**
	 * The current Maven project.
	 */
	@Parameter(readonly = true, defaultValue = "${project}")
	protected MavenProject project;

	/**
	 * The current Maven session.
	 */
	@Parameter(defaultValue = "${session}", readonly = true, required = true)
	protected MavenSession session;

	/**
	 * Maven settings used to resolve credentials from {@code settings.xml}.
	 */
	@Parameter(readonly = true, defaultValue = "${settings}")
	private Settings settings;

	/**
	 * Maven {@code server} id used to resolve GenAI credentials.
	 */
	@Parameter(property = AbstractAIProvider.SERVERID_PROP_NAME, required = false)
	private String serverId;

	/**
	 * Reactor projects available in the current Maven session.
	 */
	@Parameter(defaultValue = "${reactorProjects}", readonly = true)
	protected List<MavenProject> reactorProjects;

	/**
	 * Executes the interactive action and scans documents using the configured
	 * action prompt.
	 *
	 * @throws MojoExecutionException if an I/O failure occurs while processing
	 *                                files
	 */
	@Override
	public void execute() throws MojoExecutionException {
		UsageStatistics.init();

		PropertiesConfigurator configuration = getConfiguration();
		Boolean interactive = configuration.getBoolean(GWConstants.INTERACTIVE_MODE_PROP_NAME, null);

		String model = configuration.get(GWConstants.MODEL_PROP_NAME, this.model);
		if (model != null) {
			logger.info("Model: {}", model);
		}
		ActProcessor actProcessor = new ActProcessor(basedir, model, configuration) {
			@Override
			public ProjectLayout getProjectLayout(File projectDir) throws FileNotFoundException {
				ProjectLayout projectLayout = super.getProjectLayout(projectDir);
				projectLayout.projectDir(projectDir);

				if (projectLayout instanceof MavenProjectLayout) {
					MavenProjectLayout mavenProjectLayout = (MavenProjectLayout) projectLayout;
					Model model = mavenProjectLayout.getModel();
					updateMavenProjectLayout(mavenProjectLayout, model);
				}

				return projectLayout;
			}
		};

		List<MavenProject> modules = session.getAllProjects();
		boolean nonRecursive = project.getModules().size() > 1 && modules.size() == 1;
		actProcessor.setNonRecursive(nonRecursive);

		boolean isParallel = session.isParallel();
		if (isParallel) {
			int threads = session.getRequest().getDegreeOfConcurrency();
			actProcessor.setThreads(threads);
		}

		if (interactive != null) {
			actProcessor.setInteractive(interactive);
		}

		if (instructions != null) {
			if (logger.isInfoEnabled()) {
				logger.info("Instructions: {}", StringUtils.abbreviate(instructions, AbstractAIProvider.LOG_LINE_LENG));
			}
			actProcessor.setInstructions(instructions);
		}

		if (model != null) {
			actProcessor.setModel(model);
		}

		try {
			process(actProcessor);
		} catch (ProcessTerminationException e) {
			if (e.getExitCode() != 0) {
				throw e;
			}
		}
	}

	private void updateMavenProjectLayout(MavenProjectLayout mavenProjectLayout, Model model) {
		for (MavenProject mavenProject : session.getAllProjects()) {
			if (Strings.CS.equals(mavenProject.getArtifactId(), model.getArtifactId())) {
				mavenProjectLayout.model(mavenProject.getModel());
				break;
			}
		}
	}

	protected void process(ActProcessor actProcessor) throws MojoExecutionException {
		try {
			configureAndScan(actProcessor);

		} catch (IOException e) {
			getLog().error("I/O error occurred during file processing: " + e.getMessage());
			throw new MojoExecutionException("I/O error occurred during file processing", e);
		} finally {
			UsageStatistics.logUsage();
		}
	}

	public void configureAndScan(ActProcessor actProcessor) throws MojoExecutionException, IOException {
		actProcessor.setAct("bindex");
		scanDocuments(actProcessor);
	}

	protected void scanDocuments(ActProcessor actProcessor) throws IOException {
		String gwPaths = actProcessor.getConfigurator().get(GWConstants.PATH_PROP_NAME, null);
		String resolvedPaths = Objects.toString(path, gwPaths);
		resolvedPaths = Objects.toString(resolvedPaths, basedir.getAbsolutePath());

		logger.info("Starting scan of path: `{}`", resolvedPaths);

		actProcessor.scanDocuments(basedir, resolvedPaths);
		logger.info("Finished scanning path: {}", resolvedPaths);
	}

	/**
	 * Builds the processor configuration.
	 *
	 * <p>
	 * If a Maven server id is configured, this method reads the matching server
	 * entry from {@code settings.xml} and copies its username, password, and any
	 * custom XML configuration values into the returned configurator.
	 * </p>
	 *
	 * @return configuration for downstream workflow execution
	 * @throws MojoExecutionException if Maven settings are unavailable or the
	 *                                configured server cannot be found
	 */
	protected PropertiesConfigurator getConfiguration() throws MojoExecutionException {
		if (settings == null) {
			throw new MojoExecutionException("Maven settings are not available.");
		}

		PropertiesConfigurator config = new PropertiesConfigurator();

		if (serverId != null) {
			Server server = settings.getServer(serverId);
			if (server == null) {
				throw new MojoExecutionException("No <server> with id '" + serverId + "' found in Maven settings.xml.");
			}

			String username = server.getUsername();
			if (StringUtils.isNotBlank(username)) {
				config.set(AbstractAIProvider.USERNAME_PROP_NAME, username);
			}
			String password = server.getPassword();
			if (StringUtils.isNotBlank(password)) {
				config.set(AbstractAIProvider.PASSWORD_PROP_NAME, password);
			}

			if (server.getConfiguration() instanceof Xpp3Dom) {
				Xpp3Dom configuration = (Xpp3Dom) server.getConfiguration();
				Xpp3Dom[] children = configuration.getChildren();
				for (Xpp3Dom xpp3Dom : children) {
					config.set(xpp3Dom.getName(), xpp3Dom.getValue());
				}
			}
		}

		return config;
	}

	/**
	 * Configures and executes document scanning for the current project context.
	 *
	 * <p>
	 * This method applies configured excludes, optional instructions, input
	 * logging, and scan directory selection before invoking
	 * {@link GuidanceProcessor#scanDocuments(File, String)}. When a Maven project
	 * is present in the request, class-related helper tools are also registered
	 * with the processor.
	 * </p>
	 *
	 * @param processor the processor to configure and execute
	 * @throws MojoExecutionException if scanning or processing fails
	 */
	protected void scanDocuments(GuidanceProcessor processor) throws MojoExecutionException {

		File projectBasedir = project.getBasedir();
		if (projectBasedir == null) {
			projectBasedir = SystemUtils.getUserDir();
		}

		try {
			if (instructions != null) {
				if (logger.isInfoEnabled()) {
					logger.info("Instructions: {}",
							StringUtils.abbreviate(instructions, AbstractAIProvider.LOG_LINE_LENG));
				}
				processor.setInstructions(instructions);
			}

			File projectDir = new File(session.getExecutionRootDirectory());

			if (path == null) {
				path = projectDir.getAbsolutePath();
			}

			logger.info("Starting scan of path: `{}`", path);

			processor.scanDocuments(projectBasedir, path);
			logger.info("Scanning finished.");

		} catch (Exception e) {
			getLog().error(e);
			throw new MojoExecutionException("File processing failed.", e);

		} finally {
			UsageStatistics.logUsage();
			logger.info("File processing finished.");
		}
	}
}
