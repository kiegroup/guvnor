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
import org.drools.guvnor.client.explorer.ExplorerNodeConfig;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.MetaDataQuery;
import org.drools.guvnor.client.rpc.TableDataResult;
import org.drools.guvnor.server.test.GuvnorIntegrationTest;
import org.drools.repository.AssetItem;
import org.drools.repository.ModuleItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.drools.guvnor.client.rpc.Path;

import static org.junit.Assert.*;

public class ServiceImplSecurityIntegrationTest extends GuvnorIntegrationTest {

    private static final String USER_NAME = "serviceImplSecurityUser";

    public ServiceImplSecurityIntegrationTest() {
        autoLoginAsAdmin = false;
    }

    @Before
    public void loginAsSpecificUser() {
        loginAs(USER_NAME);

    }

    @After
    public void logoutAsSpecificUser() {
        logoutAs(USER_NAME);
    }

    // Access an asset that belongs to no category. No role permission defined. RoleBasedAuthorization is not enabled

    @Test
    public void testLoadRuleAssetNoCategory() throws Exception {
        rulesRepository.createModule("testLoadRuleAssetNoCategoryPack1",
                "desc");
        repositoryCategoryService.createCategory("",
                "testLoadRuleAssetNoCategoryCat1",
                "this is a cat");

        Path uuid = serviceImplementation.createNewRule("testLoadRuleAssetNoCategoryRule1",
                "description",
                null,
                "testLoadRuleAssetNoCategoryPack1",
                AssetFormats.DRL);

        // Like this by default: roleBasedPermissionResolver.setEnableRoleBasedAuthorization(false);

        // now lets see if we can access this asset with the permissions
        Asset asset = repositoryAssetService.loadRuleAsset(uuid);
        assertNotNull(asset);
    }

    @Test
    public void testCreateNewRule() throws Exception {
        rulesRepository.createModule("testSecurityCreateNewRule",
                "desc");
        repositoryCategoryService.createCategory("",
                "testSecurityCreateNewRule",
                "this is a cat");


        serviceImplementation.createNewRule("testCreateNewRuleName22",
                "an initial desc",
                "testSecurityCreateNewRule",
                "testSecurityCreateNewRule",
                AssetFormats.DSL_TEMPLATE_RULE);
        fail("not allowed");
        // TODO leave roleBasedAuthorization enabled and add RoleType.PACKAGE_DEVELOPER permission
        serviceImplementation.createNewRule("testCreateNewRuleName22",
                "an initial desc",
                "testSecurityCreateNewRule",
                "testSecurityCreateNewRule",
                AssetFormats.DSL_TEMPLATE_RULE);

    }

    @Test
    public void testTableDisplayHandler() throws Exception {
        String rule11Name = "testLoadRuleAssetWithRoleBasedAuthrozationForMetaData11";
        String rule12Name = "testLoadRuleAssetWithRoleBasedAuthrozationForMetaData12";

        String package11Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyPack11";
        String category11Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyCat11";
        ModuleItem packageItem11 = rulesRepository.createModule(package11Name,
                "desc");
        @SuppressWarnings("unused")
        String packageItem11UUID = packageItem11.getUUID();
        repositoryCategoryService.createCategory("",
                category11Name,
                "this is a dock");
        @SuppressWarnings("unused")
        Path uuid11 = serviceImplementation.createNewRule(rule11Name,
                "DisplayHandlerDescription11",
                category11Name,
                package11Name,
                AssetFormats.DRL);

        String package12Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyPack12";
        String category12Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyCat12";
        ModuleItem packageItem12 = rulesRepository.createModule(package12Name,
                "desc");
        @SuppressWarnings("unused")
        String packageItem12UUID = packageItem12.getUUID();
        repositoryCategoryService.createCategory("",
                category12Name,
                "this is a sparrow");
        @SuppressWarnings("unused")
        Path uuid12 = serviceImplementation.createNewRule(rule12Name,
                "DisplayHandlerDescription12",
                category12Name,
                package12Name,
                AssetFormats.DRL);

        MetaDataQuery[] qr = new MetaDataQuery[1];
        qr[0] = new MetaDataQuery();
        qr[0].attribute = AssetItem.DESCRIPTION_PROPERTY_NAME;
        qr[0].valueList = "DisplayHandlerDescription%";

        TableDataResult result = serviceImplementation.queryMetaData(qr,
                null,
                null,
                null,
                null,
                false,
                1,
                1);
        assertEquals(1,
                result.data.length);

        result = serviceImplementation.queryMetaData(qr,
                null,
                null,
                null,
                null,
                false,
                0,
                1);
        assertEquals(1,
                result.data.length);

        result = serviceImplementation.queryMetaData(qr,
                null,
                null,
                null,
                null,
                false,
                0,
                4);
        assertEquals(2,
                result.data.length);

        result = serviceImplementation.queryMetaData(qr,
                null,
                null,
                null,
                null,
                false,
                -1,
                4);
        assertEquals(2,
                result.data.length);

        result = serviceImplementation.queryMetaData(qr,
                null,
                null,
                null,
                null,
                false,
                6,
                4);
        assertEquals(0,
                result.data.length);

    }

