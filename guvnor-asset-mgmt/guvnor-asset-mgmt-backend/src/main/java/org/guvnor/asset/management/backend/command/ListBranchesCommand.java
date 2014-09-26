package org.guvnor.asset.management.backend.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.enterprise.inject.spi.BeanManager;

import org.guvnor.asset.management.backend.AssetManagementRuntimeException;
import org.guvnor.asset.management.backend.model.BranchInfo;
import org.guvnor.asset.management.backend.utils.CDIUtils;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ExecutionResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListBranchesCommand extends AbstractCommand {

    private static final Logger logger = LoggerFactory.getLogger(ListBranchesCommand.class);

    @Override
    public ExecutionResults execute(CommandContext commandContext) throws Exception {
        try {
            String gitRepo = (String) getParameter(commandContext, "GitRepository");
            BeanManager beanManager = CDIUtils.lookUpBeanManager(commandContext);
            logger.debug("BeanManager " + beanManager);
            RepositoryService repositoryService = CDIUtils.createBean(RepositoryService.class, beanManager);

            Repository repository = repositoryService.getRepository(gitRepo);
            if (repository == null) {
                throw new IllegalArgumentException("No repository found for alias " + gitRepo);
            }

            Collection<String> branchNames = repository.getBranches();

            List<BranchInfo> branchInfos = new ArrayList<BranchInfo>();
            for (String branch : branchNames) {
                branchInfos.add(new BranchInfo("default://"+branch+"@"+gitRepo, branch));
            }

            ExecutionResults results = new ExecutionResults();
            results.setData("Branches", branchInfos);
            return results;
        } catch (Throwable e) {
            throw new AssetManagementRuntimeException(e);
        }
    }
}
