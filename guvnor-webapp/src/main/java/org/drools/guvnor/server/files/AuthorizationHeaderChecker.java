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

package org.drools.guvnor.server.files;

import java.util.Locale;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.util.codec.Base64;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Deprecated
public class AuthorizationHeaderChecker {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    protected Identity identity;

    @Inject
    protected Credentials credentials;

    /**
     * Deprecated: should use the seam-security support for this
     * <p/>
     * Check the users credentials.
     * This takes the Authorization string from the HTTP request header (the whole lot).
     * uses Seam Identity component to set the user up.
     */
    @Deprecated
    public boolean loginByHeader(String auth) {
        //If the request is from same session, the user should be logged in already.
        if (identity.isLoggedIn()) {
            return true;
        }
        String username = null;
        String password = null;
        if (auth != null && auth.toUpperCase(Locale.ENGLISH).startsWith("BASIC ")) {
            String[] a = unpack(auth);
            username = a[0];
            password = a[1];
            credentials.setUsername(username);
            credentials.setCredential(new org.picketlink.idm.impl.api.PasswordCredential(password));
        }
        identity.login();
        if ( !identity.isLoggedIn() ) {
            log.warn("Unable to authenticate for rest api: " + username);
            return false;
        }
        log.info(username + " authenticated for rest api");
        return true;
    }

    protected String[] unpack(String auth) {
        // Get encoded user and password, comes after "BASIC "
        String userpassEncoded = auth.substring(6);
        String userpassDecoded = new String(Base64.decodeBase64(userpassEncoded.getBytes()));

        String[] a = userpassDecoded.split(":");
        for (int i = 0; i < a.length; i++) {
            a[i] = a[i].trim();
        }
        if (a.length == 2) {
            return a;
        } else if (a.length == 1) {
            //pwd is empty
            return new String[]{a[0], ""};
        } else {
            return new String[]{"", ""};
        }
    }

}
