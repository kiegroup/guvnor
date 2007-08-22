package org.drools.brms.server.selector;

import org.drools.repository.AssetItem;


/**
 *
 * Asset selectors can be used to choose if an asset is part of a build.
 *
 * Asset selectors should be stateless, they will be called multiple times.
 *
 * @author Michael Neale
 *
 */
public interface AssetSelector {


	/**
	 *
	 * @param asset The asset to be tested.
	 * @return true if asset it allowed.
	 */
	boolean isAssetAllowed(AssetItem asset);

}
