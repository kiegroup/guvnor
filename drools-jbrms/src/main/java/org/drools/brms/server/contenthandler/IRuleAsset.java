package org.drools.brms.server.contenthandler;

import java.io.IOException;

import org.drools.brms.server.builder.BRMSPackageBuilder;
import org.drools.brms.server.builder.ContentPackageAssembler;
import org.drools.compiler.DroolsParserException;
import org.drools.repository.AssetItem;

/**
 * This inferface indicates that an asset is a rule asset content type, 
 * ie not a model, dsl etc that supports package compilation.
 * 
 * @author Michael Neale
 */
public interface IRuleAsset {

    /**
     * This will be called when the asset is required to compile itself, 
     * in the context of the given builder.
     */
    public void compile(BRMSPackageBuilder builder, AssetItem asset, ContentPackageAssembler.ErrorLogger logger) throws DroolsParserException,
                                                                    IOException;

    /**
     * This will be called when a rule asset is to render itself to DRL source.
     */
    public void assembleDRL(BRMSPackageBuilder builder, AssetItem asset, StringBuffer buf);
    
}
