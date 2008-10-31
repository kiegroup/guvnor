package org.drools.guvnor.server.contenthandler;

import junit.framework.TestCase;

import org.drools.guvnor.client.common.AssetFormats;

public class ContentManagerTest extends TestCase {

	public void testConfig() throws Exception {
		ContentManager mgr = ContentManager.getInstance();
		ContentManager mgr_ = ContentManager.getInstance();
		assertSame(mgr, mgr_);

		assertTrue(mgr.getContentHandlers().size() > 10);
		assertTrue(mgr.getContentHandlers().get("drl") instanceof DRLFileContentHandler);

		assertTrue(mgr.getContentHandlers().containsKey(AssetFormats.TEST_SCENARIO));
		assertTrue(mgr.getContentHandlers().get(AssetFormats.TEST_SCENARIO) instanceof ScenarioContentHandler);

	}

}
