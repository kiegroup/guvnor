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

import org.jboss.seam.security.BaseAuthenticator;
import org.picketlink.idm.impl.api.model.SimpleUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This will let any user in, effectively removing any authentication (as the system
 * will attempt to auto login the first time).
 */
public class DemoAuthenticator extends BaseAuthenticator {

    protected transient final Logger log = LoggerFactory.getLogger(getClass());

    public void authenticate() {
        log.info("All users are guests.");
        setStatus(AuthenticationStatus.SUCCESS);
        setUser(new SimpleUser("guest"));
    }

}
