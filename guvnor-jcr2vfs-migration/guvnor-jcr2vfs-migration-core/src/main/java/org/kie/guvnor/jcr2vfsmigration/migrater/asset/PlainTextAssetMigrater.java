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
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.base.options.CommentedOption;
import org.kie.commons.java.nio.file.NoSuchFileException;
import org.kie.guvnor.jcr2vfsmigration.migrater.util.MigrationPathManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

@ApplicationScoped
public class PlainTextAssetMigrater {
    protected static final Logger logger = LoggerFactory.getLogger(PlainTextAssetMigrater.class);

    @Inject
    protected RepositoryAssetService jcrRepositoryAssetService;

    @Inject
    private Paths paths;
    
    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    protected MigrationPathManager migrationPathManager;

    public void migrate(Module jcrModule, Asset jcrAsset, final String checkinComment, final Date lastModified, String lastContributor) {
        Path path = migrationPathManager.generatePathForAsset(jcrModule, jcrAsset);
        final org.kie.commons.java.nio.file.Path nioPath = paths.convert( path );

        Map<String, Object> attrs;

        try {
            attrs = ioService.readAttributes( nioPath );
        } catch ( final NoSuchFileException ex ) {
            attrs = new HashMap<String, Object>();
        }

        logger.debug("======" + jcrAsset.getName() + ", " + jcrAsset.getVersionNumber() + "," + jcrAsset.getCheckinComment());
        ioService.write( nioPath, ((RuleContentText)jcrAsset.getContent()).content, attrs, new CommentedOption(lastContributor, null, checkinComment, lastModified ));
    }

 }
