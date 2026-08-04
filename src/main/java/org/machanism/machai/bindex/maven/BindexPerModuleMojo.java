package org.machanism.machai.bindex.maven;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.machanism.macha.core.commons.configurator.PropertiesConfigurator;
import org.machanism.machai.ai.manager.UsageStatistics;
import org.machanism.machai.gw.processor.ActProcessor;
import org.machanism.machai.gw.processor.GWConstants;
import org.machanism.machai.gw.tools.ProcessTerminationException;
import org.machanism.machai.project.ProjectLayoutManager;
import org.machanism.machai.project.layout.MavenProjectLayout;
import org.machanism.machai.project.layout.ProjectLayout;

@Mojo(name = "bindex-per-module", aggregator = false, threadSafe = true, requiresProject = true, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class BindexPerModuleMojo extends BindexMojo {

	@Override
	public void execute() throws MojoExecutionException {
		UsageStatistics.init();
		
		List<MavenProject> modules = session.getAllProjects();
		boolean nonRecursive = project.getModules().size() > 1 && modules.size() == 1;
		String executionRootDirectory = session.getExecutionRootDirectory();
		boolean isExecutionRootProject = executionRootDirectory.equals(basedir.getAbsolutePath());
		PropertiesConfigurator configuration = getConfiguration();

		Properties userProperties = session.getUserProperties();
		boolean nonRecursiveProp = (boolean) ObjectUtils
				.getIfNull(userProperties.get(GWConstants.NONRECURSIVE_PROP_NAME), nonRecursive);

		if (isExecutionRootProject || !nonRecursiveProp) {

			File projectDir = new File(session.getExecutionRootDirectory());

			String model = configuration.get(GWConstants.MODEL_PROP_NAME, this.model);
			if (model != null) {
				logger.info("Model: {}", model);
			}
			
			ActProcessor actProcessor = new ActProcessor(projectDir, model, configuration) {

				@Override
				public ProjectLayout getProjectLayout(File projectDir) throws FileNotFoundException {
					ProjectLayout projectLayout = ProjectLayoutManager.detectProjectLayout(projectDir);

					if (projectLayout instanceof MavenProjectLayout) {
						MavenProjectLayout mavenProjectLayout = (MavenProjectLayout) projectLayout;
						mavenProjectLayout.projectDir(projectDir);
						Model model = project.getModel();
						mavenProjectLayout.model(model);
					}

					return projectLayout;
				}

				@Override
				protected void processModule(File projectDir, String module) throws IOException {
					// No-op for this implementation
				}
			};

			try {
				process(actProcessor);
			} catch (ProcessTerminationException e) {
				if (e.getExitCode() != 0) {
					throw e;
				}
			}
		} else {
			getLog().info("Non-recursive mode, skip scanning modules.");
		}
	}

	@Override
	protected void scanDocuments(ActProcessor actProcessor) throws IOException {
		boolean nonRecursiveConf = actProcessor.isNonRecursive();
		Properties userProperties = session.getUserProperties();
		userProperties.put(GWConstants.NONRECURSIVE_PROP_NAME, nonRecursiveConf);
		actProcessor.setNonRecursive(true);
		super.scanDocuments(actProcessor);
	}
}
