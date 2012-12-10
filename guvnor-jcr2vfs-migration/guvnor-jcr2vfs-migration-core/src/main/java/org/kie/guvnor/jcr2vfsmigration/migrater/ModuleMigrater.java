package org.kie.guvnor.jcr2vfsmigration.migrater;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ModuleMigrater {

    protected static final Logger logger = LoggerFactory.getLogger(ModuleMigrater.class);

//    @Inject
//    protected RepositoryModuleService jcrRepositoryModuleService;

    public void migrateAll() {
        logger.info("  Module migration started");
        // TODO
        logger.info("  Module migration ended");
    }

}
