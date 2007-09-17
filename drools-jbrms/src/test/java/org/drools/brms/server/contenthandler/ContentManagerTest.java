package org.drools.brms.server.contenthandler;

import junit.framework.TestCase;

public class ContentManagerTest extends TestCase {

	public void testConfig() throws Exception {
		ContentManager mgr = ContentManager.getInstance();
		ContentManager mgr_ = ContentManager.getInstance();
		assertSame(mgr, mgr_);

		assertEquals(9, mgr.getContentHandlers().size());
		assertTrue(mgr.getContentHandlers().get("drl") instanceof DRLFileContentHandler);

	}

}
