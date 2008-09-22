package org.drools.guvnor.server.util;

import junit.framework.TestCase;

public class AssetFormatHelperTest extends TestCase {

	public void testGetRegisteredAssetFormats() {
		AssetFormatHelper hlp = new AssetFormatHelper();
		String[] ls = hlp.listRegisteredTypes();
		assertTrue(ls.length > 1);
	}

}
