package org.guvnor.asset.management.backend.command;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import javax.enterprise.inject.spi.BeanManager;

import org.guvnor.asset.management.backend.AssetManagementRuntimeException;
import org.guvnor.asset.management.backend.model.CommitInfo;

import org.guvnor.asset.management.backend.utils.CDIUtils;
import org.guvnor.asset.management.backend.utils.NamedLiteral;
import org.guvnor.asset.management.social.AssetsPromotedEvent;
import org.guvnor.structure.repositories.RepositoryInfo;
import org.guvnor.structure.repositories.RepositoryService;
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
        try {
            String gitRepo = (String) getParameter(commandContext, "GitRepository");
            String toBranchName = (String) getParameter(commandContext, "ToBranchName");
            String fromBranchName = (String) getParameter(commandContext, "FromBranchName");
            List<CommitInfo> commitsInfos = (List<CommitInfo>) getParameter(commandContext, "CommitsInfos");
            String commitsString = (String) getParameter(commandContext, "CommitsString");

            String outcome = "unknown";

            BeanManager beanManager = CDIUtils.lookUpBeanManager(commandContext);
            logger.debug("BeanManager " + beanManager);

            RepositoryService repositoryService = CDIUtils.createBean( RepositoryService.class, beanManager );
            String repositoryURI = readRepositoryURI( repositoryService, gitRepo );

            AssetsPromotedEvent event = getSocialEvent( gitRepo, repositoryURI, fromBranchName, toBranchName, commitsInfos, "system" );

            try {
                if (commitsString != null && !commitsString.equals("")) {
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

                    IOService ioService = CDIUtils.createBean(IOService.class, beanManager, new NamedLiteral("ioStrategy"));
                    logger.debug("IoService " + ioService);

                    Path fromBranchPath = ioService.get(URI.create("default://" + fromBranchName + "@" + gitRepo));
                    Path toBranchPath = ioService.get(URI.create("default://" + toBranchName + "@" + gitRepo));

                    CherryPickCopyOption copyOption = new CherryPickCopyOption(orderedCommits);

                    logger.debug("Cherry pick command execution");
                    ioService.copy(fromBranchPath, toBranchPath, copyOption);
                }

                outcome = "success";

            } catch (Exception e) {
                outcome = "failure : " + e.getMessage();
                event.addError( outcome );
                logger.error("Error when cherry picking commits from {} to {}", fromBranchName, toBranchName, e);
            } finally {
                if (beanManager != null && event != null) {
                    beanManager.fireEvent( event );
                }
            }

            ExecutionResults results = new ExecutionResults();
            results.setData("CherryPickResult", outcome);

            return results;
        } catch (Throwable e) {
            throw new AssetManagementRuntimeException(e);
        }
    }

    private AssetsPromotedEvent getSocialEvent(String repository,
            String repositoryURI,
            String sourceBranch,
            String targetBranch,
            List<CommitInfo> commitsInfos,
            String user) {

        TreeSet<String> files = new TreeSet<String>(  );
        if ( commitsInfos != null ) {
            for ( CommitInfo commitInfo : commitsInfos ) {
                List<String> currentFiles = commitInfo.getFiles();
                if ( currentFiles != null ) {
                    for ( String currentFile : currentFiles ) {
                        files.add( currentFile );
                    }
                }
            }

        }
        List<String> promotedFiles = new ArrayList<String>(  );
        promotedFiles.addAll( files );

        return new AssetsPromotedEvent( "PromoteAssets",
                repository,
                repositoryURI,
                sourceBranch,
                targetBranch,
                promotedFiles,
                user,
                System.currentTimeMillis());
    }

    String readRepositoryURI(RepositoryService repositoryService, String alias) {
        String uri = null;
        RepositoryInfo repositoryInfo = alias != null ? repositoryService.getRepositoryInfo( alias ) : null;
        if ( repositoryInfo != null && repositoryInfo.getRoot() != null ) {
            uri = repositoryInfo.getRoot().toURI();
        }
        return uri;
    }
}