/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.drools.guvnor.server.builder.drools;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.server.MockAssetItemIterator;
import org.drools.guvnor.server.builder.AssetItemValidator;
import org.drools.guvnor.server.contenthandler.ContentHandler;
import org.drools.guvnor.server.contenthandler.drools.BRLContentHandler;
import org.drools.guvnor.server.contenthandler.drools.DRLFileContentHandler;
import org.drools.guvnor.server.contenthandler.drools.DSLDefinitionContentHandler;
import org.drools.guvnor.server.contenthandler.drools.DSLRuleContentHandler;
import org.drools.guvnor.server.contenthandler.drools.EnumerationContentHandler;
import org.drools.guvnor.server.contenthandler.drools.FactModelContentHandler;
import org.drools.guvnor.server.contenthandler.drools.FunctionContentHandler;
import org.drools.guvnor.server.contenthandler.drools.GuidedDTContentHandler;
import org.drools.guvnor.server.contenthandler.drools.RuleTemplateHandler;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemIterator;
import org.drools.repository.ModuleItem;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

public class AssetItemValidatorTest {

    private ModuleItem packageItem;
    private AssetItem   unsavedAssetItem;
    private AssetItem   savedAssetItem;

    @Before
    public void setUp() throws Exception {
        setUpPackageItem();
        setUpUnsavedAssetItem();
        setUpSavedAssetItem();
    }

    @Test
    public void testValidateBRL() throws Exception {
        testValidate( AssetFormats.BUSINESS_RULE,
                      new BRLContentHandler() );
    }

    @Test
    public void testValidateDRL() throws Exception {
        testValidate( AssetFormats.DRL,
                      new DRLFileContentHandler() );
    }

    @Test
    public void testValidateDecisionTableGuided() throws Exception {
        testValidate( AssetFormats.DECISION_TABLE_GUIDED,
                      new GuidedDTContentHandler() );
    }

    @Test
    public void testValidateDrlModel() throws Exception {
        testValidate( AssetFormats.DRL_MODEL,
                      new FactModelContentHandler() );
    }

    @Test
    public void testValidateDsl() throws Exception {
        testValidate( AssetFormats.DSL,
                      new DSLDefinitionContentHandler() );
    }

    @Test
    public void testValidateFunction() throws Exception {
        testValidate( AssetFormats.FUNCTION,
                      new FunctionContentHandler() );
    }

    @Test
    public void testValidateRuleTemplate() throws Exception {
        testValidate( AssetFormats.RULE_TEMPLATE,
                      new RuleTemplateHandler() );
    }

    @Test
    public void testValidateDslTemplateRule() throws Exception {
        setUpMockAssets( AssetFormats.DSL_TEMPLATE_RULE );
        setUpAssetItemIterators( AssetFormats.DSL_TEMPLATE_RULE );
        setUpMockDSL();
        verifyValidate( new DSLRuleContentHandler() );
    }

    @Test
    public void testValidateDecisionSpreadsheetXLS() throws Exception {
        InputStream in = this.getClass().getResourceAsStream( "EmptyDecisionTable.xls" );
        when( savedAssetItem.getFormat() ).thenReturn( AssetFormats.DECISION_SPREADSHEET_XLS );
        when( savedAssetItem.getBinaryContentAttachment() ).thenReturn( in );
        when( unsavedAssetItem.getFormat() ).thenReturn( AssetFormats.DECISION_SPREADSHEET_XLS );
        when( unsavedAssetItem.getBinaryContentAttachment() ).thenReturn( in );

        setUpAssetItemIterators( AssetFormats.DECISION_SPREADSHEET_XLS );

        runValidate( new DSLRuleContentHandler() );
        verify( unsavedAssetItem ).getBinaryContentAttachment();
        verify( savedAssetItem,
                never() ).getBinaryContentAttachment();
    }

    @Test
    public void testValidateEnumeration() throws Exception {

        setUpMockAssets( AssetFormats.ENUMERATION,
                         "" );
        setUpAssetItemIterators( AssetFormats.ENUMERATION );

        EnumerationContentHandler contentHandler = spy( new EnumerationContentHandler() );
        runValidate( contentHandler );

        verify( contentHandler ).validateAsset( Matchers.<AssetItem> any() );
        verify( unsavedAssetItem ).getContent();
        verify( savedAssetItem,
                never() ).getContent();
    }

