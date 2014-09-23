package org.guvnor.asset.management.backend.command;

import java.net.URI;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.enterprise.inject.spi.BeanManager;
import org.guvnor.asset.management.backend.model.CommitInfo;

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
        List<CommitInfo> commitsInfos = (List<CommitInfo>) getParameter(commandContext, "CommitsInfos");
        String commitsString = (String) getParameter(commandContext, "CommitsString");
        String[] commits = commitsString.split(",");

        Collections.sort(commitsInfos, new Comparator<CommitInfo>() {

            @Override
            public int compare(CommitInfo o1, CommitInfo o2) {
                if (o1.getCommitDate().before(o2.getCommitDate())) {
                    return -1;
                } else if (o1.getCommitDate().after(o2.getCommitDate())) {
                    return 1;
                }
                return 0;
            }
        });
        String[] orderedCommits = new String[commits.length];
        int count = 0;
        for (CommitInfo c : commitsInfos) {
            for (String s : commits) {
                if(c.getCommitId().equals(s)){
                    orderedCommits[count] = s;
                    count++;
                }
            }
        }

        BeanManager beanManager = CDIUtils.lookUpBeanManager(commandContext);
        logger.debug("BeanManager " + beanManager);

        IOService ioService = CDIUtils.createBean(IOService.class, beanManager, new NamedLiteral("ioStrategy"));
        logger.debug("IoService " + ioService);

        Path fromBranchPath = ioService.get(URI.create("default://" + fromBranchName + "@" + gitRepo));
        Path toBranchPath = ioService.get(URI.create("default://" + toBranchName + "@" + gitRepo));

        CherryPickCopyOption copyOption = new CherryPickCopyOption(orderedCommits);
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
