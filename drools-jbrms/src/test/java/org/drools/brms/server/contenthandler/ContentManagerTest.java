package org.drools.brms.server.contenthandler;

import org.drools.brms.client.common.AssetFormats;

import junit.framework.TestCase;

public class ContentManagerTest extends TestCase {

	public void testConfig() throws Exception {
		ContentManager mgr = ContentManager.getInstance();
		ContentManager mgr_ = ContentManager.getInstance();
		assertSame(mgr, mgr_);

		assertEquals(11, mgr.getContentHandlers().size());
		assertTrue(mgr.getContentHandlers().get("drl") instanceof DRLFileContentHandler);

		assertTrue(mgr.getContentHandlers().containsKey(AssetFormats.TEST_SCENARIO));
		assertTrue(mgr.getContentHandlers().get(AssetFormats.TEST_SCENARIO) instanceof ScenarioContentHandler);

	}

}
