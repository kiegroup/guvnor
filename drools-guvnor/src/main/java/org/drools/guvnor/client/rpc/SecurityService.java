package org.drools.guvnor.client.rpc;
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



import org.drools.guvnor.client.security.Capabilities;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * Contains methods for authenticating/authorising from the front end.
 *
 * @author Michael Neale
 */
public interface SecurityService extends RemoteService {

    /**
     * This will do a password authentication, using the configured JAAS provider.
     * This may be a default one (which allows anything in).
     *
     * @return true if user is logged in successfully.
     */
    public boolean login(String userName, String password);

    /**
     * @return This returns the current user's name if they are logged in. If not
     * then null is returned (inside a context). Will also return some other handy stuff for
     * changing the GUI based on security context.
     */
    public UserSecurityContext getCurrentUser();


}