    private void setUpMockDSL() {
        AssetItem dslAssetItem = mock( AssetItem.class );
        when( dslAssetItem.getUUID() ).thenReturn( "TempMockUUID" );
        when( dslAssetItem.getFormat() ).thenReturn( AssetFormats.DSL );
        when( dslAssetItem.getContent() ).thenReturn( "" );
        MockAssetItemIterator mockAssetItemIterator = createMockAssetItemIterator( dslAssetItem );
        when(
                packageItem.listAssetsWithVersionsSpecifiedByDependenciesByFormat( AssetFormats.DSL ) ).thenReturn(
                                                                                                                    mockAssetItemIterator
                );
    }

    public void testValidate(String assetFormat,
                             ContentHandler contentHandler) throws Exception {
        setUpMockAssets( assetFormat );
        setUpAssetItemIterators( assetFormat );
        verifyValidate( contentHandler );
    }

    private void setUpMockAssets(String assetFormat,
                                 String content) {
        when( unsavedAssetItem.getFormat() ).thenReturn( assetFormat );
        when( unsavedAssetItem.getContent() ).thenReturn( content );
        when( savedAssetItem.getFormat() ).thenReturn( assetFormat );
        when( savedAssetItem.getContent() ).thenReturn( content );
    }

    private void setUpMockAssets(String assetFormat) {
        when( unsavedAssetItem.getFormat() ).thenReturn( assetFormat );
        when( savedAssetItem.getFormat() ).thenReturn( assetFormat );
    }

    private void verifyValidate(ContentHandler contentHandler) {
        runValidate( contentHandler );
        verify( unsavedAssetItem ).getContent();
        verify( savedAssetItem,
                never() ).getContent();
    }

    private void runValidate(ContentHandler contentHandler) {
        AssetItemValidator assetItemValidator = new AssetItemValidator( contentHandler,
                                                                        unsavedAssetItem );
        BuilderResult builderResult = assetItemValidator.validate();

        assertTrue( builderResult.getLines().isEmpty() );
    }

    private void setUpPackageItem() {
        packageItem = mock( ModuleItem.class );

        when( packageItem.getName() ).thenReturn( "mock" );
    }

    private void setUpUnsavedAssetItem() {
        unsavedAssetItem = mock( AssetItem.class );
        when( unsavedAssetItem.getModule() ).thenReturn( packageItem );
        when( unsavedAssetItem.getContent() ).thenReturn( "" );
        when( unsavedAssetItem.getUUID() ).thenReturn( "mock" );
    }

    private void setUpSavedAssetItem() {
        savedAssetItem = mock( AssetItem.class );
        when( savedAssetItem.getModule() ).thenReturn( packageItem );
        when( savedAssetItem.getContent() ).thenReturn( "" );
        when( savedAssetItem.getUUID() ).thenReturn( "mock" );
    }

    private void setUpAssetItemIterators(String assetFormat) {
        AssetItemIterator assetItemIterator = createMockAssetItemIterator();
        setUpConfigurations( assetItemIterator );
        setUpAnIteratorForAllAssetFormats( assetItemIterator );

        setUpAssetIteratorForAssetFormat( assetFormat );
    }

    private void setUpAssetIteratorForAssetFormat(String assetFormat) {
        MockAssetItemIterator mockAssetItemIterator = createMockAssetItemIterator( savedAssetItem );
        when(
                packageItem.listAssetsWithVersionsSpecifiedByDependenciesByFormat( assetFormat ) ).thenReturn(
                                                                                                               mockAssetItemIterator
                );
    }

    private void setUpAnIteratorForAllAssetFormats(AssetItemIterator assetItemIterator) {
        when(
                packageItem.listAssetsWithVersionsSpecifiedByDependenciesByFormat( Matchers.<String[]> any() ) ).thenReturn(
                                                                                                                             assetItemIterator
                );
    }

    private void setUpConfigurations(AssetItemIterator assetItemIterator) {
        when(
                packageItem.listAssetsWithVersionsSpecifiedByDependenciesByFormat( AssetFormats.PROPERTIES,
                                                                                   AssetFormats.CONFIGURATION ) ).thenReturn(
                                                                                                                              assetItemIterator
                );
    }

    private MockAssetItemIterator createMockAssetItemIterator(AssetItem... assetItems) {
        MockAssetItemIterator mockAssetItemIterator = new MockAssetItemIterator();
        mockAssetItemIterator.setAssets( assetItems );
        return mockAssetItemIterator;
    }
}
