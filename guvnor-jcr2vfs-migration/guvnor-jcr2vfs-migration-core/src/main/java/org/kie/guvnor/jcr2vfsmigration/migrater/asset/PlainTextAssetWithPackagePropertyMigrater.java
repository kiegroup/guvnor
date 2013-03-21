package org.kie.guvnor.jcr2vfsmigration.migrater.asset;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.RuleContentText;
import org.drools.guvnor.server.RepositoryAssetService;
import org.drools.guvnor.server.repository.Preferred;
import org.drools.repository.AssetItem;
import org.drools.repository.RulesRepository;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.base.options.CommentedOption;
import org.kie.commons.java.nio.file.NoSuchFileException;
import org.kie.guvnor.jcr2vfsmigration.migrater.PackageImportHelper;
import org.kie.guvnor.jcr2vfsmigration.migrater.util.MigrationPathManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

@ApplicationScoped
public class PlainTextAssetWithPackagePropertyMigrater {
    protected static final Logger logger = LoggerFactory.getLogger(PlainTextAssetWithPackagePropertyMigrater.class);

    @Inject
    protected RepositoryAssetService jcrRepositoryAssetService;

    @Inject
    private Paths paths;
    
    @Inject
    @Named("ioStrategy")
    private IOService ioService;
     
    @Inject
    protected MigrationPathManager migrationPathManager;
    
    @Inject
    PackageImportHelper packageImportHelper;

    public void migrate(Module jcrModule, AssetItem jcrAssetItem) {        
        Path path = migrationPathManager.generatePathForAsset(jcrModule, jcrAssetItem);
        final org.kie.commons.java.nio.file.Path nioPath = paths.convert( path );

        Map<String, Object> attrs;
        try {
            attrs = ioService.readAttributes( nioPath );
        } catch ( final NoSuchFileException ex ) {
            attrs = new HashMap<String, Object>();
        }
        
        String content = jcrAssetItem.getContent();
        
        String sourceWithImport = packageImportHelper.assertPackageName(content, path);

        ioService.write( nioPath, sourceWithImport, attrs, new CommentedOption(jcrAssetItem.getLastContributor(), null, jcrAssetItem.getCheckinComment(), jcrAssetItem.getLastModified().getTime() ));
    }

 }
