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

package org.drools.guvnor.server.builder;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.server.contenthandler.FactModelContentHandler;
import org.drools.repository.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import javax.jcr.NodeIterator;
import java.util.Arrays;
import java.util.Iterator;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class AssetItemValidatorTest {

    private PackageItem packageItem;
    private AssetItem unsavedAssetItem;
    private AssetItem savedAssetItem;


    @Before
    public void setUp() throws Exception {
        setUpPackageItem();
        setUpUnsavedAssetItem();
        setUpSavedAssetItem();
        setUpAssetItemIterator();
    }

    @Test
    public void testValidateDRLModel() throws Exception {

        FactModelContentHandler factModelContentHandler = new FactModelContentHandler();

        AssetItemValidator assetItemValidator = new AssetItemValidator(factModelContentHandler, unsavedAssetItem);
        BuilderResult builderResult = assetItemValidator.validate();

        assertTrue(builderResult.getLines().isEmpty());
        verify(unsavedAssetItem).getContent();
        verify(savedAssetItem, never()).getContent();
    }

    // TODO: validate other asset types -Rikkola-
    // TODO: test failing validations -Rikkola-
    // TODO: test custom validators -Rikkola-

    private void setUpPackageItem() {
        packageItem = mock(PackageItem.class);

        when(packageItem.getName()).thenReturn("mock");
    }

    private void setUpUnsavedAssetItem() {
        unsavedAssetItem = mock(AssetItem.class);
        when(unsavedAssetItem.getPackage()).thenReturn(packageItem);
        when(unsavedAssetItem.getContent()).thenReturn("");
        when(unsavedAssetItem.getUUID()).thenReturn("mock");
    }

    private void setUpSavedAssetItem() {
        savedAssetItem = mock(AssetItem.class);
        when(savedAssetItem.getPackage()).thenReturn(packageItem);
        when(savedAssetItem.getContent()).thenReturn("");
        when(savedAssetItem.getUUID()).thenReturn("mock");
    }

    private void setUpAssetItemIterator() {
        AssetItemIterator assetItemIterator = createMockAssetItemIterator();
        when(
                packageItem.listAssetsWithVersionsSpecifiedByDependenciesByFormat(AssetFormats.PROPERTIES, AssetFormats.CONFIGURATION)
        ).thenReturn(
                assetItemIterator
        );
        when(
                packageItem.listAssetsWithVersionsSpecifiedByDependenciesByFormat(Matchers.<String[]>any())
        ).thenReturn(
                assetItemIterator
        );

        MockAssetItemIterator dslMockAssetItemIterator = createMockAssetItemIterator(savedAssetItem);
        when(
                packageItem.listAssetsWithVersionsSpecifiedByDependenciesByFormat(AssetFormats.DSL)
        ).thenReturn(
                dslMockAssetItemIterator
        );
    }

    private MockAssetItemIterator createMockAssetItemIterator(AssetItem... assetItems) {
        MockAssetItemIterator mockAssetItemIterator = new MockAssetItemIterator(mock(NodeIterator.class), mock(RulesRepository.class), new String[0]);
        mockAssetItemIterator.setAssets(assetItems);
        return mockAssetItemIterator;
    }

    class MockAssetItemIterator extends VersionedAssetItemIterator {

        private Iterator<AssetItem> assetItems;

        public MockAssetItemIterator(NodeIterator nodes, RulesRepository repo, String[] dependencies) {
            super(nodes, repo, dependencies);
        }

        public boolean hasNext() {
            return assetItems.hasNext();
        }

        public AssetItem next() {
            return assetItems.next();
        }

        public void setAssets(AssetItem[] assetItems) {
            this.assetItems = Arrays.asList(assetItems).iterator();
        }
    }
}
