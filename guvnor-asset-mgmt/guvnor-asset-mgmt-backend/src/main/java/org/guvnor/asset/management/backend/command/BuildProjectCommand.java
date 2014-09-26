package org.guvnor.asset.management.backend.command;

import java.net.URI;

import javax.enterprise.inject.spi.BeanManager;

import org.guvnor.asset.management.backend.AssetManagementRuntimeException;
import org.guvnor.asset.management.backend.utils.CDIUtils;
import org.guvnor.asset.management.backend.utils.NamedLiteral;
import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ExecutionResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

public class BuildProjectCommand extends AbstractCommand {
	
	private static final Logger logger = LoggerFactory.getLogger(BuildProjectCommand.class);

	@Override
	public ExecutionResults execute(CommandContext ctx) throws Exception {
        try {
            ExecutionResults executionResults = new ExecutionResults();
            String buildOutcome = "UNKNOWN";

            String uri = (String) getParameter(ctx, "Uri");
            String branchToBuild = (String) getParameter(ctx, "BranchToBuild");

            String projectUri = "default://"+branchToBuild+"@"+uri;

            BeanManager beanManager = CDIUtils.lookUpBeanManager(ctx);
            logger.debug("BeanManager " + beanManager);

            BuildService buildService = CDIUtils.createBean(BuildService.class, beanManager);
            logger.debug("BuildService " + buildService);

            IOService ioService = CDIUtils.createBean(IOService.class, beanManager, new NamedLiteral("ioStrategy"));
            logger.debug("IoService " + ioService);
            if (ioService != null) {
                Path projectPath  = ioService.get(URI.create(projectUri));
                logger.debug("Project path is " + projectPath);

                ProjectService projectService = CDIUtils.createBean(ProjectService.class, beanManager);
                Project project = projectService.resolveProject(Paths.convert(projectPath));
                if (project == null) {
                    throw new IllegalArgumentException("Unable to find project " + projectUri);
                }
                BuildResults results = buildService.build(project);
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
                    buildOutcome = "SUCCESSFUL";
                } else {
                    buildOutcome = "FAILURE";
                }
                executionResults.setData("Errors", results.getErrorMessages());
                executionResults.setData("Warnings", results.getWarningMessages());
                executionResults.setData("Info", results.getInformationMessages());
                executionResults.setData("GAV", results.getGAV().toString());
            }

            executionResults.setData("BuildOutcome", buildOutcome);


            return executionResults;
        } catch (Throwable e) {
            throw new AssetManagementRuntimeException(e);
        }
	}

	
}
