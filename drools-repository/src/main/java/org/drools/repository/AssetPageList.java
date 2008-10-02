package org.drools.repository;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.RangeIterator;

/**
 * Used for holding a page of asset data.
 *
 * @author Michael Neale
 */
public class AssetPageList {

	public final List<AssetItem> assets;
	public final boolean hasNext;
	public long currentPosition;

	public AssetPageList(List<AssetItem> categories, RangeIterator it) {
		this.assets = categories;
		this.hasNext = it.hasNext();
		this.currentPosition = it.getPosition();
	}

	public AssetPageList() {
		hasNext = false;
		currentPosition = 0;
		assets = new ArrayList<AssetItem>();
	}

}