    @Test
    public void testLoadChildCategories() throws Exception {
        String package1Name = "testLoadChildCategoriesPack1";
        String category1Name = "testLoadChildCategoriesCat1";
        String category2Name = "testLoadChildCategoriesCat2";

        rulesRepository.createModule(package1Name,
                "desc");
        repositoryCategoryService.createCategory("",
                category1Name,
                "this is a cat");
        repositoryCategoryService.createCategory("",
                category2Name,
                "this is a cat");

        serviceImplementation.createNewRule("testLoadChildCategoriesRule1",
                "description",
                category1Name,
                package1Name,
                AssetFormats.DRL);

        serviceImplementation.createNewRule("testLoadChildCategoriesRule2",
                "description",
                category2Name,
                package1Name,
                AssetFormats.DRL);

        String[] res = repositoryCategoryService.loadChildCategories("/");
        assertEquals(1,
                res.length);

    }

    @Test
    public void testloadRuleListForCategoriesPackageReadonly() throws Exception {
        String package1Name = "testloadRuleListForCategoriesPackageReadonlyPack1";
        String category1Name = "testloadRuleListForCategoriesPackageReadonlyCat1";

        rulesRepository.createModule(package1Name,
                "desc");
        repositoryCategoryService.createCategory("",
                category1Name,
                "this is a cat");

        serviceImplementation.createNewRule("testloadRuleListForCategoriesPackageReadonlyRule1",
                "description",
                category1Name,
                package1Name,
                AssetFormats.DRL);

        String package2Name = "testloadRuleListForCategoriesPackageReadonlyPack2";
        rulesRepository.createModule(package2Name,
                "desc");

        serviceImplementation.createNewRule("testloadRuleListForCategoriesPackageReadonlyRule2",
                "description",
                category1Name,
                package2Name,
                AssetFormats.DRL);

        String package3Name = "testloadRuleListForCategoriesPackageReadonlyPack3";
        rulesRepository.createModule(package3Name,
                "desc");

        serviceImplementation.createNewRule("testloadRuleListForCategoriesPackageReadonlyRule3",
                "description",
                category1Name,
                package3Name,
                AssetFormats.DRL);

        TableDataResult res = repositoryCategoryService.loadRuleListForCategories("testloadRuleListForCategoriesPackageReadonlyCat1",
                0,
                -1,
                ExplorerNodeConfig.RULE_LIST_TABLE_ID);
        assertEquals(0,
                res.data.length);

    }

    @Test
    public void testloadRuleListForCategoriesPackageReadonlyPositive() throws Exception {
        String package1Name = "testloadRuleListForCategoriesPackageReadonlyPositivePack1";
        String category1Name = "testloadRuleListForCategoriesPackageReadonlyPositiveCat1";

        rulesRepository.createModule(package1Name,
                "desc");
        repositoryCategoryService.createCategory("",
                category1Name,
                "this is a cat");

        serviceImplementation.createNewRule("testloadRuleListForCategoriesPackageReadonlyPositiveRule1",
                "description",
                category1Name,
                package1Name,
                AssetFormats.DRL);

        String package2Name = "testloadRuleListForCategoriesPackageReadonlyPositivePack2";
        rulesRepository.createModule(package2Name,
                "desc");

        serviceImplementation.createNewRule("testloadRuleListForCategoriesPackageReadonlyPositiveRule2",
                "description",
                category1Name,
                package2Name,
                AssetFormats.DRL);

        String package3Name = "testloadRuleListForCategoriesPackageReadonlyPositivePack3";
        rulesRepository.createModule(package3Name,
                "desc");

        serviceImplementation.createNewRule("testloadRuleListForCategoriesPackageReadonlyPositiveRule3",
                "description",
                category1Name,
                package3Name,
                AssetFormats.DRL);

        TableDataResult res = repositoryCategoryService.loadRuleListForCategories("testloadRuleListForCategoriesPackageReadonlyPositiveCat1",
                0,
                -1,
                ExplorerNodeConfig.RULE_LIST_TABLE_ID);
        assertEquals(3,
                res.data.length);

    }

}
