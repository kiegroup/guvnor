package org.kie.guvnor.jcr2vfsmigration.migrater.asset;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.rpc.SerializationException;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.AssetPageRequest;
import org.drools.guvnor.client.rpc.AssetPageRow;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.server.RepositoryAssetService;
import org.drools.guvnor.server.RepositoryModuleService;
import org.kie.guvnor.factmodel.service.FactModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class FactModelsMigrater {

    protected static final Logger logger = LoggerFactory.getLogger(FactModelsMigrater.class);

    @Inject
    protected RepositoryAssetService jcrRepositoryAssetService;

    @Inject
    protected FactModelService vfsFactModelService;

    public void migrate(Asset jcrAsset) {
        if (!AssetFormats.DRL_MODEL.equals(jcrAsset.getFormat())) {
            throw new IllegalArgumentException("The jcrAsset (" + jcrAsset
                    + ") has the wrong format (" + jcrAsset.getFormat() + ").");
        }
        // TODO
        // vfsFactModelService.save(PathFactory.newPath("default://guvnor-jcr2vfs-migration/foo/bar"), new FactModels()); // TODO remove me
    }

}
