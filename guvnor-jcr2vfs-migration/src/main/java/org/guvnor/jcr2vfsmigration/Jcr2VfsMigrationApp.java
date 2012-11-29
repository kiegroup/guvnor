/*
 * Copyright 2012 JBoss Inc
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

package org.guvnor.jcr2vfsmigration;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.guvnor.jcr2vfsmigration.config.MigrationConfig;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.introspector.WeldAnnotated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Jcr2VfsMigrationApp {

    protected static final Logger logger = LoggerFactory.getLogger(Jcr2VfsMigrationApp.class);

    public static void main(String[] args) {
        MigrationConfig migrationConfig = new MigrationConfig();
        migrationConfig.parseArgs(args);

        Weld weld = new Weld();
        WeldContainer weldContainer = weld.initialize();
        Jcr2VfsMigrater migrater = weldContainer.instance().select(Jcr2VfsMigrater.class).get();
        migrater.migrateAll();
        weld.shutdown();
    }

}
