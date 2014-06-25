package org.guvnor.structure.deployment;

import java.util.Collection;

public interface DeploymentConfigService {

    void addDeployment(String identifier, Object deploymentUnit);

    void removeDeployment(String identifier);

    DeploymentConfig getDeployment(String identifier);

    Collection<DeploymentConfig> getDeployments();
}
