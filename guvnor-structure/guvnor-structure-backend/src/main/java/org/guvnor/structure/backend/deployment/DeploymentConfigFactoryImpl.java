/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.structure.backend.deployment;

import org.guvnor.structure.deployment.DeploymentConfig;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.deployment.DeploymentConfigFactory;

public class DeploymentConfigFactoryImpl implements DeploymentConfigFactory {

    @Override
    public DeploymentConfig newDeployment( ConfigGroup groupConfig ) {
        return new DeploymentConfigImpl( groupConfig.getName(), groupConfig.getConfigItem( "unit" ).getValue() );
    }
}
