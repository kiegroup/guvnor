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

package org.guvnor.jcr2vfsmigration;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.jcr2vfsmigration.config.MigrationConfig;
import org.guvnor.jcr2vfsmigration.migrater.AssetMigrater;
import org.guvnor.jcr2vfsmigration.migrater.CategoryMigrater;
import org.guvnor.jcr2vfsmigration.migrater.ModuleMigrater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class Jcr2VfsMigrater {

    protected static final Logger logger = LoggerFactory.getLogger(Jcr2VfsMigrater.class);

    @Inject
    protected MigrationConfig migrationConfig;

    @Inject
    protected ModuleMigrater moduleMigrater;
    @Inject
    protected AssetMigrater assetMigrater;
    @Inject
    protected CategoryMigrater categoryMigrater;

    public void parseArgs(String[] args) {
        migrationConfig.parseArgs(args);
        System.setProperty("org.kie.nio.git.dir", migrationConfig.getOutputVfsRepository().getAbsolutePath());
    }

    public void migrateAll() {
        logger.info("Migration started: Reading from inputJcrRepository ({}).",
                migrationConfig.getInputJcrRepository().getAbsolutePath());
//    //TO-DO-LIST:
//    //1. How to migrate the globalArea (moduleServiceJCR.listModules() wont return globalArea)
//    //2. How to handle asset imported from globalArea. assetServiceJCR.findAssetPage will return assets imported from globalArea
//    //(like a symbol link). Use Asset.getMetaData().getModuleName()=="globalArea" to determine if the asset is actually from globalArea.
//    //3. Do we want to migrate archived assets and archived packages? probably not...
//    //4. Do we want to migrate package snapshot? probably not...As long as we migrate package history correctly, users can always build a package
//    //with the specified version by themselves.
        moduleMigrater.migrateAll();
        assetMigrater.migrateAll();
        categoryMigrater.migrateAll();
        //  TODO: migratePackagePermissions, migrateRolesAndPermissionsMetaData
        logger.info("Migration ended: Written into outputVfsRepository ({}).",
                migrationConfig.getOutputVfsRepository().getAbsolutePath());
    }

}
