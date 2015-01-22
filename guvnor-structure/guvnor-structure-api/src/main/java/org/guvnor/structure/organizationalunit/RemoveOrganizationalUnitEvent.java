package org.guvnor.structure.organizationalunit;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class RemoveOrganizationalUnitEvent extends OrganizationalUnitEventBase {

    public RemoveOrganizationalUnitEvent() {
    }

    public RemoveOrganizationalUnitEvent( final OrganizationalUnit organizationalUnit, final String userName ) {
        super( organizationalUnit, userName );
    }

}
