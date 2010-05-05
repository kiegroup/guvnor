package org.drools.guvnor.server.contenthandler;
/*
 * Copyright 2005 JBoss Inc
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



import junit.framework.TestCase;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.server.util.TestEnvironmentSessionHelper;
import org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable;
import org.drools.ide.common.server.util.GuidedDTXMLPersistence;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;

public class ContentHandlerTest extends TestCase {

    public void testContentFormat() {
        assertTrue(ContentManager.getHandler( AssetFormats.DRL ) instanceof DRLFileContentHandler);
        assertTrue(ContentManager.getHandler( AssetFormats.DSL ) instanceof DSLDefinitionContentHandler);
        assertTrue(ContentManager.getHandler( AssetFormats.DSL_TEMPLATE_RULE ) instanceof DSLRuleContentHandler);
        assertTrue(ContentManager.getHandler( AssetFormats.BUSINESS_RULE ) instanceof BRLContentHandler);
        assertTrue(ContentManager.getHandler( AssetFormats.DECISION_SPREADSHEET_XLS ) instanceof DecisionTableXLSHandler);
        assertTrue(ContentManager.getHandler( AssetFormats.ENUMERATION ) instanceof EnumerationContentHandler);
        assertTrue(ContentManager.getHandler( AssetFormats.DECISION_TABLE_GUIDED ) instanceof GuidedDTContentHandler);
        assertTrue(ContentManager.getHandler( AssetFormats.DRL_MODEL ) instanceof FactModelContentHandler);



        assertTrue(ContentManager.getHandler( "XXX" ) instanceof DefaultContentHandler);


    }

    public void testRuleAssetType() {
        assertTrue(ContentManager.getHandler( AssetFormats.DRL ).isRuleAsset());
        assertTrue(ContentManager.getHandler( AssetFormats.DSL_TEMPLATE_RULE ).isRuleAsset());
        assertTrue(ContentManager.getHandler( AssetFormats.BUSINESS_RULE ).isRuleAsset());
        assertTrue(ContentManager.getHandler( AssetFormats.DECISION_SPREADSHEET_XLS ).isRuleAsset());
        assertTrue(ContentManager.getHandler( AssetFormats.DECISION_TABLE_GUIDED ).isRuleAsset());

        assertFalse(ContentManager.getHandler( AssetFormats.DRL_MODEL ).isRuleAsset());
        assertFalse(ContentManager.getHandler( AssetFormats.DSL ).isRuleAsset());
        assertFalse(ContentManager.getHandler( AssetFormats.MODEL ).isRuleAsset());
        assertFalse(ContentManager.getHandler( AssetFormats.ENUMERATION ).isRuleAsset());
    }

    public void testValidating() throws Exception {
        RulesRepository repo = new RulesRepository( TestEnvironmentSessionHelper.getSession() );
        PackageItem pkg = repo.loadDefaultPackage();
        AssetItem asset = pkg.addAsset( "testValidatingEnum", "" );
        asset.updateFormat( AssetFormats.ENUMERATION );
        asset.updateContent( "'Person.age' : [1, 2, 3]" );

        EnumerationContentHandler ch = new EnumerationContentHandler();
        BuilderResult result =  ch.validateAsset( asset );
        assertNotNull(result);
        assertEquals(0, result.lines.length);

        asset.updateContent( "goober boy" );
        result = ch.validateAsset( asset );
        assertFalse(result.lines.length == 0);
        assertEquals(asset.getName(), result.lines[0].assetName);
        assertEquals(asset.getFormat(), result.lines[0].assetFormat);
        assertNotNull(result.lines[0].message);
        assertEquals(asset.getUUID(), result.lines[0].uuid);

    }

    public void testEmptyDT() throws Exception {
        RulesRepository repo = new RulesRepository( TestEnvironmentSessionHelper.getSession() );
        PackageItem pkg = repo.loadDefaultPackage();
        AssetItem asset = pkg.addAsset( "testEmptyDT", "" );
        asset.updateFormat(AssetFormats.DECISION_TABLE_GUIDED);
        GuidedDecisionTable gt = new GuidedDecisionTable();
        asset.updateContent(GuidedDTXMLPersistence.getInstance().marshal(gt));
        asset.checkin("");

        GuidedDTContentHandler ch = new GuidedDTContentHandler();
        ch.compile(null, asset, null);


    }



    public void testNameConvertion() {
    	assertEquals("com.foo.Bar", ModelContentHandler.convertPathToName("com/foo/Bar.class"));
    }

}