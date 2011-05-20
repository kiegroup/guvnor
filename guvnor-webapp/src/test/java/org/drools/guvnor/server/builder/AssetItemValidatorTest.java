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
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemIterator;
import org.drools.repository.PackageItem;
import org.drools.repository.VersionedAssetItemIterator;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AssetItemValidatorTest {

    private PackageItem packageItem;

    @Test
    public void testValidateDRLModel() throws Exception {
        AssetItem assetItem = setUpAssetItem();

        setUpPropertiesAndConfiguration();

        FactModelContentHandler factModelContentHandler = new FactModelContentHandler();

        AssetItemValidator assetItemValidator = new AssetItemValidator(factModelContentHandler);
        BuilderResult builderResult = assetItemValidator.validate(assetItem);

        assertTrue(builderResult.getLines().isEmpty());
    }

    private AssetItem setUpAssetItem() {
        packageItem = mock(PackageItem.class);
        when(packageItem.getName()).thenReturn("mock");
        AssetItem assetItem = mock(AssetItem.class);
        when(assetItem.getPackage()).thenReturn(packageItem);
        return assetItem;
    }

    private void setUpPropertiesAndConfiguration() {
        AssetItemIterator assetItemIterator = mock(VersionedAssetItemIterator.class);
        when(assetItemIterator.hasNext()).thenReturn(false);
        setUpIterator(assetItemIterator, AssetFormats.PROPERTIES, AssetFormats.CONFIGURATION);
    }

    private void setUpIterator(AssetItemIterator assetItemIterator, String... formats) {
        when(packageItem.listAssetsByFormat(formats)).thenReturn(assetItemIterator);
    }


}
