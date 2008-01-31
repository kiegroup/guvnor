package org.drools.repository;

import java.util.List;

/**
 * Used for holding a page of asset data.
 *
 * @author Michael Neale
 */
public class AssetPageList {

	public final List assets;
	public final long totalSize;
	public final boolean hasNext;

	public AssetPageList(List categories, long totalSize, boolean hasNext) {
		this.assets = categories;
		this.totalSize = totalSize;
		this.hasNext = hasNext;
	}



}
