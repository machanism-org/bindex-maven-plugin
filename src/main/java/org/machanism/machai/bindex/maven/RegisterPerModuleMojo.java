package org.machanism.machai.bindex.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Maven goal implementation that registers Bindex metadata for an individual
 * project in a multi-module Maven reactor.
 *
 * <p>This goal uses the per-module execution infrastructure supplied by
 * {@link BindexPerModuleMojo}. Maven invokes it in the context of each module,
 * allowing the module's generated Bindex metadata and inherited Maven settings
 * to be made available to the registration workflow. The actual registration is
 * delegated to the {@code bindex/register} Machai workflow.</p>
 *
 * <p>Registry connection details, model selection, paths, and other workflow
 * options are configured through the parameters inherited from
 * {@link BindexPerModuleMojo}.</p>
 *
 * @since 1.4.1
 */
@Mojo(name = "register-per-module", aggregator = false, threadSafe = true, requiresProject = true, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class RegisterPerModuleMojo extends BindexPerModuleMojo {

	/**
	 * Executes the per-module Bindex registration workflow.
	 *
	 * <p>The workflow receives the Maven project, session, reactor, and
	 * configuration values prepared by the inherited per-module mojo. It then
	 * registers the Bindex metadata associated with the current module.</p>
	 *
	 * @throws MojoExecutionException if the registration workflow cannot be
	 *         executed successfully
	 */
	@Override
	public void execute() throws MojoExecutionException {
		performAct("bindex/register");
	}

}
