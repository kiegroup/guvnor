package org.guvnor.structure.server.deployment;

import org.guvnor.structure.deployment.DeploymentConfig;
import org.guvnor.structure.server.config.ConfigGroup;

public interface DeploymentConfigFactory {

    DeploymentConfig newDeployment(ConfigGroup groupConfig);
}
