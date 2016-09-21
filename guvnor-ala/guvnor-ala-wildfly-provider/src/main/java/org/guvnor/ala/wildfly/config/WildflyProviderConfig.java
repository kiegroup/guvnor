
package org.guvnor.ala.wildfly.config;

import org.guvnor.ala.config.ProviderConfig;

/*
 * This interface represents the basic information that we need for configuring a
 * WildflyProvider
 * @see ProviderConfig
 */
public interface WildflyProviderConfig extends ProviderConfig {

    /*
     * Get the Provider name
     * @return String with the provider name. By default "local"
     */
    default String getName() {
        return "local";
    }

    /*
     * Get the Provider Host IP address
     * @return String host IP for the provider. If not provided it will 
     *  resolve the expression: ${input.host} from the Pipeline's Input map
     */
    default String getHostIp() {
        return "${input.host}";
    }

    /*
     * Get the Provider Host Port
     * @return String host port for the provider. If not provided it will 
     *  resolve the expression: ${input.port} from the Pipeline's Input map
     */
    default String getPort() {
        return "${input.port}";
    }

    /*
     * Get the Provider Management Port
     * @return String management port for the provider. If not provided it will 
     *  resolve the expression: ${input.management-port} from the Pipeline's Input map
     */
    default String getManagementPort() {
        return "${input.management-port}";
    }

    /*
     * Get the Provider user name
     * @return String username used to interact with the provider. If not provided it will 
     *  resolve the expression: ${input.wildfly-user} from the Pipeline's Input map
     */
    default String getUser() {
        return "${input.wildfly-user}";
    }

    /*
     * Get the Provider password
     * @return String password used to interact with the provider. If not provided it will 
     *  resolve the expression: ${input.wildfly-password} from the Pipeline's Input map
     */
    default String getPassword() {
        return "${input.wildfly-password}";
    }

}
