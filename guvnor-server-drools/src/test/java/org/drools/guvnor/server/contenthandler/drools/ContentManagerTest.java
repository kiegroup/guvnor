/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.server.contenthandler.drools;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.server.contenthandler.ContentManager;
import org.drools.guvnor.server.contenthandler.drools.DRLFileContentHandler;
import org.drools.guvnor.server.contenthandler.drools.ScenarioContentHandler;
import org.junit.Test;

public class ContentManagerTest {

    @Test
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
