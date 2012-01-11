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

package org.drools.guvnor.server;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.*;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemIterator;
import org.drools.repository.ModuleItem;
import org.drools.repository.RulesRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class VerificationServiceImplementationTest {

    // TODO this entire test must be rewritten to extend GuvnorTestBase and test it for real

    private VerificationService verificationService;
    private ModuleItem packageItem;
    private RulesRepository rulesRepository;

    @Before
    public void setUp() {
        final RepositoryAssetService repositoryAssetService = mock(RepositoryAssetService.class);
        VerificationServiceImplementation verificationServiceImplementation = new VerificationServiceImplementation() ;
        verificationServiceImplementation.repositoryAssetService = repositoryAssetService;

        rulesRepository = mock(RulesRepository.class);
        repositoryAssetService.rulesRepository = rulesRepository;
        packageItem = createPackage();

        AssetItemIterator assetItemIterator = mock(AssetItemIterator.class);
        when(assetItemIterator.hasNext()).thenReturn(false);
        when(packageItem.listAssetsByFormat(Matchers.<String>anyVararg())).thenReturn(assetItemIterator);
        when(rulesRepository.loadModule("mockPackage")).thenReturn(packageItem);

        verificationService = verificationServiceImplementation;
    }

    @Test
    public void testVerifyAsset() throws Exception {


        MockAssetItemIterator itemIterator = new MockAssetItemIterator();
        AssetItem assetItem = getAssetItem("");

        when(rulesRepository.loadAssetByUUID(Matchers.<String>any())).thenReturn(assetItem);

        itemIterator.setAssets(assetItem);
        when(packageItem.listAssetsByFormat(AssetFormats.DRL)).thenReturn(itemIterator);

        String drl = "";
        drl += "rule Test\n";
        drl += "when\n";
        drl += "P(a==true)\n";
        drl += "then\n";
        drl += "end\n";

        Asset ruleAsset = getAsset(drl);
        AnalysisReport report = verificationService.verifyAsset(
                ruleAsset,
                new HashSet<String>());

        assertNotNull(report);
        assertEquals(0,
                report.errors.length);
        assertEquals(0,
                report.warnings.length);
        assertEquals(0,
                report.notes.length);

    }

    public Asset getAsset(String content) {
        Asset ruleAsset = new Asset();

        ruleAsset.uuid = "mockUUID";
        ruleAsset.metaData = getMetaData();
        ruleAsset.setFormat( AssetFormats.DRL);
        ruleAsset.content = getRuleContentText(content);
        return ruleAsset;
    }

    private AssetItem getAssetItem(String content) {
        AssetItem assetItem = mock(AssetItem.class);
        when(assetItem.getUUID()).thenReturn("mockUUID");
        when(assetItem.getFormat()).thenReturn(AssetFormats.DRL);
        when(assetItem.getContent()).thenReturn(content);
        when(assetItem.getModule()).thenReturn(packageItem);
        return assetItem;
    }

    private RuleContentText getRuleContentText(String content) {
        RuleContentText ruleContentText = new RuleContentText();
        ruleContentText.content = content;
        return ruleContentText;
    }

    public MetaData getMetaData() {
        MetaData metaData = new MetaData();
        metaData.moduleName = "mockPackage";
        return metaData;
    }

    public ModuleItem createPackage() {
        ModuleItem packageItem = mock(ModuleItem.class);
        when(packageItem.getName()).thenReturn("mockPackage");
        return packageItem;
    }
}
