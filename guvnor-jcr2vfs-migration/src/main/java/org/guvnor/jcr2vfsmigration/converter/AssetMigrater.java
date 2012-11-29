package org.guvnor.jcr2vfsmigration.converter;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO Maybe we should make one per asset type? Or delegate to one per asset type?
@ApplicationScoped
public class AssetMigrater {

    protected static final Logger logger = LoggerFactory.getLogger(AssetMigrater.class);

    public void migrateAll() {
        logger.info("  Asset migration started");
        // TODO
        logger.info("  Asset migration ended");
    }

}
