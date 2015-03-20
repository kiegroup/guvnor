package org.guvnor.structure.organizationalunit.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class OrganizationalUnitImpl implements OrganizationalUnit {

    private String name;
    private String defaultGroupId;
    private String owner;

    private Collection<Repository> repositories = new ArrayList<Repository>();
    private Collection<String> groups = new ArrayList<String>();
    private boolean requiresRefresh = true;

    public OrganizationalUnitImpl() {
    }

    public OrganizationalUnitImpl( final String name,
                                   final String owner,
                                   final String defaultGroupId ) {
        this.name = name;
        this.owner = owner;
        this.defaultGroupId = defaultGroupId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOwner() {
        return owner;
    }

    @Override
    public String getDefaultGroupId() {
        return defaultGroupId;
    }

    @Override
    public Collection<Repository> getRepositories() {
        return repositories;
    }

    @Override
    public String getSignatureId() {
        return getClass().getName() + "#" + getName();
    }

    @Override
    public Collection<String> getTraits() {
        return Collections.emptySet();
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof OrganizationalUnitImpl ) ) {
            return false;
        }

        OrganizationalUnitImpl ou = (OrganizationalUnitImpl) o;

        if ( name != null ? !name.equals( ou.name ) : ou.name != null ) {
            return false;
        }
        if ( owner != null ? !owner.equals( ou.owner ) : ou.owner != null ) {
            return false;
        }
        if ( defaultGroupId != null ? !defaultGroupId.equals( ou.defaultGroupId ) : ou.defaultGroupId != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = ~~result;
        result = 31 * result + ( owner != null ? owner.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( defaultGroupId != null ? defaultGroupId.hashCode() : 0 );
        result = ~~result;
        return result;
    }

    @Override
    public String toString() {
        return "OrganizationalUnitImpl [name=" + name + ", owner=" + owner + ", repositories=" + repositories
                + ", groups=" + groups + ", defaultGroupId=" + defaultGroupId + "]";
    }

    @Override
    public void markAsCached() {
        this.requiresRefresh = false;
    }

    @Override
    public boolean requiresRefresh() {
        return requiresRefresh;
    }

    @Override
    public Collection<String> getGroups() {
        return groups;
    }
}
