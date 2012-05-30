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

package org.drools.guvnor.server.verification;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.AnalysisReport;
import org.drools.guvnor.client.widgets.toolbar.DefaultActionToolbarButtonsConfigurationProvider;
import org.drools.guvnor.server.MockAssetItemIterator;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemIterator;
import org.drools.repository.ModuleItem;
import org.drools.verifier.Verifier;
import org.drools.verifier.data.VerifierData;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessageBase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.Collections;
import java.util.HashMap;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class AssetVerifierTest {

    private ModuleItem packageItem;

    @Before
    public void setUp() {
        packageItem = createPackage();
        setUpAssetItemIterator();
    }

    private Verifier createVerifier() {
        Verifier verifier = mock(Verifier.class);
        VerifierReport verifierReport = createVerifierReport();
        when(verifier.getResult()).thenReturn(verifierReport);
        return verifier;
    }

    private VerifierReport createVerifierReport() {
        VerifierReport report = mock(VerifierReport.class);
        when(report.getBySeverity(Matchers.<Severity>any())).thenReturn(Collections.<VerifierMessageBase>emptyList());
        VerifierData verifierData = mock(VerifierData.class);
        when(report.getVerifierData()).thenReturn(verifierData);
        return report;
    }

    private void setUpAssetItemIterator() {
        AssetItemIterator assetItemIterator = mock(AssetItemIterator.class);
        when(assetItemIterator.hasNext()).thenReturn(false);
        when(packageItem.listAssetsByFormat(Matchers.<String>anyVararg())).thenReturn(assetItemIterator);
    }

    @Test
    public void testVerifySingleAsset() throws Exception {
        for (String format : DefaultActionToolbarButtonsConfigurationProvider.VERIFY_FORMATS) {

            if (format.equals(AssetFormats.DECISION_SPREADSHEET_XLS)) {
                // Binary asset, can not be tested with mocks.
                continue;
            }

            AssetItem originalAssetItem = getAssetItem(format);
            setUpAssetIterator(originalAssetItem, format);

            AssetItem assetItemUnderVerification = getAssetItem(format);


            AssetVerifier verifierRunner = new AssetVerifier(createVerifier(), assetItemUnderVerification);

            AnalysisReport report = verifierRunner.verify();

            verify(originalAssetItem, never()).getContent();
            verify(assetItemUnderVerification).getContent();

            assertNotNull(report);
        }
    }

    private void setUpAssetIterator(AssetItem originalAssetItem, String format) {
        MockAssetItemIterator itemIterator = new MockAssetItemIterator();
        itemIterator.setAssets(originalAssetItem);
        when(packageItem.listAssetsByFormat(format)).thenReturn(itemIterator);
    }

    private AssetItem getAssetItem(String format) {
        AssetItem assetItem = mock(AssetItem.class);
        when(assetItem.getUUID()).thenReturn("mockUUID");
        when(assetItem.getModule()).thenReturn(packageItem);
        when(assetItem.getFormat()).thenReturn(format);
        when(assetItem.getContent()).thenReturn("");
        return assetItem;
    }

    public ModuleItem createPackage() {
        ModuleItem packageItem = mock(ModuleItem.class);
        when(packageItem.getName()).thenReturn("mockPackage");
        when(packageItem.getCategoryRules()).thenReturn(new HashMap<String, String>());
        return packageItem;
    }
}
