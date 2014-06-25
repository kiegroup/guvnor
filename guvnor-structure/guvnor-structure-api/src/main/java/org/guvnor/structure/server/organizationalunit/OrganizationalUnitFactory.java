package org.guvnor.structure.server.organizationalunit;

import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.server.config.ConfigGroup;

public interface OrganizationalUnitFactory {

    OrganizationalUnit newOrganizationalUnit( ConfigGroup groupConfig );
}
