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
import org.guvnor.asset.management.social.ProjectBuiltEvent;
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

            RepositoryService repositoryService = CDIUtils.createBean( RepositoryService.class, beanManager );
            logger.debug( "RepositoryService " + repositoryService );

            ProjectBuiltEvent event = getSocialEvent( (String)ctx.getData( "_ProcessName" ),
                    uri,
                    branchToBuild,
                    repositoryService);

            if (ioService != null) {
                Path projectPath  = ioService.get(URI.create(projectUri));
                logger.debug("Project path is " + projectPath);

                ProjectService projectService = CDIUtils.createBean(new TypeLiteral<ProjectService<?>>() {}.getType(), beanManager);
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
                }
                if (results.getErrorMessages().isEmpty()) {
                    buildOutcome = "SUCCESSFUL";
                } else {
                    buildOutcome = "FAILURE";

                    PublishBatchMessagesEvent publishMessage = new PublishBatchMessagesEvent();
                    publishMessage.setCleanExisting( true );
                    List<SystemMessage> messageList = new ArrayList<SystemMessage>();

                    for ( BuildMessage message : results.getMessages() ) {
                        event.addError( message.getText() );
                        if ( logger.isDebugEnabled() ) {
                            logger.debug("Error " + message.getText());
                        }
                        messageList.add(MessageUtils.convert(message));
                    }

                    publishMessage.setMessagesToPublish(messageList);

                    beanManager.fireEvent(publishMessage);

                }
                executionResults.setData("Errors", processMessages(results.getErrorMessages()));
                executionResults.setData("Warnings", processMessages(results.getWarningMessages()));
                executionResults.setData("Info", processMessages(results.getInformationMessages()));
                executionResults.setData("GAV", results.getGAV().toString());



                beanManager.fireEvent( event );
            }

            executionResults.setData("BuildOutcome", buildOutcome);

            return executionResults;
        } catch (Throwable e) {
            throw new AssetManagementRuntimeException(e);
        }
	}

    private ProjectBuiltEvent getSocialEvent(String processName, String uriParam, String branchToBuild, RepositoryService repositoryService) {

        String repository = null;
        String projectName = null;
        String repositoryURI = null;

        if ( uriParam != null && uriParam.indexOf( "/" ) > 0 ) {
            repository = uriParam.substring( 0, uriParam.indexOf( "/" ) );
            projectName = uriParam.substring( uriParam.indexOf( "/" )+1, uriParam.length() );
            repositoryURI = DataUtils.readRepositoryURI( repositoryService, repository );
        }

        ProjectBuiltEvent event = new ProjectBuiltEvent( processName,
                repository,
                repositoryURI,
                "system",
                System.currentTimeMillis());
        event.setBranchName( branchToBuild );
        event.setProjectName( projectName );

        return event;
    }

    protected List<String> processMessages(List<BuildMessage> messageList) {
        List<String> messages = new ArrayList<String>();

        if (messageList != null) {
            for (BuildMessage buildMessage : messageList) {
                messages.add(buildMessage.getText());
            }
        }

        return messages;
    }
	
}
