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
    protected SessionInfo sessionInfo;

    public OrganizationalUnitEventBase() {
    }

    public OrganizationalUnitEventBase( final OrganizationalUnit organizationalUnit, final SessionInfo sessionInfo ) {
        this.organizationalUnit = organizationalUnit;
        this.sessionInfo = sessionInfo;
    }

    public OrganizationalUnit getOrganizationalUnit() {
        return organizationalUnit;
    }

    public void setOrganizationalUnit( OrganizationalUnit organizationalUnit ) {
        this.organizationalUnit = organizationalUnit;
    }

    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }

    public void setSessionInfo( SessionInfo sessionInfo ) {
        this.sessionInfo = sessionInfo;
    }

}
