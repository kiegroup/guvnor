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

package org.kie.guvnor.backend.server;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.commons.services.cdi.Startup;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryService;
import org.uberfire.backend.server.config.ConfigGroup;
import org.uberfire.backend.server.config.ConfigType;
import org.uberfire.backend.server.config.ConfigurationFactory;
import org.uberfire.backend.server.config.ConfigurationService;

//This is a temporary solution when running in PROD-MODE as /webapp/.niogit/system.git folder
//is not deployed to the Application Servers /bin folder. This will be remedied when an
//installer is written to create the system.git repository in the correct location.
@Startup
@ApplicationScoped
public class AppSetup {

    // default repository section - start
    private static final String DROOLS_WB_PLAYGROUND_SCHEME = "git";
    private static final String DROOLS_WB_PLAYGROUND_ALIAS = "uf-playground";
    private static final String DROOLS_WB_PLAYGROUND_ORIGIN = "https://github.com/guvnorngtestuser1/guvnorng-playground.git";
    private static final String DROOLS_WB_PLAYGROUND_UID = "guvnorngtestuser1";
    private static final String DROOLS_WB_PLAYGROUND_PWD = "test1234";

    private static final String GLOBAL_SETTINGS = "settings";
    // default repository section - end

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private ConfigurationFactory configurationFactory;

    @PostConstruct
    public void assertPlayground() {
        // TODO in case repo is not defined in system repository so we add default
        final Repository repository = repositoryService.getRepository( DROOLS_WB_PLAYGROUND_ALIAS );
        if ( repository == null ) {
            repositoryService.cloneRepository( DROOLS_WB_PLAYGROUND_SCHEME,
                                               DROOLS_WB_PLAYGROUND_ALIAS,
                                               DROOLS_WB_PLAYGROUND_ORIGIN,
                                               DROOLS_WB_PLAYGROUND_UID,
                                               DROOLS_WB_PLAYGROUND_PWD );
        }

        // TODO in case mandatory properties are not defined in system repository so we add default
        List<ConfigGroup> configGroups = configurationService.getConfiguration( ConfigType.GLOBAL );
        boolean globalSettingsDefined = false;
        for ( ConfigGroup configGroup : configGroups ) {
            if ( GLOBAL_SETTINGS.equals( configGroup.getName() ) ) {
                globalSettingsDefined = true;
                break;
            }
        }
        if ( !globalSettingsDefined ) {
            configurationService.addConfiguration( getGlobalConfiguration() );
        }
    }

    private ConfigGroup getGlobalConfiguration() {
        final ConfigGroup group = configurationFactory.newConfigGroup( ConfigType.GLOBAL,
                                                                       "settings",
                                                                       "" );
        group.addConfigItem( configurationFactory.newConfigItem( "drools.dateformat",
                                                                 "dd-MMM-yyyy" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "drools.datetimeformat",
                                                                 "dd-MMM-yyyy hh:mm:ss" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "drools.defaultlanguage",
                                                                 "en" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "drools.defaultcountry",
                                                                 "US" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "build.enable-incremental",
                                                                 "true" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "rule-modeller-onlyShowDSLStatements",
                                                                 "false" ) );
        return group;
    }

}
