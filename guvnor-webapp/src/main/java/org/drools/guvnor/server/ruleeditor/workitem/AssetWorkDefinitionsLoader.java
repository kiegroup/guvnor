package org.drools.guvnor.server.ruleeditor.workitem;

import java.util.Arrays;

import javax.inject.Inject;

import org.drools.guvnor.client.rpc.AssetPageRequest;
import org.drools.guvnor.client.rpc.AssetPageRow;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.RuleContentText;
import org.drools.guvnor.server.RepositoryAssetService;

import com.google.gwt.user.client.rpc.SerializationException;

/**
 * Class to load Work Definitions from a Guvnor Package
 */
public class AssetWorkDefinitionsLoader extends AbstractWorkDefinitionsLoader {

    @Inject
    private RepositoryAssetService      repositoryAssetService;
    
    private String packageUUID;

    public AssetWorkDefinitionsLoader() {}
    
    public AssetWorkDefinitionsLoader(String packageUUID) {
        this.packageUUID = packageUUID;
    }

    //Load file into a String
    public String loadWorkDefinitions() throws SerializationException {
        StringBuffer sb = new StringBuffer();
        AssetPageRequest workDefinitionAssetRequest = new AssetPageRequest( packageUUID,
                                                                            Arrays.asList( new String[]{"wid"} ),
                                                                            null,
                                                                            0,
                                                                            null );
        PageResponse<AssetPageRow> assetWorkDefinitions = repositoryAssetService.findAssetPage( workDefinitionAssetRequest );
        for ( AssetPageRow row : assetWorkDefinitions.getPageRowList() ) {
            RuleAsset asset = repositoryAssetService.loadRuleAsset( row.getUuid() );
            RuleContentText content = (RuleContentText) asset.getContent();
            sb.append( content.content );
            sb.append( NEW_LINE );
            sb.append( NEW_LINE );
        }
        return sb.toString();
    }

}
