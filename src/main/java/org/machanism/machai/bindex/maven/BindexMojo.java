package org.machanism.machai.bindex.maven;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
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
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.machanism.macha.core.commons.configurator.PropertiesConfigurator;
import org.machanism.machai.ai.manager.UsageStatistics;
import org.machanism.machai.ai.provider.AbstractAIProvider;
import org.machanism.machai.gw.processor.ActProcessor;
import org.machanism.machai.gw.processor.GWConstants;
import org.machanism.machai.gw.tools.ProcessTerminationException;
import org.machanism.machai.project.layout.MavenProjectLayout;
import org.machanism.machai.project.layout.ProjectLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mojo(name = "bindex", aggregator = true, threadSafe = true, requiresProject = false, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class BindexMojo extends AbstractMojo {

	static final Logger logger = LoggerFactory.getLogger(BindexMojo.class);

	/**
	 * Configuration property name for the target GenAI server identifier.
	 */
	public static final String SERVERID_PROP_NAME = "genai.serverId";

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
	@Parameter(property = SERVERID_PROP_NAME, required = false)
	private String serverId;

	/**
	 * Reactor projects available in the current Maven session.
	 */
	@Parameter(defaultValue = "${reactorProjects}", readonly = true)
	protected List<MavenProject> reactorProjects;

	/**
	 * Additional key-value configuration entries merged into the processor
	 * configuration.
	 *
	 * <p>For example, plugin XML can provide
	 * {@code <params><timeout>30</timeout></params>}.</p>
	 */
	@Parameter
	protected Map<String, String> params;

	/**
	 * Optional configuration file used when no Maven server id is configured.
	 * For example, {@code -D} followed by {@link GWConstants#CONFIG_PROP_NAME}
	 * followed by {@code =machai.properties} selects a custom configuration
	 * file.
	 */
	@Parameter(property = GWConstants.CONFIG_PROP_NAME, required = false)
	private File configFile;

	/**
	 * Executes the interactive action and scans documents using the configured
	 * action prompt.
	 *
	 * <p>
	 * This method initializes usage statistics, configures the processor, and
	 * executes the document scanning workflow. It supports interactive mode,
	 * parallel execution, and non-recursive processing based on the current Maven
	 * session and configuration.
	 * </p>
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

	/**
	 * Updates the Maven project layout with the appropriate model configuration.
	 *
	 * @param mavenProjectLayout the Maven project layout to update
	 * @param model              the Maven model containing project metadata
	 */
	private void updateMavenProjectLayout(MavenProjectLayout mavenProjectLayout, Model model) {
		for (MavenProject mavenProject : session.getAllProjects()) {
			if (Strings.CS.equals(mavenProject.getArtifactId(), model.getArtifactId())) {
				mavenProjectLayout.model(mavenProject.getModel());
				break;
			}
		}
	}

	/**
	 * Processes the document scanning workflow using the provided processor.
	 *
	 * @param actProcessor the processor configured for scanning and processing
	 *                     documents
	 * @throws MojoExecutionException if an error occurs during processing
	 */
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

	/**
	 * Configures and executes the document scanning workflow.
	 *
	 * @param actProcessor the processor configured for scanning and processing
	 *                     documents
	 * @throws MojoExecutionException if scanning or processing fails
	 * @throws IOException            if an I/O error occurs
	 */
	public void configureAndScan(ActProcessor actProcessor) throws MojoExecutionException, IOException {
		actProcessor.setAct("bindex");
		scanDocuments(actProcessor);
	}

	/**
	 * Scans documents in the specified project context using the configured
	 * processor.
	 *
	 * @param actProcessor the processor configured for scanning and processing
	 *                     documents
	 * @throws IOException if an I/O error occurs during scanning
	 */
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
		} else {
			try {
				String configPath = configFile != null ? configFile.getAbsolutePath() : GWConstants.GW_CONFIG_FILE_NAME;
				config.setConfiguration(configPath);
				logger.info("Configuration successfully loaded from: " + configPath);
			} catch (IOException e) {
				if (configFile != null) {
					throw new MojoExecutionException("Failed to load configuration from: " + configFile, e);
				}
			}
		}

		if (params != null) {
			params.entrySet().stream().forEach(e -> config.set(e.getKey(), e.getValue()));
		}

		return config;
	}
}