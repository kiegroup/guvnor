package org.kie.guvnor.jcr2vfsmigration.migrater;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.server.RepositoryModuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ModuleMigrater {

    protected static final Logger logger = LoggerFactory.getLogger(ModuleMigrater.class);

    @Inject
    protected RepositoryModuleService jcrRepositoryModuleService;

    public void migrateAll() {
        logger.info("  Module migration started");
        Module[] modules = jcrRepositoryModuleService.listModules();
        for (Module module : modules) {
            migrate(module);
            logger.debug("    Module ({}) migrated.", module.getName());
        }
        logger.info("  Module migration ended");
    }

    private void migrate(Module module) {
        // TODO REPLACE ME WITH ACTUAL CODE
        logger.debug("    TODO migrate module ({}).", module.getName());
    }

}
