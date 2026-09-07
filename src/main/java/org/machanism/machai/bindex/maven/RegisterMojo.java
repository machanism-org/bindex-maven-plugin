package org.machanism.machai.bindex.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Maven goal that registers the project's generated Bindex metadata with the
 * configured Bindex registry.
 *
 * <p>This aggregator goal does not require a Maven project and resolves compile
 * and runtime dependencies before delegating registration to the
 * {@code bindex/register} Machai workflow. Configuration inherited from
 * {@link BindexMojo} supplies the registry connection and workflow settings.</p>
 *
 * @since 1.4.1
 */
@Mojo(name = "register", aggregator = true, threadSafe = true, requiresProject = false, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class RegisterMojo extends BindexMojo {

	/**
	 * Executes the Bindex registration workflow.
	 *
	 * @throws MojoExecutionException if the registration workflow cannot be
	 *         executed
	 */
	@Override
	public void execute() throws MojoExecutionException {
		performAct("bindex/register");
	}

}
