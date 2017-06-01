/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guvnor.ala.wildfly.model;

import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.runtime.providers.Provider;

/*
 * Represents the specific information that we are going to use to configure and
 * provision applications to Wildfly.
 * @see ProviderConfig
 * @see Provider
 */
public interface WildflyProvider extends ProviderConfig,
        Provider {

    /*
     * Get the Host Id where a Wildfly Instance is configured
     * @return String with the port
     */
    String getHostId();

    /*
     * Get the Port number used by the Wilfly Instance
     * @return String with the port
     */
    String getPort();

    /*
     * Get the Management Port number used by the Wilfly Instance
     * @return String with the management port
     */
    String getManagementPort();

    /*
     * Get the User name used to configure or remotely interact the Wilfly Instance
     * @return String with the username
     */
    String getUser();

    /*
     * Get the Password used to configure or remotely interact the Wilfly Instance
     * @return String with the password
     */
    String getPassword();

}
