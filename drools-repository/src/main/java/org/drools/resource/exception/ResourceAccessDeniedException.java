/**
 * Copyright 2010 JBoss Inc
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

package org.drools.resource.exception;

/**
 * @author jwilliams
 *
 */
public class ResourceAccessDeniedException extends Exception {

    private String url;
    private String username;
    private String password;

    public ResourceAccessDeniedException(String url,
                                         String username,
                                         String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public String getMessage() {
        return "You do not have the right access priveleges for this resource: " + url + "\nwith username=" + username + " and password=" + password;
    }
}
