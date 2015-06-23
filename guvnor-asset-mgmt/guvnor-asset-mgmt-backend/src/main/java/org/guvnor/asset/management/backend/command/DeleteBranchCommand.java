/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.asset.management.backend.command;

import java.net.URI;
import javax.enterprise.inject.spi.BeanManager;

import org.guvnor.asset.management.backend.AssetManagementRuntimeException;
import org.guvnor.asset.management.backend.utils.CDIUtils;
import org.guvnor.asset.management.backend.utils.NamedLiteral;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ExecutionResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.io.IOService;

public class DeleteBranchCommand extends AbstractCommand {

    private static final Logger logger = LoggerFactory.getLogger(ListBranchesCommand.class);

    @Override
    public ExecutionResults execute(CommandContext commandContext) throws Exception {

        try {
            String gitRepo = (String) getParameter(commandContext, "GitRepository");
            String branchName = (String) getParameter(commandContext, "BranchName");

            BeanManager beanManager = CDIUtils.lookUpBeanManager(commandContext);
            logger.debug("BeanManager " + beanManager);

            RepositoryService repositoryService = CDIUtils.createBean(RepositoryService.class, beanManager);

            Repository repository = repositoryService.getRepository(gitRepo);
            if (repository == null) {
                throw new IllegalArgumentException("No repository found for alias " + gitRepo);
            }


            IOService ioService = CDIUtils.createBean(IOService.class, beanManager, new NamedLiteral("ioStrategy"));
            logger.debug("IoService " + ioService);
            if (ioService != null) {
                ioService.delete(ioService.get(URI.create("default://" + branchName + "@" + gitRepo)));
            }

            ExecutionResults results = new ExecutionResults();

            return results;
        } catch (Throwable e) {
            throw new AssetManagementRuntimeException(e);
        }
    }
}
