/*
 * Copyright 2011 JBoss Inc
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

package org.drools.guvnor.server.repository;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.enterprise.context.ApplicationScoped;

import org.drools.repository.RulesRepositoryConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class GuvnorBootstrapConfiguration {

    private static final String ADMIN_USERNAME_DEFAULT = "admin";
    private static final String ADMIN_USERNAME_PROPERTY = "org.drools.repository.admin.username";
    private static final String ADMIN_PASSWORD_DEFAULT = "password";
    private static final String ADMIN_PASSWORD_PROPERTY = "org.drools.repository.admin.password";

    private static final String MAILMAN_USERNAME_DEFAULT = "mailman";
    private static final String MAILMAN_USERNAME_PROPERTY = "org.drools.repository.mailman.username";
    private static final String MAILMAN_PASSWORD_DEFAULT = "password";
    private static final String MAILMAN_PASSWORD_PROPERTY = "org.drools.repository.mailman.password";

    private static final String SECURE_PASSWORDS_PROPERTY = "org.drools.repository.secure.passwords";

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private Map<String, String> properties = new HashMap<String, String>();

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void validate() {
        if (!properties.containsKey(RulesRepositoryConfigurator.CONFIGURATOR_CLASS)) {
            throw new IllegalStateException("The beans.xml file does not have a GuvnorBootstrapConfiguration " +
                    "with a property for the configurator class (" + RulesRepositoryConfigurator.CONFIGURATOR_CLASS
                    + ") configured.");
        }
    }

    public String extractAdminUsername() {
        if (!properties.containsKey(ADMIN_USERNAME_PROPERTY)) {
            return ADMIN_USERNAME_DEFAULT;
        }
        return properties.get(ADMIN_USERNAME_PROPERTY);
    }

    public String extractAdminPassword() {
        if (!properties.containsKey(ADMIN_PASSWORD_PROPERTY)) {
            log.debug("Could not find property " + ADMIN_PASSWORD_PROPERTY + " for user " + ADMIN_USERNAME_DEFAULT);
            return ADMIN_PASSWORD_DEFAULT;
        }
        String password = properties.get(ADMIN_PASSWORD_PROPERTY);
        if ("true".equalsIgnoreCase(properties.get(SECURE_PASSWORDS_PROPERTY))) {
            password = decode(password);
        }
        return password;
    }

    public String extractMailmanUsername() {
        if (!properties.containsKey(MAILMAN_USERNAME_PROPERTY)) {
            return MAILMAN_USERNAME_DEFAULT;
        }
        return properties.get(MAILMAN_USERNAME_PROPERTY);
    }

    public String extractMailmanPassword() {
        if (!properties.containsKey(MAILMAN_PASSWORD_PROPERTY)) {
            log.debug("Could not find property " + MAILMAN_PASSWORD_PROPERTY + " for user " + MAILMAN_USERNAME_DEFAULT);
            return MAILMAN_PASSWORD_DEFAULT;
        }
        String password = properties.get(MAILMAN_PASSWORD_PROPERTY);
        if ("true".equalsIgnoreCase(properties.get(SECURE_PASSWORDS_PROPERTY))) {
            password = decode(password);
        }
        return password;
    }

    private String decode(String secret) {
        String decodedPassword = secret;
        try {
            byte[] kbytes = "jaas is the way".getBytes();
            SecretKeySpec key = new SecretKeySpec(kbytes, "Blowfish");

            BigInteger n = new BigInteger(secret, 16);
            byte[] encoding = n.toByteArray();

            //SECURITY-344: fix leading zeros
            if (encoding.length % 8 != 0) {
                int length = encoding.length;
                int newLength = ((length / 8) + 1) * 8;
                int pad = newLength - length; //number of leading zeros
                byte[] old = encoding;
                encoding = new byte[newLength];

                for (int i = old.length - 1; i >= 0; i--) {
                    encoding[i + pad] = old[i];
                }
            }

            Cipher cipher = Cipher.getInstance("Blowfish");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decode = cipher.doFinal(encoding);
            decodedPassword = new String(decode);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return decodedPassword;
    }

}
