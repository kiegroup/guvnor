package org.guvnor.asset.management.backend.command;

import java.net.URI;

import javax.enterprise.inject.spi.BeanManager;

import org.guvnor.asset.management.backend.AssetManagementRuntimeException;
import org.guvnor.asset.management.backend.utils.CDIUtils;
import org.guvnor.asset.management.backend.utils.NamedLiteral;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.ProjectService;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ExecutionResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

public class UpdateProjectVersionCommand extends AbstractCommand {
	
	private static final Logger logger = LoggerFactory.getLogger(UpdateProjectVersionCommand.class);

	@Override
	public ExecutionResults execute(CommandContext ctx) throws Exception {
		try {
            ExecutionResults executionResults = new ExecutionResults();


            String uri = (String) getParameter(ctx, "Uri");
            String branchToUpdate = (String) getParameter(ctx, "BranchToUpdate");
            String version = (String) getParameter(ctx, "Version");

            String projectUri = "default://"+branchToUpdate+"@"+uri;

            BeanManager beanManager = CDIUtils.lookUpBeanManager(ctx);
            logger.debug("BeanManager " + beanManager);

            POMService pomService = CDIUtils.createBean(POMService.class, beanManager);
            logger.debug("POMService " + pomService);

            IOService ioService = CDIUtils.createBean(IOService.class, beanManager, new NamedLiteral("ioStrategy"));
            logger.debug("IoService " + ioService);
            if (ioService != null) {
                Path projectPath  = ioService.get(URI.create(projectUri));
                logger.debug("Project path is " + projectPath);

                if (projectPath == null) {
                    throw new IllegalArgumentException("Unable to find project location " + projectUri);
                }

                ProjectService projectService = CDIUtils.createBean(ProjectService.class, beanManager);
                Project project = projectService.resolveProject(Paths.convert(projectPath));

                if (project == null) {
                    throw new IllegalArgumentException("Unable to find project " + projectUri);
                }

                POM pom = pomService.load(project.getPomXMLPath());
                pom.getGav().setVersion(version);
                pomService.save(project.getPomXMLPath(), pom, null, "Update project version during release");
                executionResults.setData("GAV", pom.getGav().toString());
            }

            return executionResults;
        } catch (Throwable e) {
            throw new AssetManagementRuntimeException(e);
        }
	}

	
}
