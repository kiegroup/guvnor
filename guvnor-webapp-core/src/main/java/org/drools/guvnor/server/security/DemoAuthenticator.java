/*
 * Copyright 2005 JBoss Inc
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

package org.drools.guvnor.server.security;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;

import org.jboss.seam.security.BaseAuthenticator;
import org.jboss.seam.security.Credentials;
import org.picketlink.idm.api.Credential;
import org.picketlink.idm.impl.api.PasswordCredential;
import org.picketlink.idm.impl.api.model.SimpleUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This will let any user in (as long as the password matches the username),
 * effectively removing proper authentication.
 * <p/>
 * Useful for demo's, tests and development.
 */
public class DemoAuthenticator extends BaseAuthenticator implements Serializable {

    protected transient final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    private Credentials credentials;

    @Inject
    private RoleBasedPermissionStore roleBasedPermissionStore;

    public void authenticate() {
        upgradeGuestToAdmin();
        String username = credentials.getUsername();
        Credential credential = credentials.getCredential();
        if (username == null || !(credential instanceof PasswordCredential)) {
            setStatus(AuthenticationStatus.FAILURE);
            log.info("Demo login for user (" + username + ") failed: unsupported username/credential.");
            return;
        }
        PasswordCredential passwordCredential = (PasswordCredential) credentials.getCredential();
        if (!username.equals(passwordCredential.getValue())) {
            setStatus(AuthenticationStatus.FAILURE);
            log.info("Demo login for user (" + username + ") failed: wrong username/password.");
            return;
        }
        setStatus(AuthenticationStatus.SUCCESS);
        setUser(new SimpleUser(username));
        log.info("Demo login for user (" + username + ") succeeded.");
    }

    private void upgradeGuestToAdmin() {
        if (credentials.getUsername() != null && credentials.getUsername().equals("guest")) {
            credentials.setUsername("admin");
            credentials.setCredential(new org.picketlink.idm.impl.api.PasswordCredential("admin"));
        }
    }

}
