package org.drools.guvnor.client.rulelist;

import org.drools.guvnor.client.common.GenericCallback;

/**
 * This is used by the grid view to load data, in a paged fashion (if possible to do paging).
 *
 * @author Michael Neale
 */
public interface AssetItemGridDataLoader {

	/**
	 * This will be called by the grid when loading data, needs to know how to skip, and how many rows to load up.
	 *
	 * cb will return TableDataResult type.
	 */
	void loadData(int startRow, int numberOfRows, GenericCallback cb);
}
