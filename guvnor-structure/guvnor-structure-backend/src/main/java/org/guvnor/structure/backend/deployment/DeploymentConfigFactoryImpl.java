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
