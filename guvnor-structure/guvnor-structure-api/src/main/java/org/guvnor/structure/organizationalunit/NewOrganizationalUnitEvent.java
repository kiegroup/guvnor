package org.guvnor.structure.organizationalunit;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.rpc.SessionInfo;

@Portable
public class NewOrganizationalUnitEvent {

    private OrganizationalUnit organizationalUnit;
    private SessionInfo sessionInfo;

    public NewOrganizationalUnitEvent() {
    }

    public NewOrganizationalUnitEvent( final OrganizationalUnit organizationalUnit, SessionInfo sessionInfo ) {
        this.organizationalUnit = organizationalUnit;
        this.sessionInfo = sessionInfo;
    }

    public OrganizationalUnit getOrganizationalUnit() {
        return organizationalUnit;
    }

    public void setOrganizationalUnit( final OrganizationalUnit organizationalUnit ) {
        this.organizationalUnit = organizationalUnit;
    }

    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }

    public void setSessionInfo( SessionInfo sessionInfo ) {
        this.sessionInfo = sessionInfo;
    }
}
