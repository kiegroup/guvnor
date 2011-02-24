/*
 * Copyright 2011 JBoss Inc
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
package org.drools.guvnor.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.server.security.RoleTypes;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.guvnor.server.util.PackageConfigDataFactory;
import org.drools.repository.PackageItem;
import org.drools.repository.PackageIterator;
import org.drools.repository.RepositoryFilter;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import com.google.gwt.user.client.rpc.SerializationException;

/**
 * Handles operations for packages
 */
@Name("org.drools.guvnor.server.RepositoryPackageOperations")
@AutoCreate
public class RepositoryPackageOperations {
    private RulesRepository            repository;

    private static final LoggingHelper log = LoggingHelper
                                                   .getLogger( RepositoryPackageOperations.class );

    public void setRulesRepository(RulesRepository repository) {
        this.repository = repository;
    }

    public RulesRepository getRulesRepository() {
        return repository;
    }

    protected PackageConfigData[] listPackages(boolean archive,
                                               String workspace,
                                               RepositoryFilter filter) {
        List<PackageConfigData> result = new ArrayList<PackageConfigData>();
        PackageIterator pkgs = getRulesRepository().listPackages();
        handleIteratePackages( archive,
                               workspace,
                               filter,
                               result,
                               pkgs );

        sortPackages( result );
        return result.toArray( new PackageConfigData[result.size()] );
    }

    private void handleIteratePackages(boolean archive,
                                       String workspace,
                                       RepositoryFilter filter,
                                       List<PackageConfigData> result,
                                       PackageIterator pkgs) {
        pkgs.setArchivedIterator( archive );
        while ( pkgs.hasNext() ) {
            PackageItem pkg = pkgs.next();

            PackageConfigData data = new PackageConfigData();
            data.uuid = pkg.getUUID();
            data.name = pkg.getName();
            data.archived = pkg.isArchived();
            data.workspaces = pkg.getWorkspaces();
            handleIsPackagesListed( archive,
                                    workspace,
                                    filter,
                                    result,
                                    data );

            data.subPackages = listSubPackages( pkg,
                                                archive,
                                                null,
                                                filter );
        }
    }

    private PackageConfigData[] listSubPackages(PackageItem parentPkg,
                                                boolean archive,
                                                String workspace,
                                                RepositoryFilter filter) {
        List<PackageConfigData> children = new LinkedList<PackageConfigData>();

        PackageIterator pkgs = parentPkg.listSubPackages();
        handleIteratePackages( archive,
                               workspace,
                               filter,
                               children,
                               pkgs );

        sortPackages( children );
        return children.toArray( new PackageConfigData[children.size()] );
    }

    void sortPackages(List<PackageConfigData> result) {
        Collections.sort( result,
                          new Comparator<PackageConfigData>() {

                              public int compare(final PackageConfigData d1,
                                                 final PackageConfigData d2) {
                                  return d1.name.compareTo( d2.name );
                              }

                          } );
    }

    private void handleIsPackagesListed(boolean archive,
                                        String workspace,
                                        RepositoryFilter filter,
                                        List<PackageConfigData> result,
                                        PackageConfigData data) {
        if ( !archive && (filter == null || filter.accept( data,
                                                           RoleTypes.PACKAGE_READONLY )) && (workspace == null || isWorkspace( workspace,
                                                                                                                               data.workspaces )) ) {
            result.add( data );
        } else if ( archive && data.archived && (filter == null || filter.accept( data,
                                                                                  RoleTypes.PACKAGE_READONLY )) && (workspace == null || isWorkspace( workspace,
                                                                                                                                                      data.workspaces )) ) {
            result.add( data );
        }
    }

    private boolean isWorkspace(String workspace,
                                String[] workspaces) {
        for ( String w : workspaces ) {
            if ( w.equals( workspace ) ) {
                return true;
            }
        }
        return false;
    }

    protected PackageConfigData loadGlobalPackage() {
        PackageItem item = getRulesRepository().loadGlobalArea();

        PackageConfigData data = PackageConfigDataFactory.createPackageConfigDataWithOutDependencies( item );

        if ( data.isSnapshot ) {
            data.snapshotName = item.getSnapshotName();
        }

        return data;
    }

    protected void copyPackage(String sourcePackageName,
                               String destPackageName) throws SerializationException {

        try {
            log.info( "USER:" + getCurrentUserName() + " COPYING package [" + sourcePackageName + "] to  package [" + destPackageName + "]" );

            getRulesRepository().copyPackage( sourcePackageName,
                                              destPackageName );
        } catch ( RulesRepositoryException e ) {
            log.error( "Unable to copy package.",
                       e );
            throw e;
        }

        // If we allow package owner to copy package, we will have to update the
        // permission store
        // for the newly copied package.
        // Update permission store
        /*
         * String copiedUuid = ""; try { PackageItem source =
         * repository.loadPackage( destPackageName ); copiedUuid =
         * source.getUUID(); } catch (RulesRepositoryException e) { log.error( e
         * ); } PackageBasedPermissionStore pbps = new
         * PackageBasedPermissionStore(); pbps.addPackageBasedPermission(new
         * PackageBasedPermission(copiedUuid,
         * Identity.instance().getPrincipal().getName(),
         * RoleTypes.PACKAGE_ADMIN));
         */
    }

    protected void removePackage(String uuid) {

        try {
            PackageItem item = getRulesRepository().loadPackageByUUID( uuid );
            log.info( "USER:" + getCurrentUserName() + " REMOVEING package [" + item.getName() + "]" );
            item.remove();
            getRulesRepository().save();
        } catch ( RulesRepositoryException e ) {
            log.error( "Unable to remove package.",
                       e );
            throw e;
        }
    }

    private String getCurrentUserName() {
        return getRulesRepository().getSession().getUserID();
    }
}
