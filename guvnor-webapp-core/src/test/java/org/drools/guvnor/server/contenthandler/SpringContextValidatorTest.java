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
package org.drools.guvnor.server.contenthandler;

import java.io.InputStream;
import java.io.StringWriter;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.server.MockAssetItemIterator;
import org.drools.guvnor.server.builder.AssetItemValidator;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemIterator;
import org.drools.repository.ModuleItem;
import org.drools.repository.utils.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

public class SpringContextValidatorTest {

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
    public void testValidContext() {
        SpringContextValidator validator = new SpringContextValidator();
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream( "org/drools/guvnor/server/contenthandler/valid-spring-context.xml" );
        validator.setContent( resourceAsStream );
        assertEquals( "",
                      validator.validate() );
    }

    @Test
    public void testInvalidContext() {
        SpringContextValidator validator = new SpringContextValidator();
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream( "org/drools/guvnor/server/contenthandler/invalid-spring-context.xml" );
        validator.setContent( resourceAsStream );
        assertFalse( validator.validate().length() == 0 );
    }

    @Test
    public void testMalformedContext() {
        SpringContextValidator validator = new SpringContextValidator();
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream( "org/drools/guvnor/server/contenthandler/malformed-spring-context.xml" );
        validator.setContent( resourceAsStream );
        assertFalse( validator.validate().length() == 0 );
    }

    @Test
    public void testValidateSpringContext() throws Exception {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream( "org/drools/guvnor/server/contenthandler/valid-spring-context.xml" );
        StringWriter stringWriter = new StringWriter();
        IOUtils.copy( resourceAsStream,
                      stringWriter );

        setUpMockAssets( AssetFormats.SPRING_CONTEXT,
                         stringWriter.toString() );
        setUpAssetItemIterators( AssetFormats.SPRING_CONTEXT );

        SpringContextContentHandler contentHandler = spy( new SpringContextContentHandler() );
        runValidate( contentHandler );

        verify( contentHandler ).validateAsset( Matchers.<AssetItem> any() );
        verify( unsavedAssetItem ).getContent();
        verify( savedAssetItem,
                never() ).getContent();
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

    private void runValidate(ContentHandler contentHandler) {
        AssetItemValidator assetItemValidator = new AssetItemValidator( contentHandler,
                                                                        unsavedAssetItem );
        BuilderResult builderResult = assetItemValidator.validate();

        assertTrue( builderResult.getLines().isEmpty() );
    }

    private void setUpMockAssets(String assetFormat,
                                 String content) {
        when( unsavedAssetItem.getFormat() ).thenReturn( assetFormat );
        when( unsavedAssetItem.getContent() ).thenReturn( content );
        when( savedAssetItem.getFormat() ).thenReturn( assetFormat );
        when( savedAssetItem.getContent() ).thenReturn( content );
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
