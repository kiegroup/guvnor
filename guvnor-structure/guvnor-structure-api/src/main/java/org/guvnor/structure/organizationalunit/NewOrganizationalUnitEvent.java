package org.guvnor.structure.organizationalunit;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class NewOrganizationalUnitEvent extends OrganizationalUnitEventBase {

    public NewOrganizationalUnitEvent() {
    }

    public NewOrganizationalUnitEvent( final OrganizationalUnit organizationalUnit, final String userName ) {
        super( organizationalUnit, userName );
    }
}
