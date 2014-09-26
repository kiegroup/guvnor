package org.guvnor.asset.management.backend.command;

import java.net.URI;
import javax.enterprise.inject.spi.BeanManager;

import org.guvnor.asset.management.backend.AssetManagementRuntimeException;
import org.guvnor.asset.management.backend.utils.CDIUtils;
import org.guvnor.asset.management.backend.utils.NamedLiteral;
import org.guvnor.structure.repositories.NewBranchEvent;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ExecutionResults;
import org.kie.uberfire.social.activities.service.SocialUserRepositoryAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

public class CreateBranchCommand extends AbstractCommand {
    private static final Logger logger = LoggerFactory.getLogger(CreateBranchCommand.class);

    @Override
    public ExecutionResults execute(CommandContext commandContext) throws Exception {

        try {
            String gitRepo = (String) getParameter(commandContext, "GitRepository");

            String branchName = (String) getParameter(commandContext, "BranchName");
            String branchOriginName = (String) getParameter(commandContext, "OriginBranchName");
            String version = (String) getParameter(commandContext, "Version");
            if (version != null && !version.isEmpty()) {
                branchName = branchName + "-" + version;
            }

            BeanManager beanManager = CDIUtils.lookUpBeanManager(commandContext);
            logger.debug("BeanManager " + beanManager);

            IOService ioService = CDIUtils.createBean(IOService.class, beanManager, new NamedLiteral("ioStrategy"));
            logger.debug("IoService " + ioService);

            Path branchPath = ioService.get(URI.create("default://" + branchName + "@" + gitRepo));
            Path branchOriginPath = ioService.get(URI.create("default://" + branchOriginName + "@" + gitRepo));

            ioService.copy(branchOriginPath, branchPath);

            beanManager.fireEvent( new NewBranchEvent( gitRepo, branchName, Paths.convert(branchPath), System.currentTimeMillis() ) );

            ExecutionResults results = new ExecutionResults();
            return results;
        } catch (Throwable e) {
            throw new AssetManagementRuntimeException(e);
        }
    }
}
