package org.kie.guvnor.jcr2vfsmigration.migrater;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO Maybe we should make one per asset type? Or delegate to one per asset type?
@ApplicationScoped
public class CategoryMigrater {

    protected static final Logger logger = LoggerFactory.getLogger(CategoryMigrater.class);

//    @Inject
//    protected RepositoryCategoryService jcrRepositoryCategoryService;

    public void migrateAll() {
        logger.info("  Category migration started");
        // TODO
        logger.info("  Category migration ended");
    }

}
