package org.kie.guvnor.jcr2vfsmigration.migrater.asset;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.server.RepositoryAssetService;
import org.drools.guvnor.server.contenthandler.drools.GuidedDTContentHandler;
import org.drools.guvnor.server.repository.Preferred;
import org.drools.repository.RulesRepository;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.base.options.CommentedOption;
import org.kie.commons.java.nio.file.NoSuchFileException;
import org.kie.guvnor.guided.rule.service.GuidedRuleEditorService;
import org.kie.guvnor.jcr2vfsmigration.migrater.util.MigrationPathManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

@ApplicationScoped
public class GuidedDecisionTableMigrater {

    protected static final Logger logger = LoggerFactory.getLogger(GuidedDecisionTableMigrater.class);

    @Inject
    protected RepositoryAssetService jcrRepositoryAssetService;
    
    @Inject @Preferred
    private RulesRepository rulesRepository;
    
    @Inject
    protected GuidedRuleEditorService guidedRuleEditorService;

    @Inject
    protected MigrationPathManager migrationPathManager;
    
    @Inject
    private Paths paths;
    
    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    public void migrate(Module jcrModule, Asset jcrAsset, final String checkinComment, final Date lastModified, String lastContributor) {
        if (!AssetFormats.DECISION_TABLE_GUIDED.equals(jcrAsset.getFormat())) {
            throw new IllegalArgumentException("The jcrAsset (" + jcrAsset
                    + ") has the wrong format (" + jcrAsset.getFormat() + ").");
        }
        Path path = migrationPathManager.generatePathForAsset(jcrModule, jcrAsset);
        
        GuidedDTContentHandler h = new GuidedDTContentHandler();
        String sourceDRL = h.getRawDRL(rulesRepository.loadAssetByUUID(jcrAsset.getUuid()));
        
        final org.kie.commons.java.nio.file.Path nioPath = paths.convert( path );

        Map<String, Object> attrs;
        try {
            attrs = ioService.readAttributes( nioPath );
        } catch ( final NoSuchFileException ex ) {
            attrs = new HashMap<String, Object>();
        }

        ioService.write( nioPath, sourceDRL, attrs, new CommentedOption(lastContributor, null, checkinComment, lastModified ));
    }
}
