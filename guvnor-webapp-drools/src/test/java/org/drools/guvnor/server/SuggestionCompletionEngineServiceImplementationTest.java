/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.server;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.server.util.DroolsHeader;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.repository.AssetItem;
import org.drools.repository.ModuleItem;
import org.junit.Test;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SuggestionCompletionEngineServiceImplementationTest
        extends GuvnorTestBase {

    @Inject
    protected SuggestionCompletionEngineServiceImplementation suggestionCompletionEngineServiceImplementation;

    @Test
    public void testLoadSuggestionCompletionEngine() throws Exception {
        // create our package
        ModuleItem pkg = rulesRepository.createModule( "testSILoadSCE",
                "" );

        AssetItem model = pkg.addAsset( "MyModel",
                "" );
        model.updateFormat( AssetFormats.MODEL );
        model.updateBinaryContentAttachment( this.getClass().getResourceAsStream( "/billasurf.jar" ) );
        model.checkin( "" );
        DroolsHeader.updateDroolsHeader("import com.billasurf.Board",
                pkg);

        AssetItem m2 = pkg.addAsset( "MyModel2",
                "" );
        m2.updateFormat( AssetFormats.DRL_MODEL );
        m2.updateContent( "declare Whee\n name: String\nend" );
        m2.checkin( "" );

        AssetItem r1 = pkg.addAsset( "garbage",
                "" );
        r1.updateFormat( AssetFormats.DRL );
        r1.updateContent( "this will not compile" );
        r1.checkin( "" );

        SuggestionCompletionEngine eng = suggestionCompletionEngineServiceImplementation.loadSuggestionCompletionEngine( pkg.getName() );
        assertNotNull( eng );

        //The loader could define extra imports
        assertTrue( eng.getFactTypes().length >= 2 );
        List<String> factTypes = Arrays.asList(eng.getFactTypes());

        assertTrue( factTypes.contains( "Board" ) );
        assertTrue( factTypes.contains( "Whee" ) );

    }
}
