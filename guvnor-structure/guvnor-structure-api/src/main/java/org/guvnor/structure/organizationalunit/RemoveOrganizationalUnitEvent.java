package org.guvnor.structure.organizationalunit;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.rpc.SessionInfo;

@Portable
public class RemoveOrganizationalUnitEvent extends OrganizationalUnitEventBase {

    public RemoveOrganizationalUnitEvent() {
    }

    public RemoveOrganizationalUnitEvent( final OrganizationalUnit organizationalUnit, final SessionInfo sessionInfo ) {
        super( organizationalUnit, sessionInfo );
    }

}
