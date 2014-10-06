package org.guvnor.asset.management.backend.command;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.TypeLiteral;

import org.guvnor.asset.management.backend.AssetManagementRuntimeException;
import org.guvnor.asset.management.backend.utils.CDIUtils;
import org.guvnor.asset.management.backend.utils.NamedLiteral;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ExecutionResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;

public class ConfigureDevelopmentBranchCommand extends AbstractCommand {
	
	private static final Logger logger = LoggerFactory.getLogger(ConfigureDevelopmentBranchCommand.class);

	@Override
	public ExecutionResults execute(CommandContext ctx) throws Exception {
		try {
            ExecutionResults executionResults = new ExecutionResults();


            String repository = (String) getParameter(ctx, "GitRepository");
            if (repository.endsWith(".git")) {
                repository = repository.substring(0, repository.length() - 4);
            }
            String branchToUpdate = (String) getParameter(ctx, "BranchName");
            String version = (String) getParameter(ctx, "Version");
            if (version == null) {
                version = "1.0.0";
            } else if (!version.endsWith("-SNAPSHOT")) {
                version = version.concat("-SNAPSHOT");
            }


            BeanManager beanManager = CDIUtils.lookUpBeanManager(ctx);
            logger.debug("BeanManager " + beanManager);

            POMService pomService = CDIUtils.createBean(POMService.class, beanManager);
            logger.debug("POMService " + pomService);

            IOService ioService = CDIUtils.createBean(IOService.class, beanManager, new NamedLiteral("ioStrategy"));
            logger.debug("IoService " + ioService);
            if (ioService != null) {


                ProjectService projectService = CDIUtils.createBean(new TypeLiteral<ProjectService<?>>() {}.getType(), beanManager);

                RepositoryService repositoryService = CDIUtils.createBean(RepositoryService.class, beanManager);
                logger.debug("RepositoryService " + repositoryService);

                if (repositoryService != null) {

                    Repository repo = repositoryService.getRepository(repository);

                    Map<String, Object> config = new HashMap<String, Object>();
                    config.put("branch", branchToUpdate + "-" + version);

                    repo = repositoryService.updateRepository(repo, config);
                    logger.debug("Updated repository " + repo);

                    // update all pom.xml files of projects on the dev branch
                    Set<Project> projects = getProjects(repo, ioService, projectService);

                    for (Project project : projects) {

                        POM pom = pomService.load(project.getPomXMLPath());
                        pom.getGav().setVersion(version);
                        pomService.save(project.getPomXMLPath(), pom, null, "Update project version on development branch");
                        executionResults.setData(project.getProjectName() +  "-GAV", pom.getGav().toString());
                    }
                }
            }

            return executionResults;
        } catch (Throwable e) {
            throw new AssetManagementRuntimeException(e);
        }
	}

    private Set<Project> getProjects( final Repository repository, final IOService ioService, final ProjectService projectService ) {
        final Set<Project> authorizedProjects = new HashSet<Project>();
        if ( repository == null ) {
            return authorizedProjects;
        }
        final Path repositoryRoot = Paths.convert(repository.getRoot());
        final DirectoryStream<Path> nioRepositoryPaths = ioService.newDirectoryStream( repositoryRoot );
        for ( Path nioRepositoryPath : nioRepositoryPaths ) {
            if ( Files.isDirectory( nioRepositoryPath ) ) {
                final org.uberfire.backend.vfs.Path projectPath = Paths.convert( nioRepositoryPath );
                final Project project = projectService.resolveProject( projectPath );
                if ( project != null ) {
                    authorizedProjects.add( project );
                    
                }
            }
        }
        return authorizedProjects;
    }
	
}
