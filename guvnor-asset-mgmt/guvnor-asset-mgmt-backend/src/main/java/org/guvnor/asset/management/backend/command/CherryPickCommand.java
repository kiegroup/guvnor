package org.guvnor.asset.management.backend.command;

import java.net.URI;
import javax.enterprise.inject.spi.BeanManager;

import org.guvnor.asset.management.backend.utils.CDIUtils;
import org.guvnor.asset.management.backend.utils.NamedLiteral;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ExecutionResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CherryPickCopyOption;
import org.uberfire.java.nio.file.Path;

public class CherryPickCommand extends AbstractCommand {

	private static final Logger logger = LoggerFactory.getLogger(CherryPickCommand.class);
	
	@Override
    public ExecutionResults execute(CommandContext commandContext) throws Exception {


        String gitRepo = (String) getParameter(commandContext, "GitRepository");
        String toBranchName = (String) getParameter(commandContext, "ToBranchName");
        String fromBranchName = (String) getParameter(commandContext, "FromBranchName");

        String commitsString = (String) getParameter(commandContext, "Commits");
        String[] commits = commitsString.split(",");


        BeanManager beanManager = CDIUtils.lookUpBeanManager(commandContext);
        logger.debug("BeanManager " + beanManager);


        IOService ioService = CDIUtils.createBean(IOService.class, beanManager, new NamedLiteral("ioStrategy"));
        logger.debug("IoService " + ioService);

        Path fromBranchPath = ioService.get(URI.create("default://" + fromBranchName + "@" + gitRepo));
        Path toBranchPath = ioService.get(URI.create("default://" + toBranchName + "@" + gitRepo));

        CherryPickCopyOption copyOption = new CherryPickCopyOption(commits);
        String outcome = "unknown";
        try {
            logger.debug("Cherry pick command execution");
            ioService.copy(fromBranchPath, toBranchPath, copyOption);

            outcome = "success";
        } catch (Exception e) {
            outcome = "failure : " + e.getMessage();
            logger.error("Error when cherry picking commits from {} to {}", fromBranchName, toBranchName, e);
        }

        ExecutionResults results = new ExecutionResults();
        results.setData("CherryPickResult", outcome);

        return results;
    }
}
