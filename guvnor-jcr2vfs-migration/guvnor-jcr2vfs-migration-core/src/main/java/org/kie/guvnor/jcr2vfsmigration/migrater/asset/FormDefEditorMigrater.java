package org.kie.guvnor.jcr2vfsmigration.migrater.asset;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.RuleContentText;
import org.drools.guvnor.server.RepositoryAssetService;
import org.kie.guvnor.enums.service.EnumService;
import org.kie.guvnor.jcr2vfsmigration.migrater.util.MigrationPathManager;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.vfs.Path;

@ApplicationScoped
public class FormDefEditorMigrater {

    protected static final Logger logger = LoggerFactory.getLogger(FormDefEditorMigrater.class);

    @Inject
    protected RepositoryAssetService jcrRepositoryAssetService;

    @Inject
    protected EnumService enumService;

    @Inject
    protected MigrationPathManager migrationPathManager;

    public void migrate(Module jcrModule, Asset jcrAsset) {
        if (!AssetFormats.FORM_DEFINITION.equals(jcrAsset.getFormat())) {
            throw new IllegalArgumentException("The jcrAsset (" + jcrAsset
                    + ") has the wrong format (" + jcrAsset.getFormat() + ").");
        }
        Path path = migrationPathManager.generatePathForAsset(jcrModule, jcrAsset);

        Metadata m = null;        
        enumService.save(path, ((RuleContentText)jcrAsset.getContent()).content, m, "migrated from jcr");
    }

 }
