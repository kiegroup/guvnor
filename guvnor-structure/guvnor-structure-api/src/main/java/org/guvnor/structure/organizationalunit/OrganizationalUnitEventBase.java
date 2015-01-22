/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.structure.organizationalunit;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.rpc.SessionInfo;

/**
 * Common attributes for OU events, sub classes should be fired instead of this.
 */
@Portable
public class OrganizationalUnitEventBase {

    protected OrganizationalUnit organizationalUnit;
    protected String userName;

    public OrganizationalUnitEventBase() {
    }

    public OrganizationalUnitEventBase( final OrganizationalUnit organizationalUnit, final String userName ) {
        this.organizationalUnit = organizationalUnit;
        this.userName = userName;
    }

    public OrganizationalUnit getOrganizationalUnit() {
        return organizationalUnit;
    }

    public void setOrganizationalUnit( OrganizationalUnit organizationalUnit ) {
        this.organizationalUnit = organizationalUnit;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    /*
     * we need to keep the old things like session info methods as it's portable object
     * only for cr4 patch to deliver pure jars without gwt/errai compilation
     */
    public SessionInfo getSessionInfo() {
        return null;
    }

    public void setSessionInfo( SessionInfo sessionInfo ) {

    }
}
