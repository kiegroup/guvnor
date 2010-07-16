/**
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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.compiler.DroolsParserException;
import org.drools.guvnor.client.factmodel.FactMetaModel;
import org.drools.guvnor.client.factmodel.FactModels;
import org.drools.guvnor.client.factmodel.FieldMetaModel;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.RuleContentText;
import org.drools.guvnor.server.ServiceImplementation;
import org.drools.guvnor.server.util.TestEnvironmentSessionHelper;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;

public class ModelContentHandlerTest extends TestCase {

    public void testModelAttached() throws Exception {
        RulesRepository repo = new RulesRepository( TestEnvironmentSessionHelper.getSession() );
        PackageItem pacakge = repo.createPackage( "testModelAttachedPack",
                                                  "for test" );
        AssetItem asset = pacakge.addAsset( "testModelAttachedAsset",
                                            "description" );

        InputStream is = this.getClass().getResourceAsStream( "domain.objects-1.1.8.jar" );
        asset.updateBinaryContentAttachment( is );

        ModelContentHandler modelContentHandler = new ModelContentHandler();
        modelContentHandler.onAttachmentAdded( asset );

        String header = ServiceImplementation.getDroolsHeader( pacakge );
        assertTrue( header.indexOf( "package-info.class" ) == -1 );
    }

    public void testModelRemoved() throws Exception {
        RulesRepository repo = new RulesRepository( TestEnvironmentSessionHelper.getSession() );
        PackageItem pkg = repo.createPackage( "testModelRemovedPackage",
                                              "for test" );
        AssetItem asset = pkg.addAsset( "testModelRemovedAsset",
                                        "description" );
        ServiceImplementation.updateDroolsHeader( "import something.Else\n",
                                                  pkg );

        InputStream is = this.getClass().getResourceAsStream( "domain.objects-1.1.8.jar" );
        asset.updateBinaryContentAttachment( is );

        ModelContentHandler modelContentHandler = new ModelContentHandler();
        modelContentHandler.onAttachmentAdded( asset );

        String header = ServiceImplementation.getDroolsHeader( pkg );
        assertTrue( header.length() > 0 );

        modelContentHandler.onAttachmentRemoved( asset );

        header = ServiceImplementation.getDroolsHeader( pkg );
        assertEquals( "import something.Else",
                      header.trim() );

    }

}
