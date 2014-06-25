package org.guvnor.structure.backend.organizationalunit;

import java.util.List;
import javax.inject.Inject;

import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.impl.OrganizationalUnitImpl;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
import org.guvnor.structure.server.organizationalunit.OrganizationalUnitFactory;

public class OrganizationalUnitFactoryImpl implements OrganizationalUnitFactory {

    @Inject
    private RepositoryService repositoryService;

    @Override
    public OrganizationalUnit newOrganizationalUnit( ConfigGroup groupConfig ) {

        OrganizationalUnitImpl organizationalUnit = new OrganizationalUnitImpl( groupConfig.getName(),
                                                                                groupConfig.getConfigItemValue( "owner" ) );
        ConfigItem<List<String>> repositories = groupConfig.getConfigItem( "repositories" );
        if ( repositories != null ) {
            for ( String alias : repositories.getValue() ) {
                organizationalUnit.getRepositories().add( repositoryService.getRepository( alias ) );
            }
        }

        //Copy in Security Roles required to access this resource
        ConfigItem<List<String>> roles = groupConfig.getConfigItem( "security:roles" );
        if ( roles != null ) {
            for ( String role : roles.getValue() ) {
                organizationalUnit.getRoles().add( role );
            }
        }
        return organizationalUnit;
    }
}
