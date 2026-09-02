package org.machanism.machai.bindex.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.machanism.machai.gw.maven.AbstractActPerModuleMojo;

/**
 * Maven goal that coordinates Bindex generation for a multi-module reactor.
 */
@Mojo(name = "bindex-per-module", aggregator = false, threadSafe = true, requiresProject = true, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class BindexPerModuleMojo extends AbstractActPerModuleMojo {

	/**
	 * Creates the per-module Bindex goal.
	 */
	public BindexPerModuleMojo() {
		super();
	}

	/**
	 * Executes the per-module Bindex workflow.
	 *
	 * @throws MojoExecutionException if the workflow cannot be executed
	 * @throws MojoFailureException if the goal fails without an execution error
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		performAct("bindex");
	}
}
