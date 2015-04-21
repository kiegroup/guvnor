package org.guvnor.structure.backend.organizationalunit;

import java.util.List;
import javax.inject.Inject;

import org.guvnor.structure.backend.backcompat.BackwardCompatibleUtil;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.impl.OrganizationalUnitImpl;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
import org.guvnor.structure.server.organizationalunit.OrganizationalUnitFactory;

public class OrganizationalUnitFactoryImpl implements OrganizationalUnitFactory {

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private BackwardCompatibleUtil backward;

    @Override
    public OrganizationalUnit newOrganizationalUnit( ConfigGroup groupConfig ) {

        OrganizationalUnitImpl organizationalUnit = new OrganizationalUnitImpl( groupConfig.getName(),
                                                                                groupConfig.getConfigItemValue( "owner" ),
                                                                                groupConfig.getConfigItemValue( "defaultGroupId" ) );
        ConfigItem<List<String>> repositories = groupConfig.getConfigItem( "repositories" );
        if ( repositories != null ) {
            for ( String alias : repositories.getValue() ) {

                final Repository repo = repositoryService.getRepository( alias );
                if ( repo != null ) {
                    organizationalUnit.getRepositories().add( repo );
                }
            }
        }

        //Copy in Security Roles required to access this resource
        ConfigItem<List<String>> groups = backward.compat( groupConfig ).getConfigItem( "security:groups" );
        if ( groups != null ) {
            for ( String group : groups.getValue() ) {
                organizationalUnit.getGroups().add( group );
            }
        }
        return organizationalUnit;
    }
}
