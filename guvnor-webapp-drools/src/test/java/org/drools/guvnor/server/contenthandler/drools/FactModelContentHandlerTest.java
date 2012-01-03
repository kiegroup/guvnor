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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.drools.compiler.DroolsParserException;
import org.drools.guvnor.client.asseteditor.drools.factmodel.FactMetaModel;
import org.drools.guvnor.client.asseteditor.drools.factmodel.FactModels;
import org.drools.guvnor.client.asseteditor.drools.factmodel.FieldMetaModel;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.RuleContentText;
import org.drools.guvnor.server.GuvnorTestBase;
import org.drools.guvnor.server.ServiceImplementation;
import org.drools.guvnor.server.contenthandler.drools.FactModelContentHandler;
import org.drools.repository.AssetItem;
import org.drools.repository.ModuleItem;
import org.drools.repository.RulesRepository;
import org.junit.Test;

public class FactModelContentHandlerTest extends GuvnorTestBase {

    @Test
    public void testToDrl() {

        List<FieldMetaModel> fields = new ArrayList<FieldMetaModel>();
        fields.add( new FieldMetaModel( "f1",
                                        "int" ) );
        fields.add( new FieldMetaModel( "f2",
                                        "String" ) );

        FactMetaModel mm = new FactMetaModel( "FooBar",
                                              fields );

        FactModelContentHandler ch = new FactModelContentHandler();
        String drl = ch.toDRL( mm );
        assertNotNull( drl );
        System.err.println( drl );
        assertEquals( "declare FooBar\n\tf1: int\n\tf2: String\nend",
                      drl );

        FactMetaModel mm2 = new FactMetaModel( "BooBah",
                                               new ArrayList<FieldMetaModel>() );
        List<FactMetaModel> models = new ArrayList<FactMetaModel>();
        models.add( mm );
        models.add( mm2 );

        drl = ch.toDRL( models );
        System.err.println( drl );
        assertTrue( drl.indexOf( "FooBar" ) > -1 );
        assertTrue( drl.indexOf( "BooBah" ) > drl.indexOf( "FooBar" ) );
    }

    @Test
    public void testFromDrlDeclarationEmpty() throws Exception {

        String drl = "declare FooBar\nend";

        FactModelContentHandler ch = new FactModelContentHandler();
        List<FactMetaModel> list = ch.toModel( drl );
        assertEquals( 1,
                      list.size() );
        FactMetaModel mm = list.get( 0 );
        assertEquals( "FooBar",
                      mm.getName() );
        assertEquals( 0,
                      mm.getFields().size() );
    }

    @Test
    public void testFromDrlDeclarationWithFields() throws Exception {
        String drl = "declare FooBar\n\tf1: int\n\tf2: String\nend";

        FactModelContentHandler ch = new FactModelContentHandler();
        List<FactMetaModel> list = ch.toModel( drl );
        assertEquals( 1,
                      list.size() );
        FactMetaModel mm = list.get( 0 );
        assertEquals( "FooBar",
                      mm.getName() );
        assertEquals( 2,
                      mm.getFields().size() );
        for ( int i = 0; i < mm.getFields().size(); i++ ) {
            FieldMetaModel fm = (FieldMetaModel) mm.getFields().get( 1 );
            if ( fm.name.equals( "f1" ) ) {
                assertEquals( "f1",
                              fm.name );
                assertEquals( "int",
                              fm.type );
            } else {
                assertEquals( "f2",
                              fm.name );
                assertEquals( "String",
                              fm.type );
            }
        }
    }

    @Test
    public void testFromDrlDeclarationWithAnnotations() throws Exception {

        String drl = "declare FooBar\n\t@role(event)\nend";

        FactModelContentHandler ch = new FactModelContentHandler();
        List<FactMetaModel> list = ch.toModel( drl );
        assertEquals( 1,
                      list.size() );
        FactMetaModel mm = list.get( 0 );
        assertEquals( "FooBar",
                      mm.getName() );
        assertEquals( 0,
                      mm.getFields().size() );
        assertEquals( 1,
                      mm.getAnnotations().size() );
        assertEquals( "event",
                      mm.getAnnotations().get( 0 ).values.get( "value" ) );
    }

    @Test
    public void testAdvanced() throws Exception {

        String drl = "#advanced editor \ndeclare FooBar\n\t name: String  \nend";
        try {
            FactModelContentHandler ch = new FactModelContentHandler();
            ch.toModel( drl );
            fail( "should not parse this" );
        } catch ( DroolsParserException e ) {
            assertNotNull( e.getMessage() );
        }
    }

    @Test
    public void testFromEmptyDrl() throws Exception {
        String drl = "";

        FactModelContentHandler ch = new FactModelContentHandler();
        List<FactMetaModel> list = ch.toModel( drl );
        assertNotNull( list );

    }

    @Test
    public void testStore() throws Exception {
        FactModelContentHandler ch = new FactModelContentHandler();

        RulesRepository repo = rulesRepository;

        ModuleItem pkg = repo.loadDefaultModule();
        AssetItem asset = pkg.addAsset( "testDeclaredTypeStore",
                                        "" );
        asset.updateFormat( "model.drl" );
        asset.updateContent( "declare Foo\n name: String\n end" );
        asset.checkin( "" );

        Asset ass = new Asset();
        ch.retrieveAssetContent( ass,
                                 asset );
        assertTrue( ass.getContent() instanceof FactModels );
        FactModels fm = (FactModels) ass.getContent();

        assertEquals( 1,
                      fm.models.size() );
        FactMetaModel mm = (FactMetaModel) fm.models.get( 0 );
        assertEquals( 1,
                      mm.getFields().size() );
        assertEquals( "Foo",
                      mm.getName() );

        FieldMetaModel fmm = (FieldMetaModel) mm.getFields().get( 0 );
        assertEquals( "name",
                      fmm.name );

        mm.getFields().add( new FieldMetaModel( "age",
                                                "int" ) );

        ch.storeAssetContent( ass,
                              asset );

        assertTrue( asset.getContent().indexOf( "age: int" ) > -1 );

        asset.updateContent( "rubbish here" );
        asset.checkin( "" );

        ch.retrieveAssetContent( ass,
                                 asset );
        assertTrue( ass.getContent() instanceof RuleContentText );

        ch.storeAssetContent( ass,
                              asset );

        assertEquals( "rubbish here",
                      asset.getContent() );

    }

}
