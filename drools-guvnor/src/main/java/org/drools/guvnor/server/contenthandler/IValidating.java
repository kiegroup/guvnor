package org.drools.guvnor.server.contenthandler;

import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.repository.AssetItem;

/**
 * This interface indicates that an asset can validate itself, and present errors if requested.
 * Each IRuleAsset can already do this, so its not really required for them. Only other non-rule type assets.
 * (eg supporting). The idea is that feedback can be provided where the user is entering stuff.
 */
public interface IValidating {

    BuilderResult validateAsset(AssetItem asset);


}
