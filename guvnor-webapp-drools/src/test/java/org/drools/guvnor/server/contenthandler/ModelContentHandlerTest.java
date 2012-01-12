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

package org.drools.guvnor.server.contenthandler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.drools.guvnor.server.GuvnorTestBase;
import org.drools.guvnor.server.ServiceImplementation;
import org.drools.guvnor.server.util.DroolsHeader;
import org.drools.repository.AssetItem;
import org.drools.repository.ModuleItem;
import org.drools.repository.RulesRepository;
import org.junit.Test;

public class ModelContentHandlerTest extends GuvnorTestBase {

    @Test
    public void testModelAttached() throws Exception {

        RulesRepository repo = rulesRepository;

        ModuleItem pacakge = repo.createModule( "testModelAttachedPack",
                                                  "for test" );
        AssetItem asset = pacakge.addAsset( "testModelAttachedAsset",
                                            "description" );

        InputStream is = this.getClass().getResourceAsStream( "domain.objects-1.1.8.jar" );
        asset.updateBinaryContentAttachment( is );

        ModelContentHandler modelContentHandler = new ModelContentHandler();
        modelContentHandler.onAttachmentAdded( asset );

        String header = DroolsHeader.getDroolsHeader( pacakge );
        assertTrue( header.indexOf( "package-info.class" ) == -1 );
    }

    @Test
    public void testModelRemoved() throws Exception {

        RulesRepository repo = rulesRepository;

        ModuleItem pkg = repo.createModule( "testModelRemovedPackage",
                                              "for test" );
        AssetItem asset = pkg.addAsset( "testModelRemovedAsset",
                                        "description" );
        DroolsHeader.updateDroolsHeader( "import something.Else\n",
                                                  pkg );

        InputStream is = this.getClass().getResourceAsStream( "domain.objects-1.1.8.jar" );
        asset.updateBinaryContentAttachment( is );

        ModelContentHandler modelContentHandler = new ModelContentHandler();
        modelContentHandler.onAttachmentAdded( asset );

        String header = DroolsHeader.getDroolsHeader( pkg );
        assertTrue( header.length() > 0 );

        modelContentHandler.onAttachmentRemoved( asset );

        header = DroolsHeader.getDroolsHeader( pkg );
        assertEquals( "import something.Else",
                      header.trim() );

    }

}
