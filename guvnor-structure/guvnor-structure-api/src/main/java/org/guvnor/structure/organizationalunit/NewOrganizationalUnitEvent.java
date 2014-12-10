package org.guvnor.structure.organizationalunit;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.rpc.SessionInfo;

@Portable
public class NewOrganizationalUnitEvent extends OrganizationalUnitEventBase {

    public NewOrganizationalUnitEvent() {
    }

    public NewOrganizationalUnitEvent( final OrganizationalUnit organizationalUnit, final SessionInfo sessionInfo ) {
        super( organizationalUnit, sessionInfo );
    }
}
