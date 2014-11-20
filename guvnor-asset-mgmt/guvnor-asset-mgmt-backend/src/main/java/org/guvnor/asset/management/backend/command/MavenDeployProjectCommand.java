package org.guvnor.asset.management.backend.command;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.TypeLiteral;

import org.guvnor.asset.management.backend.AssetManagementRuntimeException;
import org.guvnor.asset.management.backend.utils.CDIUtils;
import org.guvnor.asset.management.backend.utils.DataUtils;
import org.guvnor.asset.management.backend.utils.NamedLiteral;
import org.guvnor.asset.management.social.ProjectDeployedEvent;
import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.messageconsole.events.MessageUtils;
import org.guvnor.messageconsole.events.PublishBatchMessagesEvent;
import org.guvnor.messageconsole.events.SystemMessage;
import org.guvnor.structure.repositories.RepositoryService;
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
		try {
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

            RepositoryService repositoryService = CDIUtils.createBean( RepositoryService.class, beanManager );
            logger.debug( "RepositoryService " + repositoryService );

            ProjectDeployedEvent event = getSocialEvent( (String)ctx.getData( "_ProcessName" ),
                    uri,
                    gavElements,
                    repositoryService);

            if (ioService != null) {
                Path projectPath  = ioService.get(URI.create(projectUri));
                logger.debug("Project path is " + projectPath);

                ProjectService projectService = CDIUtils.createBean(new TypeLiteral<ProjectService<?>>() {}.getType(), beanManager);
                Project project = projectService.resolveProject(Paths.convert(projectPath));

                // suppress handlers to avoid auto deploys to runtime
                BuildResults results = builder.buildAndDeploy(project, true);

                // dump to debug if enabled
                if (logger.isDebugEnabled()) {
                    logger.debug("Errors " + results.getErrorMessages().size());
                    logger.debug("Warnings " + results.getWarningMessages().size());
                    logger.debug("Info " + results.getInformationMessages().size());
                }
                if (results.getErrorMessages().isEmpty()) {
                    deployOutcome = "SUCCESSFUL";
                } else {
                    deployOutcome = "FAILURE";
                    for ( BuildMessage message : results.getMessages() ) {
                        event.addError( message.getText() );
                        if ( logger.isDebugEnabled() ) {
                            logger.debug("Error " + message.getText());
                        }
                    }

                    PublishBatchMessagesEvent publishMessage = new PublishBatchMessagesEvent();
                    publishMessage.setCleanExisting( true );
                    List<SystemMessage> messageList = new ArrayList<SystemMessage>();

                    SystemMessage buildOutcomeMsg = new SystemMessage();
                    buildOutcomeMsg.setLevel(SystemMessage.Level.ERROR);
                    buildOutcomeMsg.setText("Maven install process failed for project " + project.getProjectName());
                    buildOutcomeMsg.setMessageType( MessageUtils.BUILD_SYSTEM_MESSAGE );
                    messageList.add(buildOutcomeMsg);

                    beanManager.fireEvent(publishMessage);
                }
                executionResults.setData("Errors", results.getErrorMessages());
                executionResults.setData("Warnings", results.getWarningMessages());
                executionResults.setData("Info", results.getInformationMessages());
                executionResults.setData("GAV", results.getGAV().toString());

                beanManager.fireEvent( event );
            }
            executionResults.setData("MavenDeployOutcome", deployOutcome);


            return executionResults;
        } catch (Throwable e) {
            throw new AssetManagementRuntimeException(e);
        }
	}

    private ProjectDeployedEvent getSocialEvent(String processName,
            String uriParam,
            String[] gavElements,
            RepositoryService repositoryService) {

        String repository = null;
        String projectName = null;
        String repositoryURI = null;

        if ( uriParam != null && uriParam.indexOf( "/" ) > 0 ) {
            repository = uriParam.substring( 0, uriParam.indexOf( "/" ) );
            projectName = uriParam.substring( uriParam.indexOf( "/" )+1, uriParam.length() );
            repositoryURI = DataUtils.readRepositoryURI( repositoryService, repository );
        }

        ProjectDeployedEvent event = new ProjectDeployedEvent(processName,
                repository,
                repositoryURI,
                "system",
                System.currentTimeMillis());

        event.setDeployType( ProjectDeployedEvent.DeployType.MAVEN );
        event.setProjectName( projectName );
        if ( gavElements != null && gavElements.length == 3) {
            event.setGroupId( gavElements[0] );
            event.setArtifactId( gavElements[1] );
            event.setVersion( gavElements[2] );
        }

        return event;
    }
	
}