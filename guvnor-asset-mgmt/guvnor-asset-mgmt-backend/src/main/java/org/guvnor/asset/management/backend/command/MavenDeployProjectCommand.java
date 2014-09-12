package org.guvnor.asset.management.backend.command;

import java.io.ByteArrayInputStream;
import java.net.URI;

import javax.enterprise.inject.spi.BeanManager;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.guvnor.asset.management.backend.utils.CDIUtils;
import org.guvnor.asset.management.backend.utils.NamedLiteral;
import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.m2repo.backend.server.ExtendedM2RepoService;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ExecutionResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

public class MavenDeployProjectCommand extends AbstractCommand {
	
	private static final Logger logger = LoggerFactory.getLogger(MavenDeployProjectCommand.class);

	@Override
	public ExecutionResults execute(CommandContext ctx) throws Exception {
		ExecutionResults executionResults = new ExecutionResults();
		String deployOutcome = "UNKNOWN";
		String uri = (String) getParameter(ctx, "Uri");
		String branchToBuild = (String) getParameter(ctx, "BranchToBuild");
		
		String projectUri = "default://"+branchToBuild+"@"+uri;
		String gav = (String) getParameter(ctx, "GAV");
		String[] gavElements = gav.split(":");
		
		BeanManager beanManager = CDIUtils.lookUpBeanManager(ctx);
		logger.debug("BeanManager " + beanManager);

        BuildService builder = CDIUtils.createBean(BuildService.class, beanManager);
		logger.debug("Builder " + builder);
		
		IOService ioService = CDIUtils.createBean(IOService.class, beanManager, new NamedLiteral("ioStrategy"));
		logger.debug("IoService " + ioService);
		if (ioService != null) {
			Path projectPath  = ioService.get(URI.create(projectUri));
			logger.debug("Project path is " + projectPath);
			
			ProjectService projectService = CDIUtils.createBean(ProjectService.class, beanManager);
			Project project = projectService.resolveProject(Paths.convert(projectPath));

            BuildResults results = builder.buildAndDeploy(project);

            // dump to debug if enabled
            if (logger.isDebugEnabled()) {
                logger.debug("Errors " + results.getErrorMessages().size());
                logger.debug("Warnings " + results.getWarningMessages().size());
                logger.debug("Info " + results.getInformationMessages().size());
                for (BuildMessage msg : results.getErrorMessages()) {
                    logger.debug("Error " + msg);
                }
            }
            if (results.getErrorMessages().isEmpty()) {
                deployOutcome = "SUCCESSFUL";
            } else {
                deployOutcome = "FAILURE";
            }
            executionResults.setData("Errors", results.getErrorMessages());
            executionResults.setData("Warnings", results.getWarningMessages());
            executionResults.setData("Info", results.getInformationMessages());
            executionResults.setData("GAV", results.getGAV().toString());
		}
		executionResults.setData("MavenDeployOutcome", deployOutcome);
		

		return executionResults;
	}

	
}
