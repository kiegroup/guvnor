package org.drools.guvnor.server.contenthandler;

import org.drools.guvnor.server.builder.BRMSPackageBuilder;
import org.drools.repository.AssetItem;

/**
 * 
 * @author Toni Rikkola
 */
public interface IRuleAsset
    extends
    ICompilable {

    /**
     * This will be called when a rule asset is to render itself to DRL source.
     */
    public void assembleDRL(BRMSPackageBuilder builder,
                            AssetItem asset,
                            StringBuffer buf);

    /**
     * If the rule has DSL in it, it is presented unexpanded.
     */
    public String getRawDRL(AssetItem asset);
}
